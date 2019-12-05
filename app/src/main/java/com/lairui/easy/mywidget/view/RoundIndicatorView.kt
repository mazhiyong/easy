package com.lairui.easy.mywidget.view

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.content.ContextCompat

import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager

import com.lairui.easy.R
import com.lairui.easy.utils.tool.UtilTools

/**
 */

class RoundIndicatorView @JvmOverloads constructor( context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var paint: Paint? = null
    private var paint_2: Paint? = null
    private var paint_3: Paint? = null
    private var paint_4: Paint? = null
    private var maxNum: Int = 0
    private var startAngle: Int = 0
    private var sweepAngle: Int = 0
    private var radius: Int = 0
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var sweepInWidth: Int = 0//内圆的宽度
    private var sweepOutWidth: Int = 0//外圆的宽度
    var currentNum = 0
        set(currentNum) {
            field = currentNum
            invalidate()
        }//需设置setter、getter 供属性动画使用
    private val text = arrayOf("较差", "中等", "良好", "优秀", "极好")
    //    private int[] indicatorColor = {0xffffffff,0x00ffffff,0x99ffffff,0xffffffff};
    private val indicatorColor = intArrayOf(R.color.black, R.color.red)

    var moneyStr = "0.00"


    private val mScreenWidth: Int

    init {
        mScreenWidth = UtilTools.getScreenWidth(context)
        setBackgroundColor(-0x9cb9)
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        initAttr(attrs)
        initPaint()
    }

    fun setCurrentNumAnim(num: Int) {
        val duration = Math.abs(num - this.currentNum).toFloat() / maxNum * 1500 + 500 //根据进度差计算动画时间
        //        ObjectAnimator anim = ObjectAnimator.ofInt(this,"currentNum",maxNum,num);
        val anim = ObjectAnimator.ofInt(this, "currentNum", 0, num)
        anim.duration = Math.min(duration, 2000f).toLong()
        anim.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            // int color = calculateColor(value);
            //setBackgroundColor(color);
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
        anim.start()
    }

    private fun calculateColor(value: Int): Int {
        val evealuator = ArgbEvaluator()
        var fraction = 0f
        var color = 0
        if (value <= maxNum / 2) {
            fraction = value.toFloat() / (maxNum / 2)
            color = evealuator.evaluate(fraction, -0x9cb9, -0x7400) as Int //由红到橙
        } else {
            fraction = (value.toFloat() - maxNum / 2) / (maxNum / 2)
            color = evealuator.evaluate(fraction, -0x7400, -0xff312f) as Int //由橙到蓝
        }
        return color
    }

    private fun initPaint() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.isDither = true
        paint!!.style = Paint.Style.STROKE
        paint!!.color = -0x1
        //  paint.setColor(ContextCompat.getColor(context,R.color.magenta));
        paint_2 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint_3 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint_4 = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private fun initAttr(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RoundIndicatorView)
        maxNum = array.getInt(R.styleable.RoundIndicatorView_maxNum, 500)
        //        startAngle = array.getInt(R.styleable.RoundIndicatorView_startAngle,160);
        //        sweepAngle = array.getInt(R.styleable.RoundIndicatorView_sweepAngle,220);
        startAngle = array.getInt(R.styleable.RoundIndicatorView_startAngle2, 140)
        sweepAngle = array.getInt(R.styleable.RoundIndicatorView_sweepAngle2, 260)
        //内外圆的宽度
        sweepInWidth = dp2px(8)
        sweepOutWidth = dp2px(3)
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val wSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val wMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val hSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val hMode = View.MeasureSpec.getMode(heightMeasureSpec)

        if (wMode == View.MeasureSpec.EXACTLY) {
            mWidth = wSize
        } else {
            mWidth = dp2px(300)
            mWidth = mScreenWidth
        }
        if (hMode == View.MeasureSpec.EXACTLY) {
            mHeight = hSize
        } else {
            mHeight = (mWidth * 0.68).toInt()
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /*  radius = (int)(getMeasuredWidth()/3.2); //不要在构造方法里初始化，那时还没测量宽高
        canvas.save();
        canvas.translate((int)(mWidth/2),(int)(mWidth/2.3));*/
        radius = (measuredWidth / 3.2).toInt() //不要在构造方法里初始化，那时还没测量宽高
        radius = mWidth / 3 //不要在构造方法里初始化，那时还没测量宽高
        canvas.save()
        canvas.translate((mWidth / 2).toFloat(), (radius + radius / 4).toFloat())
        drawRound(canvas)  //画内外圆
        //drawScale(canvas);//画刻度
        drawIndicator(canvas) //画当前进度值
        drawCenterText(canvas)//画中间的文字
        canvas.restore()
    }

    private fun drawCenterText(canvas: Canvas) {
        canvas.save()
        paint_4!!.style = Paint.Style.FILL
        paint_4!!.textSize = (radius / 5).toFloat()
        paint_4!!.color = -0x1
        paint_4!!.color = ContextCompat.getColor(context, R.color.black)


        val ss = UtilTools.getMoney(moneyStr)

        canvas.drawText(ss + "", -paint_4!!.measureText(ss + "") / 2, 50f, paint_4!!)
        paint_4!!.textSize = (radius / 8).toFloat()
        paint_4!!.color = ContextCompat.getColor(context, R.color.gray)

        var content = "信用"
        if (this.currentNum < maxNum * 1 / 5) {
            content += text[0]
        } else if (this.currentNum >= maxNum * 1 / 5 && this.currentNum < maxNum * 2 / 5) {
            content += text[1]
        } else if (this.currentNum >= maxNum * 2 / 5 && this.currentNum < maxNum * 3 / 5) {
            content += text[2]
        } else if (this.currentNum >= maxNum * 3 / 5 && this.currentNum < maxNum * 4 / 5) {
            content += text[3]
        } else if (this.currentNum >= maxNum * 4 / 5) {
            content += text[4]
        }

        content = "可借额度(元)"
        val r = Rect()

        paint_4!!.getTextBounds(content, 0, content.length, r)
        // canvas.drawText(content,-r.width()/2,r.height()+20,paint_4);
        val width = paint_4!!.measureText(content) //相比getTextBounds来说，这个方法获得的类型是float，更精确些

        canvas.drawText(content, -width / 2, (-r.height() - 50).toFloat(), paint_4!!)

        canvas.restore()
    }

    private fun drawIndicator(canvas: Canvas) {
        canvas.save()
        paint_2!!.color = ContextCompat.getColor(context, R.color.font_c)
        val sweep: Int
        if (this.currentNum <= maxNum) {
            sweep = (this.currentNum.toFloat() / maxNum.toFloat() * sweepAngle).toInt()
        } else {
            sweep = sweepAngle
        }

        /*if (currentNum == 0){
            sweep = sweepAngle;
        }*/

        paint_2!!.strokeWidth = sweepOutWidth.toFloat()
        paint_2!!.strokeJoin = Paint.Join.ROUND
        paint_2!!.strokeCap = Paint.Cap.ROUND
        paint_2!!.style = Paint.Style.STROKE
        val w = dp2px(10)
        val rectf = RectF((-radius - w).toFloat(), (-radius - w).toFloat(), (radius + w).toFloat(), (radius + w).toFloat())
        if (sweep > 0) {
            canvas.drawArc(rectf, startAngle.toFloat(), sweep.toFloat(), false, paint_2!!)
        }
        paint_2!!.strokeWidth = sweepInWidth.toFloat()
        val rectf2 = RectF((-radius).toFloat(), (-radius).toFloat(), radius.toFloat(), radius.toFloat())
        if (sweep > 0) {
            canvas.drawArc(rectf2, startAngle.toFloat(), sweep.toFloat(), false, paint_2!!)
        }

        val x = ((radius + dp2px(10)) * Math.cos(Math.toRadians((startAngle + sweep).toDouble()))).toFloat()
        val y = ((radius + dp2px(10)) * Math.sin(Math.toRadians((startAngle + sweep).toDouble()))).toFloat()
        paint_3!!.style = Paint.Style.FILL
        paint_3!!.color = ContextCompat.getColor(context, R.color.font_c)
        // paint_3.setShader(shader);
        // paint_3.setColor(ContextCompat.getColor(context,R.color.red));
        paint_3!!.maskFilter = BlurMaskFilter(dp2px(3).toFloat(), BlurMaskFilter.Blur.SOLID) //需关闭硬件加速
        canvas.drawCircle(x, y, dp2px(3).toFloat(), paint_3!!)
        canvas.restore()
    }

    private fun drawScale(canvas: Canvas) {
        canvas.save()
        val angle = sweepAngle.toFloat() / 30//刻度间隔
        canvas.rotate((-270 + startAngle).toFloat()) //将起始刻度点旋转到正上方（270)
        for (i in 0..30) {
            if (i % 6 == 0) {   //画粗刻度和刻度值
                paint!!.strokeWidth = dp2px(2).toFloat()
                paint!!.alpha = 0x70
                //                paint.setAlpha(255);
                //canvas.drawLine(0, -radius-sweepInWidth/2,0, -radius+sweepInWidth/2+dp2px(1), paint);
                canvas.drawLine(0f, (-radius - sweepInWidth / 2).toFloat(), 0f, (-radius + sweepInWidth / 2).toFloat(), paint!!)
                //drawText(canvas,i*maxNum/30+"",paint);
                drawText(canvas, "", paint!!)
            } else {         //画细刻度
                paint!!.strokeWidth = dp2px(1).toFloat()
                paint!!.alpha = 0x50
                //                paint.setAlpha(255);
                canvas.drawLine(0f, (-radius - sweepInWidth / 2).toFloat(), 0f, (-radius + sweepInWidth / 2).toFloat(), paint!!)
            }
            if (i == 3 || i == 9 || i == 15 || i == 21 || i == 27) {  //画刻度区间文字
                paint!!.strokeWidth = dp2px(2).toFloat()
                paint!!.alpha = 0x50
                //                paint.setAlpha(255);
                //drawText(canvas,text[(i-3)/6], paint);
                drawText(canvas, "", paint!!)
            }
            canvas.rotate(angle) //逆时针
        }
        canvas.restore()
    }

    private fun drawText(canvas: Canvas, text: String, paint: Paint) {
        paint.style = Paint.Style.FILL
        paint.textSize = sp2px(2).toFloat()
        val width = paint.measureText(text) //相比getTextBounds来说，这个方法获得的类型是float，更精确些
        //        Rect rect = new Rect();
        //        paint.getTextBounds(text,0,text.length(),rect);

        //canvas.drawText(text,-width/2 , -radius + dp2px(15),paint);

        val r = Rect()
        paint.getTextBounds(text, 0, text.length, r)
        canvas.drawText(text, (-r.width() / 2).toFloat(), (r.height() + 20).toFloat(), paint_4!!)

        paint.style = Paint.Style.STROKE
    }

    private fun drawRound(canvas: Canvas) {
        canvas.save()
        paint!!.strokeJoin = Paint.Join.ROUND
        paint!!.strokeCap = Paint.Cap.ROUND
        //内圆
        paint!!.alpha = 0x40
        paint!!.style = Paint.Style.STROKE
        paint!!.color = ContextCompat.getColor(context, R.color.cricle_c)
        paint!!.strokeWidth = sweepInWidth.toFloat()
        val rectf = RectF((-radius).toFloat(), (-radius).toFloat(), radius.toFloat(), radius.toFloat())
        canvas.drawArc(rectf, startAngle.toFloat(), sweepAngle.toFloat(), false, paint!!)
        //外圆
        paint!!.strokeWidth = sweepOutWidth.toFloat()
        val w = dp2px(10)
        val rectf2 = RectF((-radius - w).toFloat(), (-radius - w).toFloat(), (radius + w).toFloat(), (radius + w).toFloat())

        canvas.drawArc(rectf2, startAngle.toFloat(), sweepAngle.toFloat(), false, paint!!)
        canvas.restore()
    }


    //一些工具方法
    protected fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                resources.displayMetrics).toInt()
    }

    protected fun sp2px(sp: Int): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp.toFloat(),
                resources.displayMetrics).toInt()
    }

    companion object {
        fun getScreenMetrics(context: Context): DisplayMetrics {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            return dm
        }
    }
}
