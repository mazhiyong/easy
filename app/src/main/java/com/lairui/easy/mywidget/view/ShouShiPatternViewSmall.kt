package com.lairui.easy.mywidget.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager

import java.util.ArrayList

/**
 * 手势登录（小）
 */
class ShouShiPatternViewSmall(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var i = 0

    private val pause = ArrayList<Int>()
    lateinit var cell: Array<Cell?>
    lateinit var selectedCell: IntArray
    internal var RADIUS: Int = 0
    internal var OFFSET: Int = 0
    internal var ScreenWidth: Int = 0
    internal var ScreenHeight: Int = 0
    internal var startX: Int = 0
    internal var startY: Int = 0
    internal var selectedCount: Int = 0
    internal var lastX: Int = 0
    internal var lastY: Int = 0
    internal var drawFinish: Boolean = false

    lateinit var mPaint: Paint

    init {
        init(context)
    }

    //将已选择的点，进行标记
    fun cellSeleced(list: List<Int>) {
        i = 0
        while (i < 9) {
            cell[i]!!.isSelected = false
            i++
        }
        i = 0
        while (i < list.size) {
            Log.i("show", ">>>:" + list[i])
            val num = list[i]
            if (num < 0) {
                return
            }
            cell[list[i]]!!.isSelected = true
            i++
        }


        /* int[] arr = new int[str.length()];
        for(int i=0; i<str.length(); i++){
           // arr[i] = Integer.parseInt(str.substring(i,i+1));
            cell[Integer.parseInt(str.substring(i,i+1))].setIsSelected(true);
        }
*/

    }

    private fun initCell() {
        //初始化各点
        for (i in 0 until COUNT)
            for (j in 0 until COUNT) {
                cell[i * COUNT + j]!!.isSelected = false
                cell[i * COUNT + j]!!.x = startX + OFFSET * j - RADIUS / 2
                cell[i * COUNT + j]!!.y = startY + OFFSET * i - RADIUS / 2
            }
    }

    private fun init(context: Context) {

        cell = arrayOfNulls(COUNT * COUNT)
        selectedCell = IntArray(COUNT * COUNT)
        mPaint = Paint()
        //获取屏幕的宽度和高度
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        manager.defaultDisplay.getMetrics(dm)

        ScreenWidth = dm.widthPixels / 4
        ScreenHeight = dm.heightPixels / 6

        this.minimumWidth = ScreenWidth
        this.minimumHeight = ScreenHeight

        drawFinish = false //是否绘制完成
        selectedCount = 0 //已经选中的点个数
        RADIUS = ScreenWidth / 15 //半径
        OFFSET = ScreenWidth / 4 //点之间的间距
        startX = OFFSET * 7 //起始点横坐标
        startY = (ScreenHeight - OFFSET * 2) / 4 //起始点纵坐标

        for (i in 0 until COUNT * COUNT) {
            cell[i] = Cell()
        }
        initCell()
    }

    @SuppressLint("ResourceAsColor")
    internal fun drawCell(canvas: Canvas) {
        for (i in 0 until COUNT * COUNT) {
            //选择画笔&&画圆
            if (cell[i]!!.isSelected) {
                //mPaint.setColor(Color.RED);
                //mPaint.setStrokeWidth(10);
                //画圆
                //  canvas.drawCircle(cell[i].getX(),cell[i].getY(),RADIUS,mPaint);
                //mPaint.setStrokeWidth(20);
                mPaint.color = Color.RED
                mPaint.style = Paint.Style.FILL
                //画点
                canvas.drawCircle(cell[i]!!.x.toFloat(), cell[i]!!.y.toFloat(), 20f, mPaint)
                // canvas.drawPoint(cell[i].getX(),cell[i].getY(),mPaint);
            } else {
                // mPaint.setColor(Color.WHITE);
                //mPaint.setStrokeWidth(5);
                //画圆
                // canvas.drawCircle(cell[i].getX(),cell[i].getY(),RADIUS,mPaint);
                //画点
                mPaint.color = Color.GRAY
                mPaint.style = Paint.Style.FILL
                canvas.drawCircle(cell[i]!!.x.toFloat(), cell[i]!!.y.toFloat(), 20f, mPaint)
                //canvas.drawPoint(cell[i].getX(),cell[i].getY(),mPaint);
            }

        }
    }

    internal fun drawLine(canvas: Canvas) {
        mPaint.color = Color.GREEN
        mPaint.strokeWidth = 5f
        for (i in 1 until selectedCount) {
            val lastCell = cell[selectedCell[i - 1]]
            val thisCell = cell[selectedCell[i]]
            canvas.drawLine(lastCell!!.x.toFloat(), lastCell.y.toFloat(), thisCell!!.x.toFloat(), thisCell.y.toFloat(), mPaint)
        }
        if (selectedCount != 0 && (lastX != 0 || lastY != 0)) {
            canvas.drawLine(cell[selectedCell[selectedCount - 1]]!!.x.toFloat(), cell[selectedCell[selectedCount - 1]]!!.y.toFloat(), lastX.toFloat(), lastY.toFloat(), mPaint)
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


    inner class Cell {
        var x: Int = 0
        var y: Int = 0
        var isSelected: Boolean = false
    }

    companion object {
        private val COUNT = 3
    }
}
