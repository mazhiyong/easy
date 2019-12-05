package com.lairui.easy.mywidget.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

import com.lairui.easy.R


/**
 * RecyclerView的HeaderView，简单的展示一个TextView
 */
class SampleHeader : RelativeLayout {
    internal var resId: Int = 0
    fun init(context: Context, resId: Int) {

        View.inflate(context, resId, this)
    }

    constructor(context: Context, resId: Int) : super(context) {
        init(context, resId)
    }

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

        View.inflate(context, R.layout.sample_header, this)
    }
}