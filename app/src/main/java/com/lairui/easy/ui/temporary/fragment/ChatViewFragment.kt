package com.lairui.easy.ui.temporary.fragment

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.OnClick
import cn.wildfire.chat.kit.ChatManagerHolder
import cn.wildfire.chat.kit.IMConnectionStatusViewModel
import cn.wildfire.chat.kit.IMServiceStatusViewModel
import cn.wildfire.chat.kit.WfcScheme
import cn.wildfire.chat.kit.contact.ContactListFragment
import cn.wildfire.chat.kit.contact.newfriend.SearchUserActivity
import cn.wildfire.chat.kit.conversation.CreateConversationActivity
import cn.wildfire.chat.kit.conversationlist.ConversationListFragment
import cn.wildfire.chat.kit.group.GroupInfoActivity
import cn.wildfire.chat.kit.group.GroupListFragment
import cn.wildfire.chat.kit.qrcode.ScanQRCodeActivity
import cn.wildfire.chat.kit.search.SearchResult
import cn.wildfire.chat.kit.search.SearchResultAdapter
import cn.wildfire.chat.kit.search.SearchViewModel
import cn.wildfire.chat.kit.search.SearchableModule
import cn.wildfire.chat.kit.search.module.ContactSearchModule
import cn.wildfire.chat.kit.search.module.ConversationSearchModule
import cn.wildfire.chat.kit.search.module.GroupSearchViewModule
import cn.wildfire.chat.kit.user.UserInfoActivity
import cn.wildfire.chat.kit.user.UserViewModel
import cn.wildfirechat.client.ConnectionStatus
import cn.wildfirechat.remote.ChatManager
import com.androidkun.xtablayout.XTabLayout
import com.lairui.easy.R
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.ui.module5.adapter.MyViewPagerAdapter
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.*
import com.flyco.dialog.utils.CornerUtils
import com.jaeger.library.StatusBarUtil
import com.king.zxing.Intents
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.fragment_chat_view.*
import q.rorbin.badgeview.QBadgeView
import java.util.*

class ChatViewFragment :BasicFragment(),RequestView,ReLoadingData, ViewPager.OnPageChangeListener{


    @BindView(R.id.rightImg)
    lateinit var rightImg: ImageView
    @BindView(R.id.rightLay)
    lateinit var rightLay: LinearLayout
    @BindView(R.id.ivSearch)
    lateinit var ivSearch: ImageView
    @BindView(R.id.etSearch)
    lateinit var etSearch: EditText
    @BindView(R.id.tabLayout)
    lateinit var tabLayout: XTabLayout
    @BindView(R.id.pageView)
    lateinit var pageView: PageView
    @BindView(R.id.contentLayout)
    lateinit var contentLayout: LinearLayout
    @BindView(R.id.refreshListview)
    lateinit var refreshListview: RecyclerView
    @BindView(R.id.refreshLayout)
    lateinit var refreshLayout: SmartRefreshLayout
    @BindView(R.id.viewPager)
    lateinit var viewPager: ViewPager


    var mFraments = ArrayList<Fragment>()
    //会话界面
    var conversationListFragment :ConversationListFragment? = null
    //联系人界面
    var contactListFragment: ContactListFragment? = null
    //群组界面
    var groupListFragment: GroupListFragment? = null

    var mAnimUtil:AnimUtil? = null

    //检索
    var modules: MutableList<SearchableModule<*, *>> = ArrayList()
    var mAdapter: SearchResultAdapter? = null
    var searchViewModel:SearchViewModel? = null


    lateinit var module0:SearchableModule<*,*>
    lateinit var module1:SearchableModule<*,*>
    lateinit var module2:SearchableModule<*,*>

    //好友申请消息
    var unreadFriendRequestBadgeView: QBadgeView? = null

    val REQUEST_CODE_SCAN_QR_CODE = 100
    val REQUEST_IGNORE_BATTERY_CODE = 101

    var isInitialized = false

    override val layoutId: Int
        get() = R.layout.fragment_chat_view

    private val imStatusLiveDataObserver = Observer { status: Boolean ->
        if (status && !isInitialized) {
            isInitialized = true
        }
    }

