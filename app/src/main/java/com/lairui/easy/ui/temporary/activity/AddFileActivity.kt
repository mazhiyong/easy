package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.AddFileAdapter
import com.lairui.easy.ui.temporary.adapter.GridImageAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils

import java.io.File
import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.temporary.adapter.GridImageAdapter.OnItemClickListener

/**
 * 上传附件信息   界面
 */
class AddFileActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.but_next)
    lateinit var mButNext: Button

    private val mRequestTag = ""

    private var mAddFileAdapter: AddFileAdapter? = null

    private var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private val mDataList = ArrayList<MutableMap<String, Any>>()
    private var mPage = 1

    private var selectList: List<LocalMedia> = ArrayList()

    private var mTitleList: List<MutableMap<String, Any>>? = null

    private var mDataMap: MutableMap<String, Any>? = null //获取图片信息
    private var mSign: String? = ""

    private var mCurrentPosition = 0

    private var mFileNum = 0

    override val contentView: Int
        get() = R.layout.activity_add_file


    internal var mOnItemClickListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(position: Int, v: View, mType: String) {
            var localMediaList: List<LocalMedia> = ArrayList()
            for (i in mTitleList!!.indices) {
                val item = mTitleList!![i]
                var ss = item["connpk"]!!.toString() + ""
                if (mSign == "1") {
                    ss = item["connpk"]!!.toString() + ""
                } else if (mSign == "2") {
                    ss = item["filetype"]!!.toString() + ""
                }
                if (mType == ss) {
                    localMediaList = (item["selectPicList"] as List<LocalMedia>?)!!
                    break
                }
            }

            if (localMediaList.size > 0) {
                val media = localMediaList[position]
                val pictureType = media.pictureType
                val mediaType = PictureMimeType.pictureToVideo(pictureType)
                when (mediaType) {
                    1 ->
                        // 预览图片 可自定长按保存路径
                        //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                        PictureSelector.create(this@AddFileActivity).themeStyle(R.style.picture_default_style).openExternalPreview(position, localMediaList)
                    2 ->
                        // 预览视频
                        PictureSelector.create(this@AddFileActivity).externalPictureVideo(media.path)
                    3 ->
                        // 预览音频
                        PictureSelector.create(this@AddFileActivity).externalPictureAudio(media.path)
                }
            }
        }
    }

    private val onAddPicClickListener = object : GridImageAdapter.onAddPicClickListener {
        override fun onAddPicClick(mType: String) {//点击添加时间，然后把文件类型 传递到前面过来  然后选择图片 上传图片

            for (i in mTitleList!!.indices) {
                val item = mTitleList!![i]
                var ss = item["connpk"]!!.toString() + ""
                if (mSign == "1") {
                    ss = item["connpk"]!!.toString() + ""
                } else if (mSign == "2") {
                    ss = item["filetype"]!!.toString() + ""
                }
                if (mType == ss) {
                    mCurrentPosition = i
                    selectPic()
                    return
                }
            }
        }

        override fun onDeleClick(position: Int, mType: String) {
            for (i in mTitleList!!.indices) {
                val item = mTitleList!![i]
                var ss = item["connpk"]!!.toString() + ""
                if (mSign == "1") {
                    ss = item["connpk"]!!.toString() + ""
                } else if (mSign == "2") {
                    ss = item["filetype"]!!.toString() + ""
                }
                if (mType == ss) {
                    val resultData = item["resultData"] as MutableList<MutableMap<String, Any>>?
                    resultData!!.removeAt(position)
                    mAddFileAdapter!!.notifyDataSetChanged()
                    break
                }
            }
        }

    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.add_fujian)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
            mSign = bundle.getString("TYPE")
            if (mDataMap != null) {
                if (mSign == "1") {///申请额度  上传附件  标记
                    mTitleList = mDataMap!!["contList"] as MutableList<MutableMap<String, Any>>?
                } else if (mSign == "2") {//借款申请  上传附件  标记 之前是conts  现在都统一成了  contList
                    mTitleList = mDataMap!!["contList"] as MutableList<MutableMap<String, Any>>?
                }
            }
        }

        LogUtil.i("show", "$mSign!!!!!!!!!!$mTitleList")

        mRightImg!!.visibility = View.VISIBLE
        mRightImg!!.setImageResource(R.drawable.shuaixuan)
        mRightTextTv!!.visibility = View.VISIBLE
        mRightLay!!.visibility = View.GONE



        initView()
        getSelectPic()
        // traderListAction();
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        //mPageView.showEmpty();
        mPageView!!.showContent()
        mPageView!!.reLoadingData = this
        val manager = LinearLayoutManager(this@AddFileActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = manager
        mRefreshListView!!.setOnRefreshListener { mPage = 1 }

        mRefreshListView!!.setOnLoadMoreListener { mPage++ }
    }


    private fun getSelectPic() {
        if (mTitleList == null || mTitleList!!.size <= 0) {
            mPageView!!.showEmpty()
            return
        }

        mAddFileAdapter = mSign?.let { AddFileAdapter(this@AddFileActivity, it) }
        mAddFileAdapter!!.onAddPicClickListene = onAddPicClickListener
        mAddFileAdapter!!.onItemClickListener = mOnItemClickListener
        mAddFileAdapter!!.addAll(mTitleList!!)
        mLRecyclerViewAdapter = LRecyclerViewAdapter(mAddFileAdapter)
        mRefreshListView!!.adapter = mLRecyclerViewAdapter

        mRefreshListView!!.setPullRefreshEnabled(false)
        mRefreshListView!!.setLoadMoreEnabled(false)
    }

    private fun selectPic() {

        val path = File(MbsConstans.UPLOAD_PATH)
        if (!path.exists()) {
            path.mkdirs()
        }
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(this@AddFileActivity)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                // .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(9)// 最大图片选择数量 int
                .minSelectNum(0)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
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
                .cropCompressQuality(80)// 裁剪压缩质量 默认90 int
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == mCurrentPosition) {
                // 图片选择结果回调
                selectList = PictureSelector.obtainMultipleResult(data)
                val map = mTitleList!![mCurrentPosition]
                var code = map["connpk"]!!.toString() + ""
                if (mSign == "1") {
                    code = map["connpk"]!!.toString() + ""
                } else if (mSign == "2") {
                    code = map["filetype"]!!.toString() + ""
                }

                /*   Map<String,Object> map = mTitleList.get(mCurrentPosition);
					BitmapResizeUtil.compressBitmap(saveP, MbsConstans.PHOTO_PATH, fileName, RotatePictureUtil.getBitmapDegree(saveP));

                map.put("selectPicList",selectList);
                mAddFileAdapter.notifyDataSetChanged();*/
                mFileNum = 0

                showProgressDialog("正在上传，请稍后...")
                for (i in selectList.indices) {
                    val localMedia = selectList[i]
                    /*String fileName = String.valueOf(System.currentTimeMillis())+"_"+i + ".jpg";
                    BitmapResizeUtil.compressBitmap(localMedia.getPath(),MbsConstans.UPLOAD_PATH, fileName, RotatePictureUtil.getBitmapDegree(localMedia.getPath()));
                    localMedia.setCompressPath(MbsConstans.UPLOAD_PATH + fileName);
                    BitmapUtil.saveCompressImg(localMedia.getPath(),MbsConstans.UPLOAD_PATH+fileName+".png");*/
                    uploadFile(localMedia, code)
                    LogUtil.i("show", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@开始上传")
                }
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                for (media in selectList) {
                    Log.i("图片-----》", media.path)
                }
                /*mGridImageAdapter.setList(selectList);
                    mGridImageAdapter.notifyDataSetChanged();*/
            }
        }
    }

    private fun uploadFile(localMedia: LocalMedia, code: String) {

        // mRequestTag = MethodUrl.creUploadFile;
        val mSignMap = HashMap<String, Any>()
        mSignMap["file"] = localMedia
        mSignMap["index"] = mCurrentPosition
        mSignMap["code"] = code
        val oldPath = localMedia.path

        val map = HashMap<String, Any>()
        map["file"] = localMedia.compressPath
        //map.put("fileName",mDataMap.get("precreid")+""+System.currentTimeMillis()+""+i);
        val mHeaderMap = HashMap<String, String>()


        val mParamMap = HashMap<String, Any>()


        mRequestPresenterImp!!.postFileToMap(mHeaderMap, MethodUrl.creUploadFile, mSignMap, mParamMap, map)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent? = null
        when (view.id) {
            R.id.but_next -> finish()
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    override fun showProgress() {
        //showProgressDialog();
    }

    override fun disimissProgress() {
        //dismissProgressDialog();
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {


        val intent: Intent
        when (mType) {
            MethodUrl.creUploadFile//
            -> {
                mFileNum++
                val code = tData["code"]!!.toString() + ""
                for (i in mTitleList!!.indices) {

                    val item = mTitleList!![i]
                    var ss = item["connpk"]!!.toString() + ""
                    if (mSign == "1") {
                        ss = item["connpk"]!!.toString() + ""
                    } else if (mSign == "2") {
                        ss = item["filetype"]!!.toString() + ""
                    }
                    if (code == ss) {
                        var resutData: MutableList<MutableMap<String, Any>>? = item["resultData"] as MutableList<MutableMap<String, Any>>?
                        val mm = HashMap<String, Any>()
                        val str1 = tData["remotepath"]!!.toString() + ""
                        val str2 = tData["filemd5"]!!.toString() + ""
                        mm["remotepath"] = str1
                        mm["filemd5"] = str2
                        if (resutData != null) {
                            resutData.add(mm)
                        } else {
                            resutData = ArrayList()
                            resutData.add(mm)
                            item.put("resultData", resutData)
                        }

                        var fileList: MutableList<LocalMedia>? = item["selectPicList"] as MutableList<LocalMedia>?

                        if (fileList != null) {
                            fileList.add(tData["file"] as LocalMedia)
                        } else {
                            fileList = ArrayList()
                            fileList.add(tData["file"] as LocalMedia)
                            item.put("selectPicList", fileList)
                        }

                        // mAddFileAdapter.notifyDataSetChanged();
                        break
                    }
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"].toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.creUploadFile -> {
                    }
                }//                        uploadFile();
            }
        }//mRefreshListView.refreshComplete(10);
        if (mFileNum == selectList.size) {
            mAddFileAdapter!!.notifyDataSetChanged()
            mRefreshListView!!.refreshComplete(10)
            dismissProgressDialog()
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.creUploadFile//
            -> mFileNum++
        }
        if (mFileNum == selectList.size) {
            mAddFileAdapter!!.notifyDataSetChanged()
            mRefreshListView!!.refreshComplete(10)
            dismissProgressDialog()
        }
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {}


    /*  @Override
    public void finish() {
        super.finish();
        System.gc();
        System.runFinalization();
    }*/


    override fun onDestroy() {
        super.onDestroy()
        try {
            PictureFileUtils.deleteCacheDirFile(this)
            PictureFileUtils.deleteExternalCacheDirFile(this)
        } catch (e: Exception) {

        }

        //        FileUtils.deleteDir(MbsConstans.UPLOAD_PATH);
        val intent = Intent()
        intent.action = MbsConstans.BroadcastReceiverAction.FILE_TIP_ACTION
        intent.putExtra("DATA", mTitleList as Serializable?)
        sendBroadcast(intent)


    }
}
