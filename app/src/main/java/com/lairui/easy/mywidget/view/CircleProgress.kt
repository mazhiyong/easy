package com.lairui.easy.mywidget.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.graphics.Shader
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View

import com.lairui.easy.R
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools

import java.math.BigDecimal

import androidx.core.content.ContextCompat

class CircleProgress(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mContext: Context? = null
    //默认大小
    private var mDefaultSize: Int = 0
    //是否开启抗锯齿
    var isAntiAlias: Boolean = false
        private set
    //绘制提示
    private var mHintPaint: TextPaint? = null
    var hint: CharSequence? = null
    private var mHintColor: Int = 0
    private var mHintSize: Float = 0.toFloat()
    private var mHintOffset: Float = 0.toFloat()

    //绘制单位
    private var mUnitPaint: TextPaint? = null
    var unit: CharSequence? = null
    private var mUnitColor: Int = 0
    private var mUnitSize: Float = 0.toFloat()
    private var mUnitOffset: Float = 0.toFloat()

    //绘制数值
    private var mValuePaint: TextPaint? = null
    private var mValue: Float = 0.toFloat()
    /**
     * 获取最大值
     *
     * @return
     */
    /**
     * 设置最大值
     *
     * @param maxValue
     */
    var maxValue: Float = 0.toFloat()
    private var mValueOffset: Float = 0.toFloat()
    private var mPrecision: Int = 0
    private var mPrecisionFormat: String? = null
    private var mValueColor: Int = 0
    private var mValueSize: Float = 0.toFloat()

    //绘制圆弧
    private var mArcPaint: Paint? = null
    private var mArcWidth: Float = 0.toFloat()
    private var mStartAngle: Float = 0.toFloat()
    private var mSweepAngle: Float = 0.toFloat()
    private var mRectF: RectF? = null
    //当前进度，[0.0f,1.0f]
    private var mPercent: Float = 0.toFloat()
    //动画时间
    var animTime: Long = 0
    //属性动画
    private var mAnimator: ValueAnimator? = null
    private var mAnimator2: ValueAnimator? = null

    private var mNum = 0.00f

    //绘制背景圆弧
    private var mBgArcPaint: Paint? = null
    private var mBgArcColor: Int = 0
    private var mArcColor: Int = 0
    private var mBgArcWidth: Float = 0.toFloat()

    //圆心坐标，半径
    private var mCenterPoint: Point? = null
    private var mRadius: Float = 0.toFloat()
    private var mTextOffsetPercentInRadius: Float = 0.toFloat()

    private var mArcCenterX: Int = 0
    // 内部虚线的外部半径
    private var mExternalDottedLineRadius: Float = 0.toFloat()
    // 内部虚线的内部半径
    private var mInsideDottedLineRadius: Float = 0.toFloat()

    // 线条数
    private var mDottedLineCount = 100
    // 圆弧跟虚线之间的距离
    private var mLineDistance = 20
    // 线条宽度
    private var mDottedLineWidth = 40f
    //是否使用渐变
    protected var useGradient = true
    //前景色起始颜色
    private var foreStartColor: Int = 0
    //前景色结束颜色
    private var foreEndColcor: Int = 0
    protected var mWidth: Int = 0
    protected var mHeight: Int = 0


    private var paint_3: Paint? = null
    private var paint_4: Paint? = null

    /**
     * 设置当前值
     *
     * @param value
     */
    var value: Float
        get() = mValue
        set(value) {
            var value = value
            mNum = value
            mPercent = 0f
            reset()
            if (value > maxValue) {
                value = maxValue
            }
            val start = mPercent
            val bi = UtilTools.divide(value.toDouble(), maxValue.toDouble())
            val bg = BigDecimal(bi)
            val end = bg.toFloat()
            startAnimator(start, end, animTime)
        }

    /**
     * 获取精度
     *
     * @return
     */
    var precision: Int
        get() = mPrecision
        set(precision) {
            mPrecision = precision
            mPrecisionFormat = getPrecisionFormat(precision)
        }


    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        mContext = context
        mDefaultSize = dipToPx(mContext!!, 150f)
        mAnimator = ValueAnimator()
        mRectF = RectF()
        mCenterPoint = Point()
        initAttrs(attrs)
        initPaint()
        value = mValue
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = mContext!!.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar)

        isAntiAlias = typedArray.getBoolean(R.styleable.CircleProgressBar_antiAlias, true)

        hint = typedArray.getString(R.styleable.CircleProgressBar_hint)
        mHintColor = typedArray.getColor(R.styleable.CircleProgressBar_hintColor, Color.BLACK)
        mHintSize = typedArray.getDimension(R.styleable.CircleProgressBar_hintSize, 15f)

        mValue = typedArray.getFloat(R.styleable.CircleProgressBar_value, 50f)
        maxValue = typedArray.getFloat(R.styleable.CircleProgressBar_maxValue, 50f)
        //内容数值精度格式
        mPrecision = typedArray.getInt(R.styleable.CircleProgressBar_precision, 0)
        mPrecisionFormat = getPrecisionFormat(mPrecision)
        mValueColor = typedArray.getColor(R.styleable.CircleProgressBar_valueColor, Color.BLACK)
        mValueSize = typedArray.getDimension(R.styleable.CircleProgressBar_valueSize, 15f)

        unit = typedArray.getString(R.styleable.CircleProgressBar_unit)
        mUnitColor = typedArray.getColor(R.styleable.CircleProgressBar_unitColor, Color.BLACK)
        mUnitSize = typedArray.getDimension(R.styleable.CircleProgressBar_unitSize, 30f)

        mArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_arcWidth, 15f)
        mStartAngle = typedArray.getFloat(R.styleable.CircleProgressBar_startAngle, 270f)
        mSweepAngle = typedArray.getFloat(R.styleable.CircleProgressBar_sweepAngle, 360f)

        mBgArcColor = typedArray.getColor(R.styleable.CircleProgressBar_bgArcColor, Color.WHITE)
        mArcColor = typedArray.getColor(R.styleable.CircleProgressBar_arcColors, Color.RED)
        mBgArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_bgArcWidth, 15f)
        mTextOffsetPercentInRadius = typedArray.getFloat(R.styleable.CircleProgressBar_textOffsetPercentInRadius, 0.33f)
        animTime = typedArray.getInt(R.styleable.CircleProgressBar_animTime, 1000).toLong()
        mDottedLineCount = typedArray.getInteger(R.styleable.CircleProgressBar_dottedLineCount, mDottedLineCount)
        mLineDistance = typedArray.getInteger(R.styleable.CircleProgressBar_lineDistance, mLineDistance)
        mDottedLineWidth = typedArray.getDimension(R.styleable.CircleProgressBar_dottedLineWidth, mDottedLineWidth)
        foreStartColor = typedArray.getColor(R.styleable.CircleProgressBar_foreStartColor, Color.BLUE)
        foreEndColcor = typedArray.getColor(R.styleable.CircleProgressBar_foreEndColor, Color.BLUE)
        typedArray.recycle()
    }

    private fun initPaint() {
        paint_3 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint_4 = Paint(Paint.ANTI_ALIAS_FLAG)


        mHintPaint = TextPaint()
        // 设置抗锯齿,会消耗较大资源，绘制图形速度会变慢。
        mHintPaint!!.isAntiAlias = isAntiAlias
        // 设置绘制文字大小
        mHintPaint!!.textSize = mHintSize
        // 设置画笔颜色
        mHintPaint!!.color = mHintColor
        // 从中间向两边绘制，不需要再次计算文字
        mHintPaint!!.textAlign = Paint.Align.CENTER

        mValuePaint = TextPaint()
        mValuePaint!!.isAntiAlias = isAntiAlias
        mValuePaint!!.textSize = mValueSize
        mValuePaint!!.color = mValueColor
        // 设置Typeface对象，即字体风格，包括粗体，斜体以及衬线体，非衬线体等
        // mValuePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mValuePaint!!.textAlign = Paint.Align.CENTER

        mUnitPaint = TextPaint()
        mUnitPaint!!.isAntiAlias = isAntiAlias
        mUnitPaint!!.textSize = mUnitSize
        mUnitPaint!!.color = mUnitColor
        mUnitPaint!!.textAlign = Paint.Align.CENTER

        mArcPaint = Paint()
        mArcPaint!!.isAntiAlias = isAntiAlias
        // 设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE
        mArcPaint!!.style = Paint.Style.STROKE
        // 设置画笔粗细
        mArcPaint!!.strokeWidth = mArcWidth
        // 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式
        // Cap.ROUND(圆形样式)或Cap.SQUARE(方形样式)
        mArcPaint!!.strokeCap = Paint.Cap.ROUND

        mBgArcPaint = Paint()
        mBgArcPaint!!.isAntiAlias = isAntiAlias
        mBgArcPaint!!.color = mBgArcColor
        mBgArcPaint!!.style = Paint.Style.STROKE
        mBgArcPaint!!.strokeWidth = mBgArcWidth
        // 设置画笔粗细
        mBgArcPaint!!.strokeWidth = mArcWidth
        mBgArcPaint!!.strokeCap = Paint.Cap.ROUND
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureView(widthMeasureSpec, mDefaultSize),
                measureView(heightMeasureSpec, mDefaultSize))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mArcCenterX = (w / 2f).toInt()
        Log.d(TAG, "onSizeChanged: w = $w; h = $h; oldw = $oldw; oldh = $oldh")
        //求圆弧和背景圆弧的最大宽度
        val maxArcWidth = Math.max(mArcWidth, mBgArcWidth)
        //求最小值作为实际值
        val minSize = Math.min(w - paddingLeft - paddingRight - 2 * maxArcWidth.toInt(),
                h - paddingTop - paddingBottom - 2 * maxArcWidth.toInt())
        //减去圆弧的宽度，否则会造成部分圆弧绘制在外围
        mRadius = (minSize / 2).toFloat()
        //获取圆的相关参数
        mCenterPoint!!.x = w / 2
        mCenterPoint!!.y = h / 2 + dipToPx(mContext!!, 20f)
        //绘制圆弧的边界
        mRectF!!.left = mCenterPoint!!.x.toFloat() - mRadius - maxArcWidth / 2
        mRectF!!.top = mCenterPoint!!.y.toFloat() - mRadius - maxArcWidth / 2
        mRectF!!.right = mCenterPoint!!.x.toFloat() + mRadius + maxArcWidth / 2
        mRectF!!.bottom = mCenterPoint!!.y.toFloat() + mRadius + maxArcWidth / 2
        //计算文字绘制时的 baseline
        //由于文字的baseline、descent、ascent等属性只与textSize和typeface有关，所以此时可以直接计算
        //若value、hint、unit由同一个画笔绘制或者需要动态设置文字的大小，则需要在每次更新后再次计算
        mValueOffset = (mCenterPoint!!.y + (1.5 * getBaselineOffsetFromY(mValuePaint!!)).toInt()).toFloat()

        //        mHintOffset = mCenterPoint.y - mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mHintPaint);
        //        mUnitOffset = mCenterPoint.y + mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mUnitPaint);

        mHintOffset = mCenterPoint!!.y.toFloat() + mValueOffset + getBaselineOffsetFromY(mValuePaint!!)
        mUnitOffset = mCenterPoint!!.y.toFloat() - 2 * getBaselineOffsetFromY(mValuePaint!!) - getBaselineOffsetFromY(mUnitPaint!!)

        if (useGradient) {
            val gradient = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), foreEndColcor, foreStartColor, Shader.TileMode.CLAMP)
            mArcPaint!!.shader = gradient
        } else {
            mArcPaint!!.color = mArcColor
        }

        Log.d(TAG, "onSizeChanged: 控件大小 = " + "(" + w + ", " + h + ")"
                + "圆心坐标 = " + mCenterPoint!!.toString()
                + ";圆半径 = " + mRadius
                + ";圆的外接矩形 = " + mRectF!!.toString())

        // 虚线的外部半径
        mExternalDottedLineRadius = ((mRectF!!.width() / 2).toInt() + mLineDistance).toFloat()
        // 虚线的内部半径
        mInsideDottedLineRadius = mExternalDottedLineRadius - mDottedLineWidth
    }

    private fun getBaselineOffsetFromY(paint: Paint): Float {
        val fontMetrics = paint.fontMetrics
        return (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawText(canvas)
        drawArc(canvas)
    }

    /**
     * 绘制内容文字
     *
     * @param canvas
     */
    private fun drawText(canvas: Canvas) {
        // canvas.drawText(String.format(mPrecisionFormat, mValue), mCenterPoint.x, mValueOffset, mValuePaint);

        val text = UtilTools.getNormalMoney(mValue.toString() + "")
        //        Spannable spannable = UtilTools.getDianType2(mContext,text);
        canvas.drawText(text, mCenterPoint!!.x.toFloat(), mValueOffset, mValuePaint!!)
        if (hint != null) {
            canvas.drawText(hint!!.toString(), mCenterPoint!!.x.toFloat(), mHintOffset, mHintPaint!!)
        }

        if (unit != null) {
            canvas.drawText(unit!!.toString(), mCenterPoint!!.x.toFloat(), mUnitOffset, mUnitPaint!!)
        }
    }

    private fun drawArc(canvas: Canvas) {
        // 绘制背景圆弧
        // 从进度圆弧结束的地方开始重新绘制，优化性能
        canvas.save()

        // 360 * Math.PI / 180
        val evenryDegrees = (2.0f * Math.PI / mDottedLineCount).toFloat()
        val startDegress = (135 * Math.PI / 180).toFloat()
        val endDegress = (225 * Math.PI / 180).toFloat()
        for (i in 0 until mDottedLineCount) {
            val degrees = i * evenryDegrees
            // 过滤底部90度的弧长
            if (degrees > startDegress && degrees < endDegress) {
                continue
            }
            val startX = mArcCenterX + Math.sin(degrees.toDouble()).toFloat() * mInsideDottedLineRadius
            val startY = mArcCenterX - Math.cos(degrees.toDouble()).toFloat() * mInsideDottedLineRadius

            val stopX = mArcCenterX + Math.sin(degrees.toDouble()).toFloat() * mExternalDottedLineRadius
            val stopY = mArcCenterX - Math.cos(degrees.toDouble()).toFloat() * mExternalDottedLineRadius

            //canvas.drawLine(startX, startY, stopX, stopY, mBgArcPaint);
        }

        canvas.drawArc(mRectF!!, mStartAngle, mSweepAngle, false, mBgArcPaint!!)




        canvas.rotate(mStartAngle, mCenterPoint!!.x.toFloat(), mCenterPoint!!.y.toFloat())

        // 第一个参数 oval 为 RectF 类型，即圆弧显示区域
        // startAngle 和 sweepAngle  均为 float 类型，分别表示圆弧起始角度和圆弧度数
        // 3点钟方向为0度，顺时针递增
        // 如果 startAngle < 0 或者 > 360,则相当于 startAngle % 360
        // useCenter:如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
        val currentAngle = mSweepAngle * mPercent
        //canvas.drawArc(mRectF, 2, currentAngle, false, mArcPaint);
        canvas.drawArc(mRectF!!, 0f, currentAngle, false, mArcPaint!!)


        val x = ((mRadius + mArcWidth / 2) * Math.cos(Math.toRadians(currentAngle.toDouble()))).toFloat()
        val y = ((mRadius + mArcWidth / 2) * Math.sin(Math.toRadians(currentAngle.toDouble()))).toFloat()
        paint_3!!.style = Paint.Style.FILL
        paint_3!!.color = ContextCompat.getColor(mContext!!, R.color.circle_start_color)
        // paint_3.setShader(shader);
        // paint_3.setColor(ContextCompat.getColor(context,R.color.red));
        paint_3!!.maskFilter = BlurMaskFilter(dipToPx(mContext!!, 3f).toFloat(), BlurMaskFilter.Blur.SOLID) //需关闭硬件加速
        // canvas.drawCircle( mCenterPoint.x+mRadius+mArcWidth/2, mCenterPoint.y,dipToPx(mContext,3),paint_3);


        paint_3!!.strokeWidth = mArcWidth / 2f / 3f * 4
        paint_3!!.style = Paint.Style.STROKE
        paint_3!!.maskFilter = BlurMaskFilter(mArcWidth / 3, BlurMaskFilter.Blur.SOLID) //需关闭硬件加速
        canvas.drawCircle(mCenterPoint!!.x + x, mCenterPoint!!.y + y, mArcWidth / 2f / 3f, paint_3!!)////圆形动态
        //         canvas.drawCircle( mCenterPoint.x+mRadius+mArcWidth/2, mCenterPoint.y,mArcWidth/3,paint_3);


        paint_4!!.style = Paint.Style.FILL
        paint_4!!.color = ContextCompat.getColor(mContext!!, R.color.white)
        // paint_3.setShader(shader);
        // paint_3.setColor(ContextCompat.getColor(context,R.color.red));
        paint_4!!.maskFilter = BlurMaskFilter(mArcWidth / 6, BlurMaskFilter.Blur.SOLID) //需关闭硬件加速
        canvas.drawCircle(mCenterPoint!!.x + x, mCenterPoint!!.y + y, mArcWidth / 6, paint_4!!)  //圆形动态
        //        canvas.drawCircle( mCenterPoint.x+mRadius+mArcWidth/2, mCenterPoint.y,mArcWidth/6,paint_4);


        canvas.restore()
    }

    private fun startAnimator(start: Float, end: Float, animTime: Long) {
        mAnimator = ValueAnimator.ofFloat(start, end)
        mAnimator!!.duration = animTime
        mAnimator!!.addUpdateListener { animation ->
            mPercent = animation.animatedValue as Float

            /*double all = UtilTools.mul(mPercent,mMaxValue);
                BigDecimal bg = new BigDecimal(all);
                mValue = bg.floatValue();
                LogUtil.i(TAG, "onAnimationUpdate: percent = " + mPercent
                        + ";currentAngle = " + (mSweepAngle * mPercent)
                        + ";value = " + mValue);*/
            invalidate()
        }
        mAnimator!!.start()

        mAnimator2 = ValueAnimator.ofFloat(0F, 1F)
        mAnimator2!!.duration = animTime
        mAnimator2!!.addUpdateListener { animation ->
            val p = animation.animatedValue as Float

            val all = UtilTools.mul(p.toDouble(), mNum.toDouble())
            val bg = BigDecimal(all)
            mValue = bg.toFloat()
            LogUtil.i(TAG, ";value = $mValue")
            invalidate()
        }
        mAnimator2!!.start()
    }

    /**
     * 重置
     */
    fun reset() {
        startAnimator(mPercent, 0.0f, 1000L)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //释放资源
    }

    companion object {

        private val TAG = CircleProgress::class.java.simpleName

        /**
         * 测量 View
         *
         * @param measureSpec
         * @param defaultSize View 的默认大小
         * @return
         */
        private fun measureView(measureSpec: Int, defaultSize: Int): Int {
            var result = defaultSize
            val specMode = View.MeasureSpec.getMode(measureSpec)
            val specSize = View.MeasureSpec.getSize(measureSpec)

            if (specMode == View.MeasureSpec.EXACTLY) {
                result = specSize
            } else if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
            return result
        }

        /**
         * dip 转换成px
         *
         * @param dip
         * @return
         */
        fun dipToPx(context: Context, dip: Float): Int {
            val density = context.resources.displayMetrics.density
            return (dip * density + 0.5f * if (dip >= 0) 1 else -1).toInt()
        }

        /**
         * 获取数值精度格式化字符串
         *
         * @param precision
         * @return
         */
        fun getPrecisionFormat(precision: Int): String {
            return "%." + precision + "f"
        }
    }
}