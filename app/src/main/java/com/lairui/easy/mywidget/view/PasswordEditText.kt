package com.lairui.easy.mywidget.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.EditText

import com.lairui.easy.R

/**
 * Email 240336124@qq.com
 * Created by Darren on 2016/12/24.
 * Version 1.0
 * Description: 自定义输入密码框
 */
class PasswordEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : EditText(context, attrs) {
    // 画笔
    private var mPaint: Paint? = null
    // 一个密码所占的宽度
    private var mPasswordItemWidth: Int = 0
    // 密码的个数默认为6位数
    private val mPasswordNumber = 6
    // 背景边框颜色
    private var mBgColor = Color.parseColor("#d1d2d6")
    // 背景边框大小
    private var mBgSize = 1
    // 背景边框圆角大小
    private var mBgCorner = 0
    // 分割线的颜色
    private var mDivisionLineColor = mBgColor
    // 分割线的大小
    private var mDivisionLineSize = 1
    // 密码圆点的颜色
    private var mPasswordColor = mDivisionLineColor
    // 密码圆点的半径大小
    private var mPasswordRadius = 4

    // 设置当前密码是否已满的接口回掉
    private var mListener: PasswordFullListener? = null

    init {
        initAttributeSet(context, attrs)
        initPaint()
        // 默认只能够设置数字和字母
        inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        mPaint = Paint()
        // 抗锯齿
        mPaint!!.isAntiAlias = true
        // 防抖动
        mPaint!!.isDither = true
    }

    /**
     * 初始化属性
     */
    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText)
        // 获取大小
        mDivisionLineSize = array.getDimension(R.styleable.PasswordEditText_divisionLineSize, dip2px(mDivisionLineSize)).toInt()
        mPasswordRadius = array.getDimension(R.styleable.PasswordEditText_passwordRadius, dip2px(mPasswordRadius)).toInt()
        mBgSize = array.getDimension(R.styleable.PasswordEditText_bgSize, dip2px(mBgSize)).toInt()
        mBgCorner = array.getDimension(R.styleable.PasswordEditText_bgCorner, 0f).toInt()
        // 获取颜色
        mBgColor = array.getColor(R.styleable.PasswordEditText_bgColor, mBgColor)
        mDivisionLineColor = array.getColor(R.styleable.PasswordEditText_divisionLineColor, mDivisionLineColor)
        mPasswordColor = array.getColor(R.styleable.PasswordEditText_passwordColor, mDivisionLineColor)
        array.recycle()
    }

    /**
     * dip 转 px
     */
    private fun dip2px(dip: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dip.toFloat(), resources.displayMetrics).toInt().toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        // 一个密码的宽度
        mPasswordItemWidth = (width - 2 * mBgSize - (mPasswordNumber - 1) * mDivisionLineSize) / mPasswordNumber
        // 画背景
        drawBg(canvas)
        // 画分割线
        drawDivisionLine(canvas)
        // 画密码
        drawPassword(canvas)

        // 当前密码是不是满了
        if (mListener != null) {
            val password = text.toString().trim { it <= ' ' }
            if (password.length >= mPasswordNumber) {
                mListener!!.passwordFull(password)
            }
        }
    }

    /**
     * 绘制密码
     */
    private fun drawPassword(canvas: Canvas) {
        // 密码绘制是实心
        mPaint!!.style = Paint.Style.FILL
        // 设置密码的颜色
        mPaint!!.color = mPasswordColor
        // 获取当前text
        val text = text.toString().trim { it <= ' ' }
        // 获取密码的长度
        val passwordLength = text.length
        // 不断的绘制密码
        for (i in 0 until passwordLength) {
            val cy = height / 2
            val cx = mBgSize + i * mPasswordItemWidth + i * mDivisionLineSize + mPasswordItemWidth / 2
            canvas.drawCircle(cx.toFloat(), cy.toFloat(), mPasswordRadius.toFloat(), mPaint!!)
        }
    }

    /**
     * 绘制分割线
     */
    private fun drawDivisionLine(canvas: Canvas) {
        // 给画笔设置大小
        mPaint!!.strokeWidth = mDivisionLineSize.toFloat()
        // 设置分割线的颜色
        mPaint!!.color = mDivisionLineColor
        for (i in 0 until mPasswordNumber - 1) {
            val startX = mBgSize + (i + 1) * mPasswordItemWidth + i * mDivisionLineSize
            val startY = mBgSize
            val endY = height - mBgSize
            canvas.drawLine(startX.toFloat(), startY.toFloat(), startX.toFloat(), endY.toFloat(), mPaint!!)
        }
    }

    /**
     * 绘制背景
     */
    private fun drawBg(canvas: Canvas) {
        val rect = RectF(mBgSize.toFloat(), mBgSize.toFloat(), (width - mBgSize).toFloat(), (height - mBgSize).toFloat())
        // 给画笔设置大小
        mPaint!!.strokeWidth = mBgSize.toFloat()
        // 设置背景的颜色
        mPaint!!.color = mBgColor
        // 画空心
        mPaint!!.style = Paint.Style.STROKE

        // 绘制背景  drawRect , drawRoundRect  ,
        // 如果有圆角那么就绘制drawRoundRect，否则绘制drawRect
        if (mBgCorner == 0) {
            canvas.drawRect(rect, mPaint!!)
        } else {
            canvas.drawRoundRect(rect, mBgCorner.toFloat(), mBgCorner.toFloat(), mPaint!!)
        }
    }

    /**
     * 添加一个密码
     */
    fun addPassword(number: String) {
        // 把之前的密码取出来
        var password = text.toString().trim { it <= ' ' }
        if (password.length >= mPasswordNumber) {
            // 密码不能超过当前密码个输
            return
        }
        // 密码叠加
        password += number
        setText(password)
    }

    /**
     * 删除最后一位密码
     */
    fun deleteLastPassword() {
        var password = text.toString().trim { it <= ' ' }
        // 判断当前密码是不是空
        if (TextUtils.isEmpty(password)) {
            return
        }
        password = password.substring(0, password.length - 1)
        setText(password)
    }

    fun setOnPasswordFullListener(listener: PasswordFullListener) {
        this.mListener = listener
    }

    /**
     * 密码已经全部填满
     */
    interface PasswordFullListener {
        fun passwordFull(password: String)
    }
}
