package broz.tito.xrenovation.data.add_house

import android.content.Context
import android.net.Uri
import android.util.Log
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.entities.*
import broz.tito.xrenovation.data.get_houses.entities.FailureGetPointResult
import broz.tito.xrenovation.data.get_houses.entities.GetPointResult
import broz.tito.xrenovation.data.get_houses.entities.PendingGetPointResult
import broz.tito.xrenovation.data.get_houses.entities.RawSuccessGetPointResult
import broz.tito.xrenovation.presentation.entities.DisplayComment
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.app
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.StorageReference
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.mapkit.search.Session.SearchListener
import com.yandex.mapkit.search.SuggestSession.SuggestListener
import com.yandex.runtime.Error
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.File
import java.util.Collections
import javax.inject.Inject

class HouseModel @Inject constructor(val searchManager: SearchManager, val storageReference : StorageReference, val houseService: HouseService, val timeService: TimeService) {

    private val TAG = "AddHouseModel"

    fun suggestAddress(context : Context, address : String) : Flow<SuggestAddressResult> = callbackFlow<SuggestAddressResult> {
        val suggestSession = searchManager.createSuggestSession()
        try {
            trySend(PendingSuggestAddressResult())
            suggestSession.suggest(context.getString(R.string.moscow) + "," + address, BoundingBox(
                Point(55.143833, 36.80325), Point(56.021389, 37.189278)
            ),
                SuggestOptions()
                .setSuggestTypes(
                    SuggestType.GEO.value
                ),
                object  : SuggestListener {
                    override fun onResponse(response : SuggestResponse) {
                        trySend(SuccessSuggestAddressResult(response.items))
                    }

                    override fun onError(error : Error) {
                        Log.d(TAG, "error -- $error")
                        trySend(FailureSuggestAddressResult(error.toString()))
                    }

                })
        }
        catch (e : Exception) {
            Log.d(TAG, "error -- ${e.message}")
            trySend(FailureSuggestAddressResult(e.message.toString()))
        }
        awaitClose {
            suggestSession.reset()
        }
    }.flowOn(Dispatchers.Main)

    fun searchPoint(point: Point) : Flow<SearchPointResult> = callbackFlow<SearchPointResult> {
        trySend(PendingSearchPointResult())
        val session = searchManager.submit(point,null,SearchOptions(),object : SearchListener {
            override fun onSearchResponse(response : Response) {
                val components = response.collection.children.firstOrNull()?.obj?.
                metadataContainer?.
                getItem(ToponymObjectMetadata::class.java)?.
                address?.
                components
                val region = components?.firstOrNull { it.kinds.contains(Address.Component.Kind.REGION) }?.name
                val province = components?.firstOrNull { it.kinds.contains(Address.Component.Kind.PROVINCE) }?.name
                val area = components?.firstOrNull { it.kinds.contains(Address.Component.Kind.AREA) }?.name
                val district = components?.firstOrNull { it.kinds.contains(Address.Component.Kind.DISTRICT) }?.name
                val street = components?.firstOrNull { it.kinds.contains(Address.Component.Kind.STREET) }?.name
                val house = components?.firstOrNull { it.kinds.contains(Address.Component.Kind.HOUSE) }?.name
                components?.firstOrNull { it.kinds.contains(Address.Component.Kind.LOCALITY) }?.name.let {
                    if (it != null) {
                        trySend(SuccessSearchPointResult(SearchPointAddress(province,area,it,street,house)))
                        Log.d(TAG, "region: $region -- province: $province district: $district")
                        Log.d(TAG, "success -- $area -- $it -- $street -- $house")
                    }
                    else {
                        FailureSearchPointResult("CITY_NOT_FOUND")
                        Log.d(TAG, "failure -- search_point -- $it")
                    }
                }
            }

            override fun onSearchError(error : Error) {
                trySend(FailureSearchPointResult(error.toString()))
                Log.d(TAG, "failure -- search_point")
            }
        }
        )
        awaitClose {
            session.cancel()
        }
    }.flowOn(Dispatchers.Main)

