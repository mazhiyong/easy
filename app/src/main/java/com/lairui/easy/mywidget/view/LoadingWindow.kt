package com.lairui.easy.mywidget.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.AnimationDrawable

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.lairui.easy.R
import com.lairui.easy.api.RxApiManager
import com.lairui.easy.utils.tool.UtilTools


/**
 *
 *
 * @Description:   加载等待动画
 */

class LoadingWindow(private val mContext: Context, style: Int) : Dialog(mContext, style), DialogInterface.OnKeyListener {

    private var mImageView: ImageView? = null
    private var mTextView: TextView? = null
    private val mRotateAnimation: Animation? = null


    internal var aniDrawable: AnimationDrawable? = null

    init {
        initView()
        this.setOnKeyListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if(savedInstanceState == null){
            super.onCreate(Bundle())
        }else{
            super.onCreate(savedInstanceState)
        }

        initView()
    }


    fun setTipText(tipStr: String) {
        mTextView!!.visibility = View.VISIBLE
        mTextView!!.text = tipStr
    }


    fun initView() {

        val view = layoutInflater.inflate(R.layout.loading_view, null)
        mImageView = view.findViewById<View>(R.id.load_imageview) as ImageView
        mTextView = view.findViewById<View>(R.id.loading_text_tv) as TextView

        mTextView!!.text = "加载中，请稍后..."

        val options = RequestOptions()
                .centerCrop()
                .circleCrop()//设置圆形
                .placeholder(R.color.body_bg) //占位图
                .error(R.color.body_bg)       //错误图
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(mContext)
                .asGif()
                .load(R.drawable.loding)
                .apply(options)
                .into(mImageView!!)

        /*Glide.with(mContext)
				.asGif()
				.load(R.drawable.loding)
				.into(mImageView);*/




        this.setContentView(view, LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT))


        val window = this.window

        val lp = window!!.attributes
        lp.x = 0
        lp.y = -UtilTools.dip2px(mContext, 40)
        lp.dimAmount = 0.0f   // 背景没有黑色
        this.window!!.attributes = lp
        // 设置点击外围解散
        this.setCanceledOnTouchOutside(false)
        this.setCancelable(true)
        initAnimation()

        //		window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        //				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);//不获得焦点
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)//背景不变暗
        //window.setGravity(Gravity.CENTER);


    }


    @SuppressLint("NewApi")
    private fun copySystemUiVisibility() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            window!!.decorView.systemUiVisibility = (mContext as Activity).window.decorView.systemUiVisibility
        }
    }

    override fun show() {
        // Set the dialog to not focusable.
        this.window!!.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        //copySystemUiVisibility();
        // Show the dialog with NavBar hidden.
        super.show()
        this.window!!.decorView.systemUiVisibility = (mContext as Activity).window.decorView.systemUiVisibility
        // Set the dialog to focusable again.
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    private fun initAnimation() {
        //aniDrawable = (AnimationDrawable) mImageView.getDrawable();

    }

    fun showView() {
        // 为了防止在onCreate方法中只显示第一帧的解决方案之一
        /*mImageView.postDelayed(new Runnable() {
			@Override
			public void run() {
				aniDrawable.start();

			}
		},1000);*/
        show()
        //aniDrawable.start();

    }

    fun cancleView() {
        //aniDrawable.stop();
        this.dismiss()
    }

    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            RxApiManager.get()!!.cancel(mContext)
            //cancleView();
            this.dismiss()
            return true
        }
        return false
    }

    companion object {
        /**旋转动画的时间 */
        internal val ROTATION_ANIMATION_DURATION = 1200
        /**动画插值 */
        internal val ANIMATION_INTERPOLATOR: Interpolator = LinearInterpolator()
    }
}
