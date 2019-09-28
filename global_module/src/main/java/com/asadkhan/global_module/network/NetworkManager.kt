package com.asadkhan.global_module.network

import com.asadkhan.global_module.domain.WeatherListData
import com.asadkhan.global_module.tryHere
import com.asadkhan.global_module.tryThis
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject.create
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManager @Inject constructor(
  private val service: ApiService, private val gson: Gson
) : NetworkService {

  private var jsonStringSubject = create<String>()

  val content = "{\"{\"Error\":\"Unable to read response body\"}\"}"

  private fun getJsonMimeType() = MediaType.parse("application/json")

  override fun getRawResponse(path: String): Observable<String> {
    val stream = service.get(path)
    return stream.map { res ->
      tryHere({
        res.body()?.source()?.buffer?.clone()?.readUtf8()
      }, "")
    }
  }

  override fun <RESPONSE> get(clazz: Class<RESPONSE>, path: String): Observable<Response<RESPONSE>> {
    val stream = service.get(path)
    return response(clazz, stream)
  }

  override fun <RESPONSE> getWithQueries(
    clazz: Class<RESPONSE>, path: String, query: Map<String, String>
  ): Observable<Response<RESPONSE>> {

    val stream = service.getWithQueries(path, query)
    return response(clazz, stream)
  }

  private fun <RESPONSE> response(
    clazz: Class<RESPONSE>, ob: Observable<Response<ResponseBody>>
  ): Observable<Response<RESPONSE>> {
    return ob.map { body -> unpackResponseBody(clazz, body) }
  }

  private fun <RESPONSE> unpackResponseBody(
    clazz: Class<RESPONSE>, responseBody: Response<ResponseBody>?
  ): Response<RESPONSE> {
    responseBody?.let { res ->
      println("Response is not null")
      val responseData = res.body()
      responseData?.let { body ->
        println("Response body is not null")
        val bodyConverter = responseConverter(clazz)

        if (clazz == WeatherListData::class.java) {
          publish(body)
        }
        try {
          val genericResponse = bodyConverter.convert(body)
          val raw = res.raw()
          return Response.success(genericResponse, raw)
        }
        catch (e: IOException) {
          e.printStackTrace()
          var message = e.message ?: "Error: Something went wrong"
          if (message.isNotEmpty()) {
            message = "Error: $message"
          }
          val s = "Failed to de-serialize response object : ${clazz.name} \n$message"
          throw RuntimeException(s)
        }
        catch (e: Exception) {
          e.printStackTrace()
          throw RuntimeException("Failed to parse response body. Please check: " + e.message, e)
        }
      }
      // ResponseBody is null, It is highly probable an Http Exception occurred
      // In that case, errorBody will hold the response value
      // Handle All Http Exceptions here.

      var msg = "" //= "Unable to read response body";
      try {
        res.errorBody()?.let { eBody ->
          try {
            val utfString = eBody.source().buffer.clone().readUtf8()
            if (utfString != null && utfString.isNotEmpty()) {
              msg = utfString
            }
          }
          catch (e: Exception) {
            e.printStackTrace()
          }
        }
        if (msg.isEmpty()) {
          msg = content
        }
        println("\nError Message\n")
        println(msg)
      }
      catch (e: Exception) {
        e.printStackTrace()
      }
      val rawResponse = createErrorResponseFromBody(res)
      return Response.error(defaultErrorResponseBody(), rawResponse)
    }
    // The entire response body is null
    return Response.error(900, defaultErrorResponseBody())
  }

  private fun publish(body: ResponseBody) {
    try {
      val string = body.source().buffer.clone().readUtf8()
      val subscribe = Observable.just(string)
        .subscribeOn(io())
        .delay(2, SECONDS)
        .subscribe({
          tryThis {
            jsonStringSubject.onNext(string)
          }
        }, Throwable::printStackTrace)

    }
    catch (e: Exception) {
      e.printStackTrace()
      jsonStringSubject = create()
    }
  }

  private fun createErrorResponseFromBody(response: Response<ResponseBody>): okhttp3.Response {
    return okhttp3.Response.Builder().body(defaultErrorResponseBody()).request(response.raw().request())
      .protocol(response.raw().protocol()).code(response.code()).headers(response.headers()).message(response.message())
      .build()
  }

  private fun defaultErrorResponseBody() = ResponseBody.create(getJsonMimeType(), content)

  private fun <RESPONSE> responseConverter(
    clazz: Class<RESPONSE>
  ): GsonResponseBodyConverter<RESPONSE> {
    val typeAdapter = gson.getAdapter(clazz)
    return GsonResponseBodyConverter(gson, typeAdapter)
  }

  override fun getJsonStringResponse(): Observable<String> {
    return jsonStringSubject.subscribeOn(io())
  }
}
