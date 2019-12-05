package com.lairui.easy.mywidget.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup.LayoutParams
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.RxApiManager


/**
 *
 *
 * @Description:   自定义dialog
 */

class TestDialog(private val mContext: Context, style: Int) : Dialog(mContext, style), DialogInterface.OnKeyListener {

    private val mImageView: ImageView? = null
    private val mTextView: TextView? = null
    private val mRotateAnimation: Animation? = null


    internal var aniDrawable: AnimationDrawable? = null

    init {
        this.setOnKeyListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if(savedInstanceState == null){
            super.onCreate(Bundle())
        }else{
            super.onCreate(savedInstanceState)
        }

        setContentView(R.layout.dialog_trade_condition)


        val window = this.window

        window!!.setGravity(Gravity.BOTTOM)
        val lp = window.attributes
        lp.x = 0
        lp.y = 0
        lp.width = LayoutParams.MATCH_PARENT
        lp.height = 600
        //lp.dimAmount=0.0f;   // 背景没有黑色
        this.window!!.attributes = lp
        // 设置点击外围解散
        this.setCanceledOnTouchOutside(false)
        this.setCancelable(true)
        initAnimation()

        //		window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        //				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);//不获得焦点
        //window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//背景不变暗
        //window.setGravity(Gravity.CENTER);

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
