package com.asadkhan.global_module.network

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {
  
  @GET("{path}")
  fun get(@Path(value = "path", encoded = true) path: String): Observable<Response<ResponseBody>>
  
  @GET("{path}")
  fun get(
    @Path(value = "path", encoded = true) path: String, @HeaderMap headerMap: Map<String, String>
  ): Observable<Response<ResponseBody>>
  
  @GET("{path}")
  fun getWithQueries(
    @Path(value = "path", encoded = true) path: String, @QueryMap queryMap: Map<String, String>
  ): Observable<Response<ResponseBody>>
  
  @GET("{path}")
  fun getWithQueries(
    @Path(value = "path", encoded = true) path: String, @QueryMap queryMap: Map<String, String>, @HeaderMap headerMap: Map<String, String>
  ): Observable<Response<ResponseBody>>
  
}
