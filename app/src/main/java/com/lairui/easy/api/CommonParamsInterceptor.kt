package com.lairui.easy.api

import java.io.IOException

import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * 自定义一个拦截器
 */
class CommonParamsInterceptor : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //得到原始的请求对象
        var request = chain.request()

        // 执行本次网络请求操作，返回response信息
        val response = chain.proceed(request)

        //得到用户所使用的请求方式
        val method = request.method()

        //得到原有的请求参数
        val oldBody = request.body() as FormBody?//1 2 3

        //得到原始的url
        val oldUrl = request.url().toString()

        //重新构建请求体
        request = Request.Builder()
                .url(oldUrl)
                .build()
        //重新发送请求
        return chain.proceed(request)
    }
}