package com.lairui.easy.basic

import android.os.Environment
import androidx.annotation.IntDef
import com.lairui.easy.R

object MbsConstans {
    /**
     *
     * 参数设置信息开始 ******************************************
     */

    /**
     * 毫秒与毫秒的倍数
     */
    const val MSEC = 1
    /**
     * 秒与毫秒的倍数
     */
    const val SEC = 1000
    /**
     * 分与毫秒的倍数
     */
    const val MIN = 60000
    /**
     * 时与毫秒的倍数
     */
    const val HOUR = 3600000
    /**
     * 天与毫秒的倍数
     */
    const val DAY = 86400000

    @IntDef(MSEC, SEC, MIN, HOUR, DAY)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Unit

    val SERVER_IP_PORT = "172.16.1.65:12170"
    val WEBSOCKET_URL = "ws://$SERVER_IP_PORT/appsvr/"

    val SERVER_URL = "http://fysp.bjfable.com"
    val HANGQING_SERVER_URL = "http://hq.sinajs.cn/"  //涨跌幅
    val HOME_SERVER_URL = "http://qt.gtimg.cn/q=sh000001,sz399001,sz399006" //三大板块
    val QUERY_SERVER_URL = "http://smartbox.gtimg.cn/s3/" //检索
    val DETIAL_SERVER_URL = "http://qt.gtimg.cn/" //详情

    val TIME_MINUTE_SERVER_URL = "http://web.ifzq.gtimg.cn/appstock/app/minute/query?_var=min_data_sz000656 " //1分 分时
    val TIME_FDAY_SERVER_URL = "http://web.ifzq.gtimg.cn/appstock/app/day/query?_var=fdays_data_sz000656&code=sz000656" //5日 分时

    val KLINE_MINUTE_SERVER_URL = "http://ifzq.gtimg.cn/appstock/app/kline/mkline?" //分钟 K线
    val KLINE_DAY_SERVER_URL = "http://web.ifzq.gtimg.cn/appstock/app/fqkline/get?_var=kline_dayqfq" //日 K线
    val KLINE_WEEK_SERVER_URL = "http://web.ifzq.gtimg.cn/appstock/app/fqkline/get?_var=kline_weekqfq&param=sz000656,week,,,320,qfq " //周 K线
    val KLINE_MONTHS_SERVER_URL = "http://web.ifzq.gtimg.cn/appstock/app/fqkline/get?_var=kline_monthqfq&param=sz000656,month,,,320,qfq" //月 K线









    val CHENGJIAO_SERVER_URL = "http://stock.gtimg.cn/data/view/rank.php"


    val PIC_URL = SERVER_URL + "viewer/image?path="

    val DATABASE_NAME = "derongs.db"
    val DATABASE_PATH = "/data/data/com.lairui.easy/databases"
    val DATA_PATH = "/data/data/android/files"

    // SDCard路径
    val SD_PATH = Environment.getExternalStorageDirectory().absolutePath

    val UPDATE_CODE = "derong"
    // 路径
    @JvmField val BASE_PATH = "$SD_PATH/derong/"
    //保存图片的路径
    val PIC_SAVE = "$SD_PATH/derong/pic/"
    //头像路径
    @JvmField val HEAD_IMAGE_PATH = "$SD_PATH/derong/headImage/"
    //身份证照保存路径
    val IDCARD_IMAGE_PATH = "$SD_PATH/derong/idCard/"
    //身份证照片名字
    val IDCARD_FRONT = "idcard_front"
    val IDCARD_BACK = "idcard_back"

    //调用照相机路径 Environment.getExternalStorageDirectory()+ "/gagayi/Photo_LJ/"
    val PHOTO_PATH = "$SD_PATH/derong/derong_photo/"
    //上传图片压缩路径
    val UPLOAD_PATH = "$SD_PATH/derong/upload/"
    // 缓存图片路径
    val BASE_IMAGE_CACHE = "$BASE_PATH/cache/images/"
    //apk下载路径
    var APP_DOWN_PATH = BASE_PATH + "apk"
    /*
	 * 登录用户一些基本信息
	 */
    var IS_LOGIN = false
    var TEMP_TOKEN = ""



    //{auth=1, firm_kind=1, head_pic=default, name=刘德华,
    // uscc=91370105123D5P8C9U, tel=158****0123, comname=超神学院有限公司, idno=410725****3616, cmpl_info=1}
    var USER_MAP: MutableMap<String, Any>? = null
    var RONGYUN_MAP: Map<String, Any>? = null
    var MONEY_INFO: MutableMap<String, Any>? = null
    @JvmField var ACCESS_TOKEN = ""
    @JvmField var REFRESH_TOKEN = ""