    fun loadPhotosToFireBase(localId: String,path: String, list : ArrayList<String>) : Flow<LoadPhotosResult> = flow<LoadPhotosResult> {
        var result : LoadPhotosResult = PendingLoadPhotosResult()
        emit(result)
        val lastTimePosted = getLastTimePosted(localId)
        val now = getTime()
        if (lastTimePosted != null && now != null && (now - lastTimePosted.lastTimePosted < MILLISECONDS_DAY)) {
            result = FailureLoadPhotosResult("POST_TIMEOUT")
            Log.d(TAG, "loadPhotosToFireBase -- failure: POST_TIMEOUT")
        }
        else if (lastTimePosted != null && now != null) {
            try {
                val photosList = withContext(Dispatchers.IO) {async {
                    loadPhotos(path,list)
                }}.await()
                Log.d(TAG, "loadPhotosToFireBase -- success -- $photosList")
                result = SuccessLoadPhotosResult(photosList as ArrayList<String>)
            }
            catch (e : Exception) {
                result = FailureLoadPhotosResult(e.message.toString())
                Log.d(TAG, "loadPhotosToFireBase -- ${e.message}")
            }
        }
        else {
            result = FailureLoadPhotosResult("POST_TIMEOUT")
            Log.d(TAG, "loadPhotosToFireBase -- failure: POST_TIMEOUT")
        }
        emit(result)
    }.flowOn(Dispatchers.IO)


    @Throws(Exception::class)
    private suspend fun loadPhotos(path : String, list : ArrayList<String>) : List<String> = coroutineScope() {
        Log.d(TAG, "loadPhotos -- orig list : $list")
        val syncedList = Collections.synchronizedList(arrayListOf<String?>(null,null,null,null,null))
        val res = ArrayList<String>()
        val time = System.currentTimeMillis()
        list.map {uri ->
            async(Dispatchers.IO) {
                val index = list.indexOf(uri)
                val time = System.currentTimeMillis()
                val child = storageReference.child("$path-house/house-$index.jpg")
                    child.putFile(Uri.fromFile(File(uri)))
                        .await().also {
                            if (it.task.isSuccessful) {
                                Log.d(TAG, "Downloading file-$index took: ${System.currentTimeMillis()-time}")
                                child.downloadUrl.await().also {
                                    syncedList.add(index,it.toString())
                                    Log.d(TAG, "Getting file-url-$index took: ${System.currentTimeMillis()-time}")                                }
                            }
                            else {
                                throw Exception("FAILED_TO_DOWNLOAD")
                            }
                        }
            }
        }.awaitAll()
        syncedList.forEach {
            it?.let {
                res.add(it)
            }
        }
        return@coroutineScope res.also {
            Log.d(TAG, "loadPhotos -- url list : $res")
            Log.d(TAG, "loadPhotos: took time: ${System.currentTimeMillis() - time}")
        }
    }


    fun addHouse(localId: String, name : String, body : House, accessToken : String) : Flow<AddHouseResult> = flow {
        var result : AddHouseResult = PendingAddHouseResult()
        emit(result)
        try {
                val response = houseService.addHouse(body,accessToken)
                Log.d(TAG, "addHouse: ${response.raw()}")
                if (!response.isSuccessful) {
                    result = FailureAddHouseResult(response.body().toString())
                    Log.d(TAG, "addHouse -- failure -- ${response.code()}")
                }
                else {
                    response.body()?.let {
                        result = SuccessAddHouseResult(it)
                        Log.d(TAG, "addHouse -- success: ${it}")
                    }
                }
            }
            catch (e : Exception) {
                result = FailureAddHouseResult(e.message.toString())
                Log.d(TAG, "addHouse -- failure -- ${e.message.toString()}")
            }
        emit(result)
    }.flowOn(Dispatchers.IO)

