package com.lairui.easy.utils.face

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

import com.lairui.easy.R


class CircleProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val textPaint: TextPaint
    var progress = 100
        set(progress) {
            field = progress
            invalidate()
        }
    var max = 100
    private val paint: Paint
    private val oval: RectF
    internal var Width: Int = 20
    private var Radius = 75
    private val bit: Bitmap

    internal var sweepGradient: SweepGradient? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        val use = if (width > height) height else width
        val sum = Width + Radius
        Width = use / 2 * Width / sum
        Radius = use / 2 * Radius / sum
        setMeasuredDimension(use, use)
    }

    init {
        paint = Paint()
        oval = RectF()
        textPaint = TextPaint()
        bit = BitmapFactory.decodeResource(resources, R.drawable.circle)
        sweepGradient = SweepGradient((width / 2).toFloat(), (height / 2).toFloat(), intArrayOf(-0x16572, -0xc02e1c, -0x236972), null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //        oval.set(0,0,getWidth(), getHeight());

        //        canvas.drawBitmap(bit,null, oval, paint);
        paint.isAntiAlias = true
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.color = -0x1000000
        paint.strokeWidth = Width.toFloat()// 设置画笔宽度
        paint.style = Paint.Style.STROKE// 设置中空的样�?
        canvas.drawCircle((Radius + Width).toFloat(), (Radius + Width).toFloat(), Radius.toFloat(), paint)// 在中心为�?00,100）的地方画个半径�?5的圆，宽度为setStrokeWidth�?0，也就是灰色的底�?


        //        paint.setShader(sweepGradient);
        paint.color = -0xc02e1c// 设置画笔为绿�?
        oval.set(Width.toFloat(), Width.toFloat(), (Radius * 2 + Width).toFloat(), (Radius * 2 + Width).toFloat())// 设置类似于左上角坐标�?5,45），右下角坐标（155,155），这样也就保证了半径为55
        canvas.drawArc(oval, -90f, this.progress.toFloat() / max * 360, false, paint)// 画圆弧，第二个参数为：起始角度，第三个为跨的角度，第四个为true的时候是实心，false的时候为空心
        paint.reset()
        //        textPaint.setStrokeWidth(3);
        //        textPaint.setTextSize(getMeasuredWidth() / 3);// 设置文字的大�?
        //        textPaint.setColor(0xff318deb);// 设置画笔颜色
        //        textPaint.setTextAlign(Paint.Align.CENTER);
        //        FontMetrics fontMetrics= textPaint.getFontMetrics();
        //        float baseX = Radius + Width;
        //        float baseY = Radius + Width;
        //        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        //        if (progress == max) {
        //            // canvas.drawText("完成", baseX, baseY - fontMetrics.descent
        //            // + (textHeight) / 2, paint);
        //            if (bit!=null)
        //            canvas.drawBitmap(bit, Radius + Width - bit.getHeight() / 2,
        //                    Radius + Width - bit.getHeight() / 2, paint);
        //        } else {
        //            canvas.drawText(progress * 100 / max + "%", baseX, baseY - fontMetrics.descent
        //                    + (textHeight) / 2, textPaint);
        //
        //        }
    }
}
