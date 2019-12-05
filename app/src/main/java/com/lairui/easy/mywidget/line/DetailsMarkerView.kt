package com.lairui.easy.mywidget.line

import android.content.Context
import android.widget.TextView

import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.lairui.easy.R

import java.math.BigDecimal

/**
 * @author Lai
 * @time 2018/5/13 17:32
 * @describe describe
 */

class DetailsMarkerView
/**
 * Constructor. Sets up the MarkerView with a custom layout resource.
 *
 * @param context
 */
(context: Context) : MarkerView(context, R.layout.item_chart_des_marker_item_3) {

    private val mTvMonth: TextView
    private val mTvChart1: TextView

    var list: List<String>? = null

    init {
        mTvMonth = findViewById(R.id.tv_chart_month)
        mTvChart1 = findViewById(R.id.tv_chart_1)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        try {
            //收入
            //            if (e.getY() == 0) {
            //                mTvChart1.setText("0.00");
            //            } else {
            mTvChart1.text = concat(e!!.y, "")
            //            }
            mTvMonth.text = list!![e.x.toInt()]
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        super.refreshContent(e, highlight)
    }


    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }


    fun concat(money: Float, values: String): String {
        return values + BigDecimal(money.toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "万元"
    }

}
