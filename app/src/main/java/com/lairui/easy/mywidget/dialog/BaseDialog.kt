package com.lairui.easy.mywidget.dialog


import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import android.widget.LinearLayout

import com.flyco.animation.BaseAnimatorSet
import com.flyco.dialog.utils.StatusBarUtils

abstract class BaseDialog
/**
 * method execute order:
 * show:constrouctor---show---oncreate---onStart---onAttachToWindow
 * dismiss:dismiss---onDetachedFromWindow---onStop
 */
(
        /** mContext(上下文)  */
        protected open var mContext: Context) : Dialog(mContext) {
    /** mTag(日志)  */
    protected var mTag: String
    /** (DisplayMetrics)设备密度  */
    protected lateinit var mDisplayMetrics: DisplayMetrics
    /** enable dismiss outside dialog(设置点击对话框以外区域,是否dismiss)  */
    protected var mCancel: Boolean = false
    /** dialog width scale(宽度比例)  */
    protected var mWidthScale = 1f
    /** dialog height scale(高度比例)  */
    protected var mHeightScale: Float = 0.toFloat()
    /** mShowAnim(对话框显示动画)  */
    private var mShowAnim: BaseAnimatorSet? = null
    /** mDismissAnim(对话框消失动画)  */
    private var mDismissAnim: BaseAnimatorSet? = null
    /** top container(最上层容器)  */
    protected lateinit var mLlTop: LinearLayout
    /** container to control dialog height(用于控制对话框高度)  */
    protected lateinit var mLlControlHeight: LinearLayout
    /** the child of mLlControlHeight you create.(创建出来的mLlControlHeight的直接子View)  */
    /** get actual created view(获取实际创建的View)  */
    lateinit var createView: View
        protected set
    /** is mShowAnim running(显示动画是否正在执行)  */
    private var mIsShowAnim: Boolean = false
    /** is DismissAnim running(消失动画是否正在执行)  */
    private var mIsDismissAnim: Boolean = false
    /** max height(最大高度)  */
    protected var mMaxHeight: Float = 0.toFloat()
    /** show Dialog as PopupWindow(像PopupWindow一样展示Dialog)  */
    private var mIsPopupStyle: Boolean = false
    /** automatic dimiss dialog after given delay(在给定时间后,自动消失)  */
    private var mAutoDismiss: Boolean = false
    /** delay (milliseconds) to dimiss dialog(对话框消失延时时间,毫秒值)  */
    private var mAutoDismissDelay: Long = 1500

    private val mHandler = Handler(Looper.getMainLooper())

    init {
        setDialogTheme()
        mTag = javaClass.simpleName
        setCanceledOnTouchOutside(true)
        Log.d(mTag, "constructor")
    }

    constructor(context: Context, isPopupStyle: Boolean) : this(context) {
        mIsPopupStyle = isPopupStyle
    }

    /** set dialog theme(设置对话框主题)  */
    private fun setDialogTheme() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)// android:windowNoTitle
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))// android:windowBackground
        window!!.addFlags(LayoutParams.FLAG_DIM_BEHIND)// android:backgroundDimEnabled默认是true的
    }

    /**
     * inflate layout for dialog ui and return (填充对话框所需要的布局并返回)
     * <pre>
     * public View onCreateView() {
     * View inflate = View.inflate(mContext, R.layout.dialog_share, null);
     * return inflate;
     * }
    </pre> *
     */
    abstract fun onCreateView(): View

    fun onViewCreated(inflate: View) {}

    /** set Ui data or logic opreation before attatched window(在对话框显示之前,设置界面数据或者逻辑)  */
    abstract fun setUiBeforShow()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(mTag, "onCreate")
        mDisplayMetrics = mContext.resources.displayMetrics
        mMaxHeight = (mDisplayMetrics.heightPixels - StatusBarUtils.getHeight(mContext)).toFloat()
        // mMaxHeight = mDisplayMetrics.heightPixels;

        mLlTop = LinearLayout(mContext)
        mLlTop.gravity = Gravity.CENTER

        mLlControlHeight = LinearLayout(mContext)
        mLlControlHeight.orientation = LinearLayout.VERTICAL

        createView = onCreateView()
        mLlControlHeight.addView(createView)
        mLlTop.addView(mLlControlHeight)
        onViewCreated(createView)

        if (mIsPopupStyle) {
            setContentView(mLlTop, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))
        } else {
            setContentView(mLlTop, ViewGroup.LayoutParams(mDisplayMetrics.widthPixels, mMaxHeight.toInt()))
        }

        mLlTop.setOnClickListener {
            if (mCancel) {
                dismiss()
            }
        }

        createView.isClickable = true
    }

    /**
     * when dailog attached to window,set dialog width and height and show anim
     * (当dailog依附在window上,设置对话框宽高以及显示动画)
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(mTag, "onAttachedToWindow")

        setUiBeforShow()

        val width: Int
        if (mWidthScale == 0f) {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            width = (mDisplayMetrics.widthPixels * mWidthScale).toInt()
        }

        val height: Int
        if (mHeightScale == 0f) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        } else if (mHeightScale == 1f) {
            //            height = ViewGroup.LayoutParams.MATCH_PARENT;
            height = mMaxHeight.toInt()
        } else {
            height = (mMaxHeight * mHeightScale).toInt()
        }

        mLlControlHeight.layoutParams = LinearLayout.LayoutParams(width, height)

        if (mShowAnim != null) {
            mShowAnim!!.listener(object : BaseAnimatorSet.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                    mIsShowAnim = true
                }

                override fun onAnimationRepeat(animator: Animator) {}

                override fun onAnimationEnd(animator: Animator) {
                    mIsShowAnim = false
                    delayDismiss()
                }

                override fun onAnimationCancel(animator: Animator) {
                    mIsShowAnim = false
                }
            }).playOn(mLlControlHeight)
        } else {
            BaseAnimatorSet.reset(mLlControlHeight)
            delayDismiss()
        }
    }


    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        this.mCancel = cancel
        super.setCanceledOnTouchOutside(cancel)
    }

    override fun show() {
        Log.d(mTag, "show")
        super.show()
    }


    override fun onStart() {
        super.onStart()
        Log.d(mTag, "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(mTag, "onStop")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d(mTag, "onDetachedFromWindow")
    }

    override fun dismiss() {
        Log.d(mTag, "dismiss")
        if (mDismissAnim != null) {
            mDismissAnim!!.listener(object : BaseAnimatorSet.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                    mIsDismissAnim = true
                }

                override fun onAnimationRepeat(animator: Animator) {}

                override fun onAnimationEnd(animator: Animator) {
                    mIsDismissAnim = false
                    superDismiss()
                }

                override fun onAnimationCancel(animator: Animator) {
                    mIsDismissAnim = false
                    superDismiss()
                }
            }).playOn(mLlControlHeight)
        } else {
            superDismiss()
        }
    }

    /** dismiss without anim(无动画dismiss)  */
    fun superDismiss() {
        super.dismiss()
    }

    /** dialog anim by styles(动画弹出对话框,style动画资源)  */
    fun show(animStyle: Int) {
        val window = window
        window!!.setWindowAnimations(animStyle)
        show()
    }

    /** show at location only valid for mIsPopupStyle true(指定位置显示,只对isPopupStyle为true有效)  */
    fun showAtLocation(gravity: Int, x: Int, y: Int) {
        if (mIsPopupStyle) {
            val window = window
            val params = window!!.attributes
            window.setGravity(gravity)
            params.x = x
            params.y = y
        }

        show()
    }

    /** show at location only valid for mIsPopupStyle true(指定位置显示,只对isPopupStyle为true有效)  */
    fun showAtLocation(x: Int, y: Int) {
        val gravity = Gravity.LEFT or Gravity.TOP//Left Top (坐标原点为右上角)
        showAtLocation(gravity, x, y)
    }

    /** set window dim or not(设置背景是否昏暗)  */
    fun dimEnabled(isDimEnabled: Boolean) {
        if (isDimEnabled) {
            window!!.addFlags(LayoutParams.FLAG_DIM_BEHIND)
        } else {
            window!!.clearFlags(LayoutParams.FLAG_DIM_BEHIND)
        }
    }

    /** set dialog width scale:0-1(设置对话框宽度,占屏幕宽的比例0-1)  */
    fun widthScale(widthScale: Float) {
        this.mWidthScale = widthScale

    }

    /** set dialog height scale:0-1(设置对话框高度,占屏幕宽的比例0-1)  */
    fun heightScale(heightScale: Float) {
        mHeightScale = heightScale

    }

    /** set show anim(设置显示的动画)  */
    fun showAnim(showAnim: BaseAnimatorSet) {
        mShowAnim = showAnim

    }

    /** set dismiss anim(设置隐藏的动画)  */
    fun dismissAnim(dismissAnim: BaseAnimatorSet) {
        mDismissAnim = dismissAnim

    }

    /** automatic dimiss dialog after given delay(在给定时间后,自动消失)  */
    fun autoDismiss(autoDismiss: Boolean) {
        mAutoDismiss = autoDismiss

    }

    /** set dealy (milliseconds) to dimiss dialog(对话框消失延时时间,毫秒值)  */
    fun autoDismissDelay(autoDismissDelay: Long) {
        mAutoDismissDelay = autoDismissDelay

    }

    private fun delayDismiss() {
        if (mAutoDismiss && mAutoDismissDelay > 0) {
            mHandler.postDelayed({ dismiss() }, mAutoDismissDelay)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (mIsDismissAnim || mIsShowAnim || mAutoDismiss) {
            true
        } else super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if (mIsDismissAnim || mIsShowAnim || mAutoDismiss) {
            return
        }
        super.onBackPressed()
    }

    /** dp to px  */
    protected fun dp2px(dp: Float): Int {
        val scale = mContext.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}
