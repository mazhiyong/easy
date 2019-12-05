package com.lairui.easy.utils.face

import android.content.Context

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.luck.picture.lib.tools.Constant
import org.apache.http.Header
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Random

/**
 * 代码参考
 *
 * 这里写了一些代码帮助（仅供参考）
 */
class CodeHelp {

    /**
     * 获取活体检测的BestImage和Delta 注意：需要在活体检测成功后调用
     *
     * 如何获取idDataStruct： （从活体检测器中获取） FaceIDDataStruct idDataStruct =
     * detector.getFaceIDDataStruct();
     */
/*
    fun getBestImageAndDelta(idDataStruct: FaceIDDataStruct) {
        val delta = idDataStruct.delta // 获取delta；
        val images = idDataStruct.images as HashMap<String, ByteArray>// 获取所有图片
        for (key in idDataStruct.images.keys) {
            val data = idDataStruct.images[key]
            if (key == "image_best") {
                val imageBestData = data// 这是最好的一张图片
            } else if (key == "image_env") {
                val imageEnvData = data// 这是一张全景图
            } else {
                // 其余为其他图片，根据需求自取
            }
        }
    }
*/

    /**
     * 如何调用detect方法
     */
    private fun imageDetect() {
        val asyncHttpclient = AsyncHttpClient()
        val params = RequestParams()
        params.put("api_key", "API_KEY")
        params.put("api_secret", "API_SECRET")

        try {
            params.put("image", File("imagePath"))// 传入照片路径
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val url = "https://api.faceid.com/faceid/v1/detect"
        asyncHttpclient.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>,
                                   responseByte: ByteArray) {
                // 上传成功获取token
                val successStr = String(responseByte)
                try {
                    val jObject = JSONObject(successStr)
                            .getJSONArray("faces").getJSONObject(0)
                    val token = jObject.getString("token")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>,
                                   responseBody: ByteArray, error: Throwable) {
                // 上传失败
            }
        })
    }

    /**
     * 如何调用Verify1.0方法
     *
     * 如何获取idDataStruct： （从活体检测器中获取） FaceIDDataStruct idDataStruct =
     * detector.getFaceIDDataStruct();
     */
/*
    fun imageVerify_1(idDataStruct: FaceIDDataStruct) {
        val requestParams = RequestParams()
        requestParams.put("idcard_name", "身份证姓名")
        requestParams.put("idcard_number", "身份证号码")
        try {
            requestParams.put("image_idcard", File("image_idcard"))// 传入身份证头像照片路径
        } catch (e: Exception) {
        }

        requestParams.put("delta", idDataStruct.delta)
        requestParams.put("api_key", "API_KEY")
        requestParams.put("api_secret", "API_SECRET")

        for ((key, value) in idDataStruct.images) {
            requestParams.put(key,
                    ByteArrayInputStream(value))
        }
        val asyncHttpClient = AsyncHttpClient()
        val url = "https://api.faceid.com/faceid/v1/verify"
        asyncHttpClient.post(url, requestParams,
                object : AsyncHttpResponseHandler() {
                    override fun onSuccess(i: Int, headers: Array<Header>, bytes: ByteArray) {
                        try {
                            val successStr = String(bytes)
                            val jsonObject = JSONObject(successStr)
                            if (!jsonObject.has("error")) {
                                // 活体最好的一张照片和公安部系统上身份证上的照片比较
                                val faceidConfidence = jsonObject
                                        .getJSONObject("result_faceid")
                                        .getDouble("confidence")
                                val jsonObject2 = jsonObject
                                        .getJSONObject("result_faceid")
                                        .getJSONObject("thresholds")
                                val faceidThreshold = jsonObject2
                                        .getDouble("1e-3")
                                val faceidTenThreshold = jsonObject2
                                        .getDouble("1e-4")
                                val faceidHundredThreshold = jsonObject2
                                        .getDouble("1e-5")

                                try {
                                    // 活体最好的一张照片和拍摄身份证上的照片的比较
                                    val jObject = jsonObject
                                            .getJSONObject("result_idcard")
                                    val idcardConfidence = jObject
                                            .getDouble("confidence")
                                    val idcardThreshold = jObject
                                            .getJSONObject("thresholds")
                                            .getDouble("1e-3")
                                    val idcardT = jObject.getJSONObject(
                                            "thresholds").getDouble("1e-4")
                                    val idcardHundredThreshold = jObject
                                            .getJSONObject("thresholds")
                                            .getDouble("1e-5")
                                } catch (e: Exception) {

                                }

                                val verifyresult = jsonObject.toString()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    override fun onFailure(i: Int, headers: Array<Header>,
                                           bytes: ByteArray, throwable: Throwable) {
                        // 请求失败
                    }
                })
    }
*/

    /**
     * 如何调用Verify2.0方法
     *
     * 如何获取idDataStruct： （从活体检测器中获取） FaceIDDataStruct idDataStruct =
     * detector.getFaceIDDataStruct();
     */
