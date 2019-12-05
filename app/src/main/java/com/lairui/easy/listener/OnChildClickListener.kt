package com.lairui.easy.listener

import android.view.View

interface OnChildClickListener {
    fun onChildClickListener(view: View, position: Int, mParentMap: MutableMap<String, Any>)
}