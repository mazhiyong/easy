package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.tool.PingYinUtil

import java.util.Collections
import java.util.ConcurrentModificationException

import butterknife.BindView
import butterknife.ButterKnife

class BankNameListAdapter(context: Context) : ListBaseAdapter() {

    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_bank_name, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder


        val catalog = PingYinUtil.getPingYin((item["opnbnknm"]!!.toString() + "").substring(0, 1))
        if (position == 0) {
            viewHolder.mTitleTv!!.visibility = View.VISIBLE
            viewHolder.mTitleTv!!.text = catalog
        } else {
            val lastCatalog = PingYinUtil.getPingYin((mDataList[position - 1]["opnbnknm"]!!.toString() + "").substring(0, 1))
            if (catalog == lastCatalog) {
                viewHolder.mTitleTv!!.visibility = View.GONE
            } else {
                viewHolder.mTitleTv!!.visibility = View.VISIBLE
                viewHolder.mTitleTv!!.text = catalog
            }
        }


        val name = item["opnbnknm"]!!.toString() + ""
        GlideUtils.loadCircleImage(mContext!!, item["logopath"]!!.toString() + "", viewHolder.mImageView!!)

        viewHolder.mNameValueTv!!.text = name
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.image_view)
        lateinit var mImageView: ImageView
        @BindView(R.id.name_value_tv)
        lateinit var mNameValueTv: TextView
        @BindView(R.id.title_tv)
        lateinit var mTitleTv: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }


    fun simpleOrder() {
        if (mDataList != null && mDataList.size > 0) {
            try {
                Collections.sort(mDataList) { arg0, arg1 ->
                    // TODO Auto-generated method stub
                    val str1 = PingYinUtil.getPingYin2(arg0["opnbnknm"]!!.toString() + "")
                    val str2 = PingYinUtil.getPingYin2(arg1["opnbnknm"]!!.toString() + "")
                    str1.compareTo(str2)
                }
            } catch (e: ConcurrentModificationException) {
            }

        }
    }


    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    fun getPositionForSection(s: String): Int {
        for (i in 0 until itemCount) {
            val item = mDataList[i]
            val catalog = PingYinUtil.getPingYin((item["opnbnknm"]!!.toString() + "").substring(0, 1))
            if (catalog == s) {
                return i
            }
        }
        return -1
    }

}
