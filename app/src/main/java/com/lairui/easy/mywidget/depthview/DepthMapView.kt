package com.lairui.easy.mywidget.depthview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import com.lairui.easy.R
import java.math.BigDecimal
import java.util.*

/**
 * @Description:
 * @author: lll
 * @date: 2019-07-19
 */
class DepthMapView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var mWidth = 0
    //圆点半径
    private var mDotRadius = 2
    //圆圈半径
    private var mCircleRadius = 8
    private var mGridWidth = 0f
    //底部价格区域高度
    private var mBottomPriceHeight = 0
    //右侧委托量绘制个数
    private var mLineCount = 0
    //背景颜色
    private var mBackgroundColor = 0
    private val mIsHave = false
    //是否是长按
    private var mIsLongPress = false
    //最大的委托量
    private var mMaxVolume = 0f
    private var mMultiple = 0f
    private var mLastPosition = 0
    private var mDrawWidth = 0
    private var mDrawHeight = 0
    //触摸点的X轴值
    private var mEventX = 0
    //文案绘制画笔
    private var mTextPaint: Paint? = null
    //买入区域边线绘制画笔
    private var mBuyLinePaint: Paint? = null
    //卖出区域边线绘制画笔
    private var mSellLinePaint: Paint? = null
    //买入区域绘制画笔
    private var mBuyPathPaint: Paint? = null
    //卖出取悦绘制画笔
    private var mSellPathPaint: Paint? = null
    //选中时圆点绘制画笔
    private var mRadioPaint: Paint? = null
    //选中时圆圈绘制画笔
    private var mCirclePaint: Paint? = null
    //选中时中间文案背景画笔
    private var mSelectorBackgroundPaint: Paint? = null
    //选中时中间文案画笔
    private var mSelectorTextPaint: Paint? = null
    private val mBuyPath = Path()
    private val mSellPath = Path()
    private var mBuyData: MutableList<DepthBuySellData>? = null
    private var mSellData: MutableList<DepthBuySellData>? = null
    private var buyAmountMoney: MutableList<Float>? = null
    private var sellAmountMoney: MutableList<Float>? = null
    //    价格显示精度限制
    var mPriceLimit = 7
    //    private int mVolumeLimit = 5;
    private var mMapX: HashMap<Int, DepthBuySellData>? = null
    private var mMapY: HashMap<Int, Float>? = null
    private var mAmount: HashMap<Int, Float>? = null
    private var mBottomPrice: Array<Float?>?=null
    private var onTouch = false
    private var onLongPress = false
    private var onVerticalMove = false
    private var tradePost = ""
    private var coin = ""
    private var pricisionCoin = 0
    private var pricisionPrice = 0
    private var priTradePost = 2 //显示委托价精度
    @SuppressLint("UseSparseArrays")
    private fun init(attrs: AttributeSet?) {
        mBottomPriceHeight = 40
        mMapX = HashMap()
        mMapY = HashMap()
        mAmount = HashMap()
        mBottomPrice = arrayOfNulls(4)
        mBuyData = ArrayList()
        mSellData = ArrayList()
        buyAmountMoney = ArrayList()
        sellAmountMoney = ArrayList()
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.textAlign = Paint.Align.RIGHT
        mBuyLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBuyLinePaint!!.style = Paint.Style.STROKE
        mBuyLinePaint!!.textAlign = Paint.Align.CENTER
        mBuyPathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSellLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSellLinePaint!!.style = Paint.Style.STROKE
        mSellLinePaint!!.textAlign = Paint.Align.CENTER
        mSellPathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRadioPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRadioPaint!!.textAlign = Paint.Align.CENTER
        mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCirclePaint!!.textAlign = Paint.Align.CENTER
        mSelectorBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSelectorTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSelectorTextPaint!!.textAlign = Paint.Align.RIGHT
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DepthMapView)
        if (typedArray != null) {
            try {
                mLineCount = typedArray.getInt(R.styleable.DepthMapView_line_count, 4)
                mDotRadius = typedArray.getDimensionPixelSize(R.styleable.DepthMapView_dot_radius, dip2px(mDotRadius.toFloat()))
                mCircleRadius = typedArray.getDimensionPixelSize(R.styleable.DepthMapView_circle_radius, dip2px(mCircleRadius.toFloat()))
                mBackgroundColor = typedArray.getColor(R.styleable.DepthMapView_background_color, ContextCompat.getColor(context, android.R.color.white))
                mBuyLinePaint!!.strokeWidth = typedArray.getDimensionPixelSize(R.styleable.DepthMapView_line_width, dip2px(1.5f)).toFloat()
                mSellLinePaint!!.strokeWidth = typedArray.getDimensionPixelSize(R.styleable.DepthMapView_line_width, dip2px(1.5f)).toFloat()
                mTextPaint!!.color = typedArray.getColor(R.styleable.DepthMapView_text_color, Color.parseColor("#9B9B9B"))
                mTextPaint!!.textSize = typedArray.getDimension(R.styleable.DepthMapView_text_size, dip2px(10f).toFloat())
                mBuyLinePaint!!.color = typedArray.getColor(R.styleable.DepthMapView_buy_line_color, Color.parseColor("#00BE66"))
                mSellLinePaint!!.color = typedArray.getColor(R.styleable.DepthMapView_sell_line_color, Color.parseColor("#EA573C"))
                val buyColor = typedArray.getColor(R.styleable.DepthMapView_buy_path_color, Color.parseColor("#00BE66"))
                val sellColor = typedArray.getColor(R.styleable.DepthMapView_sell_path_color, Color.parseColor("#EA573C"))
                mSelectorBackgroundPaint!!.color = typedArray.getColor(R.styleable.DepthMapView_selector_background_color, Color.parseColor("#252B3E"))
                mSelectorTextPaint!!.color = typedArray.getColor(R.styleable.DepthMapView_selector_text_color, ContextCompat.getColor(context, android.R.color.white))
                mSelectorTextPaint!!.textSize = typedArray.getDimension(R.styleable.DepthMapView_selector_text_size, dip2px(10f).toFloat())
                val buyLg = LinearGradient(0.0f, 0.0f, 0.0f, 800.0f, buyColor, Color.parseColor("#2000BE66") , Shader.TileMode.CLAMP )
                mBuyPathPaint!!.shader = buyLg
                val sellLg = LinearGradient(width.toFloat(), 0.0f, width.toFloat(), 800.0f, sellColor, Color.parseColor("#20EA573C"), Shader.TileMode.CLAMP)
                mSellPathPaint!!.shader = sellLg
            } finally {
                typedArray.recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mDrawWidth = mWidth / 2 - 1
        mDrawHeight = h - mBottomPriceHeight
    }

    //设置交易区精度
    fun setPriTradePost(priTradePost: Int) {
        this.priTradePost = priTradePost
    }

    fun setData(buyData: MutableList<DepthBuySellData>?, sellData: MutableList<DepthBuySellData>?, name: String, prinPrice: Int, priCoin: Int) {
        var vol = 0.0
        var amountmoney = 0f
        if (buyData!!.size > 0) {
            mBuyData!!.clear()
            buyAmountMoney!!.clear()
            //买入数据按价格进行排序
            Collections.sort(buyData, comparePrice())
            var depthDataBean: DepthBuySellData
            //累加买入委托量
            for (index in buyData.indices.reversed()) {
                depthDataBean = buyData[index]
                vol += java.lang.Double.valueOf(depthDataBean.amount)
                amountmoney += java.lang.Float.valueOf(depthDataBean.amount) * java.lang.Float.valueOf(depthDataBean.price)
                depthDataBean.amount = "" + vol
                mBuyData!!.add(0, depthDataBean)
                buyAmountMoney!!.add(0, amountmoney)
            }
            //修改底部买入价格展示
            mBottomPrice!![0] = java.lang.Float.valueOf(mBuyData!![0].price)
            mBottomPrice!![1] = java.lang.Float.valueOf(mBuyData!![if (mBuyData!!.size > 1) mBuyData!!.size - 1 else 0].price)
            mMaxVolume = java.lang.Float.valueOf(mBuyData!![0].amount)
        }
        if (sellData!!.size > 0) {
            mSellData!!.clear()
            sellAmountMoney!!.clear()
            vol = 0.0
            amountmoney = 0f
            //卖出数据按价格进行排序
            Collections.sort(sellData, comparePrice())
            //累加卖出委托量
            for (depthDataBean in sellData) {
                vol += java.lang.Double.valueOf(depthDataBean.amount)
                amountmoney += java.lang.Float.valueOf(depthDataBean.amount) * java.lang.Float.valueOf(depthDataBean.price)
                depthDataBean.amount = "" + vol
                mSellData!!.add(depthDataBean)
                sellAmountMoney!!.add(amountmoney)
            }
            //修改底部卖出价格展示
            mBottomPrice!![2] = java.lang.Float.valueOf(mSellData!![0].price)
            mBottomPrice!![3] = java.lang.Float.valueOf(mSellData!![if (mSellData!!.size > 1) mSellData!!.size - 1 else 0].price)
            mMaxVolume = Math.max(mMaxVolume, java.lang.Float.valueOf(mSellData!![mSellData!!.size - 1].amount))
        }
        mMaxVolume = mMaxVolume * 1.05f
        mMultiple = mMaxVolume / mLineCount
        tradePost = name
        coin = name
        pricisionPrice = prinPrice
        pricisionCoin = priCoin
        invalidate()
    }

    private val gestureDetector = GestureDetector(getContext(), object : SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            if (onTouch) {
                onLongPress = true
                mIsLongPress = true
                invalidate()
            }
        }
    })
    private val gestureCompat: GestureMoveActionCompat = GestureMoveActionCompat(null)
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        mEventX = event.x.toInt()
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                onTouch = true
                mIsLongPress = true
            }
            MotionEvent.ACTION_MOVE -> if (event.pointerCount == 1) { //                    mIsLongPress = true;
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                onTouch = false
                onLongPress = false
                mIsLongPress = true
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                onTouch = false
                onLongPress = false
            }
        }
        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val onHorizontalMove: Boolean = gestureCompat.onTouchEvent(event, event.x, event.y)
        val action = MotionEventCompat.getActionMasked(event)
        onVerticalMove = false
        if (action == MotionEvent.ACTION_MOVE) {
            if (!onHorizontalMove && !onLongPress && gestureCompat.isDragging) {
                onTouch = false
                onVerticalMove = true
            }
        }
        parent.requestDisallowInterceptTouchEvent(!onVerticalMove)
        return super.dispatchTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(mBackgroundColor)
        canvas.save()
        //绘制买入区域
        drawBuy(canvas)
        //绘制卖出区域
        drawSell(canvas)
        //绘制界面相关文案
        drawText(canvas)
        canvas.restore()
    }

    private fun drawBuy(canvas: Canvas) {
        mGridWidth = mDrawWidth * 1.0f / if (mBuyData!!.size - 1 == 0) 1 else mBuyData!!.size - 1
        mBuyPath.reset()
        mMapX!!.clear()
        mMapY!!.clear()
        mAmount!!.clear()
        var x: Float
        var y: Float
        for (i in mBuyData!!.indices) {
            if (i == 0) {
                mBuyPath.moveTo(0f, getY(java.lang.Float.valueOf(mBuyData!![0].amount)))
            }
            y = getY(java.lang.Float.valueOf(mBuyData!![i].amount))
            if (i >= 1) {
                canvas.drawLine(mGridWidth * (i - 1), getY(java.lang.Float.valueOf(mBuyData!![i - 1].amount)), mGridWidth * i, y, mBuyLinePaint)
            }
            if (i != mBuyData!!.size - 1) {
                mBuyPath.quadTo(mGridWidth * i, y, mGridWidth * (i + 1), getY(java.lang.Float.valueOf(mBuyData!![i + 1].amount)))
            }
            x = mGridWidth * i
            mMapX!![x.toInt()] = mBuyData!![i]
            mMapY!![x.toInt()] = y
            mAmount!![x.toInt()] = buyAmountMoney!![i]
            if (i == mBuyData!!.size - 1) {
                mBuyPath.quadTo(mGridWidth * i, y, mGridWidth * i, mDrawHeight.toFloat())
                mBuyPath.quadTo(mGridWidth * i, mDrawHeight.toFloat(), 0f, mDrawHeight.toFloat())
                mBuyPath.close()
            }
        }
        if (mBuyData!!.size > 1) {
            canvas.drawPath(mBuyPath, mBuyPathPaint)
        }
    }

    private fun drawSell(canvas: Canvas) {
        mGridWidth = mDrawWidth * 1.0f / if (mSellData!!.size - 1 == 0) 1 else mSellData!!.size - 1
        mSellPath.reset()
        var x: Float
        var y: Float
        for (i in mSellData!!.indices) {
            if (i == 0) {
                mSellPath.moveTo(mDrawWidth.toFloat(), getY(java.lang.Float.valueOf(mSellData!![0].amount)))
            }
            y = getY(java.lang.Float.valueOf(mSellData!![i].amount))
            if (i >= 1) {
                canvas.drawLine(mGridWidth * (i - 1) + mDrawWidth, getY(java.lang.Float.valueOf(mSellData!![i - 1].amount)),
                        mGridWidth * i + mDrawWidth, y, mSellLinePaint)
            }
            if (i != mSellData!!.size - 1) {
                mSellPath.quadTo(mGridWidth * i + mDrawWidth, y,
                        mGridWidth * (i + 1) + mDrawWidth, getY(java.lang.Float.valueOf(mSellData!![i + 1].amount)))
            }
            x = mGridWidth * i + mDrawWidth
            mMapX!![x.toInt()] = mSellData!![i]
            mMapY!![x.toInt()] = y
            mAmount!![x.toInt()] = sellAmountMoney!![i]
            if (i == mSellData!!.size - 1) {
                mSellPath.quadTo(mWidth.toFloat(), y, mGridWidth * i + mDrawWidth, mDrawHeight.toFloat())
                mSellPath.quadTo(mGridWidth * i + mDrawWidth, mDrawHeight.toFloat(), mDrawWidth.toFloat(), mDrawHeight.toFloat())
                mSellPath.close()
            }
        }
        if (mSellData!!.size > 1) {
            canvas.drawPath(mSellPath, mSellPathPaint)
        }
    }

    private fun drawText(canvas: Canvas) {
        var value: Float
        var str: String
        for (j in 0 until mLineCount) {
            value = mMaxVolume - mMultiple * j
            str = getSelectTotalValue(value)
            canvas.drawText(str, mWidth.toFloat(), mDrawHeight / mLineCount * j + 30.toFloat(), mTextPaint)
        }
        val size = mBottomPrice!!.size
        val height = mDrawHeight + mBottomPriceHeight / 2 + 10
        if (size > 0 && mBottomPrice!![0] != null) {
            var data = getSelectPriceValue(mBottomPrice!![0]!!)
            canvas.drawText(data, mTextPaint!!.measureText(data), height.toFloat(), mTextPaint)
            data = getSelectPriceValue(mBottomPrice!![1]!!)
            canvas.drawText(data, mDrawWidth - 10.toFloat(), height.toFloat(), mTextPaint)
            data = getSelectPriceValue(mBottomPrice!![2]!!)
            canvas.drawText(data, mDrawWidth + mTextPaint!!.measureText(data) + 10, height.toFloat(), mTextPaint)
            data = getSelectPriceValue(mBottomPrice!![3]!!)
            canvas.drawText(data, mWidth.toFloat(), height.toFloat(), mTextPaint)
        }
        if (mIsLongPress) {
            var mIsHave = false
            for (key in mMapX!!.keys) {
                if (key == mEventX) {
                    mIsHave = true
                    mLastPosition = mEventX
                    break
                }
            }
            if (mIsHave) {
                drawSelectorView(canvas, mLastPosition)
            } else {
                mLastPosition = getApproximate(mEventX)
                drawSelectorView(canvas, mLastPosition)
            }
        }
    }

    private fun getApproximate(x: Int): Int {
        var minDifference = Int.MAX_VALUE
        var key1 = 0
        for (key in mMapX!!.keys) {
            val temp = Math.abs(key - x)
            if (temp < minDifference) {
                minDifference = temp
                key1 = key
            }
        }
        return key1
    }

    private fun drawSelectorView(canvas: Canvas, position: Int) { //        mIsHave = true;
        val y = mMapY!![position] ?: return
        if (position < mDrawWidth) {
            mCirclePaint!!.color = Color.parseColor("#454E6B")
            mRadioPaint!!.color = Color.parseColor("#7E9AEF")
        } else {
            mCirclePaint!!.color = Color.parseColor("#454E6B")
            mRadioPaint!!.color = Color.parseColor("#7E9AEF")
        }
        canvas.drawCircle(position.toFloat(), y, mCircleRadius.toFloat(), mCirclePaint)
        canvas.drawCircle(position.toFloat(), y, mDotRadius.toFloat(), mRadioPaint)
        val volume = "累计数量：" + getSelectVolumeValue(java.lang.Double.valueOf(mMapX!![position]!!.amount)) + " " + coin
        val price = "委托价：" + getSelectPriceValue(java.lang.Float.valueOf(mMapX!![position]!!.price)) + " " + tradePost
        val totalPrice: Float? = mAmount!![position]
        val total = "累计金额：" + getSelectTotalValue(totalPrice) + " " + tradePost
        var width = Math.max(mSelectorTextPaint!!.measureText(volume), mSelectorTextPaint!!.measureText(price))
        width = Math.max(width, mSelectorTextPaint!!.measureText(total))
        val metrics = mSelectorTextPaint!!.fontMetrics
        val textHeight = metrics.descent - metrics.ascent
        val padding = dip2px(5f)
        canvas.drawRoundRect(RectF(mDrawWidth - width / 2 - padding, 20.toFloat(), mDrawWidth + width / 2 + padding * 2, textHeight * 3 + padding * 3 + 20), 10f, 10f, mSelectorBackgroundPaint)
        canvas.drawText("委托价：",
                mDrawWidth - width / 2 + padding + mSelectorTextPaint!!.measureText("委托价："), textHeight + 22, mSelectorTextPaint)
        canvas.drawText(getSelectPriceValue(java.lang.Float.valueOf(mMapX!![position]!!.price)) + " " + tradePost, mDrawWidth + width / 2 + padding, textHeight + 22, mSelectorTextPaint)
        canvas.drawText("累计数量：",
                mDrawWidth - width / 2 + padding + mSelectorTextPaint!!.measureText("累计数量："), textHeight * 2 + padding + 20, mSelectorTextPaint)
        canvas.drawText(getSelectVolumeValue(java.lang.Double.valueOf(mMapX!![position]!!.amount)) + " " + coin, mDrawWidth + width / 2 + padding, textHeight * 2 + padding + 20, mSelectorTextPaint)
        canvas.drawText("累计金额：",
                mDrawWidth - width / 2 + padding + mSelectorTextPaint!!.measureText("累计金额："), textHeight * 3 + padding * 2 + 20, mSelectorTextPaint)
        canvas.drawText(getSelectTotalValue(totalPrice) + " " + tradePost, mDrawWidth + width / 2 + padding, textHeight * 3 + padding * 2 + 20, mSelectorTextPaint)
    }

    inner class comparePrice : Comparator<DepthBuySellData> {
        override fun compare(o1: DepthBuySellData, o2: DepthBuySellData): Int {
            val str1 = java.lang.Float.valueOf(o1.price)
            val str2 = java.lang.Float.valueOf(o2.price)
            return java.lang.Float.compare(str1, str2)
        }
    }

    private fun getY(volume: Float): Float {
        return mDrawHeight - mDrawHeight * volume / mMaxVolume
    }

    private fun getValue(value: Float): String { //        String value = new BigDecimal(data).toPlainString();
//        return subZeroAndDot(value);
        return String.format("%." + mPriceLimit + "f", value)
    }

    @SuppressLint("DefaultLocale")
    private fun getVolumeValue(value: Float): String {
        return String.format("%.4f", value)
    }

    private fun getSelectVolumeValue(targetValue: Double): String {
        return if (targetValue > 100000) {
            val bigDecimal = BigDecimal(targetValue / 1000)
            bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K"
        } else {
            val bigDecimal = BigDecimal(targetValue)
            bigDecimal.setScale(pricisionCoin, BigDecimal.ROUND_HALF_UP).toPlainString()
        }
    }

    private fun getSelectPriceValue(targetValue: Float): String {
        val bigDecimal = BigDecimal(targetValue.toDouble())
        return bigDecimal.setScale(pricisionPrice, BigDecimal.ROUND_HALF_UP).toPlainString()
    }

    private fun getSelectTotalValue(targetValue: Float?): String {
        if (targetValue != null) {
            return if (targetValue > 100000) {
                val bigDecimal = BigDecimal((targetValue / 1000).toDouble())
                bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K"
            } else {
                val bigDecimal = BigDecimal(targetValue.toDouble())
                bigDecimal.setScale(priTradePost, BigDecimal.ROUND_HALF_UP).toPlainString()
            }
        }else{
            return "0.00"
        }

    }

    companion object {
        fun dip2px(dpValue: Float): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }

    init {
        val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        gestureCompat.setTouchSlop(touchSlop)
        init(attrs)
    }
}