/*
    fun imageVerify_2(idDataStruct: FaceIDDataStruct) {
        val requestParams = RequestParams()
        requestParams.put("name", "身份证姓名")
        requestParams.put("idcard", "身份证号码")
        try {
            requestParams.put("image_ref1", FileInputStream(File(
                    "image_idcard")))// 传入身份证头像照片路径
        } catch (e: Exception) {
        }

        requestParams.put("delta", idDataStruct.delta)
        requestParams.put("api_key", "API_KEY")
        requestParams.put("api_secret", "API_SECRET")

        requestParams.put("comparison_type", 1.toString() + "")
        requestParams.put("face_image_type", "meglive")

        for ((key, value) in idDataStruct.images) {
            requestParams.put(key,
                    ByteArrayInputStream(value))
        }
        val asyncHttpClient = AsyncHttpClient()
        val url = "https://api.megvii.com/faceid/v2/verify"
        asyncHttpClient.post(url, requestParams,
                object : AsyncHttpResponseHandler() {
                    override fun onSuccess(i: Int, headers: Array<Header>, bytes: ByteArray) {

                        val successStr = String(bytes)
                        val jsonObject: JSONObject
                        try {
                            jsonObject = JSONObject(successStr)
                            if (!jsonObject.has("error")) {
                                // 活体最好的一张照片和公安部系统上身份证上的照片比较
                                val confidence = jsonObject.getJSONObject(
                                        "result_faceid")
                                        .getDouble("confidence")
                                val jsonObject2 = jsonObject
                                        .getJSONObject("result_faceid")
                                        .getJSONObject("thresholds")
                                val threshold = jsonObject2
                                        .getDouble("1e-3")
                                val tenThreshold = jsonObject2
                                        .getDouble("1e-4")
                                val hundredThreshold = jsonObject2
                                        .getDouble("1e-5")

                                try {
                                    // 活体最好的一张照片和拍摄身份证上的照片的比较
                                    val jObject = jsonObject
                                            .getJSONObject("result_ref1")
                                    val idcard_confidence = jObject
                                            .getDouble("confidence")
                                    val idcard_threshold = jObject
                                            .getJSONObject("thresholds")
                                            .getDouble("1e-3")
                                    val idcard_tenThreshold = jObject
                                            .getJSONObject("thresholds")
                                            .getDouble("1e-4")
                                    val idcard_hundredThreshold = jObject
                                            .getJSONObject("thresholds")
                                            .getDouble("1e-5")
                                } catch (e: Exception) {

                                }

                                // 解析faceGen
                                val jObject = jsonObject
                                        .getJSONObject("face_genuineness")

                                val mask_confidence = jObject
                                        .getDouble("mask_confidence").toFloat()
                                val mask_threshold = jObject
                                        .getDouble("mask_threshold").toFloat()
                                val screen_replay_confidence = jObject
                                        .getDouble("screen_replay_confidence").toFloat()
                                val screen_replay_threshold = jObject
                                        .getDouble("screen_replay_threshold").toFloat()
                                val synthetic_face_confidence = jObject
                                        .getDouble("synthetic_face_confidence").toFloat()
                                val synthetic_face_threshold = jObject
                                        .getDouble("synthetic_face_threshold").toFloat()
                            }
                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }

                    }

                    override fun onFailure(i: Int, headers: Array<Header>,
                                           bytes: ByteArray, throwable: Throwable) {
                        // 请求失败
                    }
                })
    }
*/

    /**
     * ocridcard接口调用
     */
    fun imageOCR() {
        val rParams = RequestParams()
        rParams.put("api_key", "API_KEY")
        rParams.put("api_secret", "API_SECRET")
        try {
            rParams.put("image", File("imagePath"))// 身份证照片图片地址
        } catch (e1: FileNotFoundException) {
            e1.printStackTrace()
        }

        rParams.put("legality", 1.toString() + "")// 传入1可以判断身份证是否
        // 被编辑/是真实身份证/是复印件/是屏幕翻拍/是临时身份证
        val asyncHttpclient = AsyncHttpClient()
        val url = "https://api.faceid.com/faceid/v1/ocridcard"
        asyncHttpclient.post(url, rParams, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>,
                                   responseByte: ByteArray) {
                val successStr = String(responseByte)
                try {
                    val jObject = JSONObject(successStr)
                    if ("back" == jObject.getString("side")) {
                        // 身份证背后信息
                    } else {
                        // 身份证正面信息
                    }
                } catch (e: Exception) {
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>,
                                   responseBody: ByteArray, error: Throwable) {
                // 上传失败
            }
        })
    }

    companion object {

        /**
         * 根据byte数组，生成图片
         */
        fun saveJPGFile(mContext: Context, data: ByteArray?, key: String): String? {
            if (data == null)
                return null

            val mediaStorageDir = mContext.getExternalFilesDir(Constant.ACTION_CROP_DATA)
            if (!mediaStorageDir!!.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null
                }
            }

            var bos: BufferedOutputStream? = null
            var fos: FileOutputStream? = null
            try {
                val jpgFileName = (System.currentTimeMillis().toString() + ""
                        + Random().nextInt(1000000) + "_" + key + ".jpg")
                fos = FileOutputStream("$mediaStorageDir/$jpgFileName")
                bos = BufferedOutputStream(fos)
                bos.write(data)
                return mediaStorageDir.absolutePath + "/" + jpgFileName
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (bos != null) {
                    try {
                        bos.close()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
            }
            return null
        }
    }
}