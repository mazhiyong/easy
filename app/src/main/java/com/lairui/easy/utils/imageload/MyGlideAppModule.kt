package com.lairui.easy.utils.imageload

import android.content.Context
import android.util.Log

import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.lairui.easy.api.HttpsUtil

import java.io.InputStream
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient

@GlideModule
class MyGlideAppModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setLogLevel(Log.DEBUG)
        /* int memoryCacheSizeBytes = 1024 * 1024 * 300; // 20mb
        int diskCacheSizeBytes = 1024 * 1024 * 400;  //100 MB
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes))
                .setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));*/

    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }


    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        /*OkHttpClient mHttpClient = new OkHttpClient().newBuilder()
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .build();

        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mHttpClient));*/

        val mHttpClient = OkHttpClient().newBuilder()
                .sslSocketFactory(SSLSocketClient.sslSocketFactory, HttpsUtil.UnSafeTrustManager())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(ProgressInterceptor())
                .hostnameVerifier(SSLSocketClient.hostnameVerifier)
                .build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(mHttpClient))
        //registry.prepend(String.class, InputStream.class, new CustomUrlModelLoader.Factory());
    }

    companion object {
        private val TAG = "MyAppGlideModule"
    }
}