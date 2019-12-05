package com.lairui.easy.listener

import android.view.View

interface OnItemClickListener {
    fun onItemClickListener(view: View, position: Int, map: MutableMap<String, Any>)
}