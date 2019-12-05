package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.FileUtils
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import com.lairui.easy.mywidget.view.BankCardTextWatcher
import com.jaeger.library.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils

import java.io.File
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 企业账号信息录入   界面
 */
class QiyeCardInfoActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.name_tv)
    lateinit var mNameTv: EditText
    @BindView(R.id.zhanghu_eidt)
    lateinit var mZhanghuEidt: EditText
    @BindView(R.id.kaihu_bank_value_tv)
    lateinit var mKaihuBankValueTv: TextView
    @BindView(R.id.name_bank_lay)
    lateinit var mNameBankLay: CardView
    @BindView(R.id.kaihu_bank_dian_tv)
    lateinit var mKaihuBankDianTv: TextView
    @BindView(R.id.kaihu_bank_lay)
    lateinit var mKaihuBankLay: CardView
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.qiye_xuke_image)
    lateinit var mQiyeXukeImage: ImageView


    private var mRequestTag = ""

    private var mBankNum = ""
    private var mName = ""

    private lateinit var mHezuoMap: MutableMap<String, Any>

    private val mIsShow = false

    private lateinit var mBankMap: MutableMap<String, Any>
    private lateinit var mWangDianMap: MutableMap<String, Any>
    private lateinit var mAddMap: MutableMap<String, String>

    override val contentView: Int
        get() = R.layout.activity_qiye_card_info


    private val mSelectPicCode = 1002
    private var selectList: List<LocalMedia> = ArrayList()

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mHezuoMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }

        mTitleText!!.text = resources.getString(R.string.qiye_dakuan_check)

        mZhanghuEidt?.let { BankCardTextWatcher.bind(it) }

        getUserInfoAction()
    }

    /**
     * 获取用户基本信息
     */
    fun getUserInfoAction() {
        mRequestTag = MethodUrl.userInfo
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.userInfo, map)
    }


    private fun uploadFile(localMedia: LocalMedia, code: String) {

        // mRequestTag = MethodUrl.creUploadFile;
        val mSignMap = HashMap<String, Any>()
        mSignMap["file"] = localMedia
        mSignMap["code"] = code
        val oldPath = localMedia.path

        val mParamMap = HashMap<String, Any>()

        val map = HashMap<String, Any>()
        map["file"] = localMedia.compressPath
        //map.put("fileName",mDataMap.get("precreid")+""+System.currentTimeMillis()+""+i);
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.postFileToMap(mHeaderMap, MethodUrl.creUploadFile, mSignMap, mParamMap, map)
    }


    private fun submitAction() {
        /*if (UtilTools.isEmpty(mNameTv, "户名")) {
            showToastMsg("户名不能为空");
            mButNext.setEnabled(true);
            return;
        }*/
        if (UtilTools.isEmpty(mZhanghuEidt!!, "账户")) {
            showToastMsg("账户不能为空")
            mButNext!!.isEnabled = true
            return
        }
        if (UtilTools.isEmpty(mKaihuBankValueTv!!, "开户行")) {
            showToastMsg("开户行不能为空")
            mButNext!!.isEnabled = true
            return
        }
        /* if (UtilTools.isEmpty(mKaihuBankDianTv, "开户网点")) {
            showToastMsg("开户网点不能为空");
            mButNext.setEnabled(true);
            return;
        }*/

        mBankNum = mZhanghuEidt!!.text.toString() + ""
        mBankNum = mBankNum.replace(" ".toRegex(), "")

        /* boolean b = RegexUtil.isSiCard(mBankNum);
        if (!b) {
            showToastMsg("请输入合法的银行卡号");
            mButNext.setEnabled(true);
            return;
        }*/
        mName = mNameTv!!.text.toString() + ""

        mRequestTag = MethodUrl.companyPay
        mAddMap = HashMap()
        //mAddMap.put("accname", mName);//户名
        mAddMap!!["accid"] = mBankNum//帐号
        mAddMap!!["opnbnkid"] = mBankMap!!["bankid"]!!.toString() + ""//开户行ID
        mAddMap!!["opnbnknm"] = mBankMap!!["bankname"]!!.toString() + ""//开户行名称
        /* // mAddMap.put("crossmark",mBankMap.get("crossmark")+"");//跨行标识（1 本行 2 跨行）
        mAddMap.put("wdcode", mWangDianMap.get("opnbnkwdcd") + "");//开户网点编号
        mAddMap.put("wdname", mWangDianMap.get("opnbnkwdnm") + "");//开户网点名称*/

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.companyPay, mAddMap!!)
    }


    @OnClick(R.id.kaihu_bank_lay, R.id.but_next, R.id.back_img, R.id.name_bank_lay, R.id.left_back_lay, R.id.qiye_xuke_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.kaihu_bank_lay -> if (mBankMap == null) {
                showToastMsg("请先填写银行卡信息")
            } else {
                intent = Intent(this@QiyeCardInfoActivity, ChoseBankAddActivity::class.java)
                intent.putExtra("bankid", mBankMap!!["bankid"]!!.toString() + "")
                startActivityForResult(intent, 200)
            }
            R.id.but_next -> {

                /* intent = new Intent(QiyeCardInfoActivity.this,QiyeDakuanCheckActivity.class);
                startActivity(intent);*/

                mButNext!!.isEnabled = false
                submitAction()
            }
            R.id.name_bank_lay -> {
                intent = Intent(this@QiyeCardInfoActivity, BankNameListActivity::class.java)
                startActivityForResult(intent, 300)
            }
            R.id.qiye_xuke_lay -> PermissionsUtils.requsetRunPermission(this@QiyeCardInfoActivity, object : RePermissionResultBack {
                override fun requestSuccess() {
                    selectPic()
                }

                override fun requestFailer() {
                    toast(R.string.failure)
                }
            }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
        }
    }

    private fun selectPic() {
        val path = File(MbsConstans.UPLOAD_PATH)
        if (!path.exists()) {
            path.mkdirs()
        }
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(this@QiyeCardInfoActivity)
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
                .forResult(mSelectPicCode)//结果回调onActivityResult code
        //注意  这个  mCurrentPosition   是标记后台返回的文件类别类型的索引
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            30 -> {
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
        when (mType) {
            MethodUrl.userInfo//用户信息 //{auth=1, firm_kind=0, head_pic=default, name=刘英超, tel=151****3298, idno=4107****3616, cmpl_info=0}
            -> {
                MbsConstans.USER_MAP = tData
                SPUtils.put(this@QiyeCardInfoActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.instance.objectToJson(MbsConstans.USER_MAP!!))
                mNameTv!!.setText(tData["comname"]!!.toString() + "")
            }
            //上传文件成功
            MethodUrl.creUploadFile -> {
                dismissProgressDialog()
                val code = tData["code"]!!.toString() + ""
                if (code == "1") {
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
                    GlideUtils.loadImage(this@QiyeCardInfoActivity, path, mQiyeXukeImage!!)
                }
            }
            MethodUrl.companyPay//{custid=null}
            -> {
                val intent = Intent(this@QiyeCardInfoActivity, QiyeDakuanCheckActivity::class.java)
                intent.putExtra("remitid", tData["remitid"]!!.toString() + "")
                startActivity(intent)
                mButNext!!.isEnabled = true
            }

            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.companyPay -> submitAction()
                    MethodUrl.userInfo//用户信息 //{auth=1, firm_kind=0, head_pic=default, name=刘英超, tel=151****3298, idno=4107****3616, cmpl_info=0}
                    -> getUserInfoAction()
                }
            }
        }//showUpdateDialog();

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.creUploadFile -> dismissProgressDialog()
            MethodUrl.companyPay//{custid=null}
            -> mButNext!!.isEnabled = true
            MethodUrl.checkBankCard//{"bank_same":"0","bankid":"305","accid":"6226203000645932","bankname":"民生银行","card_type":"1"}
            -> {
            }
        }
        dealFailInfo(map, mType)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bundle: Bundle?
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                mSelectPicCode -> {
                    // 图片选择结果回调
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
                        // uploadFile(localMedia,code);
                        LogUtil.i("打印log日志", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@开始上传")
                        uploadFile(localMedia, "1")
                    }
                }
                100 -> {
                }
                200 -> {
                    bundle = data!!.extras
                    if (bundle != null) { //{"opnbnkwdnm":"南洋商业银行（中国）有限公司北京分行","opnbnkwdcd":"503100000015"}
                        mWangDianMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mKaihuBankDianTv!!.text = mWangDianMap!!["opnbnkwdnm"]!!.toString() + ""
                        mKaihuBankDianTv!!.setError(null, null)
                    }
                }
                300 -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mBankMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mBankMap!!["bankid"] = mBankMap!!["opnbnkid"]!!.toString() + ""
                        mBankMap!!["bankname"] = mBankMap!!["opnbnknm"]!!.toString() + ""
                        mBankMap!!["logopath"] = mBankMap!!["logopath"]!!.toString() + ""
                        mKaihuBankValueTv!!.text = mBankMap!!["opnbnknm"]!!.toString() + ""
                        mKaihuBankValueTv!!.setError(null, null)
                    }
                }
            }
        }
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
            e.printStackTrace()
            LogUtil.i("PictureFileUtils删除文件", "没有申请权限")
        }

    }
}
