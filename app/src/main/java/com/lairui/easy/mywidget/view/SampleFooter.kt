package com.lairui.easy.mywidget.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

import com.lairui.easy.R


/**
 * RecyclerView的FooterView，简单的展示一个TextView
 */
class SampleFooter : RelativeLayout {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context) {

        View.inflate(context, R.layout.sample_footer, this)
    }
}