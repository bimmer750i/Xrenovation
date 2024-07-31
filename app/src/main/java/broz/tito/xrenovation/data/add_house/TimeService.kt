package broz.tito.xrenovation.data.add_house

import broz.tito.xrenovation.data.add_house.entities.GetTimeResponse
import retrofit2.Response
import retrofit2.http.GET

interface TimeService {

    @GET("api/timezone/utc")
    suspend fun getTime() : Response<GetTimeResponse>

}