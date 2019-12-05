package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.OnMyItemClickListener
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

class CompanyAdapter(private val mContext: Context, var datas: List<MutableMap<String, Any>>?) : RecyclerView.Adapter<CompanyAdapter.ViewHolder>() {

    var onMyItemClickListener: OnMyItemClickListener? = null

    private val mType = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.kind_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTextView!!.text = datas!![position]["firmname"]!!.toString() + ""
        holder.mLayout!!.setOnClickListener { v ->
            if (onMyItemClickListener != null) {
                onMyItemClickListener!!.OnMyItemClickListener(v, position)
            }
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return datas!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tv_bank_num)
        lateinit var mTextView: TextView
        @BindView(R.id.ll_layout)
        lateinit var mLayout: LinearLayout

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