    fun addHousePoint(body : HousePoint, houseId : String, accessToken : String) : Flow<AddHousePointResult> = flow {
        var result : AddHousePointResult = PendingAddHousePointResult()
        emit(result)
        try {
            val response = houseService.addPoint(body,houseId,accessToken)
            if (!response.isSuccessful) {
                result = FailureAddHousePointResult(response.body().toString())
                Log.d(TAG, "addHouse_Point -- failure -- ${response.code()}")
            }
            else {
                response.body()?.let {
                    result = SuccessAddHousePointResult(it)
                    Log.d(TAG, "addHouse_Point -- success: ${it}")
                }
            }
        }
        catch (e : Exception) {
            result = FailureAddHousePointResult(e.message.toString())
            Log.d(TAG, "addHouse_Point -- failure -- ${e.message.toString()}")
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    fun getPoints() : Flow<GetPointResult> = flow {
        var result : GetPointResult = PendingGetPointResult()
        emit(result)
        val startTime = System.currentTimeMillis()
        try {
            val response = houseService.getPoints()
            if (!response.isSuccessful) {
                result = FailureGetPointResult(response.body().toString())
                Log.d(TAG, "getPoints -- failure -- ${response.code()}")
            }
            else {
                response.body()?.let {
                    result = RawSuccessGetPointResult(it)
                }
                Log.d(TAG, "getPoints - took ${System.currentTimeMillis() - startTime} mS -- success -- ${(response.body().toString())}")
            }
        }
        catch (e : Exception) {
            result = FailureGetPointResult(e.message.toString())
            Log.d(TAG, "getPoints -- failure -- ${e.message.toString()}")
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    fun getHouse(houseId : String) : Flow<GetHouseResult> = flow {
        var result : GetHouseResult = PendingGetHouseResult()
        emit(result)
        try {
            val response = houseService.getHouse(houseId)
            if (!response.isSuccessful) {
                result = FailureGetHouseResult(response.body().toString())
                Log.d(TAG, "getHouses -- failure -- ${response.code()}")
            }
            else {
                response.body()?.let {
                    result = SuccessGetHouseResult(houseId,it)
                    Log.d(TAG, "getHouse -- success -- $it ")
                }
            }
        }
        catch (e : Exception) {
            result = FailureGetHouseResult(e.message.toString())
            Log.d(TAG, "getHouses -- failure -- ${e.message.toString()}")
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    fun addComment(localId: String,houseId: String,comment: Comment, accessToken : String) : Flow<AddCommentResult> = flow {
        var result : AddCommentResult = PendingAddCommentResult()
        emit(result)
        val lastTimePosted = getLastTimePosted(localId)
        val now = getTime()
        Log.d(TAG, "addComment -- lastTimePosted: ${lastTimePosted?.lastTimePosted}")
        if (lastTimePosted?.lastTimePosted != null && now != null && (now - lastTimePosted.lastTimePosted < MILLISECONDS_DAY)) {
            result = FailureAddCommentResult("POST_TIMEOUT")
            Log.d(TAG, "addComment -- failure: POST_TIMEOUT")
        }
        else  {
            try {
                val time = Timestamp.now().seconds
                comment.timeAdded = time
                val response = houseService.addComment(comment,accessToken)
                if (!response.isSuccessful) {
                    result = FailureAddCommentResult(response.body().toString())
                    Log.d(TAG, "addComment -- failure: ${response.body().toString()}")
                }
                else {
                    response.body()?.let {
                        it.name?.let {
                            result = SuccessAddCommentResult(it)
                            Log.d(TAG, "addComment -- success -- $it")
                            val time = getTime()
                            time?.let {
                                addLastTimePosted(localId,LastTimePosted(localId,it),accessToken)
                            }
                        }
                    }
                }
            }
            catch (e : Exception) {
                result = FailureAddCommentResult(e.message.toString())
                Log.d(TAG, "addComment -- failure: ${e.message}")
            }
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    fun getComments(houseId: String) : Flow<GetCommentsResult> = flow {
        var result : GetCommentsResult = PendingGetCommentsResult()
        emit(result)
        try {
            val response = houseService.getComments(houseId)
            if (!response.isSuccessful) {
                result = FailureGetCommentsResult(response.body().toString())
                Log.d(TAG, "getComments -- failure -- ${response.body().toString()}")
            }
            else {
                response.body()?.let {
                    if (it is JsonNull) {
                        result= RawSuccessGetCommentsResult(JsonObject())
                        Log.d(TAG, "getComments -- success -- no comments")
                    }
                    else {
                        result = RawSuccessGetCommentsResult(it as JsonObject)
                        Log.d(TAG, "getComments -- success -- $it")
                    }
                }
            }
        }
        catch (e : Exception) {
            result = FailureGetCommentsResult(e.message.toString())
            Log.d(TAG, "getComments -- failure -- exception: -- ${e.message.toString()}")
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    // TODO CHECK TIME
    fun addHouseCorrection(localId : String, body : HouseCorrection, accessToken : String) : Flow<AddHouseCorrectionResult> = flow {
        var result : AddHouseCorrectionResult = PendingAddHouseCorrectionResult()
        emit(result)
        val lastTimePosted = getLastTimePosted(localId)
        val now = getTime()
        if (lastTimePosted != null && now != null && (now - lastTimePosted.lastTimePosted < MILLISECONDS_DAY)) {
                result = FailureAddHouseCorrectionResult("POST_TIMEOUT")
                Log.d(TAG, "addHouseCorrection -- failure: POST_TIMEOUT")
        }
        else if (lastTimePosted != null && now != null) {
            try {
                val time = Timestamp.now().seconds
                body.timeAdded = time*1000
                val response = houseService.addHouseCorrection(body,accessToken)
                if (!response.isSuccessful) {
                    result = FailureAddHouseCorrectionResult(response.body().toString())
                    Log.d(TAG, "addHouseCorrection -- failure: ${response.body().toString()}")
                }
                else {
                    response.body()?.let {
                        it.name?.let {
                            result = SuccessAddHouseCorrectionResult(it)
                            val time = Timestamp.now().seconds*1000
                            addLastTimePosted(localId,LastTimePosted(localId,time),accessToken)
                            Log.d(TAG, "addHouseCorrection -- success: $it")
                        }
                    }
                }
            }
            catch (e : Exception) {
                result = FailureAddHouseCorrectionResult(e.message.toString())
                Log.d(TAG, "addHouseCorrection -- failure: ${e.message}")
            }
        }
        else {
            result = FailureAddHouseCorrectionResult("POST_TIMEOUT")
            Log.d(TAG, "addHouseCorrection -- failure: POST_TIMEOUT")
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    suspend fun getLastTimePosted(localId: String) : LastTimePosted? {
        val result : LastTimePosted? = CoroutineScope(Dispatchers.IO).async {
            try {
                val response = houseService.getLastTimePosted(localId)
                if (!response.isSuccessful) {
                    Log.d(TAG, "getLastTimePosted -- failure: ${response.body().toString()}")
                    return@async null
                }
                else {
                    if (response.body() != null) {
                        Log.d(TAG, "getLastTimePosted -- success ${response.body()}")
                        return@async response.body()
                    }
                    else {
                        Log.d(TAG, "getLastTimePosted -- null body")
                        return@async LastTimePosted(localId,0)
                    }
                }
            }
            catch (e : Exception) {
                Log.d(TAG, "getLastTimePosted -- failure: ${e.message}")
                return@async null
            }
        }.await()
        return result
    }

    suspend fun addLastTimePosted(localId: String,body : LastTimePosted, accessToken : String) : LastTimePosted? {
        val result : LastTimePosted? = CoroutineScope(Dispatchers.IO).async {
            try {
                val time = getTime()
                if (time != null) {
                    body.lastTimePosted = time
                }
                val response = houseService.addLastTimePosted(localId, body,accessToken)
                if (!response.isSuccessful) {
                    Log.d(TAG, "addLastTimePosted -- failure: ${response.body().toString()}")
                    return@async null
                }
                else {
                    var result : LastTimePosted? = null
                    response.body()?.let {
                        result = it
                        Log.d(TAG, "addLastTimePosted -- success $it")
                    }
                    return@async result
                }
            }
            catch (e : Exception) {
                Log.d(TAG, "addLastTimePosted -- failure: ${e.message}")
                return@async null
            }
        }.await()
        return result
    }

    suspend fun getTime() : Long? {
        val result : Long? = CoroutineScope(Dispatchers.IO).async {
            try {
                val response = timeService.getTime()
                if (!response.isSuccessful) {
                    Log.d(TAG, "getTime -- failure: ${response.body().toString()}")
                    return@async null
                }
                else {
                    var result : Long? = null
                    response.body()?.let {
                        result = it.unixTime!!*1000
                        Log.d(TAG, "getTime -- success $it")
                    }
                    return@async result
                }
            }
            catch (e : Exception) {
                Log.d(TAG, "getTime -- failure: ${e.message}")
                return@async null
            }
        }.await()
        return result
    }


    companion object {
        private const val MILLISECONDS_DAY : Long = 86_400_000
    }



}