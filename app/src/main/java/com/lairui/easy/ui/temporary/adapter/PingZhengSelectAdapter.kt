package com.lairui.easy.ui.temporary.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.OnChildClickListener
import com.lairui.easy.ui.temporary.activity.InvoiceActivity
import com.lairui.easy.utils.tool.UtilTools

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

/**
 */
class PingZhengSelectAdapter(context: Context, private val mHeTongMap: MutableMap<String, Any>, private val dataType: Int) : ListBaseAdapter() {

    lateinit var booleanList: List<MutableMap<String, Any>>

    private lateinit var onChildClickListener: OnChildClickListener


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    private var ITEM_TYPE = -1

    fun setOnChildClickListener(onChildClickListener: OnChildClickListener) {
        this.onChildClickListener = onChildClickListener
    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun getItemCount(): Int {
        if (mDataList.size == 0) {
            ITEM_TYPE = 0
            return 1
        } else {
            ITEM_TYPE = 1
            return mDataList.size
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_pingzheng_list, parent, false))
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        if (ITEM_TYPE == 0) { //无数据
            viewHolder.mContentLay!!.visibility = View.GONE
            viewHolder.mTipTv!!.visibility = View.VISIBLE
            when (dataType) {
                0//凭证
                -> viewHolder.mTipTv!!.text = "暂无相关凭证数据"
                1 //发票
                -> {
                    viewHolder.mTipTv!!.text = "导入发票数据"
                    viewHolder.mTipTv!!.setOnClickListener { mContext!!.startActivity(Intent(mContext, InvoiceActivity::class.java)) }
                }
            }
        } else {  //有数据
            viewHolder.mContentLay!!.visibility = View.VISIBLE
            when (dataType) {
                0//凭证
                -> viewHolder.mTipTv!!.visibility = View.GONE
                1 //发票
                -> if (position + 1 == mDataList.size) { //列项最后一个显示
                    viewHolder.mTipTv!!.visibility = View.VISIBLE
                    viewHolder.mTipTv!!.text = "导入发票数据"
                    viewHolder.mTipTv!!.setOnClickListener { mContext!!.startActivity(Intent(mContext, InvoiceActivity::class.java)) }
                } else {
                    viewHolder.mContentLay!!.visibility = View.VISIBLE
                    viewHolder.mTipTv!!.visibility = View.GONE
                }
            }


            val item = mDataList[position]

            val bMap = booleanList!![position]

            val isSelect = (bMap["selected"] as Boolean?)!!
            viewHolder.mCheckBox!!.isChecked = isSelect

            val status = item["poolsta"]!!.toString() + ""
            if (status == "2") {
                viewHolder.mContentLay!!.setBackgroundResource(R.color.whitecc)
                viewHolder.mAllContentLay!!.isEnabled = false
                viewHolder.mCheckBox!!.isEnabled = false
                viewHolder.mCheckBox!!.visibility = View.GONE
                viewHolder.mCheckBox!!.isChecked = false
                viewHolder.mContentLay!!.isEnabled = false
            } else {
                viewHolder.mContentLay!!.setBackgroundResource(R.color.white)
                viewHolder.mAllContentLay!!.isEnabled = true
                viewHolder.mCheckBox!!.isEnabled = true
                viewHolder.mCheckBox!!.visibility = View.VISIBLE
                viewHolder.mCheckBox!!.isChecked = isSelect
                viewHolder.mContentLay!!.isEnabled = true
            }

            /*  for (Map<String,Object> map: mBooleanList){
            Map<String,Object> m = (Map<String, Object>) map.get("value");
            if (m == item){
                boolean b = (boolean) map.get("selected");
                if (b){
                    viewHolder.mCheckBox.setChecked(true);
                }else {
                    viewHolder.mCheckBox.setChecked(false);
                }
            }
        }*/

            viewHolder.mNameTv!!.text = item["payfirmname"]!!.toString() + ""
            viewHolder.mNumberTv!!.text = item["billid"]!!.toString() + ""
            viewHolder.mMoneyTv!!.text = UtilTools.getRMBMoney(item["paymoney"]!!.toString() + "")


            viewHolder.mCheckBox!!.setOnClickListener { viewHolder.mContentLay!!.performClick() }


            viewHolder.mContentLay!!.setOnClickListener {
                for (map in booleanList!!) {
                    val mSelectMap = map["value"] as MutableMap<String, Any>?
                    if (mSelectMap === item) {
                        val b = map["selected"] as Boolean
                        if (b) {
                            map.put("selected", false)
                        } else {
                            map.put("selected", true)
                        }
                    }
                }

                if (onChildClickListener != null) {
                    onChildClickListener!!.onChildClickListener(viewHolder.mContentLay!!, position, mHeTongMap)
                    notifyDataSetChanged()
                }
            }

        }


    }

    fun cancelSelectAll() {
        for (map in booleanList!!) {
            map.put("selected", false)
        }

        notifyDataSetChanged()
    }

    fun selectAll() {
        for (map in booleanList!!) {
            map.put("selected", true)
        }

        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.number_tv)
        lateinit var mNumberTv: TextView
        @BindView(R.id.name_tv)
        lateinit var mNameTv: TextView
        @BindView(R.id.money_tv)
        lateinit var mMoneyTv: TextView
        @BindView(R.id.content_lay)
        lateinit var mContentLay: LinearLayout
        @BindView(R.id.check_box)
        lateinit var mCheckBox: CheckBox
        @BindView(R.id.tv_tip)
        lateinit var mTipTv: TextView
        @BindView(R.id.all_content_lay)
        lateinit var mAllContentLay: LinearLayout


        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
