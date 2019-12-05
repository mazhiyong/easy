package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.flyco.dialog.widget.ActionSheetDialog
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.AppUtil
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils
import com.jaeger.library.StatusBarUtil

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.HashMap

import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.tool.UtilTools
import de.hdodenhof.circleimageview.CircleImageView


/**
 * 用户基本信息
 */
class UserInfoActivity : BasicActivity(), RequestView {
    @BindView(R.id.head_img_lay)
    lateinit var mHeadImgLay: CardView
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.head_img_view)
    lateinit var mHeadImageView: CircleImageView
    @BindView(R.id.more_info_lay)
    lateinit var mMoreInfoLay: CardView
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.phone_tv)
    lateinit var mPhoneTv: TextView
    @BindView(R.id.name_tv)
    lateinit var mNameTv: TextView
    @BindView(R.id.idcard_value_tv)
    lateinit var mIdcardValueTv: TextView
    @BindView(R.id.phone_tv2)
    lateinit var mPhoneTv2: TextView
    @BindView(R.id.logout_lay)
    lateinit var mLogoutLay: CardView
    @BindView(R.id.more_info_line)
    lateinit var mInfoLine: View

    private var mRequestTag = ""

    private lateinit var mHeadPath: Map<String, Any>

    override val contentView: Int
        get() = R.layout.activity_user_info


    private lateinit var dataIntent: Intent


    private var mHeadImgPath = ""


    private var imgName = ""
    private lateinit var uritempFile: Uri

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.base_msg)

        if (MbsConstans.USER_MAP != null && !MbsConstans.USER_MAP!!.isEmpty()) {
            val moreInfo = MbsConstans.USER_MAP!!["cmpl_info"]!!.toString() + ""
            if (moreInfo == "1") {//是否完善信息（0：未完善，1：已完善）
                mMoreInfoLay!!.visibility = View.VISIBLE
                mInfoLine!!.visibility = View.VISIBLE
            } else {
                mMoreInfoLay!!.visibility = View.GONE
                mInfoLine!!.visibility = View.GONE
            }

            mPhoneTv!!.text = MbsConstans.USER_MAP!!["tel"]!!.toString() + ""
            mPhoneTv2!!.text = MbsConstans.USER_MAP!!["tel"]!!.toString() + ""
            mNameTv!!.text = MbsConstans.USER_MAP!!["name"]!!.toString() + ""
            mIdcardValueTv!!.text = MbsConstans.USER_MAP!!["idno"]!!.toString() + ""

            initHeadPic()
        } else {
            getUserInfoAction()
        }

        // GlideUtils.loadImage(UserInfoActivity.this,"http://tupian.qqjay.com/u/2017/1201/2_161641_2.jpg",mHeadImageView);


    }

    /**
     * 获取用户信息
     */
    private fun getUserInfoAction() {
        mRequestTag = MethodUrl.userInfo
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.userInfo, map)
    }

    private fun logoutAction() {
        mRequestTag = MethodUrl.LOGIN_ACTION
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestDeleteToMap(mHeaderMap, MethodUrl.LOGIN_ACTION, map)
    }

    private fun initHeadPic() {
        var headUrl = MbsConstans.USER_MAP!!["head_pic"]!!.toString() + ""
        if (headUrl.contains("http")) {
        } else {
            headUrl = MbsConstans.PIC_URL + headUrl
        }
        mHeadImageView?.let { GlideUtils.loadImage2(this@UserInfoActivity, headUrl, it, R.drawable.head) }
    }


    @OnClick(R.id.head_img_lay, R.id.back_img, R.id.more_info_lay, R.id.logout_lay, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.head_img_lay ->
                //                intent = new Intent(UserInfoActivity.this,ModifyFileActivity.class);
                //                startActivity(intent);
                ActionSheetDialogNoTitle()
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.more_info_lay -> {
                if (!UtilTools.empty(MbsConstans.USER_MAP) && !UtilTools.empty(MbsConstans.USER_MAP!!["firm_kind"])){
                    val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""//客户类型（0：个人，1：企业）
                    if (kind == "1") {
                        intent = Intent(this@UserInfoActivity, QiyeInfoShowActivity::class.java)
                        intent.putExtra("backtype", "2")
                        startActivity(intent)
                    } else {
                        intent = Intent(this@UserInfoActivity, MoreInfoManagerActivity::class.java)
                        startActivity(intent)
                    }
                  }
                }

            R.id.logout_lay -> AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(R.string.title_dialog)
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton(R.string.but_sure) { dialog, which -> logoutAction() }
                    .setNegativeButton(R.string.cancel) { dialog, which -> }
                    .show()
        }
    }

    private fun ActionSheetDialogNoTitle() {
        val stringItems = arrayOf("从相册选择", "拍照")
        val dialog = ActionSheetDialog(this@UserInfoActivity, stringItems, null)
        dialog.isTitleShow(false).show()

        dialog.setOnOperItemClickL { parent, view, position, id ->
            dialog.dismiss()
            when (position) {
                0 ->

                    PermissionsUtils.requsetRunPermission(this@UserInfoActivity, object : RePermissionResultBack {
                        override fun requestSuccess() {
                            toast(R.string.successfully)
                            localPic()
                        }

                        override fun requestFailer() {
                            toast(R.string.failure)
                        }
                    }, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
                1 ->

                    PermissionsUtils.requsetRunPermission(this@UserInfoActivity, object : RePermissionResultBack {
                        override fun requestSuccess() {
                            toast(R.string.successfully)
                            photoPic()
                        }

                        override fun requestFailer() {
                            toast(R.string.failure)
                        }
                    }, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA)
            }
        }
    }

    private fun photoPic() {
        /**
         * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
         * 文档，you_sdk_path/docs/guide/topics/media/camera.html
         * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为
         * 官方文档太长了就不看了，其实是错的，这个地方也错了，必须改正
         */
        val uri: Uri
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //下面这句指定调用相机拍照后的照片存储的路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this@UserInfoActivity, AppUtil.getAppProcessName(this) + ".fileProvider", File(Environment.getExternalStorageDirectory(), "xiaoma.jpg"))
        } else {
            uri = Uri.fromFile(File(Environment.getExternalStorageDirectory(), "xiaoma.jpg"))
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, 2)
    }

    private fun localPic() {

        /**
         * 刚开始，我自己也不知道ACTION_PICK是干嘛的，后来直接看Intent源码，
         * 可以发现里面很多东西，Intent是个很强大的东西，大家一定仔细阅读下
         */

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, 1)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var uri: Uri
        when (requestCode) {
            // 如果是直接从相册获取
            1 -> if (data != null) {
                startPhotoZoom(data.data)
            }
            // 如果是调用相机拍照时
            2 -> {

                val temp = File(Environment.getExternalStorageDirectory().toString() + "/xiaoma.jpg")
                if (temp.exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(this@UserInfoActivity, AppUtil.getAppProcessName(this@UserInfoActivity) + ".fileProvider", temp)
                    } else {
                        uri = Uri.fromFile(temp)
                    }
                    startPhotoZoom(uri)
                }
            }
            // 取得裁剪后的图片
            3 -> {
                /**
                 * 非空判断大家一定要验证，如果不验证的话，
                 * 在剪裁之后如果发现不满意，要重新裁剪，丢弃
                 * 当前功能时，会报NullException，小马只
                 * 在这个地方加下，大家可以根据不同情况在合适的
                 * 地方做判断处理类似情况
                 *
                 */
                /*    Toast.makeText(BusinessInfoActivity.this,"EEEEEEEEEEEEEEEEEEEEEEEEEEEEEE",Toast.LENGTH_LONG).show();
                if(data != null){
                    Toast.makeText(BusinessInfoActivity.this,"QQQQQQQQQQQQQQQQQQQQQQQQQQQQQ",Toast.LENGTH_LONG).show();
                    dataIntent = data;
                    setPicToView(data);
                }else {
                    Toast.makeText(BusinessInfoActivity.this,"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$",Toast.LENGTH_LONG).show();

                }
*/

                // 将Uri图片转换为Bitmap
                var bitmap: Bitmap
                try {
                    bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uritempFile!!))
                    // TODO，将裁剪的bitmap显示在imageview控件上
                    val dr = BitmapDrawable(resources, bitmap)
                    saveCroppedImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            }
            4 -> {
            }
            else -> {
            }
        }//			if (data==null) {
        //				return;
        //			}
        //			uri = data.getData();
        //			String[] proj = { MediaStore.Images.Media.DATA };
        //			Cursor cursor = getActivity().getContentResolver().query(uri, proj, null, null,null);
        //			//Cursor cursor =
        //			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //			cursor.moveToFirst();
        //			path = cursor.getString(column_index);// 图片在的路径
        //			Intent intent3=new Intent(getActivity(), ClipActivity.class);
        //			intent3.putExtra("path", path);
        //			startActivityForResult(intent3, 4);
        //			path=photoSavePath+photoSaveName;
        //			uri = Uri.fromFile(new File(path));
        //			Intent intent2=new Intent(getActivity(), ClipActivity.class);
        //			intent2.putExtra("path", path);
        //			startActivityForResult(intent2, 4);
        //ImageLoader.getmContext().displayImage(MbsConstans.Pic_Path+MbsConstans.memberUser.getHeadPath(),mHeadImage);
        // UtilTools.showImage(MbsConstans.Pic_Path+MbsConstans.memberUser.getHeadPath(),mHeadImage, R.drawable.no_def);
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private fun setPicToView(picdata: Intent) {
        val extras = picdata.extras
        if (extras != null) {
            val photo = extras.getParcelable<Bitmap>("data")
            val drawable = BitmapDrawable(resources, photo)
            uploadPic()
        }
    }

    private fun uploadPic() {
        if (dataIntent != null) {
            val extras = dataIntent.extras
            if (extras != null) {
                val photo = extras.getParcelable<Bitmap>("data") ?: return
                saveCroppedImage(photo)
                val stream = ByteArrayOutputStream()
                photo.compress(Bitmap.CompressFormat.JPEG, 60, stream)

            }
        } else {
            Toast.makeText(this, "图片不存在", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCroppedImage(bmp: Bitmap?) {

        try {
            val saveFile = File(MbsConstans.HEAD_IMAGE_PATH)

            mHeadImgPath = MbsConstans.HEAD_IMAGE_PATH + Date().time + ".png"
            val file = File(mHeadImgPath)
            if (!saveFile.exists()) {
                saveFile.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            val saveFile2 = File(mHeadImgPath)

            val fos = FileOutputStream(saveFile2)
            bmp!!.compress(Bitmap.CompressFormat.JPEG, 50, fos)
            fos.flush()
            fos.close()

            // uploadAliPic(new Date().getTime()+".png",filepath);

            uploadPicAction()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    private fun uploadPicAction() {
        mRequestTag = MethodUrl.uploadFile
        val signMap = HashMap<String, Any>()
        val map = HashMap<String, Any>()
        val fileMap = HashMap<String, Any>()
        fileMap["file"] = mHeadImgPath
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.postFileToMap(mHeaderMap, MethodUrl.uploadFile, signMap, map, fileMap)
    }

    private fun submitPicPath() {
        mRequestTag = MethodUrl.headPath
        val map = HashMap<String, String>()
        map["remotepath"] = mHeadPath!!["remotepath"]!!.toString() + ""
        map["filemd5"] = mHeadPath!!["filemd5"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.headPath, map)
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    fun startPhotoZoom(uri: Uri?) {
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
         * yourself_sdk_path/docs/reference/android/content/Intent.html
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
         * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
         * 制做的了...吼吼
         */
        /*    Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image*//*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);*/


        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.putExtra("crop", "true")
        // intent.putExtra("noFaceDetection", true);
        // 宽高的比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // 裁剪图片宽高
        intent.putExtra("outputX", 400)
        intent.putExtra("outputY", 400)

        // intent.putExtra("scale", true);
        // intent.putExtra("return-data", true);
        // this.startActivityForResult(intent, AppFinal.RESULT_CODE_PHOTO_CUT);
        /**
         * 此方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
         * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
         */
        imgName = System.currentTimeMillis().toString() + ".jpg"
        uritempFile = Uri.parse("file:///" + MbsConstans.BASE_PATH + "/" + imgName)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        startActivityForResult(intent, 3)

    }


    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData:MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.userInfo -> {
                MbsConstans.USER_MAP = tData
                SPUtils.put(this@UserInfoActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.instance.objectToJson(MbsConstans.USER_MAP as Any))
                initHeadPic()
            }
            MethodUrl.headPath -> {
                showToastMsg("上传头像成功")
                getUserInfoAction()
                intent = Intent()
                intent.action = MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE
                sendBroadcast(intent)
            }
            MethodUrl.uploadFile -> {
                mHeadPath = tData
                submitPicPath()
            }
            MethodUrl.LOGIN_ACTION//{verify_type=FACE, state=0}
            -> {
                closeAllActivity()
                MbsConstans.USER_MAP = null
                MbsConstans.REFRESH_TOKEN = ""
                MbsConstans.ACCESS_TOKEN = ""
                SPUtils.put(this@UserInfoActivity, MbsConstans.SharedInfoConstans.LOGIN_OUT, true)
                intent = Intent(this@UserInfoActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            //获取refreshToken返回结果
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.LOGIN_ACTION -> logoutAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        dealFailInfo(map, mType)

    }
}
