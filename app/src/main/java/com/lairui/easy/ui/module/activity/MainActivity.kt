package com.lairui.easy.ui.module.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.bean.MessageEvent
import com.lairui.easy.db.IndexData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.UpdateDialog
import com.lairui.easy.service.DownloadService
import com.lairui.easy.ui.module1.fragment.IndexFragment

import com.lairui.easy.ui.module5.fragment.PersonFragment
import com.lairui.easy.ui.temporary.fragment.ChatViewFragment
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.ui.module2.fragment.HangQingFragment
import com.lairui.easy.ui.module3.fragment.CeLueFragment
import com.lairui.easy.ui.module4.fragment.TradeFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : BasicActivity(), RequestView {
    private var unreadLabel: TextView? = null
    // textview for unread event message
    private var unreadAddressLable: TextView? = null
    private var mTabs: Array<ImageView?>? = null
    private var mTextViews: Array<TextView?>? = null
    private var mIndexFragment: IndexFragment? = null
    private var mTradeFragment: TradeFragment? = null
    private var mHangQingFragment: HangQingFragment? = null
    private var mCeLueFragment: CeLueFragment? = null
    private var mPersonFragment: PersonFragment? = null
    private var fragments: Array<Fragment>? = null

    private val mAutoScrollTextView: TextView? = null

    private var index: Int = 0
    private var currentTabIndex: Int = 0

    private var mIndexBottomLay: RelativeLayout? = null
    private var snackbar: Snackbar? = null

    private var mRequestTag = ""

    private var mDownIntent: Intent? = null
    private var msgIntent: Intent? = null

    private var mIndexData: IndexData? = null

    override val contentView: Int
        get() = R.layout.activity_main


    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE) {
                getUserInfoAction()
            }
        }
    }


    private var mHandler: Handler? = null
    private var isOnKeyBacking: Boolean = false

    private val mExitTime: Long = 0
    private val onBackTimeRunnable = Runnable {
        isOnKeyBacking = false
        if (snackbar != null) {
            snackbar!!.dismiss()
        }
    }


    private var mUpdateDialog: UpdateDialog? = null
    /**
     * 主界面不需要支持滑动返回，重写该方法永久禁用当前界面的滑动返回功能
     *
     * @return
     */

    override fun isSupportSwipeBack(): Boolean {
        return false
    }

    override fun init() {

        val eventBus = EventBus.getDefault()
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this)
        }

        // getNameCodeInfo();
        val fragmentList = supportFragmentManager.fragments
        if (fragmentList.size > 0) {
            for (fragment in fragmentList) {
                supportFragmentManager.beginTransaction().remove(fragment!!)
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)

        val intent = Intent()
        intent.action = MbsConstans.BroadcastReceiverAction.MAIN_ACTIVITY
        sendBroadcast(intent)

        SPUtils.put(this, MbsConstans.SharedInfoConstans.LOGIN_OUT, false)

        StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null)
        initView()

     /*   //启动下载Service
        mDownIntent = Intent(this, DownloadService::class.java)
        startService(mDownIntent)

        //启动SocketService
        msgIntent = Intent(this, MessageService::class.java)
        startService(msgIntent)*/


      /* WebSocketMsgListner.insance!!.listener = object :WebListener{
            override fun outputMsg(content: String) {
                Log.i("show", "获取推送消息：$content")
                val msgMap = JSONUtil.instance.jsonMap(content)
                //通知栏显示
                MyNotification.mInstance!!.showNotification(1,content)
               // MyNotification.mInstance.showNotification(i++, content)
            }

        }
*/
        mIndexData = IndexData.instance

        mHandler = Handler(Looper.getMainLooper())
        // initPush();
        mInstance = this

        getAppVersion()
        getUserInfoAction()
        getNameCodeInfo()

        //mAutoScrollTextView = findViewById(R.id.scroll_text_view);
        //mAutoScrollTextView.setSelected(true);


        //连接到聊天服务器
       /* val sp = getSharedPreferences("config", Context.MODE_PRIVATE)
        val id = sp.getString("id", null)
        val token = sp.getString("token", null)
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(token)) {
            if (UtilTools.empty(MbsConstans.RONGYUN_MAP)) {
                val s = SPUtils[this@MainActivity, MbsConstans.SharedInfoConstans.RONGYUN_DATA, ""].toString()
                MbsConstans.RONGYUN_MAP = JSONUtil.instance.jsonMap(s)
            }
            ChatManagerHolder.gChatManager.connect(MbsConstans.RONGYUN_MAP!!["id"].toString() + "", MbsConstans.RONGYUN_MAP!!["token"].toString() + "")
            sp.edit().putString("id", MbsConstans.RONGYUN_MAP!!["id"].toString() + "")
                    .putString("token", MbsConstans.RONGYUN_MAP!!["token"].toString() + "")
                    .apply()
        }

        ChatManagerHolder.gChatManager.addConnectionChangeListener { status ->
            when (status) {
                ConnectionStatus.ConnectionStatusTokenIncorrect, ConnectionStatus.ConnectionStatusLogout, ConnectionStatus.ConnectionStatusUnconnected -> {
                    val intent1 = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent1)
                    finish()
                }
            }
        }*/
    }

    private fun initView() {
        mIndexBottomLay = findViewById(R.id.btn_container_index)

        unreadLabel = findViewById<View>(R.id.unread_msg_number) as TextView
        unreadAddressLable = findViewById<View>(R.id.unread_address_number) as TextView
        mTabs = arrayOfNulls<ImageView>(5)
        mTabs!![0] = findViewById<View>(R.id.btn_conversation) as ImageView
        mTabs!![1] = findViewById<View>(R.id.btn_chat) as ImageView
        mTabs!![2] = findViewById<View>(R.id.btn_address_list) as ImageView
        mTabs!![3] = findViewById<View>(R.id.btn_cart) as ImageView
        mTabs!![4] = findViewById<View>(R.id.btn_setting) as ImageView

        mTextViews = arrayOfNulls<TextView>(5)
        mTextViews!![0] = findViewById<View>(R.id.one_tv) as TextView
        mTextViews!![1] = findViewById<View>(R.id.two_tv) as TextView
        mTextViews!![2] = findViewById<View>(R.id.three_tv) as TextView
        mTextViews!![3] = findViewById<View>(R.id.four_tv) as TextView
        mTextViews!![4] = findViewById<View>(R.id.five_tv) as TextView
        // select first tab
        mTabs!![0]?.isSelected = true
        mTextViews!![0]?.isSelected = true


        mIndexFragment = IndexFragment()
        mTradeFragment = TradeFragment()
        mHangQingFragment = HangQingFragment()
        mCeLueFragment = CeLueFragment()
        mPersonFragment = PersonFragment()
        fragments = arrayOf<Fragment>(mIndexFragment!!, mHangQingFragment!!, mCeLueFragment!!, mTradeFragment!!,mPersonFragment!!)
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, mIndexFragment!!)
                .show(mIndexFragment!!)
                .commitAllowingStateLoss()

    }

    override fun onResume() {
        super.onResume()
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

    /**
     * 获取全局字典配置信息
     */
    fun getNameCodeInfo() {
        mRequestTag = MethodUrl.nameCode
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.nameCode, map)
    }

    /**
     * 获取app更新信息
     */
    fun getAppVersion() {
        mRequestTag = MethodUrl.appVersion

        val map = HashMap<String, String>()
        map["appCode"] = MbsConstans.UPDATE_CODE
        map["osType"] = "android"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.appVersion, map)
    }

    /**
     * on tab clicked
     *
     * @param view
     */
    fun onTabClicked(view: View) {
        when (view.id) {
            R.id.btn_container_index -> index = 0
            R.id.btn_container_chat -> index = 1
            R.id.btn_container_get -> index = 2
            R.id.btn_container_return -> index = 3
            R.id.btn_container_person -> index = 4
        }

        if (currentTabIndex != index) {
            val trx = supportFragmentManager.beginTransaction()
            trx.hide(fragments!![currentTabIndex])
            if (!fragments!![index].isAdded) {
                trx.add(R.id.fragment_container, fragments!![index])
            } else {
                when (index) {
                    // mIndexFragment, mHangQingFragment, mRepaymentFragment,mPersonFragment
                    0 -> (fragments!![index] as IndexFragment).setBarTextColor()
                    1 -> (fragments!![index] as HangQingFragment).setBarTextColor()
                    2 -> (fragments!![index] as CeLueFragment).setBarTextColor()
                    3 -> (fragments!![index] as TradeFragment).setBarTextColor()
                    4 -> (fragments!![index] as PersonFragment).setBarTextColor()
                }
            }
            trx.show(fragments!![index]).commitAllowingStateLoss()
        }
        mTabs!![currentTabIndex]?.isSelected = false
        // set current tab selected
        mTabs!![index]?.isSelected = true

        mTextViews!![currentTabIndex]?.isSelected = false
        mTextViews!![index]?.isSelected = true
        currentTabIndex = index
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentTabIndex != 0) {
                mIndexBottomLay!!.performClick()
            } else {
                if (isOnKeyBacking) {
                    mHandler!!.removeCallbacks(onBackTimeRunnable)
                    if (snackbar != null) {
                        snackbar!!.dismiss()
                    }
                    closeAllActivity()
                    android.os.Process.killProcess(android.os.Process.myPid())
                    System.exit(0)
                    return true
                } else {
                    isOnKeyBacking = true
                    if (snackbar == null) {
                        snackbar = Snackbar.make(findViewById(R.id.fragment_container), "再次点击退出应用", Snackbar.LENGTH_SHORT)
                        snackbar!!.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
                    }
                    snackbar!!.show()
                    mHandler!!.postDelayed(onBackTimeRunnable, 2000)
                    return true
                }
            }
            return true
        }
        //拦截MENU按钮点击事件，让他无任何操作
        return if (keyCode == KeyEvent.KEYCODE_MENU) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

     override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        // {"id":1,"appCode":"phb","downUrl":"http://ys.51zhir.cn/app_api/apk/dr20190514.apk","fileName":"dr20190514.apk",
        // "fileSize":null,"isMust":"0","md5Code":"722f70f68c262e9c585f7dd800ae327c",
        // "memo":null,"osType":"android","verCode":"1","verName":"V1.0.1 Beta","verUpdateMsg":"版本更新内容"}
        when (mType) {
            MethodUrl.appVersion -> if (tData != null && !tData.isEmpty()) {
                //网络版本号
                MbsConstans.UpdateAppConstans.VERSION_NET_CODE = UtilTools.getIntFromStr(tData["verCode"]!!.toString() + "")
                //MbsConstans.UpdateAppConstans.VERSION_NET_CODE = 3;
                //网络下载url
                MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL = tData["downUrl"]!!.toString() + ""
                //MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
                //网络版本名称
                MbsConstans.UpdateAppConstans.VERSION_NET_APK_NAME = tData["verName"]!!.toString() + ""
                //网络MD5值
                MbsConstans.UpdateAppConstans.VERSION_MD5_CODE = tData["md5Code"]!!.toString() + ""
                //本次更新内容
                MbsConstans.UpdateAppConstans.VERSION_NET_UPDATE_MSG = tData["verUpdateMsg"]!!.toString() + ""

                val mustUpdate = tData["isMust"]!!.toString() + ""
                MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE = mustUpdate != "0"
                showUpdateDialog()
            }
            MethodUrl.nameCode -> {
                val result = tData!!["result"]!!.toString() + ""
                SPUtils.put(this@MainActivity, MbsConstans.SharedInfoConstans.NAME_CODE_DATA, result)
            }
            MethodUrl.userInfo//用户信息 //{auth=1, firm_kind=0, head_pic=default, name=刘英超, tel=151****3298, idno=4107****3616, cmpl_info=0}
            -> {
                MbsConstans.USER_MAP = tData
                SPUtils.put(this@MainActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.instance.objectToJson(MbsConstans.USER_MAP!!))
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData!!["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                for (stag in mRequestTagList) {
                    when (stag) {
                        MethodUrl.userInfo -> getUserInfoAction()
                        MethodUrl.nameCode//{
                        -> getNameCodeInfo()
                    }
                    mRequestTagList = ArrayList()
                    break
                }
            }
        }//showUpdateDialog();
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.appVersion -> {
            }
        }
        dealFailInfo(map, mType)
    }

    private fun showUpdateDialog() {
        if (MbsConstans.UpdateAppConstans.VERSION_NET_CODE > MbsConstans.UpdateAppConstans.VERSION_APP_CODE) {
            mUpdateDialog = UpdateDialog(this, true)
            val onClickListener = View.OnClickListener { v ->
                when (v.id) {
                    R.id.cancel -> if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                        showToastMsg("本次升级为必须更新，请您更新！")
                    } else {
                        mUpdateDialog!!.dismiss()
                    }
                    R.id.confirm -> {
                        PermissionsUtils.requsetRunPermission(this@MainActivity, object : RePermissionResultBack {
                            override fun requestSuccess() {
                                mUpdateDialog!!.progressLay!!.visibility = View.VISIBLE
                                DownloadService.downNewFile(MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL, 1003,
                                        MbsConstans.UpdateAppConstans.VERSION_NET_APK_NAME, MbsConstans.UpdateAppConstans.VERSION_MD5_CODE, this@MainActivity)
                            }

                            override fun requestFailer() {

                            }
                        }, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)

                        if (!MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                            mUpdateDialog!!.dismiss()
                        }
                    }
                }
            }
            mUpdateDialog!!.setCanceledOnTouchOutside(false)
            mUpdateDialog!!.setCancelable(false)
            var ss = ""
            if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                ss = "必须更新"
            } else {
                ss = "建议更新"
            }
            mUpdateDialog!!.onClickListener = onClickListener
            mUpdateDialog!!.initValue("检查新版本($ss)", "更新内容:\n" + MbsConstans.UpdateAppConstans.VERSION_NET_UPDATE_MSG)
            mUpdateDialog!!.show()

            if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                mUpdateDialog!!.setCancelable(false)
                mUpdateDialog!!.tv_cancel!!.isEnabled = false
                mUpdateDialog!!.tv_cancel!!.visibility = View.GONE
            } else {
                mUpdateDialog!!.setCancelable(true)
                mUpdateDialog!!.tv_cancel!!.isEnabled = true
                mUpdateDialog!!.tv_cancel!!.visibility = View.VISIBLE
            }
            mUpdateDialog!!.progressLay!!.visibility = View.GONE
            DownloadService.mProgressBar = mUpdateDialog!!.progressBar
            DownloadService.mTextView = mUpdateDialog!!.prgText
        }
    }


    /**
     * DownLoadManager 下载时EventBus更新UI
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateProgress(event: MessageEvent) {
        when (event.type) {
            0 -> {
                val map = event.message
                Log.i("show", "eventBus:" + map!!["size"]!!)
                mUpdateDialog!!.update(map["max"]!!.toString() + "", map["size"]!!.toString() + "", "下载进度: " + map["progress"] + "")
            }
        }
    }

    /**
     * activity销毁前触发的方法
     */
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * activity恢复时触发的方法
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

    }

    override fun onPause() {
        super.onPause()
        mIsRefresh = false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
        val eventBus = EventBus.getDefault()
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this)
        }
    }

    companion object {
        lateinit var mInstance: MainActivity
    }


}