    var LOGIN_OUT = false
    //记录列表更新时间的文件名称
    var UPDATETIME = "JIUYUNDA_UPDATETIME"
    //系统类型
    val SYS_NAME = android.os.Build.BRAND
    // 手机名称
    val MOBILE_NAME = android.os.Build.MODEL
    //手机系统版本
    val SYS_VERSION = android.os.Build.VERSION.RELEASE
    //页面条数
    var PAGE_SIZE = 10
    //当前获得编码的秒
    var CURRENT_TIME = 0
    //当前图片循环的时间
    var PHOTO_CHANGE = 5.0

    var isNet = false

    var RMB = "¥"


    @JvmField var ALPHA = 0
    @JvmField var TOP_BAR_COLOR = R.color.top_bar_bg

    var APP_ID = "jpc4fc50f01af247bf"
    var ZONE_CODE = "1"
    var USER_KEY = ""


    @JvmField val SECOND_TIME_5000: Long = 5000
    @JvmField val SECOND_TIME_30 = 30 * 1000.toLong()
    @JvmField val SECOND_TIME_15 = 15 * 1000.toLong()

    /**	Android官网不建议使用enums，占用内存多（Enums often require more than twice as much memory as static constants.）。
     * Android中当你的App启动后系统会给App单独分配一块内存。App的DEX code、Heap以及运行时的内存分配都会在这块内存中。
     */
    //短信验证码界面   分类   根据不同分类，在相同界面进行不同操作）
    object CodeType {
        val CODE_KEY = "code_type"
        val CODE_PHONE_CHANGE = 0//更换手机号 老的手机号
        val CODE_NEW_PHONE_CHANGE = 1// 更换手机号 新的手机号
        val CODE_RESET_LOGIN_PASS = 2//重置登录密码
        val CODE_MODIFY_ORDER_PASS = 3//修改交易密码
        val CODE_INSTALL = 4//安装安全证书需要短信验证码
        val CODE_WANGYIN = 5//安装安全证书需要短信验证码
        val CODE_PHONE_REGIST = 6//注册

        val CODE_CARD_CHONGZHI = 7//绑定充值卡

        val CODE_CHONGZHI_MONEY = 8//充值钱，获取验证码

        val CODE_TRADE_PASS = 9//获取短信验证码  忘记交易密码

    }

    //人脸识别   分类   （根据不同分类，在相同界面进行不同操作）
    object FaceType {
        val FACE_KEY = "face_type"
        val FACE_AUTH = 10//人脸识别认证
        val FACE_INSTALL = 11//安装安全证书需要人脸认证    实名校验
        val FACE_CHECK_HUANKUAN = 12//    实名校验  还款
        val FACE_CHECK_APPLY = 13//    实名校验  申请额度
        val FACE_CHECK_BANK_PASS = 14//    实名校验  开户密码
        val FACE_CHECK_BANK_BIND = 15//    实名校验  绑定二类户

        val FACE_CHECK_INDEX = 16//  实名校验 首页申请额度等操作前，需要安装证书

        val FACE_SIGN_HETONG = 17//  签署合同需要人脸识别
        val FACE_PEOPLE_CHECK = 18//  共同借款人审核需要人脸识别
        val FACE_BORROW_MONEY = 19//  借款需要人脸识别
        val FACE_UPLOAD_USE = 20//  上传贷款用途凭证需要人脸验证
    }

    //人脸识别   分类   （根据不同分类，在相同界面进行不同操作）
    object ResultType {
        val RESULT_KEY = "result_type"
        val RESULT_APPLY_MONEY = 20//申请额度 成功
        val RESULT_PHONE_CHANGE = 21//更换手机号成功
        val RESULT_JIEKUAN = 22//借款申请成功
        val RESULT_HUANKUAN = 23//还款申请成功
    }

    //企业证书 提交结果 分类   （根据不同分类，在相同界面进行不同操作）
    object QiYeResultType {
        val RESULT_KEY = "qiye_result_type"
        val QIYE_CA_CHECK = 20//申请证书 审核中
        val QIYE_CA_FOUJUE = 21//申请证书 否决
        val QIYE_CA_TUIHUI = 22//申请证书 退回
    }


    /**--------------------------------------------------
     * 指纹识别的信息类
     */
    object FingerRecognize {

        val MSG_AUTH_SUCCESS = 100
        val MSG_AUTH_FAILED = 101
        val MSG_AUTH_ERROR = 102
        val MSG_AUTH_HELP = 103
    }


    /**--------------------------------------------------
     * app的基本信息
     */
    object UpdateAppConstans {

