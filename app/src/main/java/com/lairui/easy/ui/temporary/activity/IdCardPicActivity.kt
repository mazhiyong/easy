package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.FileUtils
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil


import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 身份证认证   界面
 */
class IdCardPicActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.loading_layout_WarrantyBar)
    lateinit var mLoadingLayoutWarrantyBar: ProgressBar
    @BindView(R.id.loading_layout_WarrantyText)
    lateinit var mLoadingLayoutWarrantyText: TextView
    @BindView(R.id.loading_layout_barLinear)
    lateinit var mLoadingLayoutBarLinear: LinearLayout
    @BindView(R.id.loading_layout_againWarrantyBtn)
    lateinit var mLoadingLayoutAgainWarrantyBtn: Button
    @BindView(R.id.my_front_image)
    lateinit var mMyFrontImage: ImageView
    @BindView(R.id.my_back_image)
    lateinit var mMyBackImage: ImageView
    @BindView(R.id.my_front_image_cardView)
    lateinit var mMyFrontImageCardView: CardView
    @BindView(R.id.my_back_image_cardView)
    lateinit var mMyBackImageCardView: CardView
    @BindView(R.id.front_tv)
    lateinit var mFrontTv: TextView
    @BindView(R.id.back_tv)
    lateinit var mBackTv: TextView
    @BindView(R.id.user_name_tv)
    lateinit var mUserNameTv: TextView
    @BindView(R.id.front_info_delete)
    lateinit var mFrontInfoDelete: ImageView
    @BindView(R.id.idcard_value_tv)
    lateinit var mIdcardValueTv: TextView
    @BindView(R.id.front_value_lay)
    lateinit var mFrontValueLay: LinearLayout
    @BindView(R.id.qianfa_jiguan_value)
    lateinit var mQianfaJiguanValue: TextView
    @BindView(R.id.back_info_delete)
    lateinit var mBackInfoDelete: ImageView
    @BindView(R.id.out_date_value)
    lateinit var mOutDateValue: TextView
    @BindView(R.id.back_value_lay)
    lateinit var mBackValueLay: LinearLayout

    private val isVertical: Boolean = false

    private val mFrontPath = ""
    private val mBackPath = ""


    private var mRequestTag = ""

    override val contentView: Int
        get() = R.layout.activity_idcard_pic


    var mSide = 0

    private var mFilePath = ""

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.id_card_check)
        mButNext!!.isEnabled = false
        network()
    }


    private fun uploadPicAction() {

        mRequestTag = MethodUrl.idCardCheck
        val mSignMap = HashMap<String, Any>()

        val mParamMap = HashMap<String, Any>()

        val map = HashMap<String, Any>()
        map["image"] = mFilePath

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.postFileToMap(mHeaderMap, MethodUrl.idCardCheck, mSignMap, mParamMap, map)
    }

    private fun submitInfoAction() {
        mRequestTag = MethodUrl.idCardSubmit

        val mSignMap = HashMap<String, Any>()


        val map = HashMap<String, Any>()
        /* map.put("idno","410725199103263616");
        map.put("idname","刘英超");
        map.put("expires","2037-08-01");
        map.put("issued","原阳县公安局");*/
        map["idno"] = mIdcardValueTv!!.text.toString() + ""
        map["idname"] = mUserNameTv!!.text.toString() + ""
        map["expires"] = mOutDateValue!!.text.toString() + ""
        map["issued"] = mQianfaJiguanValue!!.text.toString() + ""

        val fileMap = HashMap<String, Any>()

        fileMap["img_front"] = MbsConstans.IDCARD_IMAGE_PATH + MbsConstans.IDCARD_FRONT + ".png"
        fileMap["img_back"] = MbsConstans.IDCARD_IMAGE_PATH + MbsConstans.IDCARD_BACK + ".png"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.postFileToMap(mHeaderMap, MethodUrl.idCardSubmit, mSignMap, map, fileMap)
    }


    /**
     * 联网授权
     */
    private fun network() {
      /*  mLoadingLayoutBarLinear!!.visibility = View.VISIBLE
        mLoadingLayoutAgainWarrantyBtn!!.visibility = View.GONE
        mLoadingLayoutWarrantyText!!.text = "正在联网授权中..."
        mLoadingLayoutWarrantyBar!!.visibility = View.VISIBLE

        val uuid = Util.getUUIDString(this)

        Thread(Runnable {
            val manager = Manager(this@IdCardPicActivity)
            val idCardLicenseManager = IDCardQualityLicenseManager(
                    this@IdCardPicActivity)
            manager.registerLicenseManager(idCardLicenseManager)
            manager.takeLicenseFromNetwork(uuid)
            if (idCardLicenseManager.checkCachedLicense() > 0) {
                //授权成功
                UIAuthState(true)
            } else {
                //授权失败
                UIAuthState(false)
            }
        }).start()*/
    }


    private fun UIAuthState(isSuccess: Boolean) {
        runOnUiThread { authState(isSuccess) }
    }

    private fun authState(isSuccess: Boolean) {

        if (isSuccess) {
            mLoadingLayoutBarLinear!!.visibility = View.GONE
            mLoadingLayoutAgainWarrantyBtn!!.visibility = View.GONE
            mLoadingLayoutWarrantyText!!.text = "正在联网授权中..."
            mLoadingLayoutWarrantyBar!!.visibility = View.GONE
        } else {
            mLoadingLayoutBarLinear!!.visibility = View.VISIBLE
            mLoadingLayoutAgainWarrantyBtn!!.visibility = View.VISIBLE
            mLoadingLayoutWarrantyText!!.text = "正在联网授权中..."
            mLoadingLayoutWarrantyBar!!.visibility = View.VISIBLE
            mLoadingLayoutWarrantyText!!.text = "联网授权失败！请检查网络或找服务商"
        }
    }

    private fun requestCameraPerm(side: Int) {
        mSide = side

        PermissionsUtils.requsetRunPermission(this, object : RePermissionResultBack {
            override fun requestSuccess() {
                enterNextPage(mSide)
            }

            override fun requestFailer() {
                toast(R.string.failure)
            }
        }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)

    }

    private fun enterNextPage(side: Int) {
       // val intent = Intent(this, IDCardScanActivity::class.java)
        intent.putExtra("side", side)
        intent.putExtra("isvertical", isVertical)
        startActivityForResult(intent, INTO_IDCARDSCAN_PAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == EXTERNAL_STORAGE_REQ_CAMERA_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted
                //Util.showToast(this, "获取相机权限失败")
            } else
                enterNextPage(mSide)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INTO_IDCARDSCAN_PAGE && resultCode == Activity.RESULT_OK) {
            val side = data!!.getIntExtra("side", 0)
            val idcardImgData = data.getByteArrayExtra("idcardImg")
            val idcardBmp = BitmapFactory.decodeByteArray(idcardImgData, 0, idcardImgData.size)
            when (side) {
                //0   正面照片      1  反面照片
                0 -> {
                    saveCroppedImage(idcardBmp, MbsConstans.IDCARD_FRONT)
                    //mMyFrontImage.setImageBitmap(idcardBmp);
                    mFilePath = MbsConstans.IDCARD_IMAGE_PATH + MbsConstans.IDCARD_FRONT + ".png"
                    GlideUtils.loadUUIDImage(this@IdCardPicActivity, mFilePath, mMyFrontImage!!)
                    uploadPicAction()
                }
                1 -> {
                    saveCroppedImage(idcardBmp, MbsConstans.IDCARD_BACK)
                    //mMyBackImage.setImageBitmap(idcardBmp);
                    mFilePath = MbsConstans.IDCARD_IMAGE_PATH + MbsConstans.IDCARD_BACK + ".png"
                    GlideUtils.loadUUIDImage(this@IdCardPicActivity, mFilePath, mMyBackImage!!)
                    uploadPicAction()
                }
            }

        }
    }


    private fun saveCroppedImage(bmp: Bitmap, name: String) {

        try {
            val saveFile = File(MbsConstans.IDCARD_IMAGE_PATH)

            val filepath = MbsConstans.IDCARD_IMAGE_PATH + name + ".png"
            val file = File(filepath)
            if (!saveFile.exists()) {
                saveFile.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            val saveFile2 = File(filepath)

            val fos = FileOutputStream(saveFile2)
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, fos)
            fos.flush()
            fos.close()
            // uploadAliPic(new Date().getTime()+".png",filepath);

        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.loading_layout_againWarrantyBtn, R.id.my_front_image_cardView, R.id.my_back_image_cardView, R.id.front_info_delete, R.id.back_info_delete, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next ->
                /* intent = new Intent(IdCardPicActivity.this, FaceCheckActivity.class);
                startActivity(intent);*/
                submitInfoAction()
            R.id.loading_layout_againWarrantyBtn -> network()
            R.id.my_front_image_cardView -> requestCameraPerm(0)
            R.id.my_back_image_cardView -> requestCameraPerm(1)
            R.id.front_info_delete -> {
                mFrontValueLay!!.visibility = View.GONE
                mMyFrontImage!!.setImageResource(R.drawable.front_card)
                butIsEnable()
                mFrontTv!!.text = resources.getText(R.string.front_card)
                mFrontTv!!.compoundDrawablePadding = UtilTools.dip2px(this@IdCardPicActivity, 0)
                mFrontTv!!.setCompoundDrawables(null, null, null, null)
            }
            R.id.back_info_delete -> {
                mBackValueLay!!.visibility = View.GONE
                mMyBackImage!!.setImageResource(R.drawable.back_card)
                butIsEnable()
                mBackTv!!.text = resources.getText(R.string.back_card)
                mBackTv!!.compoundDrawablePadding = UtilTools.dip2px(this@IdCardPicActivity, 0)
                mBackTv!!.setCompoundDrawables(null, null, null, null)
            }
        }
    }

    private fun butIsEnable() {
        if (mFrontValueLay!!.isShown && mBackValueLay!!.isShown) {
            mButNext!!.isEnabled = true
        } else {
            mButNext!!.isEnabled = false
        }
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.idCardCheck -> {
                val drawable = ContextCompat.getDrawable(this@IdCardPicActivity, R.drawable.submit_success)
                if (mSide == 0) {
                    val name = tData["idname"]!!.toString() + ""
                    val cardNum = tData["idno"]!!.toString() + ""
                    mUserNameTv!!.text = name
                    mIdcardValueTv!!.text = cardNum
                    mFrontValueLay!!.visibility = View.VISIBLE
                    mFrontTv!!.text = "提交成功"
                    mFrontTv!!.compoundDrawablePadding = UtilTools.dip2px(this@IdCardPicActivity, 5)
                    mFrontTv!!.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                } else {
                    val dateValue = tData["expires"]!!.toString() + ""
                    val jiguan = tData["issued"]!!.toString() + ""

                    mOutDateValue!!.text = dateValue
                    mQianfaJiguanValue!!.text = jiguan
                    mBackValueLay!!.visibility = View.VISIBLE
                    mBackTv!!.text = "提交成功"
                    mBackTv!!.compoundDrawablePadding = UtilTools.dip2px(this@IdCardPicActivity, 5)
                    mBackTv!!.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                }
                butIsEnable()
            }
            MethodUrl.idCardSubmit//{auth_flow=2576f7e49ef84d498676cf099cc32aab}
            -> {
                FileUtils.deleteDir(MbsConstans.IDCARD_IMAGE_PATH)
                intent = Intent(this@IdCardPicActivity, FaceCheckActivity::class.java)
                intent.putExtra("auth_flow", tData["auth_flow"]!!.toString() + "")
                intent.putExtra("idno", mIdcardValueTv!!.text.toString() + "")
                intent.putExtra("name", mUserNameTv!!.text.toString() + "")
                startActivity(intent)
                finish()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.idCardCheck -> uploadPicAction()
                    MethodUrl.idCardSubmit -> submitInfoAction()
                }
            }
        }

    }


    override fun finish() {
        super.finish()
        FileUtils.deleteDir(MbsConstans.IDCARD_IMAGE_PATH)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }


    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {

        val EXTERNAL_STORAGE_REQ_CAMERA_CODE = 10


        private val INTO_IDCARDSCAN_PAGE = 100
    }
}
