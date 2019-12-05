package com.lairui.easy.ui.module.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Message
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.db.DataBaseHelper
import com.lairui.easy.mywidget.view.CountDownProgressView
import com.lairui.easy.utils.tool.AppUtil
import com.lairui.easy.utils.tool.HandlerUtil
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.ButterKnife


/**
 * 初始化程序界面
 */
@Suppress("UNREACHABLE_CODE")
class SplashActivity : BasicActivity() {

    @BindView(R.id.splash_view)
    lateinit var mMainView: ImageView
    @BindView(R.id.splash_logo)
    lateinit var mSplashLogo: ImageView
    @BindView(R.id.splash_loading_item)
    lateinit var mSplashImageView: ImageView
    @BindView(R.id.relativeLayout1)
    lateinit var mRelativeLayout1: RelativeLayout
    @BindView(R.id.version_name)
    lateinit var mNameView: TextView
    @BindView(R.id.textView)
    lateinit var textView: TextView
    @BindView(R.id.countdownProgressView)
    lateinit var mCountdownProgressView: CountDownProgressView
    @BindView(R.id.tiaoguo_lay)
    lateinit var mTiaoGuoLay: LinearLayout


    private lateinit var mRotateAnimation: Animation

    private lateinit var mShared: SharedPreferences//存放配置信息的文件

    private  var dataBaseHelper: DataBaseHelper? = null
    // 声明控件对象
    private var count = 3
    private lateinit var animation: Animation
    private val isJump = false
    private val isClick = false


    override val contentView: Int
        get() = R.layout.activity_splash


    override fun isSupportSwipeBack(): Boolean {
        return false
    }