    private var searchResultObserver = Observer<SearchResult> { result: SearchResult? ->
        if (result?.result == null || result.result.isEmpty()){
            pageView.visibility = View.GONE
            tabLayout.visibility = View.VISIBLE
            viewPager.visibility = View.VISIBLE
        }else{
            pageView.visibility = View.VISIBLE
            tabLayout.visibility = View.GONE
            viewPager.visibility = View.GONE
            if (mAdapter == null){
                mAdapter = SearchResultAdapter(this)
                refreshListview.layoutManager = LinearLayoutManager(activity)
                refreshListview.adapter = mAdapter
            }
            mAdapter!!.submitSearResult(result)
        }
    }


    override fun init() {
        setBarTextColor()
        initView()

        mAnimUtil = AnimUtil()
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchViewModel!!.resultLiveData.observeForever(searchResultObserver)

        //搜索会话列表Module
        module0 = ConversationSearchModule()
        //搜索联系人Module
        module1 = ContactSearchModule()
        //搜索群组Module
        module2 = GroupSearchViewModule()

        refreshLayout.setOnRefreshListener(object : OnRefreshListener{
            override fun onRefresh(refreshLayout: RefreshLayout?) {
                isConnncted()

                when(tabLayout.selectedTabPosition){
                    0 ->{
                        if (conversationListFragment != null) {
                            conversationListFragment!!.reloadConversations()
                        }
                    }
                    1 ->{
                        if (contactListFragment != null && contactListFragment!!.contactViewModel != null) {
                            contactListFragment!!.contactViewModel.reloadContact()
                            contactListFragment!!.contactViewModel.reloadFriendRequestStatus()
                        }
                    }
                    2 ->{
                        if (groupListFragment != null) {
                            groupListFragment!!.reloadGroupList()
                        }
                    }

                }

                refreshLayout!!.finishRefresh()
            }

        })

        //禁止上拉加载：
        refreshLayout.isEnableLoadMore = false
        //使上拉加载具有弹性效果：
        refreshLayout.isEnableAutoLoadMore = false
        //禁止越界拖动：
        //refreshLayout.setEnableOverScrollDrag(false);
        //禁止越界拖动：
        //refreshLayout.setEnableOverScrollDrag(false);
        refreshLayout.isNestedScrollingEnabled = true


    }



