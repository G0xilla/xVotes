import com.urbysoft.xmlparser.constants.Constants
import com.urbysoft.xmlparser.data.Nuts
import com.urbysoft.xmlparser.entity.ExternalResultError
import com.urbysoft.xmlparser.entity.ExternalResultOk
import com.urbysoft.xmlparser.entity.MunicipalitiesResultError
import com.urbysoft.xmlparser.entity.MunicipalitiesResultOk
import com.urbysoft.xmlparser.network.StatsService
import com.urbysoft.xmlparser.parse.XMLStatsParser
import okhttp3.ResponseBody
import retrofit2.*

private var api: StatsService? = null

fun main() {
    val parser = XMLStatsParser()
    val retrofitStats = Retrofit.Builder()
        .baseUrl(Constants.statsBaseUrl)
        .build()
    val statsService = retrofitStats.create<StatsService>()

    statsService.getExternalData("123")
        .enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                when(val externalResult = parser.parseExternal(response.body()!!.byteStream())) {
                    is ExternalResultError -> println(externalResult.message)
                    is ExternalResultOk -> externalResult.stats.printStats()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("Failure")
            }
        })

    statsService.getRegionData(regionCode = Nuts.JIHOMORAVSKY_KRAJ.code)
        .enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when(val regionResult = parser.parseMunicipalities(response.body()!!.byteStream())) {
                    is MunicipalitiesResultError -> println(regionResult.message)
                    is MunicipalitiesResultOk -> {
                        regionResult.region.printStats()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("Buddy")
            }

        })
}