    override fun init() {
        ButterKnife.bind(this)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            MbsConstans.ALPHA = 100
            MbsConstans.TOP_BAR_COLOR = R.color.top_bar_bg
            StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null)
        } else {
            MbsConstans.ALPHA = 0
            MbsConstans.TOP_BAR_COLOR = R.color.top_bar_bg
            StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null)
        }
        mShared = getSharedPreferences(MbsConstans.SharedInfoConstans.LOGIN_INFO, Context.MODE_PRIVATE)
        AppUtil.getInstance(this).appVersion
        setupView()
        initView()
        //loadImage();
        //imageTask();

    }


    private fun setupView() {
        animation = AnimationUtils.loadAnimation(this, R.anim.animation_text)
        mTiaoGuoLay.setOnClickListener { HandlerUtil.init(this@SplashActivity).postMessage(null, null, 0, 0) }
        mNameView.text = MbsConstans.UpdateAppConstans.VERSION_APP_NAME
        mNameView.visibility = View.GONE
        textView.text = count.toString() + ""

        mCountdownProgressView.setTimeMillis(3000)
        mCountdownProgressView.start()
        mCountdownProgressView.setProgressListener(object : CountDownProgressView.OnProgressListener {
            override fun onProgress(progress: Int) {
                if (progress <= 0) {
                    HandlerUtil.init(this@SplashActivity).postMessage(null, null, 0, 0)
                }
            }
        })
      /*  mCountdownProgressView.setProgressListener { progress ->
            progress as Int
            if (progress <=0 ) {
                HandlerUtil.init(this@SplashActivity).postMessage(null, null, 0, 0)
            }

        }*/
        mCountdownProgressView.setOnClickListener { HandlerUtil.init(this@SplashActivity).postMessage(null, null, 0, 0) }
    }

    private fun initView() {
        initSqlit()
        initAnimation()
        // HandlerUtil.init(this).postMessage(null, null, 500, count);

        HandlerUtil.doMessage(object : HandlerUtil.MessageCallBack {
            override fun runHandleMessage(msg: Message) {
                if (msg.what == 0) {
                    val bb = SPUtils[this@SplashActivity, MbsConstans.SharedInfoConstans.LOGIN_OUT, true] as Boolean
                    val intent: Intent
                    val code = SPUtils[this@SplashActivity, MbsConstans.SharedInfoConstans.IS_FIRST_START, ""]!!.toString() + ""
                    //if (code == MbsConstans.UpdateAppConstans.VERSION_APP_CODE.toString() + "") {
                        if (bb) {
                            intent = Intent(this@SplashActivity, LoginActivity::class.java)
                            this@SplashActivity.startActivity(intent)
                            this@SplashActivity.finish()
                        } else {
                            MbsConstans.ACCESS_TOKEN = SPUtils[this@SplashActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""]!!.toString()
                            MbsConstans.REFRESH_TOKEN = SPUtils[this@SplashActivity, MbsConstans.SharedInfoConstans.REFRESH_TOKEN, ""]!!.toString()
                            val s = SPUtils[this@SplashActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, ""]!!.toString()
                            MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
                            if (MbsConstans.USER_MAP == null || MbsConstans.USER_MAP!!.isEmpty()) {
                                intent = Intent(this@SplashActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
                                intent = Intent(this@SplashActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                  /*  } else {
                        intent = Intent(this@SplashActivity, GuideViewPageActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out)
                        finish()
                    }*/
                } else {
                    count--
                    textView.text = count.toString() + ""
                    animation.reset()
                    textView.startAnimation(animation)
                    HandlerUtil.init(this@SplashActivity).postMessage(null, null, 1000, count)
                }


            }
        })

    }

    private fun initAnimation() {

        /*	new Handler(){
,
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String code = mShared.getString(MbsConstans.SharedInfoConstans.IS_FIRST_START, "");
				Intent intent ;
				if (code.equals(MbsConstans.UpdateAppConstans.VERSION_APP_CODE+"")) {
					intent = new Intent(SplashActivity.this,MainActivity.class);
					startActivity(intent);
					finish();
					//loadImage();
				}else{
					intent = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
				}

			}
		}.sendEmptyMessageDelayed(0, 5000);*/


        /*float pivotValue = 0.5f;
		float toDegree = 360.0f;
		mRotateAnimation = new RotateAnimation(toDegree, toDegree, Animation.RELATIVE_TO_SELF, pivotValue,
				Animation.RELATIVE_TO_SELF, pivotValue);
		mRotateAnimation.setFillAfter(true);
		mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
		//mRotateAnimation.setRepeatCount(Animation.INFINITE);
		//mRotateAnimation.setRepeatMode(Animation.RESTART);
		mRotateAnimation.setDuration(1000);
		mSplashImageView.startAnimation(mRotateAnimation);

		mRotateAnimation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				String code = mShared.getString(MbsConstans.SharedInfoConstans.IS_FIRST_START, "");
				Intent intent ;
				if (code.equals(MbsConstans.UpdateAppConstans.VERSION_APP_CODE+"")) {
					intent = new Intent(SplashActivity.this, MainActivity.class);
				}else{
					intent = new Intent(SplashActivity.this, GuideViewPageActivity.class);
				}
				startActivity(intent);
				overridePendingTransition(R.anim.splash_fade_in,R.anim.splash_fade_out);
				SplashActivity.this.finish();
			}
		});*/
    }

    private fun initSqlit() {
        dataBaseHelper = DataBaseHelper(this, MbsConstans.DATABASE_NAME, null, 1)
        dataBaseHelper?.writableDatabase
        val dbFile = this.getDatabasePath(MbsConstans.DATABASE_NAME)
        if (!dbFile.exists()) {

        } else {

        }

    }


    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    public override fun onDestroy() {
        super.onDestroy()
        HandlerUtil.release()
    }

    private fun getCount() {
        count--
        textView!!.text = count.toString() + ""
        animation!!.reset()
        textView!!.startAnimation(animation)
    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {

    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun disimissProgress() {

    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }



    companion object {
        /**
         * 旋转动画的时间
         */
        val ROTATION_ANIMATION_DURATION = 1200
        /**
         * 动画插值
         */
         val ANIMATION_INTERPOLATOR: Interpolator = LinearInterpolator()
    }
}

