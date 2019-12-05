package com.lairui.easy.mywidget.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

import com.lairui.easy.R
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools

import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.ArrayList
import androidx.core.content.ContextCompat

class MyLineChartView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var originX: Int = 0 // 原点x坐标

    private var originY: Int = 0 // 原点y坐标

    private var firstPointX: Int = 0 //第一个点x坐标
    private val firstPointY: Int = 0 //第一个点y坐标

    private var firstMinX: Int = 0 // 移动时第一个点的最小x值

    private var firstMaxX: Int = 0 //移动时第一个点的最大x值

    private val intervalX: Int // x坐标刻度的间隔

    private val intervalY: Int // y轴刻度的间隔

    private var xValues: List<String>? = null

    private var yValues: List<Float>? = null

    private var mWidth: Int = 0 // 控件宽度

    private var mHeight: Int = 0 // 控件高度

    private val startX: Int = 0 // 滑动时上一次手指的x坐标值

    private val xyTextSize: Int //xy轴文字大小

    private val paddingTop: Int? // 默认上下左右的padding

    private val paddingLeft: Int?

    private val paddingRight: Int?

    private val paddingDown: Int

    private val scaleHeight: Int // x轴刻度线高度
    private val scaleLength: Int // Y轴刻度线长度

    private val textToXYAxisGap: Int // xy轴的文字距xy线的距离

    private val leftRightExtra: Int //x轴左右向外延伸的长度

    private val lableCountY = 6 // Y轴刻度个数

    private val bigCircleR: Int

    private val smallCircleR: Int

    private var minValueY: Float = 0.toFloat() // y轴最小值

    private var maxValueY = 0f // y轴最大值

    private val shortLine = 34 // 比例图线段长度

    private var paintWhite: Paint? = null
    private var paintBlue: Paint? = null
    private var paintPoint: Paint? = null
    private var paintRed: Paint? = null
    private var paintBack: Paint? = null
    private var paintText: Paint? = null
    private var p: Paint? = null

    private val backGroundColor = Color.parseColor("#ffffff") // view的背景颜色

    private val gestureDetector: GestureDetector

    private val legendTitle = ""

    private var addDashPath = 0 //平行于Y轴虚线突出长度

    init {
        gestureDetector = GestureDetector(context, MyOnGestureListener())

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = wm.defaultDisplay.width
        LogUtil.i("show", "屏幕宽度：$width")
        val interva = (width - 100) / 7
        LogUtil.i("show", "刻度间隔：$interva")
        intervalX = interva // x坐标刻度的间隔
        intervalY = interva // y轴刻度的间隔
        leftRightExtra = intervalX / 3

        xyTextSize = UtilTools.dip2px(context, 11)//xy轴文字大小

        paddingTop = UtilTools.dip2px(context, 5)// 默认上下左右的padding

        paddingLeft = UtilTools.dip2px(context, 50)

        paddingRight = UtilTools.dip2px(context, 15)

        paddingDown = UtilTools.dip2px(context, 20)

        scaleHeight = UtilTools.dip2px(context, 5) // x轴刻度线高度
        scaleLength = UtilTools.dip2px(context, 40) // Y轴刻度线长度

        textToXYAxisGap = UtilTools.dip2px(context, 10) // xy轴的文字距xy线的距离
        bigCircleR = UtilTools.dip2px(context, 3)
        smallCircleR = UtilTools.dip2px(context, 2)

        addDashPath = UtilTools.dip2px(context, 10)

        initPaint(context)
    }

    private fun initPaint(context: Context) {
        p = Paint(Paint.ANTI_ALIAS_FLAG)
        p!!.color = ContextCompat.getColor(context, R.color.line_background)
        p!!.style = Paint.Style.STROKE

        paintWhite = Paint(Paint.ANTI_ALIAS_FLAG)
        paintWhite!!.color = Color.BLACK
        paintWhite!!.style = Paint.Style.STROKE


        paintBlue = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBlue!!.color = ContextCompat.getColor(context, R.color.font_c)
        paintBlue!!.strokeWidth = 3f
        paintBlue!!.style = Paint.Style.STROKE

        paintPoint = Paint(Paint.ANTI_ALIAS_FLAG)
        paintPoint!!.color = ContextCompat.getColor(context, R.color.font_c)
        paintPoint!!.strokeWidth = 3f
        paintPoint!!.style = Paint.Style.FILL


        paintBack = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBack!!.color = Color.parseColor("#ffffff")
        paintBack!!.style = Paint.Style.FILL

        paintRed = Paint(Paint.ANTI_ALIAS_FLAG)
        paintRed!!.color = ContextCompat.getColor(context, R.color.font_c)
        paintRed!!.strokeWidth = 1f
        paintRed!!.textSize = UtilTools.sp2px(context, 12).toFloat()
        paintRed!!.style = Paint.Style.FILL

        paintText = Paint(Paint.ANTI_ALIAS_FLAG)
        paintText!!.color = Color.BLACK
        paintText!!.textSize = xyTextSize.toFloat()
        paintText!!.strokeWidth = 2f
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mWidth = width
        mHeight = height

        originX = paddingLeft!! - leftRightExtra
        originY = mHeight - paddingDown
        LogUtil.i("show", "originY:$originY  mheight:$mHeight")

        firstPointX = paddingLeft
        firstMinX = mWidth - originX - (xValues!!.size - 1) * intervalX - leftRightExtra
        // 滑动时，第一个点x值最大为paddingLeft，在大于这个值就不能滑动了
        firstMaxX = firstPointX
        setBackgroundColor(backGroundColor)
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {


        drawBiaoGeLine(canvas)
        //绘制折线
        drawBrokenLine(canvas)
        //绘制折线中的原点
        drawPoint(canvas)
        //绘制X轴
        drawX(canvas)
        //drawLegend(canvas);
        //绘制Y轴
        drawY(canvas)


    }

    private fun drawPoint(canvas: Canvas) {
        canvas.save()
        // 折线中的圆点
        val aver = (lableCountY - 1) * intervalY / (maxValueY - minValueY)
        for (i in yValues!!.indices) {
            //            canvas.drawCircle(firstPointX + i * intervalX,
            //                    mHeight - paddingDown - yValues.get(i) * aver + minValueY * aver, bigCircleR, paintPoint);
            /*  canvas.drawCircle(firstPointX + i * intervalX,
                    mHeight - paddingDown  - yValues.get(i) * aver + minValueY * aver, smallCircleR, paintBack);*/
            val df = DecimalFormat("0.00")
            df.roundingMode = RoundingMode.HALF_DOWN
            canvas.drawText(df.format(yValues!![i]) + "", (firstPointX + i * intervalX + 10).toFloat(), mHeight.toFloat() - paddingDown.toFloat() - yValues!![i] * aver + minValueY * aver, paintRed!!)

        }
        //将折线超出x轴坐标的部分截取掉（左边）
        paintBack!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        val rectF = RectF(0f, 0f, originX.toFloat(), mHeight.toFloat())
        canvas.drawRect(rectF, paintBack!!)
        canvas.restore()
    }

    /**
     * 画x轴
     *
     * @param canvas
     */
    private fun drawX(canvas: Canvas) {
        val path = Path()
        path.moveTo(originX.toFloat(), originY.toFloat())
        for (i in xValues!!.indices) {
            // x轴线
            path.lineTo((mWidth - paddingRight!!).toFloat(), originY.toFloat())  // 写死不变
            // x轴箭头
            canvas.drawLine((mWidth - paddingRight).toFloat(), originY.toFloat(), (mWidth - paddingRight - 15).toFloat(), (originY + 10).toFloat(), paintWhite!!)
            canvas.drawLine((mWidth - paddingRight).toFloat(), originY.toFloat(), (mWidth - paddingRight - 15).toFloat(), (originY + 10).toFloat(), p!!)
            canvas.drawLine((mWidth - paddingRight).toFloat(), originY.toFloat(), (mWidth - paddingRight - 15).toFloat(), (originY - 10).toFloat(), paintWhite!!)
            canvas.drawLine((mWidth - paddingRight).toFloat(), originY.toFloat(), (mWidth - paddingRight - 15).toFloat(), (originY - 10).toFloat(), p!!)

            // x轴线上的刻度线
            //firstPointX x轴起始点

            canvas.drawLine((firstPointX + i * intervalX).toFloat(), originY.toFloat(), (firstPointX + i * intervalX).toFloat(), (originY - scaleHeight).toFloat(), paintWhite!!)
            // x轴上的文字
            canvas.drawText(xValues!![i], (firstPointX + i * intervalX - getTextWidth(paintText!!, "17.01") / 2).toFloat(),
                    (originY + textToXYAxisGap + getTextHeight(paintText!!, "17.01")).toFloat(), paintText!!)
        }
        canvas.drawPath(path, paintWhite!!)


        //        // 平行于 x轴虚线
        //        Path path1 = new Path();
        //      DashPathEffect dash = new DashPathEffect(new float[]{8, 10, 8, 10}, 0);
        //        p.setPathEffect(dash);
        //        for (int i = 0; i < lableCountY; i++) {
        //            path1.moveTo(originX, mHeight - paddingDown  - i * intervalY);
        //            path1.lineTo(mWidth - paddingRight, mHeight - paddingDown  - i * intervalY);
        //        }
        //        canvas.drawPath(path1, p);
    }

    /**
     * 画折线
     *
     * @param canvas
     */
    private fun drawBrokenLine(canvas: Canvas) {
        canvas.save()
        // y轴文字
        minValueY = 0f
        for (i in yValues!!.indices) {
            // 找出y轴的最大最小值
            if (yValues!![i] > maxValueY) {
                maxValueY = yValues!![i]
            }
            if (yValues!![i] < minValueY) {
                minValueY = yValues!![i]
            }
        }


        if (maxValueY < 1.0f || maxValueY == 1.0f) {
            maxValueY = 1.0f

        } else if (maxValueY < 5.0f || maxValueY == 5.0f) {
            maxValueY = 5.0f

        } else if (maxValueY < 10f || maxValueY == 10f) {

            maxValueY = 10.0f
        }


        // 画折线
        val aver = (lableCountY - 1) * intervalY / (maxValueY - minValueY)
        val path = Path()

        //path.moveTo(firstPointX, originY);
        var endx = 0f
        var endy = 0f

        //背景色填充
        for (i in yValues!!.indices) {
            if (i == 0) {
                path.moveTo(firstPointX.toFloat(), mHeight.toFloat() - paddingDown.toFloat() - yValues!![i] * aver + minValueY * aver)
            } else {
                path.lineTo((firstPointX + i * intervalX).toFloat(), mHeight.toFloat() - paddingDown.toFloat() - yValues!![i] * aver + minValueY * aver)
                endx = (firstPointX + i * intervalX).toFloat()
                endy = mHeight.toFloat() - paddingDown.toFloat() - yValues!![i] * aver + minValueY * aver
            }


        }

        /*   //背景色渐变
        Shader mShader = new LinearGradient(endx,endy,endx,originY,new int[] {Color.RED,Color.TRANSPARENT},null,Shader.TileMode.CLAMP);
        //新建一个线性渐变，
        // 前两个参数是渐变开始的点坐标，
        // 第三四个参数是渐变结束的点的坐标。
        // 连接这2个点就拉出一条渐变线了，玩过PS的都懂。
        // 然后那个数组是渐变的颜色。下一个参数是渐变颜色的分布，
        // 如果为空，每个颜色就是均匀分布的。
        // 最后是模式，这里设置的是循环渐变

        paintBlue.setShader(mShader);
*/
        /*path.lineTo(firstPointX + (yValues.size()-1) * intervalX,originY);
        path.close();
        paintBlue.setStyle(Paint.Style.FILL);*/
        //getShadeColorPaint();
        canvas.drawPath(path, paintBlue!!)

        //将折线超出x轴坐标的部分截取掉（左边）
        paintBack!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        val rectF = RectF(0f, 0f, originX.toFloat(), mHeight.toFloat())
        canvas.drawRect(rectF, paintBack!!)
        canvas.restore()
    }


    /**
     * 画y轴
     *
     * @param canvas
     */
    private fun drawY(canvas: Canvas) {
        canvas.save()
        val path = Path()
        path.moveTo(originX.toFloat(), originY.toFloat())

        for (i in 0 until lableCountY) {
            // y轴线
            if (i == 0) {
                path.lineTo(originX.toFloat(), (mHeight - paddingDown).toFloat())
            } else {
                path.lineTo(originX.toFloat(), (mHeight - paddingDown - i * intervalY).toFloat())
            }

            //Y轴刻度线
            val lastPointY = mHeight - paddingDown - i * intervalY
            if (i == lableCountY - 1) {
                val lastY = lastPointY - leftRightExtra - leftRightExtra / 2
                // y轴最后一个点后，需要额外加上一小段，就是一个半leftRightExtra的长度
                canvas.drawLine(originX.toFloat(), lastPointY.toFloat(), originX.toFloat(), lastY.toFloat(), paintWhite!!)
                canvas.drawLine(originX.toFloat(), lastPointY.toFloat(), originX.toFloat(), lastY.toFloat(), p!!)
                // y轴箭头
                canvas.drawLine(originX.toFloat(), lastY.toFloat(), (originX - 10).toFloat(), (lastY + 15).toFloat(), paintWhite!!)
                canvas.drawLine(originX.toFloat(), lastY.toFloat(), (originX - 10).toFloat(), (lastY + 15).toFloat(), p!!)
                canvas.drawLine(originX.toFloat(), lastY.toFloat(), (originX + 10).toFloat(), (lastY + 15).toFloat(), paintWhite!!)
                canvas.drawLine(originX.toFloat(), lastY.toFloat(), (originX + 10).toFloat(), (lastY + 15).toFloat(), p!!)
            }


            val path1 = Path()
            path1.moveTo(originX.toFloat(), (mHeight - paddingDown - i * intervalY).toFloat())
            path1.lineTo(scaleLength.toFloat(), (mHeight - paddingDown - i * intervalY).toFloat())
            canvas.drawPath(path1, paintWhite!!)
            canvas.drawPath(path1, p!!)

        }
        canvas.drawPath(path, paintWhite!!)
        canvas.drawPath(path, p!!)


        var yTitles: MutableList<String> = ArrayList()
        if (maxValueY < 1.0f || maxValueY == 1.0f) {

            yTitles = ArrayList()
            for (i in 0 until lableCountY) {
                val s = (0.2 * i).toString() + ""
                yTitles.add(s.substring(0, 3))
                //LogUtil.i("show","num:"+decimalFormat.format(minValueY + i * space));
            }

        } else if (maxValueY < 5.0f || maxValueY == 5.0f) {
            yTitles = ArrayList()
            for (i in 0 until lableCountY) {
                yTitles.add((1 * i).toString() + ".0")
                //LogUtil.i("show","num:"+decimalFormat.format(minValueY + i * space));
            }


        } else if (maxValueY < 10.0f || maxValueY == 10.0f) {

            yTitles = ArrayList()
            for (i in 0 until lableCountY) {
                yTitles.add((2 * i).toString() + ".0")
                //LogUtil.i("show","num:"+decimalFormat.format(minValueY + i * space));
            }


        } else {
            // y轴文字
            var space = ((maxValueY - minValueY) / (lableCountY - 1)).toInt()
            Log.i("show", "spece:$space")
            // 设置Y轴刻度为 5或10 的倍数
            //判断个位是否是以5或者是0
            //10的倍数
            val douTen = space / 10
            //5的倍数
            val douFive = space / 5
            val digst = space % 10

            if (digst > 0 && digst < 5) {
                //以10的倍数为Y轴单位值
                if (digst < 3) {
                    space = (douTen + 1) * 10
                } else { //以5的倍数为Y轴单位值
                    space = (douFive + 1) * 5
                }
            }
            if (digst > 5 && digst < 9) {
                //以5的倍数为Y轴单位值
                if (digst < 8) {
                    space = (douFive + 1) * 5
                } else { //以10的倍数为Y轴单位值
                    space = (douTen + 1) * 10
                }
            }
            Log.i("show", "spece2:$space")

            val decimalFormat = DecimalFormat("")
            yTitles = ArrayList()
            for (i in 0 until lableCountY) {
                yTitles.add(decimalFormat.format((minValueY + i * space).toDouble()))
                //LogUtil.i("show","num:"+decimalFormat.format(minValueY + i * space));
            }
        }
        /*
        //设置开始单位为5 或 10的倍数
        //判断个位是否是以5或者是0
        //10的倍数
        int minTen = (int) (minValueY/10);
        //5的倍数
        int minFive = (int) (minValueY/5);
        int minDigst =  (int)minValueY % 10;


        // 小于10
        if (minTen == 0){
            if (minDigst < 5){
                minValueY = 0;
            }else {
                minValueY = 5;
            }
        }else {
            if (minDigst < 5){
                minValueY = minTen * 10;
            }else {
                minValueY = minFive*5;
            }
        }
*/


        //绘制Y轴数值
        for (i in yTitles.indices) {
            canvas.drawText(yTitles[i], (originX - getTextWidth(paintText!!, "00.00")).toFloat(),
                    (mHeight - paddingDown - i * intervalY + getTextHeight(paintText!!, "00.00") / 2).toFloat(), paintText!!)
        }
        // 截取折线超出部分（右边）
        paintBack!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        val rectF = RectF(mWidth.toFloat(), 0f, mWidth.toFloat(), mHeight.toFloat())
        canvas.drawRect(rectF, paintBack!!)
        canvas.restore()


        // 平行于y轴虚线


        //        Path path1 = new Path();
        //       /* DashPathEffect dash = new DashPathEffect(new float[]{8, 10, 8, 10}, 0);
        //        p.setPathEffect(dash);*/
        //        for (int i = 0; i < xValues.size(); i++) {
        //            path1.moveTo(firstPointX + i * intervalX, originY);
        //            path1.lineTo(firstPointX + i * intervalX, mHeight - paddingDown  - (lableCountY-1)* intervalY - addDashPath );
        //        }
        //        canvas.drawPath(path1, p);
    }

    private fun drawBiaoGeLine(canvas: Canvas) {

        // 平行于 x轴虚线
        val path1 = Path()
        /*  DashPathEffect dash = new DashPathEffect(new float[]{8, 10, 8, 10}, 0);
        p.setPathEffect(dash);*/
        for (i in 0 until lableCountY) {
            path1.moveTo(originX.toFloat(), (mHeight - paddingDown - i * intervalY).toFloat())
            path1.lineTo((mWidth - paddingRight!!).toFloat(), (mHeight - paddingDown - i * intervalY).toFloat())
        }
        canvas.drawPath(path1, p!!)

        //drawLegend(canvas);
        //绘制折线中的原点
        val path2 = Path()
        /* DashPathEffect dash = new DashPathEffect(new float[]{8, 10, 8, 10}, 0);
        p.setPathEffect(dash);*/
        for (i in xValues!!.indices) {
            path2.moveTo((firstPointX + i * intervalX).toFloat(), originY.toFloat())
            path2.lineTo((firstPointX + i * intervalX).toFloat(), (mHeight - paddingDown - (lableCountY - 1) * intervalY - addDashPath).toFloat())
        }
        canvas.drawPath(path2, p!!)
    }

    /**
     * 画图例
     */
    private fun drawLegend(canvas: Canvas) {
        // 开始点的坐标
        val x = 350
        val y = mHeight - (paddingDown - textToXYAxisGap - getTextHeight(paintText!!, "06.00")) / 2
        canvas.save()
        canvas.drawLine(x.toFloat(), y.toFloat(), (x + 2 * shortLine).toFloat(), y.toFloat(), paintBlue!!)
        canvas.drawCircle((x + shortLine).toFloat(), y.toFloat(), bigCircleR.toFloat(), paintBlue!!)
        canvas.drawCircle((x + shortLine).toFloat(), y.toFloat(), smallCircleR.toFloat(), paintBack!!)
        //canvas.drawText(legendTitle, x + 2 * shortLine + 10, y + getTextHeight(paintText, legendTitle) / 2 - 2, paintText);

        canvas.drawLine((x + 2 * shortLine + getTextWidth(paintText!!, legendTitle) + 20).toFloat(),
                y.toFloat(), (x + 2 * shortLine + getTextWidth(paintText!!, legendTitle) + 20 + 2 * shortLine).toFloat(), y.toFloat(), paintRed!!)
        canvas.drawCircle((x + 2 * shortLine + getTextWidth(paintText!!, legendTitle) + 20 + shortLine).toFloat(), y.toFloat(), bigCircleR.toFloat(), paintRed!!)
        canvas.drawCircle((x + 2 * shortLine + getTextWidth(paintText!!, legendTitle) + 20 + shortLine).toFloat(), y.toFloat(), smallCircleR.toFloat(), paintBack!!)
        /*canvas.drawText("护士填写", x + 2 * shortLine + getTextWidth(paintText, legendTitle) + 30 + 2 * shortLine,
                y + getTextHeight(paintText, legendTitle) / 2 - 2, paintText);*/
        canvas.restore()
    }


    /**
     * 手势事件
     */
    internal inner class MyOnGestureListener : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent): Boolean { // 按下事件
            return false
        }

        // 按下停留时间超过瞬时，并且按下时没有松开或拖动，就会执行此方法
        override fun onShowPress(motionEvent: MotionEvent) {}

        override fun onSingleTapUp(motionEvent: MotionEvent): Boolean { // 单击抬起
            return false
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            var distanceX = distanceX
            if (e1.x > originX && e1.x < mWidth - paddingRight!! &&
                    e1.y > paddingTop!! && e1.y < mHeight - paddingDown) {
                //注意：这里的distanceX是e1.getX()-e2.getX()
                distanceX = -distanceX
                if (firstPointX + distanceX > firstMaxX) {
                    firstPointX = firstMaxX
                } else if (firstPointX + distanceX < firstMinX) {
                    firstPointX = firstMinX
                } else {
                    firstPointX = (firstPointX + distanceX).toInt()
                }
                invalidate()
            }
            return false
        }

        override fun onLongPress(motionEvent: MotionEvent) {} // 长按事件

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return false
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (yValues!!.size < 7) {
            return false
        }
        gestureDetector.onTouchEvent(event)
        return true
    }

    fun setXValues(values: List<String>) {
        this.xValues = values
    }

    fun setYValues(values: List<Float>) {
        this.yValues = values
    }

    /**
     * 获取文字的宽度
     *
     * @param paint
     * @param text
     * @return
     */
    private fun getTextWidth(paint: Paint, text: String): Int {
        return paint.measureText(text).toInt()
    }

    /**
     * 获取文字的高度
     *
     * @param paint
     * @param text
     * @return
     */
    private fun getTextHeight(paint: Paint, text: String): Int {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height()
    }


    fun updateUI() {
        requestLayout()//执行onMeasure()方法和onLayout()方法
        invalidate()//执行onDraw()方法
    }


    // 修改笔的颜色
    private fun getShadeColorPaint() {
        paintBlue!!.style = Paint.Style.FILL
        val mShader = LinearGradient(300f, 50f, 300f, 400f,
                intArrayOf(Color.parseColor("#55FF7A00"), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
        // 新建一个线性渐变，前两个参数是渐变开始的点坐标，第三四个参数是渐变结束的点的坐标。连接这2个点就拉出一条渐变线了，玩过PS的都懂。然后那个数组是渐变的颜色。下一个参数是渐变颜色的分布，如果为空，每个颜色就是均匀分布的。最后是模式，这里设置的是循环渐变
        paintBlue!!.shader = mShader
    }
}