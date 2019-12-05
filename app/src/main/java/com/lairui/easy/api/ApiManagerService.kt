package com.lairui.easy.api

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * 描述：retrofit的接口service定义
 *
 */

interface ApiManagerService {

    //Delete请求(支持RequsetBody) 返回ResponseBody
    @Headers("Content-Type: application/json", "Accept: application/json")
    @HTTP(method = "DELETE", hasBody = true)
    fun requestDeleteToRes(@HeaderMap header: Map<String, String>, @Url url: String, @Body params: RequestBody): Observable<Response<ResponseBody>>

    //Delete请求(支持RequsetBody) 返回Map
    @Headers("Content-Type: application/json", "Accept: application/json")
    @HTTP(method = "DELETE", hasBody = true)
    fun requestDeleteToMap(@HeaderMap header: Map<String, String>, @Url url: String, @Body params: RequestBody): Observable<MutableMap<String, Any>>


    //Put请求 返回ResonseBody
    @Headers("Content-Type: application/json", "Accept: application/json")
    @PUT
    fun requestPutToRes(@HeaderMap headers: Map<String, String>, @Url url: String, @Body params: RequestBody): Observable<Response<ResponseBody>>

    //Put请求 返回Map
    @Headers("Content-Type: application/json", "Accept: application/json")
    @PUT
    fun requestPutToMap(@HeaderMap headers: Map<String, String>, @Url url: String, @Body params: RequestBody): Observable<MutableMap<String, Any>>


    //Get请求 返回ResonseBody
    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET
    fun requestGetToRes(@HeaderMap headers: Map<String, String>, @Url url: String, @QueryMap param: Map<String, String>): Observable<ResponseBody>

    //Get请求 返回Map
    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET
    fun requestGetToMap(@HeaderMap headers: Map<String, String>, @Url url: String, @QueryMap param: Map<String, String>): Observable<MutableMap<String, Any>>


    //下载大文件(支持断点续传)
    @Streaming //大文件时要加不然会OOM
    @GET
    fun downloadFile(@Header("RANGE") start: String, @HeaderMap headers: Map<String, String>, @Url fileUrl: String, @QueryMap param: Map<String, String>): Observable<Response<ResponseBody>>


    //POST请求  返回ResonseBody
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST
    fun requestPostToRes(@HeaderMap headers: Map<String, String>, @Url url: String, @Body params: RequestBody): Observable<Response<ResponseBody>>

    //POST请求  返回Map
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST
    fun requestPostToMap(@HeaderMap headers: Map<String, String>, @Url url: String, @Body params: RequestBody): Observable<MutableMap<String, Any>>


    //表单提交 返回ResonseBody
    @Headers("Content-type:application/x-www-form-urlencoded;charset=UTF-8", "Accept: application/json")
    @FormUrlEncoded
    @POST
    fun requestPostFormToRes(@HeaderMap header: Map<String, String>, @Url url: String, @FieldMap params: Map<String, String>): Observable<Response<ResponseBody>>

    //表单提交 返回Map
    @Headers("Content-type:application/x-www-form-urlencoded;charset=UTF-8", "Accept: application/json")
    @FormUrlEncoded
    @POST
    fun requestPostFormToMap(@HeaderMap header: Map<String, String>, @Url url: String, @FieldMap params: Map<String, String>): Observable<MutableMap<String, Any>>


    //上传文件/图片  返回ResponseBody
    @Multipart
    @POST
    fun postFileToRes(@HeaderMap header: Map<String, String>, @Url url: String, @PartMap usermaps: Map<String, RequestBody>, @Part parts: List<MultipartBody.Part>): Observable<Response<ResponseBody>>

    //上传文件/图片  返回Map
    @Multipart
    @POST
    fun postFileToMap(@HeaderMap header: Map<String, String>, @Url url: String, @PartMap usermaps: Map<String, RequestBody>, @Part parts: List<MultipartBody.Part>): Observable<MutableMap<String, Any>>


    //    //上传文件/图片  返回Map
    //    @Multipart
    //    @POST
    //    Observable<Map<String,Object>> postOneFileToMap(@HeaderMap Map<String, String> header, @Url String url,@PartMap Map<String , RequestBody> usermaps, @Part() List<MultipartBody.Part> parts);


}