    private fun initView() {

        //contentLayout?.let { pageView!!.setContentView(it) }
        //pageView!!.showContent()
        pageView.setContentView(contentLayout)
        pageView.showContent()
        //tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("近期聊天"))
        tabLayout.addTab(tabLayout.newTab().setText("我的好友"))
        tabLayout.addTab(tabLayout.newTab().setText("我的群组"))

        conversationListFragment = ConversationListFragment()
        contactListFragment = ContactListFragment()
        groupListFragment = GroupListFragment()

        mFraments.add(conversationListFragment!!)
        mFraments.add(contactListFragment!!)
        mFraments.add(groupListFragment!!)

        viewPager.adapter = MyViewPagerAdapter(childFragmentManager,mFraments)
        viewPager.addOnPageChangeListener(XTabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab) {
                viewPager.currentItem = tab.position
                if(tab.position == 1){
                    contactListFragment!!.showQuickIndexBar(true)
                }

            }
            override fun onTabUnselected(tab: XTabLayout.Tab) {}
            override fun onTabReselected(tab: XTabLayout.Tab) {}
        })

        etSearch.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 0){
                    if(mAdapter != null){
                        mAdapter!!.reset()
                    }
                    modules.clear()

                    when(viewPager.currentItem){
                        0 -> modules.add(module0)
                        1 -> modules.add(module1)
                        2 -> modules.add(module2)
                        else ->{
                            modules.add(module0)
                            modules.add(module1)
                            modules.add(module2)
                        }
                    }
                    searchViewModel!!.search(s.toString(),modules)
                }else{
                    pageView.visibility = View.GONE
                    viewPager.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                }
            }

        })

    }

    override fun onResume() {
        super.onResume()
        isConnncted()

            if (conversationListFragment != null) {
                conversationListFragment!!.reloadConversations()
            }

            if (contactListFragment != null && contactListFragment!!.contactViewModel != null) {
                contactListFragment!!.contactViewModel.reloadContact()
                contactListFragment!!.contactViewModel.reloadFriendRequestStatus()
            }

            if (groupListFragment != null) {
                groupListFragment!!.reloadGroupList()
            }

    }

    @OnClick(R.id.rightImg, R.id.rightLay, R.id.notice_layout)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.rightLay -> initPopupWindow()
            R.id.rightImg -> initPopupWindow()
            R.id.notice_layout -> {

            }
        }
    }


    public fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity)
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }

    override fun reLoadingData() {

    }





    fun isConnncted() {
        val imServiceStatusViewModel = ViewModelProviders.of(this)[IMServiceStatusViewModel::class.java]
        imServiceStatusViewModel.imServiceStatusLiveData().observe(this, imStatusLiveDataObserver)
        val connectionStatusViewModel = ViewModelProviders.of(this)[IMConnectionStatusViewModel::class.java]
        connectionStatusViewModel.connectionStatusLiveData().observe(this, Observer { status: Int ->
            if (status == ConnectionStatus.ConnectionStatusTokenIncorrect || status == ConnectionStatus.ConnectionStatusSecretKeyMismatch || status == ConnectionStatus.ConnectionStatusRejected || status == ConnectionStatus.ConnectionStatusLogout) {
                LogUtil.i("show", "重新连接聊天服务器")
                ChatManager.Instance().disconnect(true)
                //重新连接登录
                if (UtilTools.empty(MbsConstans.RONGYUN_MAP)) {

                    val s: String = SPUtils.get(activity as Context, MbsConstans.SharedInfoConstans.RONGYUN_DATA, "").toString()
                    MbsConstans.RONGYUN_MAP = JSONUtil.instance.jsonMap(s)
                }
                ChatManagerHolder.gChatManager.connect(MbsConstans.RONGYUN_MAP!!.get("id").toString() + "", MbsConstans.RONGYUN_MAP!!.get("token").toString() + "")
            } else {
                LogUtil.i("show", "已经连接聊天服务器!!!")
            }
        })
    }
    private var popView: View? = null
    private var mConditionDialog: PopupWindow? = null
    private var bright = false

    private fun initPopupWindow() {
        var mNagView: LinearLayout
        if (mConditionDialog == null) {
            popView = LayoutInflater.from(activity).inflate(R.layout.chat_add_dialog, null)
            mConditionDialog = PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mConditionDialog!!.isClippingEnabled = false
            initConditionDialog(popView)
            //int screenWidth=UtilTools.getScreenWidth(getActivity());
            //int screenHeight=UtilTools.getScreenHeight(getActivity());
            mConditionDialog!!.width = UtilTools.dip2px(activity!!, 150)
            mConditionDialog!!.height = UtilTools.dip2px(activity!!, 180)
            //设置background后在外点击才会消失
            mConditionDialog!!.setBackgroundDrawable(CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), UtilTools.dip2px(activity!!, 5).toFloat()))
            //mConditionDialog.setOutsideTouchable(true);// 设置可允许在外点击消失
            //自定义动画
            //mConditionDialog.setAnimationStyle(R.style.PopupAnimation);
            mConditionDialog!!.animationStyle = android.R.style.Animation_Activity //使用系统动画
            mConditionDialog!!.update()
            mConditionDialog!!.isTouchable = true
            mConditionDialog!!.isFocusable = true
            //popView.requestFocus();//pop设置不setBackgroundDrawable情况，把焦点给popView，添加popView.setOnKeyListener。可实现点击外部不消失，点击反键才消失
            //mConditionDialog.showAtLocation(mCityTv, Gravity.TOP|Gravity.RIGHT, 0, 0); //设置layout在PopupWindow中显示的位置
            //mConditionDialog.showAtLocation(getActivity().getWindow().getDecorView(),  Gravity.TOP|Gravity.RIGHT, 0, 0);
            mConditionDialog!!.showAsDropDown(divide_line, -UtilTools.dip2px(activity!!, 20), 0, Gravity.RIGHT)
            toggleBright()
            mConditionDialog!!.setOnDismissListener { toggleBright() }
        } else { //mConditionDialog.showAtLocation(getActivity().getWindow().getDecorView(),  Gravity.TOP|Gravity.RIGHT, 0, 0);
            mConditionDialog!!.showAsDropDown(divide_line, -UtilTools.dip2px(activity!!, 20), 0, Gravity.RIGHT)
            toggleBright()
        }
    }

    private fun toggleBright() { //三个参数分别为： 起始值 结束值 时长 那么整个动画回调过来的值就是从0.5f--1f的
        mAnimUtil!!.setValueAnimator(0.7f, 1f, 300)
        mAnimUtil!!.addUpdateListener(object : AnimUtil.UpdateListener {
            override fun progress(progress: Float) { //此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                val bgAlpha = if (bright) progress else 1.7f - progress //三目运算，应该挺好懂的。
                //bgAlpha = progress;//三目运算，应该挺好懂的。
                bgAlpha(bgAlpha) //在此处改变背景，这样就不用通过Handler去刷新了。
            }
        })
        mAnimUtil!!.addEndListner(object : AnimUtil.EndListener {
            override fun endUpdate(animator: Animator) { //在一次动画结束的时候，翻转状态
                bright = !bright
            }

        })
        mAnimUtil!!.startAnimator()
    }

    private fun bgAlpha(alpha: Float) {
        val lp = activity!!.window.attributes
        lp.alpha = alpha // 0.0-1.0
        activity!!.window.attributes = lp
    }


    var group_chat_lay: LinearLayout? = null
    var add_friend_lay: LinearLayout? = null
    var sacn_lay: LinearLayout? = null

    private fun initConditionDialog(view: View?) {
        group_chat_lay = view!!.findViewById(R.id.group_chat_lay)
        add_friend_lay = view.findViewById(R.id.add_friend_lay)
        sacn_lay = view.findViewById(R.id.scan_lay)
        val onClickListener = View.OnClickListener { v ->
            val intent: Intent
            when (v.id) {
                R.id.group_chat_lay -> {
                    mConditionDialog!!.dismiss()
                    intent = Intent(activity, CreateConversationActivity::class.java)
                    startActivity(intent)
                }
                R.id.add_friend_lay -> {
                    mConditionDialog!!.dismiss()
                    intent = Intent(activity, SearchUserActivity::class.java)
                    startActivity(intent)
                }
                R.id.scan_lay -> {
                    mConditionDialog!!.dismiss()
                    PermissionsUtils.requsetRunPermission(activity as Context, object : RePermissionResultBack {
                        override fun requestSuccess() {
                            startActivityForResult(Intent(activity, ScanQRCodeActivity::class.java),REQUEST_CODE_SCAN_QR_CODE)
                        }

                        override fun requestFailer() {
                            Toast.makeText(activity, "相机权限授权失败", Toast.LENGTH_LONG).show()
                        }
                    }, Permission.Group.STORAGE, Permission.Group.CAMERA)
                }
            }
        }
        group_chat_lay!!.setOnClickListener(onClickListener)
        add_friend_lay!!.setOnClickListener(onClickListener)
        sacn_lay!!.setOnClickListener(onClickListener)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
           REQUEST_CODE_SCAN_QR_CODE -> if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra(Intents.Scan.RESULT)
                onScanPcQrCode(result)
            }
           REQUEST_IGNORE_BATTERY_CODE -> if (resultCode == Activity.RESULT_CANCELED) {
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onScanPcQrCode(qrcode: String) {
        val prefix = qrcode.substring(0, qrcode.lastIndexOf('/') + 1)
        val value = qrcode.substring(qrcode.lastIndexOf("/") + 1)
        when (prefix) {
            WfcScheme.QR_CODE_PREFIX_PC_SESSION -> {
            }
            WfcScheme.QR_CODE_PREFIX_USER -> showUser(value)
            WfcScheme.QR_CODE_PREFIX_GROUP -> joinGroup(value)
            WfcScheme.QR_CODE_PREFIX_CHANNEL -> {
            }
            else -> {
            }
        }
    }

    private fun showUser(uid: String) {
        val userViewModel = ViewModelProviders.of(this)[UserViewModel::class.java]
        val userInfo = userViewModel.getUserInfo(uid, true) ?: return
        val intent = Intent(activity, UserInfoActivity::class.java)
        intent.putExtra("userInfo", userInfo)
        startActivity(intent)
    }

    private fun joinGroup(groupId: String) {
        val intent = Intent(activity, GroupInfoActivity::class.java)
        intent.putExtra("groupId", groupId)
        startActivity(intent)
    }


    override fun onPageScrollStateChanged(state: Int) {
        LogUtil.i("show","onPageScrollStateChanged(state: Int)")
        if (state != ViewPager.SCROLL_STATE_IDLE) { //滚动过程中隐藏快速导航条
            LogUtil.i("show","onPageScrollStateChanged(false)")
            contactListFragment!!.showQuickIndexBar(false)
        } else {
            LogUtil.i("show","onPageScrollStateChanged(true)")
            contactListFragment!!.showQuickIndexBar(true)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }


    override fun onDestroy() {
        super.onDestroy()
        searchViewModel!!.resultLiveData.removeObserver(searchResultObserver)
    }



}