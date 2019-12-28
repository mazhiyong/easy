package com.lairui.easy.ui.module2.adapter

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class TestArrayAdapter(val mContext: Context, val mStringArray: Array<String>) : ArrayAdapter<String?>(mContext, R.layout.simple_spinner_item, mStringArray){

    var type = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null){
            val  inflater = LayoutInflater.from(mContext)
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent,false)
        }
        return  view!!
    }

    override fun getDropDownView(position: Int,  convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null){
            val  inflater = LayoutInflater.from(mContext)
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent,false)
        }

        val  tv = view!!.findViewById(android.R.id.text1) as TextView
        tv.text = mStringArray[position]
        tv.setTextColor(ContextCompat.getColor(mContext,R.color.black))
        if (position == type){
            if (type == 0 ){
                tv.setTextColor(ContextCompat.getColor(mContext,R.color.black))
            }else{
                tv.setTextColor(ContextCompat.getColor(mContext,R.color.holo_blue_light))
            }
        }

        return view
    }

}
