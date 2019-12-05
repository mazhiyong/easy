package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.viewpager.widget.ViewPager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lairui.easy.R

import java.util.ArrayList

class BankCardAdapter(context: Context) : ListBaseAdapter() {

    var onChangeBankCardListener: OnChangeBankCardListener? = null
    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_bank_card, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder


        val type = item["my_type"]!!.toString() + ""
        if (type == "0") {
            viewHolder.mTitleTv.visibility = View.GONE
        } else {
            viewHolder.mTitleTv.text = item["orgname"]!!.toString() + ""
            viewHolder.mTitleTv.visibility = View.VISIBLE
        }


        //            holder.mViewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        //                    48, mContext.getResources().getDisplayMetrics()));
        // holder.mViewPager.setPageTransformer(false, new ScaleTransformer(this));

        var childList: List<MutableMap<String, Any>>? = ArrayList()
        childList = item["card_list"] as List<MutableMap<String, Any>>?

        viewHolder.mRecyclerView.setHasFixedSize(true)
        viewHolder.mRecyclerView.isNestedScrollingEnabled = false
        viewHolder.mRecyclerView.setItemViewCacheSize(20)
        if (childList != null && childList.size > 0) {

            val linearLayoutManager = LinearLayoutManager(mContext)
            viewHolder.mRecyclerView.layoutManager = linearLayoutManager
            val mBandCardChildAdapter = mContext?.let { BankCardChildAdapter(it, childList!!, 1, onChangeBankCardListener, position) }
            viewHolder.mRecyclerView.adapter = mBandCardChildAdapter
        } else {
            if (childList == null) {
                childList = ArrayList()
            }
            val view = LayoutInflater.from(mContext).inflate(R.layout.item_bank_bind, null)
            val linearLayoutManager = LinearLayoutManager(mContext)
            viewHolder.mRecyclerView.layoutManager = linearLayoutManager
            val mBandCardChildAdapter = mContext?.let { BankCardChildAdapter(it, childList, 0, onChangeBankCardListener, position) }
            mBandCardChildAdapter?.addHeaderView(view)
            mBandCardChildAdapter?.patncode = item["patncode"]!!.toString() + ""
            viewHolder.mRecyclerView.adapter = mBandCardChildAdapter
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mTitleTv: TextView
        val mRecyclerView: RecyclerView
        private val mViewPager: ViewPager

        init {
            mTitleTv = itemView.findViewById(R.id.bank_title_tv)
            mRecyclerView = itemView.findViewById(R.id.bank_child_recycleview)
            mViewPager = itemView.findViewById(R.id.viewpager)
        }
    }


    interface OnChangeBankCardListener {
        /*void onAddNewBanCardListener(Map<String,Object> map);
        void onShowMoney(Map<String,Object> map);*/
        fun onButClickListener(type: String, map: MutableMap<String, Any>, bankCardChildAdapter: BankCardChildAdapter)
    }
}
