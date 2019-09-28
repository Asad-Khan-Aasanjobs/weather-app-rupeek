package com.asadkhan.global_module.network

import io.reactivex.Observable
import retrofit2.Response

interface NetworkService {

  fun getJsonStringResponse(): Observable<String>

  fun getRawResponse(
    path: String
  ): Observable<String>

  fun <RESPONSE> get(
    clazz: Class<RESPONSE>, path: String
  ): Observable<Response<RESPONSE>>

  fun <RESPONSE> getWithQueries(
    clazz: Class<RESPONSE>, path: String, query: Map<String, String>
  ): Observable<Response<RESPONSE>>
}
