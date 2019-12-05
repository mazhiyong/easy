package com.lairui.easy.mywidget.view

import android.content.Context
import androidx.viewpager.widget.ViewPager
import androidx.cardview.widget.CardView
import android.util.TypedValue
import android.view.View

import com.lairui.easy.R


class ScaleTransformer(private val context: Context) : ViewPager.PageTransformer {
    private val MINALPHA = 0.8f
    private val elevation: Float

    init {
        elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2f, context.resources.displayMetrics)
    }

    /**
     * position取值特点：
     * 假设页面从0～1，则：
     * 第一个页面position变化为[0,-1]
     * 第二个页面position变化为[1,0]
     *
     * @param page
     * @param position
     */
    override fun transformPage(page: View, position: Float) {
        val cardView = page.findViewById<CardView>(R.id.bank_card_lay2)
        if (position < -1 || position > 1) {
            page.alpha = MINALPHA
        } else {
            //不透明->半透明
            if (position < 0) {//[0,-1]
                page.alpha = MINALPHA + (1 + position) * (1 - MINALPHA)
                cardView.cardElevation = (1 + position) * elevation
            } else {//[1,0]
                //半透明->不透明
                page.alpha = MINALPHA + (1 - position) * (1 - MINALPHA)

                cardView.cardElevation = (1 - position) * elevation
            }
        }
    }


}