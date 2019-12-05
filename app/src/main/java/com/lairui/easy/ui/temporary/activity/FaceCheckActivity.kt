package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Message

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.jaeger.library.StatusBarUtil


import butterknife.BindView
import butterknife.OnClick

/**
 * 人脸认证界面  界面
 */
class FaceCheckActivity : BasicActivity(), RequestView {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.my_image)
    lateinit var mMyImage: ImageView
    @BindView(R.id.face_but)
    lateinit var mFaceBut: Button
    @BindView(R.id.loading_layout_WarrantyBar)
    lateinit var mLoadingLayoutWarrantyBar: ProgressBar
    @BindView(R.id.loading_layout_WarrantyText)
    lateinit var mLoadingLayoutWarrantyText: TextView
    @BindView(R.id.loading_layout_barLinear)
    lateinit var mLoadingLayoutBarLinear: LinearLayout
    @BindView(R.id.loading_layout_againWarrantyBtn)
    lateinit var mLoadingLayoutAgainWarrantyBtn: Button
    @BindView(R.id.loading_expiretime)
    lateinit var mLoadingExpiretime: TextView

    private var mAuthFlow: String? = ""
    private var mIdName: String? = ""
    private var mIdNum: String? = ""

    override val contentView: Int
        get() = R.layout.activity_face_check


    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> {
                    mLoadingLayoutAgainWarrantyBtn!!.visibility = View.GONE
                    mLoadingLayoutBarLinear!!.visibility = View.GONE
                }
                2 -> {
                    mLoadingLayoutAgainWarrantyBtn!!.visibility = View.VISIBLE
                    mLoadingLayoutWarrantyText!!.setText(R.string.meglive_auth_failed)
                    mLoadingLayoutWarrantyBar!!.visibility = View.GONE
                }
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent

        val bundle = intent.extras
        if (bundle != null) {
            mAuthFlow = bundle.getString("auth_flow")
            mIdName = bundle.getString("name")
            mIdNum = bundle.getString("idno")

        }

        mTitleText!!.text = resources.getString(R.string.face_title)
        netWorkWarranty()
    }

    /**
     * 联网授权
     */
    private fun netWorkWarranty() {

      /*  val uuid = ConUtil.getUUIDString(this)


        mLoadingLayoutAgainWarrantyBtn!!.visibility = View.GONE
        mLoadingLayoutBarLinear!!.visibility = View.VISIBLE
        mLoadingLayoutAgainWarrantyBtn!!.visibility = View.GONE
        mLoadingLayoutWarrantyText!!.setText(R.string.meglive_auth_progress)
        mLoadingLayoutWarrantyBar!!.visibility = View.VISIBLE
        Thread(Runnable {
            val manager = Manager(this@FaceCheckActivity)
            val licenseManager = LivenessLicenseManager(this@FaceCheckActivity)
            manager.registerLicenseManager(licenseManager)
            manager.takeLicenseFromNetwork(uuid)
            if (licenseManager.checkCachedLicense() > 0) {
                //授权成功
                mHandler.sendEmptyMessage(1)
            } else {
                //授权失败
                mHandler.sendEmptyMessage(2)
            }
        }).start()*/
    }


    private fun requestCameraPerm() {

        PermissionsUtils.requsetRunPermission(this@FaceCheckActivity, object : RePermissionResultBack {
            override fun requestSuccess() {
                toast(R.string.successfully)
                enterNextPage()
            }

            override fun requestFailer() {
                toast(R.string.failure)
            }
        }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)

    }

    private fun enterNextPage() {
        //startActivityForResult(Intent(this, LivenessActivity::class.java), PAGE_INTO_LIVENESS)
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == EXTERNAL_STORAGE_REQ_CAMERA_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted

                //ConUtil.showToast(this, "获取相机权限失败")
            } else
                enterNextPage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PAGE_INTO_LIVENESS && resultCode == Activity.RESULT_OK) {
            //            String result = data.getStringExtra("result");
            //            String delta = data.getStringExtra("delta");
            //            Serializable images=data.getSerializableExtra("images");
            val bundle = data!!.extras
            bundle!!.putString("auth_flow", mAuthFlow)
            bundle.putString("idno", mIdNum)
            bundle.putString("name", mIdName)
            bundle.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_AUTH)

            ResultActivity.startActivity(this, bundle)
        }
    }


    @OnClick(R.id.back_img, R.id.face_but, R.id.loading_layout_againWarrantyBtn, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.face_but -> requestCameraPerm()
            R.id.loading_layout_againWarrantyBtn -> netWorkWarranty()
        }
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }


    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {

        val EXTERNAL_STORAGE_REQ_CAMERA_CODE = 10


        private val PAGE_INTO_LIVENESS = 100
    }
}
