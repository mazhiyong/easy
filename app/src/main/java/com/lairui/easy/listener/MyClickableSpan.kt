package com.lairui.easy.listener

import androidx.annotation.ColorInt
import android.text.TextPaint
import android.view.View

class MyClickableSpan(@param:ColorInt private val color: Int, private val listener: View.OnClickListener) : android.text.style.ClickableSpan() {

    override fun onClick(widget: View) {
        listener.onClick(widget)
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
        ds.color = color
    }
}
