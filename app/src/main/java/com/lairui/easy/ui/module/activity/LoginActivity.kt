package com.lairui.easy.ui.module.activity

import android.annotation.SuppressLint
import android.content.*
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import cn.wildfire.chat.kit.ChatManagerHolder
import cn.wildfirechat.client.ConnectionStatus
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.ui.temporary.activity.*
import com.lairui.easy.utils.tool.*
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : BasicActivity(), CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.user_icon)
    lateinit var mUserIcon: ImageView
    @BindView(R.id.bottom_image)
    lateinit var mBottomImage: ImageView
    @BindView(R.id.edit_uid)
    lateinit var mEditUid: EditText
    @BindView(R.id.img_login_clear_uid)
    lateinit var mImgLoginClearUid: ImageView
    @BindView(R.id.arrow_view)
    lateinit var mArrowView: ImageView
    @BindView(R.id.name_lay)
    lateinit var mNameLay: RelativeLayout
    @BindView(R.id.img_login_clear_psw)
    lateinit var mImgLoginClearPsw: ImageView
    @BindView(R.id.edit_psw)
    lateinit var mEditPsw: EditText
    @BindView(R.id.btn_login)
    lateinit var mBtnLogin: Button
    @BindView(R.id.togglePwd)
    lateinit var mTogglePwd: ToggleButton
    @BindView(R.id.code_register)
    lateinit var mCodeRegister: TextView

    private lateinit var mShared: SharedPreferences//存放配置信息的文件


    private lateinit var mAccount: String
    private lateinit var mPassWord: String
    private val mUserList = ArrayList<MutableMap<String, Any>>()//本地存储的多个登录账号的信息

    private var mFirmKind = ""


    override val contentView: Int
        get() = R.layout.activity_login


    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.MAIN_ACTIVITY) {
                finish()
            }
        }
    }

    override fun isSupportSwipeBack(): Boolean {
        return false
    }


    override fun onNewIntent(intent: Intent?) {
        if (intent != null) {
            val bundle = intent.extras
            if (bundle != null) {

            }
        }
        super.onNewIntent(intent)
    }

    override fun init() {
        mInstance = this

        //getNameCodeInfo()


        // StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 60);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        SPUtils.put(this, MbsConstans.SharedInfoConstans.LOGIN_OUT, true)

        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.MAIN_ACTIVITY)
        registerReceiver(mBroadcastReceiver, intentFilter)

        val textViewUtil = TextViewUtils()
        val s = registTv.text.toString()
        textViewUtil.init(s,registTv)
        textViewUtil.setTextColor(s.indexOf("?")+1,s.length,ContextCompat.getColor(this,R.color.font_c))
        textViewUtil.setTextClick(s.indexOf("?")+1,s.length,object :TextViewUtils.ClickCallBack{
            override fun onClick() {
                val intent = Intent(this@LoginActivity, RegistActivity::class.java)
                startActivity(intent)
            }

        })
        textViewUtil.build()

        val account = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, ""]!!.toString() + ""
        mEditUid.setText(account)
        if (!UtilTools.empty(account)) {
            mEditUid.setSelection(account.length)
        }

        mEditUid.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (mEditUid.text.toString().isNotEmpty()) {
                    mImgLoginClearUid.visibility = View.VISIBLE
                    if (mEditPsw.text.toString().isNotEmpty()) {
                        mBtnLogin.isEnabled = true
                        mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.white))
                    } else {
                        mEditPsw.setText("")
                        mBtnLogin.isEnabled = false
                        mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.black99))
                    }
                } else {
                    mEditPsw.setText("")
                    mBtnLogin.isEnabled = false
                    mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.black99))
                    mImgLoginClearUid.visibility = View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mEditPsw.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (mEditPsw.text.toString().isNotEmpty()) {
                    mImgLoginClearPsw.visibility = View.VISIBLE
                    mBtnLogin.isEnabled = mEditUid.text.toString().isNotEmpty()
                    if (mBtnLogin.isEnabled == true){
                        mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.white))
                    }else{
                        mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.black99))
                    }
                } else {
                    mBtnLogin.isEnabled = false
                    mImgLoginClearPsw.visibility = View.INVISIBLE
                    mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.black99))
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })


        mEditUid.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (!UtilTools.empty(mEditUid.text.toString())) {
                    mImgLoginClearUid.visibility = View.VISIBLE
                    mImgLoginClearPsw.visibility = View.GONE
                } else {
                    mImgLoginClearUid.visibility = View.GONE
                }
            } else {
                mImgLoginClearUid.visibility = View.GONE
            }
        }
        mEditPsw.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (!UtilTools.empty(mEditPsw.text.toString())) {
                    mImgLoginClearPsw.visibility = View.VISIBLE
                    mImgLoginClearUid.visibility = View.GONE
                } else {
                    mImgLoginClearPsw.visibility = View.GONE
                }

            } else {
                mImgLoginClearPsw.visibility = View.GONE
            }
        }


        mTogglePwd.setOnCheckedChangeListener(this)
        mBtnLogin.isEnabled = false
        mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.black99))
       /* if (ChatManagerHolder.gChatManager != null && ChatManagerHolder.gChatManager.connectionStatus == ConnectionStatus.ConnectionStatusConnected) {
            ChatManagerHolder.gChatManager.disconnect(true)
            LogUtil.i("show", "断开聊天服务器")
        } else {
            LogUtil.i("show", "聊天服务器已断开")
        }*/

        MbsConstans.USER_MAP = null
        MbsConstans.RONGYUN_MAP = null
        MbsConstans.ACCESS_TOKEN = ""
        SPUtils.put(this@LoginActivity, MbsConstans.SharedInfoConstans.LOGIN_OUT, true)
        SPUtils.put(this@LoginActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "")


    }


    /**
     * 获取全局字典配置信息
     */
    fun getNameCodeInfo() {
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MethodUrl.nameCode, map)
    }


    /**
     * 是否显示密码
     */
    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            //显示密码
            mEditPsw.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            //隐藏密码
            mEditPsw.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }


    /**
     * 清空控件文本
     */
    private fun clearText(edit: EditText) {
        edit.setText("")
    }

    private fun loginAction() {
        if (UtilTools.empty(edit_uid.text.toString())) {
            showToastMsg("请输入手机号")
            mBtnLogin.isEnabled = true
            mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.white))
            return
        }
        if (UtilTools.empty(mEditPsw.text.toString())) {
            showToastMsg("请输入密码")
            mBtnLogin.isEnabled = true
            mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.white))
            return
        }

        mAccount = mEditUid.text.toString() + ""
        mPassWord = mEditPsw.text.toString() + ""

       /* if (!RegexUtil.isPhone(mAccount)) {
            showToastMsg("手机格式不正确")
            return
        }*/
        //String pass = AESHelper.encrypt(mPassWord, AESHelper.password);
        //val pass = RSAUtils.encryptContent(mPassWord!!, RSAUtils.publicKey)
      /*  try {
            map["clientId"] = ChatManagerHolder.gChatManager.clientId
            LogUtil.i("show","clientId:"+ChatManagerHolder.gChatManager.clientId)
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.i("show","clientId is null")
        }*/
        //String ss = AESHelper.decrypt(pass,AESHelper.password);

        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.LOGIN_ACTION
        map["phone"] = mAccount
        map["password"] = mPassWord
        map["version"] = MbsConstans.UpdateAppConstans.VERSION_APP_NAME
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.LOGIN_ACTION, map)
    }


    /**
     * 获取refreshToken方法
     */
    private fun getLoginRefreshToken() {
        val map = HashMap<String, Any>()
        map["access_token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.refreshToken, map)
    }

    /* */
    /**
     * 获取用户基本信息
     *//*
    private void getUserInfoAction() {
        Map<String, String> map = new HashMap<>();
        Map<String,String> mHeaderMap = new HashMap<String,String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.userInfo, map);
    }*/

    /**
     * 获取用户认证信息
     */
    private fun getAuthInfoAction() {
        //
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToMap(mHeaderMap, MethodUrl.userAuthInfo, map)
        //        mRequestPresenterImp.requestGetToRes(mHeaderMap, MethodUrl.userAuthInfo, map);
    }


    public override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()
    }

    @OnClick(R.id.left_back_lay,R.id.forgetPassWord,R.id.img_login_clear_uid, R.id.arrow_view, R.id.img_login_clear_psw, R.id.code_register, R.id.btn_login)
    fun onClick(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.left_back_lay ->{
                finish()
            }
            R.id.forgetPassWord -> {
                intent = Intent(this@LoginActivity, ResetPassWordActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_login -> {
                mBtnLogin.isEnabled = false
                mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.black99))
                loginAction()
            }
            R.id.img_login_clear_uid    //清除用户名
            -> clearText(mEditUid)
            R.id.img_login_clear_psw    //清除密码
            -> clearText(mEditPsw)
            R.id.arrow_view -> {
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
        //mBtnLogin.isEnabled = true
        //mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.white))
        when (mType) {

            MethodUrl.LOGIN_ACTION -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                    /*  if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)){
                                MbsConstans.ACCESS_TOKEN = "";
                            }
                    if (!UtilTools.empty(tData["ry_data"].toString() + "")) {
                        MbsConstans.RONGYUN_MAP = tData["ry_data"] as Map<String, Any>?
                        SPUtils.put(this@LoginActivity, MbsConstans.SharedInfoConstans.RONGYUN_DATA, JSONUtil.instance.objectToJson(MbsConstans.RONGYUN_MAP))
                    }*/
                    MbsConstans.ACCESS_TOKEN = (tData["data"] as MutableMap<String,Any>)["token"] as String
                    SPUtils.put(this@LoginActivity, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, mEditUid.text.toString() + "")
                    //SPUtils.put(LoginActivity.this, MbsConstans.SharedInfoConstans.LOGIN_PASSWORD,"别找了，没东西了");
                    SPUtils.put(this@LoginActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, MbsConstans.ACCESS_TOKEN)

                    intent = Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
            }

            //获取refreshToken返回结果
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                getAuthInfoAction()//获取用户认证信息
            }

        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mBtnLogin.isEnabled = true
        mBtnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.white))
        val msg = map["errmsg"]!!.toString() + ""
        showToastMsg(msg)
        //dealFailInfo(map,mType);
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeAllActivity()
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }


    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mInstance: LoginActivity
    }


}
