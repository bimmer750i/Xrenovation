package broz.tito.xrenovation.data.add_house

import broz.tito.xrenovation.data.add_house.entities.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface HouseService {

    @PUT("points-suggestions/{houseId}.json")
    suspend fun addPoint(@Body body : HousePoint, @Path("houseId") houseId : String, @Query("auth") accessToken : String) : Response<AddHousePointResponse>

    @POST("houses-suggestions.json")
    suspend fun addHouse(@Body body : House, @Query("auth") accessToken : String) : Response<AddHouseResponse>

    @GET("points.json")
    suspend fun getPoints() : Response<JsonObject>

    @GET("houses/{houseId}.json")
    suspend fun getHouse(@Path("houseId") houseId : String) : Response<House>

    @GET("comments/comments{houseId}.json")
    suspend fun getComments(@Path("houseId") houseId : String) : Response<JsonElement>

    @POST("comments-suggestions.json")
    suspend fun addComment(@Body comment: Comment, @Query("auth") accessToken : String) : Response<AddCommentResponse>

    @POST("corrections.json")
    suspend fun addHouseCorrection(@Body houseCorrection: HouseCorrection, @Query("auth") accessToken : String) : Response<AddHouseCorrectionResponse>

    @PUT("timePosted/{localId}.json")
    suspend fun addLastTimePosted(@Path("localId") localId : String, @Body body : LastTimePosted, @Query("auth") accessToken : String) : Response<LastTimePosted>

    @GET("timePosted/{localId}.json")
    suspend fun getLastTimePosted(@Path("localId") localId : String) : Response<LastTimePosted>
}