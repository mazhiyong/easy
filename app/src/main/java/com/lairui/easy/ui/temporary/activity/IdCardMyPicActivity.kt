package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.cardview.widget.CardView

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.AppUtil
import com.lairui.easy.utils.tool.BitmapResizeUtil
import com.lairui.easy.utils.tool.FileUtils
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.RotatePictureUtil
import com.jaeger.library.StatusBarUtil

import java.io.File
import java.io.FileInputStream
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick


/**
 * 三次认证识别，需要自己上传图片认证    自己手动认证界面   界面
 */
class IdCardMyPicActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.my_front_image)
    lateinit var mMyFrontImage: CardView
    @BindView(R.id.my_back_image)
    lateinit var mMyBackImage: CardView
    @BindView(R.id.my_hand_image)
    lateinit var mMyHandImage: CardView

    @BindView(R.id.front_card_image)
    lateinit var mFrontCardImage: ImageView
    @BindView(R.id.back_card_image)
    lateinit var mBackCardImage: ImageView
    @BindView(R.id.hand_card_image)
    lateinit var mHandCardImage: ImageView

    private val isVertical: Boolean = false


    private var mType = 0//代表哪个拍摄  0 身份证正面    1  身份证反面   2 手持身份证

    private var mName: String? = ""
    private var mIdNum: String? = ""

    private var mRequestTag = ""

    override val contentView: Int
        get() = R.layout.activity_idcard_my_pic

    private var saveP = ""


    private lateinit var `is`: FileInputStream

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mName = bundle.getString("idname")
            mIdNum = bundle.getString("idno")
        }
        mTitleText!!.text = resources.getString(R.string.id_card_check)
    }

    private fun submitInfoAction() {

        mRequestTag = MethodUrl.peopleAuth

        val mSignMap = HashMap<String, Any>()

        //String 请求参数
        val map = HashMap<String, Any>()
        map["idno"] = mIdNum!!
        map["idname"] = mName!!
        val fileMap = HashMap<String, Any>()

        if (mFrontCardImage!!.getTag(R.id.glide_tag) == null
                || mBackCardImage!!.getTag(R.id.glide_tag) == null
                || mHandCardImage!!.getTag(R.id.glide_tag) == null) {
            showToastMsg("请上传相关证件照片")
            return
        }

        //File 请求参数
        fileMap["img_front"] = mFrontCardImage!!.getTag(R.id.glide_tag).toString() + ""
        fileMap["img_back"] = mBackCardImage!!.getTag(R.id.glide_tag).toString() + ""
        fileMap["img_hand"] = mHandCardImage!!.getTag(R.id.glide_tag).toString() + ""
        LogUtil.i("人工认证提交的参数", "" + fileMap)
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.postFileToMap(mHeaderMap, MethodUrl.peopleAuth, mSignMap, map, fileMap)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.my_front_image, R.id.my_back_image, R.id.my_hand_image, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> submitInfoAction()
            R.id.my_front_image -> {
                mType = 0
                photo()
            }
            R.id.my_back_image -> {
                mType = 1
                photo()
            }
            R.id.my_hand_image -> {
                mType = 2
                photo()
            }
        }//                intent = new Intent(IdCardMyPicActivity.this,IdCardSuccessActivity.class);
        //                startActivity(intent);
    }

    fun photo() {

        PermissionsUtils.requsetRunPermission(this, object : RePermissionResultBack {
            override fun requestSuccess() {
                getCamera()
            }

            override fun requestFailer() {
                toast(R.string.failure)
            }
        }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
    }

    private fun getCamera() {
        val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val fileName = System.currentTimeMillis().toString()
        val savePath = MbsConstans.PHOTO_PATH
        try {
            FileUtils.createSDDir("")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        saveP = savePath + File.separator + fileName + ".jpg"

        var uri = Uri.fromFile(File(saveP))

        //下面这句指定调用相机拍照后的照片存储的路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this@IdCardMyPicActivity, AppUtil.getAppProcessName(this@IdCardMyPicActivity) + ".fileProvider", File(savePath, "$fileName.jpg"))
        } else {
            uri = Uri.fromFile(File(savePath, "$fileName.jpg"))
        }
        // 指定存储路径，这样就可以保存原图了
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        // 拍照返回图片
        startActivityForResult(openCameraIntent, TAKE_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            TAKE_PICTURE -> if (resultCode == Activity.RESULT_OK) {

                /*String fileName = String.valueOf(System.currentTimeMillis());
					Bitmap bm = (Bitmap) data.getExtras().get("data");
					String savePath = FileUtils.saveBitmap(bm, fileName);
					ImageItem takePhoto = new ImageItem();
					takePhoto.setImagePath(savePath);
					takePhoto.setBitmap(bm);
					Bimp.tempSelectBitmap.add(takePhoto);*/

                /*ImageItem takePhoto = new ImageItem();
					takePhoto.setImagePath(saveP);
					takePhoto.setBitmap(BitmapFactory.decodeFile(saveP));
					Bimp.tempSelectBitmap.add(takePhoto);*/

                val fileName = System.currentTimeMillis().toString() + ".jpg"
                //调用工具类进行图片压缩（该类会把图片压缩后放置在相应的目录下，运行后直接去该路径拿图片即可）
                BitmapResizeUtil.compressBitmap(saveP, MbsConstans.PHOTO_PATH, fileName, RotatePictureUtil.getBitmapDegree(saveP))
                // takePhoto.setImagePath(MbsConstans.PHOTO_PATH + File.separator + fileName);
                // takePhoto.setBitmap(BitmapFactory.decodeFile(MbsConstans.PHOTO_PATH + File.separator + fileName));

                when (mType) {
                    0 -> {
                        //mFrontCardImage.setImageBitmap(BitmapFactory.decodeFile(MbsConstans.PHOTO_PATH + File.separator + fileName));
                        GlideUtils.loadImage(this@IdCardMyPicActivity, MbsConstans.PHOTO_PATH + File.separator + fileName, mFrontCardImage!!)
                        mFrontCardImage!!.setTag(R.id.glide_tag, MbsConstans.PHOTO_PATH + File.separator + fileName)
                    }
                    1 -> {
                        //mBackCardImage.setImageBitmap(BitmapFactory.decodeFile(MbsConstans.PHOTO_PATH + File.separator + fileName));
                        GlideUtils.loadImage(this@IdCardMyPicActivity, MbsConstans.PHOTO_PATH + File.separator + fileName, mBackCardImage!!)
                        mBackCardImage!!.setTag(R.id.glide_tag, MbsConstans.PHOTO_PATH + File.separator + fileName)
                    }
                    2 -> {
                        //mHandCardImage.setImageBitmap(BitmapFactory.decodeFile(MbsConstans.PHOTO_PATH + File.separator + fileName));
                        GlideUtils.loadImage(this@IdCardMyPicActivity, MbsConstans.PHOTO_PATH + File.separator + fileName, mHandCardImage!!)
                        mHandCardImage!!.setTag(R.id.glide_tag, MbsConstans.PHOTO_PATH + File.separator + fileName)
                    }
                }
            }
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
            MethodUrl.peopleAuth -> {
                FileUtils.deleteDir(MbsConstans.PHOTO_PATH)

                intent = Intent(this@IdCardMyPicActivity, CheckWatiActivity::class.java)
                startActivity(intent)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.peopleAuth -> submitInfoAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }


    override fun finish() {
        super.finish()
        FileUtils.deleteDir(MbsConstans.PHOTO_PATH)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {


        private val TAKE_PICTURE = 0x000001
    }
}
