package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.PDFLookActivity


class JkHetongAdapter(private val mContext: Context, private val mDatas: List<MutableMap<String, Any>>) : RecyclerView.Adapter<JkHetongAdapter.MyHolder>() {

    override fun getItemCount(): Int {
        return mDatas.size
    }


    override// 填充onCreateViewHolder方法返回的holder中的控件
    fun onBindViewHolder(holder: MyHolder, position: Int) {
        val map = mDatas[position]
        holder.mBankNameTv.text = map["pdfname"]!!.toString() + ""
        holder.mContentLay.setOnClickListener {
            val intent = Intent(mContext, PDFLookActivity::class.java)
            intent.putExtra("id", map["pdfurl"]!!.toString() + "")
            mContext.startActivity(intent)
        }
    }

    override// 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    fun onCreateViewHolder(arg0: ViewGroup, arg1: Int): MyHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_jk_hetong, null)
        return MyHolder(view)
    }

    // 定义内部类继承ViewHolder
    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mBankNameTv: TextView
        var mContentLay: RelativeLayout

        init {
            mBankNameTv = view.findViewById(R.id.item_name)
            mContentLay = view.findViewById(R.id.content_lay)
        }

    }


}