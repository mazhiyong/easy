package com.lairui.easy.mywidget.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.lairui.easy.R

/**
 * ImageView创建工厂
 */
object ViewFactory {

    /**
     * 获取ImageView视图的同时加载显示url
     *
     * @param url
     * @return
     */
    fun getImageView(context: Context, url: String): ImageView {
        val imageView = LayoutInflater.from(context).inflate(
                R.layout.item_view_banner, null) as ImageView
        Glide.with(context).load(url).into(imageView)
        return imageView
    }
}
