package com.lairui.easy.mywidget.line

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet

import java.lang.ref.WeakReference

/**
 * @author Lai
 * @time 2018/5/26 14:27
 * @describe describe
 */

class MyLineChart : LineChart {

    //弱引用覆盖物对象,防止内存泄漏
    private var mDetailsReference: WeakReference<DetailsMarkerView>? = null
    private var mRoundMarkerReference: WeakReference<RoundMarker>? = null
    private var mPositionMarkerReference: WeakReference<PositionMarker>? = null

    /**
     * 所有覆盖物是否为空
     *
     * @return TRUE FALSE
     */
    val isMarkerAllNull: Boolean
        get() = mDetailsReference!!.get() == null && mRoundMarkerReference!!.get() == null && mPositionMarkerReference!!.get() == null

    fun setDetailsMarkerView(detailsMarkerView: DetailsMarkerView) {
        mDetailsReference = WeakReference(detailsMarkerView)
    }

    fun setRoundMarker(roundMarker: RoundMarker) {
        mRoundMarkerReference = WeakReference(roundMarker)
    }

    fun setPositionMarker(positionMarker: PositionMarker) {
        mPositionMarkerReference = WeakReference(positionMarker)
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}


    /**
     * draws all MarkerViews on the highlighted positions
     */
    override fun drawMarkers(canvas: Canvas) {

        // if there is no marker view or drawing marker is disabled
        val mDetailsMarkerView = mDetailsReference!!.get()
        val mRoundMarker = mRoundMarkerReference!!.get()
        val mPositionMarker = mPositionMarkerReference!!.get()

        if (mDetailsMarkerView == null || mRoundMarker == null || mPositionMarker == null || !isDrawMarkersEnabled || !valuesToHighlight())
            return

        for (i in mIndicesToHighlight.indices) {

            val highlight = mIndicesToHighlight[i]

            val set = mData.getDataSetByIndex(highlight.dataSetIndex)

            val e = mData.getEntryForHighlight(mIndicesToHighlight[i])

            val entryIndex = set.getEntryIndex(e)

            // make sure entry not null
            if (e == null || entryIndex > set.entryCount * mAnimator.phaseX)
                continue

            val pos = getMarkerPosition(highlight)

            val dataSetByIndex = lineData.getDataSetByIndex(highlight.dataSetIndex) as LineDataSet

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                continue

            val circleRadius = dataSetByIndex.circleRadius

            // callbacks to update the content
            mDetailsMarkerView.refreshContent(e, highlight)

            mDetailsMarkerView.draw(canvas, pos[0], pos[1] - mPositionMarker.height)


            mPositionMarker.refreshContent(e, highlight)
            mPositionMarker.draw(canvas, pos[0] - mPositionMarker.width / 2, pos[1] - mPositionMarker.height)

            mRoundMarker.refreshContent(e, highlight)
            mRoundMarker.draw(canvas, pos[0] - mRoundMarker.width / 2, pos[1] + circleRadius - mRoundMarker.height)
        }
    }


}
