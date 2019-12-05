package com.lairui.easy.ui.temporary.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.OnItemClickListener

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class FukuanFangSelectAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    lateinit var onItemClickListener: OnItemClickListener

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_fukuanfang_list, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        viewHolder.mNameTv!!.text = item["payfirmname"]!!.toString() + ""

        viewHolder.mCheckBox!!.setOnClickListener { viewHolder.mItemLay!!.performClick() }
        viewHolder.mItemLay!!.setOnClickListener { v ->
            viewHolder.mCheckBox!!.isChecked = true
            if (onItemClickListener != null) {
                onItemClickListener!!.onItemClickListener(v, position, item)
                viewHolder.mCheckBox!!.isChecked = false
            }
            /* Intent intent = new Intent(mContext, ShouldShouMoneyActivity.class);
                intent.putExtra("paycustid",item.get("paycustid")+"");
                mContext.startActivity(intent);*/
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.name_tv)
        lateinit var mNameTv: TextView
        @BindView(R.id.item_lay)
        lateinit var mItemLay: CardView
        @BindView(R.id.check_box)
        lateinit var mCheckBox: CheckBox

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
