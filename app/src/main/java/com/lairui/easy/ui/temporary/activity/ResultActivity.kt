package com.lairui.easy.ui.temporary.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.ErrorHandler
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.manage.ActivityManager
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.utils.face.RotaterView
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.ui.module.activity.LoginActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams


import org.apache.http.Header
import org.json.JSONException
import org.json.JSONObject

import java.io.ByteArrayInputStream
import java.util.HashMap

class ResultActivity : BasicActivity(), View.OnClickListener, RequestView {
    private var textView: TextView? = null
    private var mImageView: ImageView? = null
    private var ll_result_image: LinearLayout? = null
    private var bestImage: ImageView? = null
    private var envImage: ImageView? = null
    private var bestPath: String? = null
    private val envPath: String? = null


    private var mSubmitBut: Button? = null

    private var mLoadingWindow: LoadingWindow? = null
    private var mActivityManager: ActivityManager? = null

    private var mAuthFlow: String? = ""
    private var mDelta: String? = ""
    private var mIdName: String? = ""
    private var mIdNum: String? = ""

    private var mIsSuccess = false

    private var mOpType = 0

    override val contentView: Int
        get() = R.layout.activity_result

    private var mMediaPlayer: MediaPlayer? = null


    private var mRequestTag = ""
    override fun init() {

        mLoadingWindow = LoadingWindow(this@ResultActivity, R.style.Dialog)

        mImageView = findViewById<View>(R.id.result_status) as ImageView
        textView = findViewById<View>(R.id.result_text_result) as TextView
        ll_result_image = findViewById<View>(R.id.ll_result_image) as LinearLayout
        bestImage = findViewById<View>(R.id.iv_best) as ImageView
        envImage = findViewById<View>(R.id.iv_env) as ImageView
        mSubmitBut = findViewById(R.id.result_next)
        mSubmitBut!!.setOnClickListener(this)

        mActivityManager = ActivityManager.instance
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) //隐藏状态栏
        initData()
    }


    private fun initData() {
        val bundle = intent.extras
        if (bundle != null) {
            mAuthFlow = bundle.getString("auth_flow")
            mIdName = bundle.getString("name")
            mIdNum = bundle.getString("idno")
            mOpType = bundle.getInt(MbsConstans.FaceType.FACE_KEY)
        }
        val resultOBJ = bundle!!.getString("result")
        try {
            val result = JSONObject(resultOBJ)
            textView!!.text = result.getString("result")
            val resID = result.getInt("resultcode")
            checkID(resID)
            val isSuccess = result.getString("result") == "校验成功"
            mIsSuccess = isSuccess
            mImageView!!.setImageResource(if (isSuccess)
                R.drawable.result_success
            else
                R.drawable.result_failded)
            if (isSuccess) {
                val delta = bundle.getString("delta")
                mDelta = bundle.getString("delta")

                val images = bundle.getSerializable("images") as Map<String, ByteArray>
                val image_best = images["image_best"]
                val image_env = images["image_env"]
                //N张动作图根据需求自取，对应字段action1、action2 ...
                // byte[] image_action1 = images.get("image_action1);
                ll_result_image!!.visibility = View.VISIBLE
                bestImage!!.setImageBitmap(BitmapFactory.decodeByteArray(image_best, 0, image_best!!.size))
                envImage!!.setImageBitmap(BitmapFactory.decodeByteArray(image_env, 0, image_env!!.size))

                //保存图片
               // bestPath = ConUtil.saveJPGFile(this, image_best, "image_best")
                //envPath = ConUtil.saveJPGFile(this, image_env, "image_env");
                //调用活体比对API
                //imageVerify(images,delta);
                mSubmitBut!!.text = resources.getString(R.string.but_submit)
            } else {
                mSubmitBut!!.text = "返回并重新验证"
                ll_result_image!!.visibility = View.GONE
            }
            doRotate(isSuccess)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun checkID(resID: Int) {
       /* if (resID == R.string.verify_success) {
            doPlay(R.raw.meglive_success)
        } else if (resID == R.string.liveness_detection_failed_not_video) {
            doPlay(R.raw.meglive_failed)
        } else if (resID == R.string.liveness_detection_failed_timeout) {
            doPlay(R.raw.meglive_failed)
        } else if (resID == R.string.liveness_detection_failed) {
            doPlay(R.raw.meglive_failed)
        } else {
            doPlay(R.raw.meglive_failed)
        }*/
    }


    /**
     * 如何调用Verify2.0方法  详细API字段请参考Verify文档描述
     */
    fun imageVerify(images: Map<String, ByteArray>, delta: String) {
        val requestParams = RequestParams()
        requestParams.put("api_key", "API_KEY")
        requestParams.put("api_secret", "API_SECRET")
        requestParams.put("comparison_type", "1")
        requestParams.put("face_image_type", "meglive")
        requestParams.put("idcard_name", "身份证姓名")
        requestParams.put("idcard_number", "身份证号码")
        requestParams.put("delta", delta)
        for ((key, value) in images) {
            requestParams.put(key,
                    ByteArrayInputStream(value))
        }
        //        try {
        //            requestParams.put("image_best", new File(bestPath));
        //            requestParams.put("image_env", new File(envPath));
        //        } catch (FileNotFoundException e) {
        //            e.printStackTrace();
        //        }

        val asyncHttpClient = AsyncHttpClient()
        val url = "https://api.megvii.com/faceid/v2/verify"
        asyncHttpClient.post(url, requestParams,
                object : AsyncHttpResponseHandler() {
                    override fun onSuccess(i: Int, headers: Array<Header>, bytes: ByteArray?) {
                        //请求成功
                        if (bytes != null) {
                            val success = String(bytes)
                            Log.e("TAG", "成功信息：$success")
                        }
                    }

                    override fun onFailure(i: Int, headers: Array<Header>,
                                           bytes: ByteArray?, throwable: Throwable) {
                        // 请求失败
                        if (bytes != null) {
                            val error = String(bytes)
                            Log.e("TAG", "失败信息：$error")
                        } else {
                            Log.e("TAG", "失败信息：$throwable")
                        }


                    }
                })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.result_next) {

            if (mIsSuccess) {
                submitInfoAction()
            } else {
                finish()
            }
            //finish();
        }
    }

    private fun doRotate(success: Boolean) {
        val rotaterView = findViewById<View>(R.id.result_rotater) as RotaterView
        rotaterView.setColour(if (success) -0xb51755 else -0x1736e)
        val statusView = findViewById<View>(R.id.result_status) as ImageView
        statusView.visibility = View.INVISIBLE
        statusView.setImageResource(if (success)
            R.drawable.result_success
        else
            R.drawable.result_failded)

        val objectAnimator = ObjectAnimator.ofInt(rotaterView,
                "progress", 0, 100)
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        objectAnimator.duration = 600
        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                val scaleanimation = AnimationUtils.loadAnimation(
                        this@ResultActivity, R.anim.scaleoutin)
                statusView.startAnimation(scaleanimation)
                statusView.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        objectAnimator.start()
    }

    private fun doPlay(rawId: Int) {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer()

        mMediaPlayer!!.reset()
        try {
            val localAssetFileDescriptor = resources
                    .openRawResourceFd(rawId)
            mMediaPlayer!!.setDataSource(
                    localAssetFileDescriptor.fileDescriptor,
                    localAssetFileDescriptor.startOffset,
                    localAssetFileDescriptor.length)
            mMediaPlayer!!.prepare()
            mMediaPlayer!!.start()
        } catch (localIOException: Exception) {
            localIOException.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.reset()
            mMediaPlayer!!.release()
        }
    }

    private fun submitInfoAction() {


        mRequestTag = MethodUrl.liveSubmit

        val mSignMap = HashMap<String, Any>()

        val mParamMap = HashMap<String, Any>()

        val map = HashMap<String, Any>()
        when (mOpType) {
            MbsConstans.FaceType.FACE_AUTH -> {
                mParamMap["delta"] = mDelta!!
                mParamMap["idno"] = mIdNum!!
                mParamMap["name"] = mIdName!!
                mParamMap["auth_type"] = "auth"
                mParamMap["auth_flow"] = mAuthFlow!!
                map["image"] = bestPath!!
            }
            MbsConstans.FaceType.FACE_INSTALL -> {
                mParamMap["delta"] = mDelta!!
                mParamMap["auth_type"] = "verify"
                map["image"] = bestPath!!
            }
            MbsConstans.FaceType.FACE_CHECK_HUANKUAN -> {
                mParamMap["delta"] = mDelta!!
                mParamMap["auth_type"] = "verify"
                map["image"] = bestPath!!
            }
            MbsConstans.FaceType.FACE_CHECK_APPLY -> {
                mParamMap["delta"] = mDelta!!
                mParamMap["auth_type"] = "verify"
                map["image"] = bestPath!!
            }
            MbsConstans.FaceType.FACE_CHECK_BANK_PASS -> {
                mParamMap["delta"] = mDelta!!
                mParamMap["auth_type"] = "verify"
                map["image"] = bestPath!!
            }
            MbsConstans.FaceType.FACE_CHECK_BANK_BIND -> {
                mParamMap["delta"] = mDelta!!
                mParamMap["auth_type"] = "verify"
                map["image"] = bestPath!!
            }
            MbsConstans.FaceType.FACE_PEOPLE_CHECK -> {
                mParamMap["delta"] = mDelta!!
                mParamMap["auth_type"] = "verify"
                map["image"] = bestPath!!
            }
            MbsConstans.FaceType.FACE_SIGN_HETONG -> {
                mParamMap["delta"] = mDelta!!
                mParamMap["auth_type"] = "verify"
                map["image"] = bestPath!!
            }
            MbsConstans.FaceType.FACE_BORROW_MONEY -> {
                mParamMap["delta"] = mDelta!!
                mParamMap["auth_type"] = "verify"
                map["image"] = bestPath!!
            }
            MbsConstans.FaceType.FACE_UPLOAD_USE -> {
                mParamMap["delta"] = mDelta!!
                mParamMap["auth_type"] = "verify"
                map["image"] = bestPath!!
            }
        }

        val mHeaderMap = HashMap<String, String>()
        LogUtil.i("人脸识别参数", "$map       mParamMap  = $mParamMap")
        mRequestPresenterImp!!.postFileToMap(mHeaderMap, MethodUrl.liveSubmit, mSignMap, mParamMap, map)
    }

    /**
     * 获取refreshToken方法
     */
    override fun getRefreshToken() {

        val map = HashMap<String, Any>()
        map["access_token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.refreshToken, map)
    }


    override fun showProgress() {
        mLoadingWindow!!.showView()
    }

    override fun disimissProgress() {
        mLoadingWindow!!.cancleView()

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.liveSubmit//{authcode=auth_code@2563fe76c7312bfd33c144bba1292ff5, auth_times=1}
            -> {
                TipsToast.showToastMsg("人脸识别成功")
                when (mOpType) {
                    MbsConstans.FaceType.FACE_AUTH -> {
                        intent = Intent(this@ResultActivity, IdCardSuccessActivity::class.java)
                        intent.putExtra(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_AUTH)
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        startActivity(intent)
                        finish()
                    }
                    MbsConstans.FaceType.FACE_INSTALL -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.FaceType.FACE_INSTALL, intent)
                        finish()
                    }
                    MbsConstans.FaceType.FACE_CHECK_HUANKUAN -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.FaceType.FACE_CHECK_HUANKUAN, intent)
                        finish()
                    }
                    MbsConstans.FaceType.FACE_CHECK_APPLY -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.FaceType.FACE_CHECK_APPLY, intent)
                        finish()
                    }
                    MbsConstans.FaceType.FACE_CHECK_BANK_PASS -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.FaceType.FACE_CHECK_BANK_PASS, intent)
                        finish()
                    }
                    MbsConstans.FaceType.FACE_CHECK_BANK_BIND -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.FaceType.FACE_CHECK_BANK_BIND, intent)
                        finish()
                    }
                    MbsConstans.FaceType.FACE_PEOPLE_CHECK -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.FaceType.FACE_PEOPLE_CHECK, intent)
                        finish()
                    }
                    MbsConstans.FaceType.FACE_SIGN_HETONG -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.FaceType.FACE_SIGN_HETONG, intent)
                        finish()
                    }
                    MbsConstans.FaceType.FACE_BORROW_MONEY -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.FaceType.FACE_BORROW_MONEY, intent)
                        finish()
                    }
                    MbsConstans.FaceType.FACE_UPLOAD_USE -> {
                        intent = Intent()
                        intent.putExtra("authcode", tData["authcode"]!!.toString() + "")
                        setResult(MbsConstans.FaceType.FACE_UPLOAD_USE, intent)
                        finish()
                    }
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                when (mRequestTag) {
                    MethodUrl.liveSubmit -> submitInfoAction()
                }
            }
        }

    }


    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        val intent: Intent


        val msg = map["errmsg"]!!.toString() + ""
        val errcodeStr = map["errcode"]!!.toString() + ""
        var errorCode = -1
        try {
            errorCode = java.lang.Double.valueOf(errcodeStr).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.i("打印log日志", "这里出现异常了" + e.message)
        }

        if (errorCode == ErrorHandler.REFRESH_TOKEN_DATE_CODE) {
            LogUtil.i("打印log日志", "refreshToken过期重新请求refreshtoken接口")
            getRefreshToken()
        } else if (errorCode == ErrorHandler.ACCESS_TOKEN_DATE_CODE) {
            mActivityManager!!.close()
            intent = Intent(this@ResultActivity, LoginActivity::class.java)
            startActivity(intent)
            TipsToast.showToastMsg(resources.getString(R.string.toast_login_again))
        } else {
            TipsToast.showToastMsg(msg)

            when (mType) {
                MethodUrl.liveSubmit//{authcode=auth_code@2563fe76c7312bfd33c144bba1292ff5, auth_times=1}
                ->

                    when (mOpType) {
                        MbsConstans.FaceType.FACE_AUTH -> {
                            mActivityManager!!.backTo(IdCardCheckActivity::class.java, false)
                            finish()
                        }
                        MbsConstans.FaceType.FACE_INSTALL -> {
                        }
                        MbsConstans.FaceType.FACE_CHECK_HUANKUAN -> {
                            intent = Intent()
                            setResult(MbsConstans.FaceType.FACE_CHECK_HUANKUAN, intent)
                            finish()
                        }
                        MbsConstans.FaceType.FACE_BORROW_MONEY -> {
                            intent = Intent()
                            setResult(MbsConstans.FaceType.FACE_BORROW_MONEY, intent)
                            finish()
                        }
                        MbsConstans.FaceType.FACE_UPLOAD_USE -> {
                            intent = Intent()
                            setResult(MbsConstans.FaceType.FACE_UPLOAD_USE, intent)
                            finish()
                        }
                    }
                MethodUrl.refreshToken//获取refreshToken返回结果
                -> {
                }
            }

        }
    }

    companion object {

        fun startActivity(context: Context, bundle: Bundle) {
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}