package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.lairui.easy.R
import com.lairui.easy.utils.tool.UtilTools


class HomeGridViewAdapter(private val context: Context, private val list: List<MutableMap<String, Any>>?) : BaseAdapter() {
    private val imageWidth: Int

    init {
        imageWidth = UtilTools.getScreenWidth(context) / 2 - 15
    }

    override fun getCount(): Int {
        return list?.size ?: 0
    }

    override fun getItem(position: Int): Any {
        return list!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        val map = list!![position]
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.home_grid_item, null)
            viewHolder = ViewHolder()
            viewHolder.image = convertView!!.findViewById<View>(R.id.home_img) as ImageView
            viewHolder.name = convertView.findViewById<View>(R.id.home_name) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        Glide.with(context)
                .load(map["url"]!!.toString() + "")
                .into(viewHolder.image!!)
        viewHolder.name!!.text = map["name"]!!.toString() + ""
        return convertView
    }

    internal inner class ViewHolder {
        var name: TextView? = null
        var image: ImageView? = null
    }
} 


