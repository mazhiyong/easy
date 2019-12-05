package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.OnSearchItemClickListener

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class BankWdAdapter(context: Context) : ListBaseAdapter(), Filterable {


    private val mLayoutInflater: LayoutInflater
    private var mSourceList: MutableList<MutableMap<String, Any>> = ArrayList()
    var onSearchItemClickListener: OnSearchItemClickListener? = null

    var sourceList: MutableList<MutableMap<String, Any>>
        get() = mSourceList
        set(sourceList) {
            mSourceList = sourceList
        }


    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_bank_wd, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        viewHolder.mItemName!!.text = item["opnbnkwdnm"]!!.toString() + ""

        viewHolder.mWangdianLay!!.setOnClickListener {
            if (onSearchItemClickListener != null) {
                onSearchItemClickListener!!.OnSearchItemClickListener(item)
            }
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mDataList = mSourceList
                } else {
                    val filteredList = ArrayList<MutableMap<String, Any>>()
                    for (map in mSourceList) {
                        val str1 = map["opnbnkwdnm"]!!.toString() + ""
                        //这里根据需求，添加匹配规则
                        if (str1.contains(charString)) {
                            filteredList.add(map)
                        }
                    }

                    mDataList = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = mDataList
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                mDataList = (filterResults.values as List<MutableMap<String, Any>>).toMutableList()
                notifyDataSetChanged()
            }
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.item_name)
        lateinit var mItemName: TextView
        @BindView(R.id.wangdian_lay)
        lateinit var mWangdianLay: RelativeLayout

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}