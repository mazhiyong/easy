package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Context
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
import com.lairui.easy.utils.tool.FileUtils
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import okhttp3.ResponseBody
import retrofit2.Response


/**
 * 企业申请证书  界面
 */
class QiyeApplyCaActivity : BasicActivity(), RequestView {

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

    @BindView(R.id.qiye_down_tv1)
    lateinit var mQiyeDownTv1: TextView
    @BindView(R.id.qiye_down_tv2)
    lateinit var mQiyeDownTv2: TextView
    @BindView(R.id.qiye_image1)
    lateinit var mQiyeImage1: ImageView
    @BindView(R.id.qiye_pdf_cardView1)
    lateinit var mQiyePdfCardView1: CardView
    @BindView(R.id.qiye_image2)
    lateinit var mQiyeImage2: ImageView
    @BindView(R.id.qiye_pdf_cardView2)
    lateinit var mQiyePdfCardView2: CardView
    @BindView(R.id.qiye_qianzhang_image)
    lateinit var mQiyeQianzhangImage: ImageView
    @BindView(R.id.qiye_qianzhang_lay)
    lateinit var mQiyeQianzhangLay: CardView
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.open1)
    lateinit var mOpen1: Button
    @BindView(R.id.open2)
    lateinit var mOpen2: Button


    private var mName: String? = ""
    private var mIdNum: String? = ""

    private var mRequestTag = ""

    private var mCurrentPosition = 0

    override val contentView: Int
        get() = R.layout.activity_qiye_apply_ca


    private var selectList: List<LocalMedia> = ArrayList()

    private var saveP = ""

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mName = bundle.getString("idname")
            mIdNum = bundle.getString("idno")
        }
        mTitleText!!.text = resources.getString(R.string.qiye_apply_ca)
    }

    private fun submitInfoAction() {

        mRequestTag = MethodUrl.peopleAuth
        val mSignMap = HashMap<String, Any>()

        val map = HashMap<String, Any>()
        map["idno"] = mIdNum!!
        map["idname"] = mName!!
        val fileMap = HashMap<String, Any>()

        fileMap["img_front"] = mQiyeImage1!!.tag
        fileMap["img_back"] = mQiyeImage2!!.tag
        fileMap["img_hand"] = mQiyeQianzhangImage!!.tag
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.postFileToMap(mHeaderMap, MethodUrl.peopleAuth, mSignMap, map, fileMap)
    }

    private fun downFileAction() {

        val url = "https://gagayi.oss-cn-beijing.aliyuncs.com/video/pdf-signed.pdf"
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()

        mRequestPresenterImp!!.downloadFile("bytes=0" + "-", mHeaderMap, url, map)
    }

    private fun isAvilible(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        // 获取所有已安装程序的包信息
        val pinfo = packageManager.getInstalledPackages(0)
        for (i in pinfo.indices) {
            if (pinfo[i].packageName.equals(packageName, ignoreCase = true))
                return true
        }
        return false
    }

    @OnClick(R.id.back_img, R.id.left_back_lay, R.id.open1, R.id.open2, R.id.qiye_down_tv1, R.id.qiye_down_tv2, R.id.qiye_pdf_cardView1, R.id.qiye_pdf_cardView2, R.id.qiye_qianzhang_lay, R.id.but_next)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.open1 -> {
            }
            R.id.open2 -> {
            }
            R.id.qiye_down_tv1 -> {
                intent = Intent(this@QiyeApplyCaActivity, PDFLookActivity::class.java)
                intent.putExtra("id", "https://gagayi.oss-cn-beijing.aliyuncs.com/video/pdf-signed.pdf")
                startActivity(intent)
            }
            R.id.qiye_down_tv2 -> {
                intent = Intent(this@QiyeApplyCaActivity, PDFLookActivity::class.java)
                intent.putExtra("id", "https://gagayi.oss-cn-beijing.aliyuncs.com/video/pdf-signed.pdf")
                startActivity(intent)
            }
            R.id.qiye_pdf_cardView1 -> {
                mCurrentPosition = 1

                PermissionsUtils.requsetRunPermission(this@QiyeApplyCaActivity, object : RePermissionResultBack {
                    override fun requestSuccess() {
                        selectPic()
                    }

                    override fun requestFailer() {
                        toast(R.string.failure)
                    }
                }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
            }
            R.id.qiye_pdf_cardView2 -> {
                mCurrentPosition = 2
                PermissionsUtils.requsetRunPermission(this@QiyeApplyCaActivity, object : RePermissionResultBack {
                    override fun requestSuccess() {
                        selectPic()
                    }

                    override fun requestFailer() {
                        toast(R.string.failure)
                    }
                }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
            }
            R.id.qiye_qianzhang_lay -> {
                mCurrentPosition = 3
                PermissionsUtils.requsetRunPermission(this@QiyeApplyCaActivity, object : RePermissionResultBack {
                    override fun requestSuccess() {
                        selectPic()
                    }

                    override fun requestFailer() {
                        toast(R.string.failure)
                    }
                }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
            }
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
                //                submitInfoAction();
                intent = Intent(this@QiyeApplyCaActivity, QiyeCaPayActivity::class.java)
                startActivity(intent)
            }
        }/* if (mOpen1.getTag() == null){

                }else {
                    File file = (File)mOpen1.getTag();
                     intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    if (isAvilible(QiyeApplyCaActivity.this, "cn.wps.moffice_eng")) {
                        intent.setClassName("cn.wps.moffice_eng",
                                "cn.wps.moffice.documentmanager.PreStartActivity2");
                    } else {
                        intent.addCategory("android.intent.category.DEFAULT");
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri;
                    //下面这句指定调用相机拍照后的照片存储的路径
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(QiyeApplyCaActivity.this,
                                AppUtil.getAppProcessName(QiyeApplyCaActivity.this)+".fileProvider", file);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    intent.setDataAndType(uri, "application/pdf");
                    startActivity(intent);

                    System.out.println(AppUtil.getAppProcessName(QiyeApplyCaActivity.this)+" "+file.getAbsolutePath());
                }
*///downFileAction();
    }


    private fun downFile(map: MutableMap<String, Any>) {
        LogUtil.i("下载文件", "这里执行么没有")
        val response = map["file"] as Response<ResponseBody>?
        if (response!!.body() == null) {
            println("资源错误")
            return
        }

        lateinit var os: OutputStream
        val `is` = response.body()!!.byteStream()

        val totalLength = response.body()!!.contentLength()
        var currentLength: Long = 0

        val rootFile = File(MbsConstans.APP_DOWN_PATH)
        if (!rootFile.exists() && !rootFile.isDirectory)
            rootFile.mkdirs()
        val file = File(MbsConstans.APP_DOWN_PATH + "/" + System.currentTimeMillis() + ".pdf")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        try {
            os = FileOutputStream(file)
            var len: Int
            val buff = ByteArray(1024)
            len = `is`!!.read(buff)
            while (len != -1) {
                os.write(buff, 0, len)
                currentLength += len.toLong()
                LogUtil.i("下载文件", "当前进度: $currentLength")
                LogUtil.i("下载文件", (100 * currentLength / totalLength).toInt().toString() + "  %")
                if ((100 * currentLength / totalLength).toInt() == 100) {
                    LogUtil.i("下载文件", "下载完成")
                }
            }
        } catch (e: FileNotFoundException) {
            LogUtil.i("下载文件", "未找到文件！")
            e.printStackTrace()
        } catch (e: IOException) {
            LogUtil.i("下载文件", "IO错误！")
            e.printStackTrace()
        } finally {
            if (os != null) {
                try {
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        mOpen1!!.tag = file
    }

    private fun selectPic() {
        val path = File(MbsConstans.UPLOAD_PATH)
        if (!path.exists()) {
            path.mkdirs()
        }
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(this@QiyeApplyCaActivity)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                // .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(0)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(true)// 是否可预览视频 true or false
                .enablePreviewAudio(true) // 是否可播放音频 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .glideOverride(160, 160)
                //.sizeMultiplier(0.4f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                //.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                // .withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //  .hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(true)// 是否显示gif图片 true or false
                .compressSavePath(MbsConstans.UPLOAD_PATH)//压缩图片保存地址
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                //.openClickSound(true)// 是否开启点击声音 true or false
                //.selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                //.previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                // .cropCompressQuality(90)// 裁剪压缩质量 默认90 int
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                //.rotateEnabled() // 裁剪是否可旋转图片 true or false
                //.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
                // .videoQuality()// 视频录制质量 0 or 1 int
                //.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
                //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                //.recordVideoSecond()//视频秒数录制 默认60s int
                //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(mCurrentPosition)//结果回调onActivityResult code
        //注意  这个  mCurrentPosition   是标记后台返回的文件类别类型的索引
    }

    fun photo() {
        PermissionsUtils.requsetRunPermission(this@QiyeApplyCaActivity, object : RePermissionResultBack {
            override fun requestSuccess() {
                selectPic()
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
            uri = FileProvider.getUriForFile(this@QiyeApplyCaActivity, AppUtil.getAppProcessName(this@QiyeApplyCaActivity) + ".fileProvider", File(savePath, "$fileName.jpg"))
        } else {
            uri = Uri.fromFile(File(savePath, "$fileName.jpg"))
        }
        // 指定存储路径，这样就可以保存原图了
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        // 拍照返回图片
        startActivityForResult(openCameraIntent, TAKE_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == mCurrentPosition) {
                selectList = PictureSelector.obtainMultipleResult(data)
                LogUtil.i("已选择的图片", selectList + "")
                showProgressDialog("正在上传，请稍后...")
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                for (i in selectList.indices) {
                    val localMedia = selectList[i]
                    uploadFile(localMedia, mCurrentPosition.toString() + "")
                }
            }
        }
    }


    private fun uploadFile(localMedia: LocalMedia, code: String) {

        // mRequestTag = MethodUrl.creUploadFile;
        val mSignMap = HashMap<String, Any>()
        mSignMap["file"] = localMedia
        mSignMap["code"] = code

        val mParamMap = HashMap<String, Any>()

        val oldPath = localMedia.path
        val map = HashMap<String, Any>()
        map["file"] = localMedia.compressPath
        //map.put("fileName",mDataMap.get("precreid")+""+System.currentTimeMillis()+""+i);
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.postFileToMap(mHeaderMap, MethodUrl.creUploadFile, mSignMap, mParamMap, map)
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
            MethodUrl.creUploadFile -> {
                dismissProgressDialog()
                val code = tData["code"]!!.toString() + ""
                val localMedia = tData["file"] as LocalMedia?
                var path = ""
                if (localMedia!!.isCut && !localMedia.isCompressed) {
                    // 裁剪过
                    path = localMedia.cutPath
                } else if (localMedia.isCompressed || localMedia.isCut && localMedia.isCompressed) {
                    // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                    path = localMedia.compressPath
                } else {
                    // 原图
                    path = localMedia.path
                }
                when (code) {
                    "1" -> GlideUtils.loadImage(this@QiyeApplyCaActivity, path, mQiyeImage1!!)
                    "2" -> GlideUtils.loadImage(this@QiyeApplyCaActivity, path, mQiyeImage2!!)
                    "3" -> GlideUtils.loadImage(this@QiyeApplyCaActivity, path, mQiyeQianzhangImage!!)
                }
            }
            "https://gagayi.oss-cn-beijing.aliyuncs.com/video/pdf-signed.pdf" -> downFile(tData)
            MethodUrl.peopleAuth -> {
                FileUtils.deleteDir(MbsConstans.PHOTO_PATH)

                intent = Intent(this@QiyeApplyCaActivity, CheckWatiActivity::class.java)
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
        when (mType) {
            MethodUrl.creUploadFile//
            -> dismissProgressDialog()
        }
        dealFailInfo(map, mType)
    }


    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun finish() {
        super.finish()
        FileUtils.deleteDir(MbsConstans.UPLOAD_PATH)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            PictureFileUtils.deleteCacheDirFile(this)
            PictureFileUtils.deleteExternalCacheDirFile(this)
        } catch (e: Exception) {

        }

    }

    companion object {


        private val TAKE_PICTURE = 0x000001
    }

}
