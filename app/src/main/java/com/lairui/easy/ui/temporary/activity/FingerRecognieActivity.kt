package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Handler
import android.os.Message
import android.provider.Settings
import androidx.annotation.RequiresApi

import androidx.appcompat.app.AlertDialog

import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.MyAuthCallback
import com.lairui.easy.utils.tool.CryptoObjectHelper
import com.lairui.easy.basic.MbsConstans

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.mywidget.dialog.BaseDialog

/**
 * 指纹识别界面
 */
class FingerRecognieActivity : BasicActivity() {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView

    lateinit  var tvshow: TextView
    private var cancellationSignal: CancellationSignal? =null
    private lateinit var fingerprintManager: FingerprintManager
    private lateinit var myAuthCallback: MyAuthCallback

    private var handler: Handler? = null

    lateinit var view: View

    override val contentView: Int
        get() = R.layout.activity_finger_recognie

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun init() {
        mTitleText!!.text = resources.getString(R.string.finger_login)
        //初始化
        fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        val dialog = object : BaseDialog(this, true) {
            override fun onCreateView(): View {
                view = layoutInflater.inflate(R.layout.figner_recogni_dialog_layout, null)
                tvshow = view.findViewById(R.id.tvshow_finger_message)
                return view
            }

            override fun setUiBeforShow() {

            }
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.widthScale(0.6f)
        dialog.showAtLocation(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MbsConstans.FingerRecognize.MSG_AUTH_SUCCESS -> {
                        setResultInfo("识别成功")
                        if (cancellationSignal != null) {
                            cancellationSignal!!.cancel()
                            cancellationSignal = null
                        }
                    }
                    MbsConstans.FingerRecognize.MSG_AUTH_FAILED -> {
                        setResultInfo("识别失败")
                        cancellationSignal = null
                    }
                    MbsConstans.FingerRecognize.MSG_AUTH_ERROR -> handleErrorCode(msg.arg1)
                    MbsConstans.FingerRecognize.MSG_AUTH_HELP -> handleHelpCode(msg.arg1)
                }
            }
        }
        try {
            val cryptoObjectHelper = CryptoObjectHelper()
            if (cancellationSignal == null) {
                cancellationSignal = CancellationSignal()
            }
            myAuthCallback = MyAuthCallback(handler!!)
            fingerprintManager!!.authenticate(cryptoObjectHelper.buildCryptoObject(),
                    cancellationSignal, 0, myAuthCallback!!, null)

        } catch (e: Exception) {
            e.printStackTrace()
            setResultInfo("识别失败")
        }

        //判断当前设备是否支持指纹解锁
        if (!fingerprintManager!!.isHardwareDetected) {
            // no fingerprint sensor is detected, show dialog to tell user.
            val builder1 = AlertDialog.Builder(this)
            builder1.setTitle("提示：")
            builder1.setMessage("当前手机设备不支持指纹解锁")
            builder1.setIcon(android.R.drawable.stat_sys_warning)
            builder1.setCancelable(false)
            builder1.setNegativeButton("取消") { dialog, which -> finish() }
            builder1.create().show()
        } else if (!fingerprintManager!!.hasEnrolledFingerprints()) {
            // 判断设备是否已有录入的指纹信息
            val builder2 = AlertDialog.Builder(this)
            builder2.setTitle("提示：")
            builder2.setMessage("当前手机设备未录入有效指纹")
            builder2.setIcon(android.R.drawable.stat_sys_warning)
            builder2.setCancelable(false)
            builder2.setNegativeButton("去设置指纹") { dialog, which ->
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
            }
            builder2.create().show()
        } else {
            try {
                //弹出对话框
                dialog.show()

                view.findViewById<View>(R.id.tvcancel_finger_recogn).setOnClickListener {
                    Toast.makeText(this@FingerRecognieActivity, "取消指纹登录", Toast.LENGTH_SHORT).show()
                    cancellationSignal!!.cancel()
                    cancellationSignal = null
                    dialog.dismiss()
                }
                myAuthCallback = MyAuthCallback(handler!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cancellationSignal != null) {
            cancellationSignal!!.cancel()
            cancellationSignal = null
        }
        if (handler != null) {
            handler = null
        }
    }

    private fun handleHelpCode(code: Int) {
        when (code) {
            FingerprintManager.FINGERPRINT_ACQUIRED_GOOD -> setResultInfo("指纹传感器不可用")
            FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY -> setResultInfo("指纹传感器不可用")
            FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT -> setResultInfo("失败次数太多，请稍后再试")
            FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL -> setResultInfo("识别失败")
            FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST -> setResultInfo("长时间未检测到指纹")
            FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW -> setResultInfo("识别失败")
        }
    }

    private fun handleErrorCode(code: Int) {
        when (code) {
            FingerprintManager.FINGERPRINT_ERROR_CANCELED,

            FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE,

            FingerprintManager.FINGERPRINT_ERROR_LOCKOUT,

            FingerprintManager.FINGERPRINT_ERROR_NO_SPACE,

            FingerprintManager.FINGERPRINT_ERROR_TIMEOUT,

            FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS -> setResultInfo("识别失败")
        }
    }

    private fun setResultInfo(str: String?) {
        Log.i("show", "识别结果：" + str!!)
        if (str != null) {
            if (str == "识别成功") {
                tvshow.text = "识别成功"
            } else {
                tvshow.text = "识别失败，再试一次"
            }

        }
    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {

    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun disimissProgress() {

    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }
}