        //本地app版本号
        var VERSION_APP_CODE = 1
        //本地app中的version_name  当前应用的版本名称
        var VERSION_APP_NAME = "1.0.0"
        // 默认的网络最新程序版本号
        var VERSION_NET_CODE = -1
        //下载的APK的对应Id
        var VERSION_APK_ID = ""
        //新版本apk名称
        var VERSION_NET_APK_NAME = ""
        //版本编号
        var VERSION_NET_CODE_NAME = ""
        //新版本的apk存放地址
        var VERSION_NET_APK_URL = ""
        //apk更新信息
        var VERSION_NET_UPDATE_MSG = ""
        //是否强制更新
        var VERSION_UPDATE_FORCE = false
        var VERSION_MD5_CODE = ""

    }


    /**----------------------------------------------------
     * 一些SharedPreferences配置文件信息
     *
     * 时间：2014年12月29日 下午3:56:02
     *
     * 电子邮件：646869341@qq.com
     *
     * QQ：646869341
     */
    object SharedInfoConstans {

        //登录用户的融云信息
        var RONGYUN_DATA = "RONG_YUN"

        @JvmField var ACCESS_TOKEN = "ACCESS_TOKEN"
        @JvmField var REFRESH_TOKEN = "REFRESH_TOKEN"

        //记录列表更新时间的文件名称
        var UPDATETIME = "DELIVERY_UPDATETIME"
        //记录登录一些配置信息的文件名称
        var LOGIN_INFO = "LOGIN_INFO"
        //存放下拉列表存储的用户名称键
        var LOGIN_NAME_LIST = "LOGIN_NAME_LIST"
        var LOGIN_NAME_LIST2 = "LOGIN_NAME_LIST2"
        //记录是否正常退出的字段
        var LOGIN_OUT = "LOGIN_OUT"
        //登录用户账号
        var LOGIN_ACCOUNT = "LOGIN_ACCOUNT"
        //登录用户密码
        var LOGIN_PASSWORD = "LOGIN_PASSWORD"
        //登录用户头像
        var HEAD_IMG = "HEAD_IMG"
        //登录用到的userKey
        var LOGIN_USER_KEY = "LOGIN_USER_KEY"
        //用户的TAG
        var PUSH_TAG = "PUSH_TAG"
        //是否首次启动
        val IS_FIRST_START = "IS_FIRST_START1"
        //新的界面加载信息
        val LOGO_URL = "LOGO_URL"

        val WEIXIN_INFO = "WEIXIN_INFO"
        //登录方式   1  微信登录   2  手机号码登录
        val LOGIN_TYPE = "LOGIN_TYPE"
        //是否显示手势轨迹
        var SHOW_SHOUSHI = "SHOW_SHOUSHI"
        //手势密码加密后的结果
        var SHOUSHI_CODE = "SHOUSHI_CODE"

        //name code  json数据
        var NAME_CODE_DATA = "NAME_CODE_DATA"


    }

    //自定义广播
    object BroadcastReceiverAction {
        var MAIN_ACTIVITY = "MAIN_ACTIVITY"
        var NET_CHECK_ACTION = "NET_CHECK_ACTION"
        var FILE_TIP_ACTION = "FILE_TIP_ACTION"//上传附件数据标志
        var WEIXIN_PAY_ACTION = "WEIXIN_PAY_ACTION"
        var ZHENGSHU_UPDATE = "ZHENGSHU_UPDATE"//证书信息更新
        var USER_INFO_UPDATE = "USER_INFO_UPDATE"//用户信息更新
        var DAIBAN_INFO_UPDATE = "DAIBAN_INFO_UPDATE"//首页 待办列表数据更新
        var SHOUXIN_UPDATE = "SHOUXIN_UPDATE"//首页 授信状态信息变化更新
        var JIE_HUAN_UPDATE = "JIE_HUAN_UPDATE"// 还款  借款 成功后更新数据
        var QIAN_YUE_WY = "QIAN_YUE_WY"// 签约网银信息
        var OPEN_BANK = "OPEN_BANK"// 开户成功后  开户界面关闭
        var CAPAY_SUC = "CAPAY_SUC"// 证书支付费用匹配成功后返回操作  支付页面也要销毁

        var MONEY_UPDATE = "MONEY_UPDATE"//余额更新
        var BANKUPDATE_UPDATE = "BANKUPDATE_UPDATE"//银行卡列表更新

        var HTONGUPDATE = "HTONGUPDATE"//合同列表更新

        var TRADE_PASS_UPDATE = "TRADE_PASS_UPDATE"//交易密码更新

    }

    object MessageEventType {
        var DOWN_LOAD = 0

    }

}
