package com.lairui.easy.mywidget.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

import com.lairui.easy.listener.ShoushiPatternCallBack
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SPUtils

import java.util.ArrayList

/**
 * 手势登录
 */
class ShouShiPatternView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var i = 0

    private val pause = ArrayList<Int>()
    var cell: Array<Cell?>? = null
    lateinit var selectedCell: IntArray
    var RADIUS: Int = 0
    var OFFSET: Int = 0
    var ScreenWidth: Int = 0
    var ScreenHeight: Int = 0
    var startX: Int = 0
    var startY: Int = 0
    var selectedCount: Int = 0
    var lastX: Int = 0
    var lastY: Int = 0
    var drawFinish: Boolean = false
    var msg: String
    lateinit var mPaint: Paint

    init {
        init(context)
        msg = (SPUtils[context, MbsConstans.SharedInfoConstans.SHOW_SHOUSHI, "ture"] as String?).toString()
    }

    private fun initCell() {
        //初始化各点
        for (i in 0 until COUNT)
            for (j in 0 until COUNT) {
                cell?.get(i * COUNT + j)!!.isSelected = false
                cell?.get(i * COUNT + j)!!.x = startX + OFFSET * j - RADIUS / 2
                cell?.get(i * COUNT + j)!!.y = startY + OFFSET * i - RADIUS / 2
            }
    }

    private fun init(context: Context) {

        cell = arrayOfNulls<Cell>(COUNT * COUNT)
        selectedCell = IntArray(COUNT * COUNT)
        mPaint = Paint()
        //获取屏幕的宽度和高度
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        manager.defaultDisplay.getMetrics(dm)

        ScreenWidth = dm.widthPixels
        ScreenHeight = dm.heightPixels

        this.minimumWidth = ScreenWidth
        this.minimumHeight = ScreenHeight

        drawFinish = false //是否绘制完成
        selectedCount = 0 //已经选中的点个数
        RADIUS = ScreenWidth / 15 //半径
        OFFSET = ScreenWidth / 4 //点之间的间距
        startX = OFFSET //起始点横坐标
        startY = (ScreenHeight - OFFSET * 2) / 4 //起始点纵坐标

        for (i in 0 until COUNT * COUNT) {
            cell!![i] = Cell()
        }
        initCell()
    }

    fun inWhichCircle(x: Int, y: Int): Int {
        for (i in 0 until COUNT * COUNT) {
            if (cell?.get(i)!!.isSelected == false && Math.abs(x - cell?.get(i)!!.x) < RADIUS && Math.abs(y - cell?.get(i)!!.y) < RADIUS) {
                return i
            }
        }
        return -1
    }


    @SuppressLint("ResourceAsColor")
    internal fun drawCell(canvas: Canvas) {
        for (i in 0 until COUNT * COUNT) {
            //选择画笔&&画圆
            if (cell?.get(i)!!.isSelected) {
                //mPaint.setColor(Color.RED);
                //mPaint.setStrokeWidth(10);
                //画圆
                //  canvas.drawCircle(cell[i].getX(),cell[i].getY(),RADIUS,mPaint);
                //mPaint.setStrokeWidth(20);
                mPaint.color = Color.RED
                mPaint.style = Paint.Style.FILL
                //画点
                canvas.drawCircle(cell!![i]!!.x.toFloat(), cell!![i]!!.y.toFloat(), 40f, mPaint)
                // canvas.drawPoint(cell[i].getX(),cell[i].getY(),mPaint);
            } else {
                // mPaint.setColor(Color.WHITE);
                //mPaint.setStrokeWidth(5);
                //画圆
                // canvas.drawCircle(cell[i].getX(),cell[i].getY(),RADIUS,mPaint);
                //画点
                mPaint.color = Color.GRAY
                mPaint.style = Paint.Style.FILL
                canvas.drawCircle(cell!![i]!!.x.toFloat(), cell!![i]!!.y.toFloat(), 40f, mPaint)
                //canvas.drawPoint(cell[i].getX(),cell[i].getY(),mPaint);
            }

        }
    }

    internal fun drawLine(canvas: Canvas) {
        mPaint.color = Color.GREEN
        mPaint.strokeWidth = 5f
        for (i in 1 until selectedCount) {
            val lastCell = cell?.get(selectedCell[i - 1])
            val thisCell = cell?.get(selectedCell[i])
            canvas.drawLine(lastCell!!.x.toFloat(), lastCell.y.toFloat(), thisCell!!.x.toFloat(), thisCell.y.toFloat(), mPaint)
        }
        if (selectedCount != 0 && (lastX != 0 || lastY != 0)) {
            canvas.drawLine(cell?.get(selectedCell[selectedCount - 1])!!.x.toFloat(), cell?.get(selectedCell[selectedCount - 1])!!.y.toFloat(), lastX.toFloat(), lastY.toFloat(), mPaint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint = Paint()
        mPaint.strokeWidth = 5f
        mPaint.isAntiAlias = true
        mPaint.color = Color.GRAY
        mPaint.style = Paint.Style.FILL
        mPaint.style = Paint.Style.STROKE
        drawCell(canvas)
        drawLine(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var tmpIndex = 0
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawFinish = false
                tmpIndex = inWhichCircle(event.x.toInt(), event.y.toInt())
                if (tmpIndex != -1) {
                    cell?.get(tmpIndex)!!.isSelected = true
                    selectedCell[selectedCount++] = tmpIndex
                    if (msg == "ture") {
                        this.postInvalidate()
                    }
                    pause.add(tmpIndex)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (drawFinish == false) {
                    tmpIndex = inWhichCircle(event.x.toInt(), event.y.toInt())
                    if (tmpIndex != -1) {
                        cell?.get(tmpIndex)!!.isSelected = true
                        Log.i("show", "经过点的位置：$tmpIndex")
                        selectedCell[selectedCount++] = tmpIndex
                        pause.add(tmpIndex)
                    }
                }

                lastX = event.x.toInt()
                lastY = event.y.toInt()
                if (msg == "ture") {
                    this.postInvalidate()
                }
            }
            //绘制完毕
            MotionEvent.ACTION_UP -> {
                Log.i("show", "绘制的点集合：$pause")
                if (pause != null && pause.size > 0) {
                    if (i > 1) {
                        i = 0
                    }
                    drawFinish = true
                    lastY = 0
                    lastX = lastY
                    selectedCount = 0

                    initCell()
                    this.postInvalidate()
                    if (pause.toString().length < 15) {
                        callBack!!.finsh(2, pause)
                    } else {
                        callBack!!.finsh(i, pause)
                        i = i + 1
                    }
                    pause.clear()
                }
            }
        }
        return true
    }


    inner class Cell {
        var x: Int = 0
        var y: Int = 0
        var isSelected: Boolean = false
    }

    companion object {

        private var callBack: ShoushiPatternCallBack? = null

        fun setCallBack(callBack: ShoushiPatternCallBack) {
            ShouShiPatternView.callBack = callBack
        }

        private val COUNT = 3
    }
}
