package com.lairui.easy.ui.temporary.adapter

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
import com.lairui.easy.ui.temporary.activity.SelectPingZhengActivity
import com.lairui.easy.utils.tool.UtilTools

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HeTongSelectAdapter(private val mContext: Context, private val mDatas: List<MutableMap<String, Any>>, private val mOnChangeBankCardListener: OnChangeBankCardListener?) : RecyclerView.Adapter<HeTongSelectAdapter.MyHolder>() {

    var booleanList: List<MutableMap<String, Any>> = ArrayList()

    private val ITEM_TYPE_NORMAL = 0
    private val ITEM_TYPE_HEADER = 1
    private val ITEM_TYPE_FOOTER = 2
    private val ITEM_TYPE_EMPTY = 3
    private var mHeaderView: View? = null
    private var mFooterView: View? = null
    private var mEmptyView: View? = null

    var patncode = ""

    private val mParentPosition: Int = 0

    private val mLayoutInflater: LayoutInflater


    private var mClickListener: OnItemClickListener? = null

    fun setClickListener(clickListener: (Any, Any) -> Unit) {
        clickListener as OnItemClickListener
        mClickListener = clickListener
    }

    init {
        mLayoutInflater = LayoutInflater.from(mContext)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        var itemCount = mDatas.size
        if (null != mEmptyView && itemCount == 0) {
            itemCount++
        }
        if (null != mHeaderView) {
            itemCount++
        }
        if (null != mFooterView) {
            itemCount++
        }
        return itemCount
    }

    override fun getItemViewType(position: Int): Int {
        if (null != mHeaderView && position == 0) {
            return ITEM_TYPE_HEADER
        }
        if (null != mFooterView && position == itemCount - 1) {
            return ITEM_TYPE_FOOTER
        }
        return if (null != mEmptyView && mDatas.size == 0) {
            ITEM_TYPE_EMPTY
        } else ITEM_TYPE_NORMAL
    }

    fun addHeaderView(view: View) {
        mHeaderView = view
        notifyItemInserted(0)
    }

    fun addFooterView(view: View) {
        mFooterView = view
        notifyItemInserted(itemCount - 1)
    }

    fun setEmptyView(view: View) {
        mEmptyView = view
        notifyDataSetChanged()
    }


    override// 填充onCreateViewHolder方法返回的holder中的控件
    fun onBindViewHolder(holder: MyHolder, position: Int) {
        val type = getItemViewType(position)
        if (type == ITEM_TYPE_HEADER) {

        } else if (type == ITEM_TYPE_FOOTER) {

            //添加合同
            holder.addHtLay?.setOnClickListener {
                val map = HashMap<String, Any>()
                mOnChangeBankCardListener?.onButClickListener("1", map)
            }
        } else if (type == ITEM_TYPE_EMPTY) {

        } else {
            //合同列表
            val map = mDatas[position]
            val bo = booleanList[position]
            holder.mHetongCodeTv?.text = map["contno"]!!.toString() + ""
            holder.mHetongDateTv?.text = map["contsgndt"]!!.toString() + ""
            holder.mHetongMoneyTv?.text = UtilTools.getMoney(map["contmny"]!!.toString() + "")

            val mDataChildList = bo["list"] as List<MutableMap<String, Any>>?

            val b = (bo["selected"] as Boolean?)!!

            if (b) {
                //holder.mCheckBox.setChecked(true);
                holder.mShowTv?.text = "收起"
                holder.mRecyclerView?.visibility = View.VISIBLE

            } else {
                //holder.mCheckBox.setChecked(false);
                holder.mShowTv?.text = "展开"
                holder.mRecyclerView?.visibility = View.GONE
            }

            if (holder.mShowTv?.text.toString() == "收起") {
                holder.mRecyclerView?.visibility = View.VISIBLE
            } else {
                holder.mRecyclerView?.visibility = View.GONE
            }


            /*  holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mContentLay.performClick();
                }
            });*/

            holder.mShowTv?.setOnClickListener { holder.mContentLay?.performClick() }

            holder.mContentLay?.setOnClickListener {
                for (m in booleanList) {
                    val mSelectMap = m["value"] as MutableMap<String, Any>?
                    if (mSelectMap === map) {
                        val b = m["selected"] as Boolean
                        if (b) {
                            m.put("selected", false)
                        } else {
                            m.put("selected", true)
                        }
                    } else {
                        m.put("selected", false)
                    }
                }
                if (mOnChangeBankCardListener != null && holder.mShowTv?.text.toString().trim { it <= ' ' } == "展开") {
                    mOnChangeBankCardListener.onButClickListener("2", map)
                    holder.mShowTv?.text = "收起"
                } else {
                    holder.mShowTv?.text = "展开"
                    holder.mRecyclerView?.visibility = View.GONE
                }
            }

            holder.mRelativeTv?.setOnClickListener {
                val intent = Intent(mContext, SelectPingZhengActivity::class.java)
                intent.putExtra("DATA", map as Serializable)
                mContext.startActivity(intent)
            }


            val mBooleanChildList = ArrayList<MutableMap<String, Any>>()
            for (m in mDataChildList!!) {
                val map2 = HashMap<String, Any>()
                map2["value"] = m
                val status = m["poolsta"]!!.toString() + ""
                if (status == "2") {
                    map2["selected"] = false
                } else {
                    map2["selected"] = true
                }
                mBooleanChildList.add(map2)
            }


            //dataType 数据类型 ：0 凭证  1 发票
            val dataType = 0
            val mAdapter = PingZhengSelectAdapter(mContext, map, dataType)
            mAdapter.booleanList = mBooleanChildList
            mAdapter.addAll(mDataChildList)

            val manager = LinearLayoutManager(mContext)
            holder.mRecyclerView?.layoutManager = manager
            holder.mRecyclerView?.adapter = mAdapter


            mAdapter.setOnChildClickListener(object : OnChildClickListener {
                override fun onChildClickListener(view: View, position: Int, mParentMap: MutableMap<String, Any>) {

                    holder.mRecyclerView?.post {
                        mAdapter.notifyDataSetChanged()
                        //HeTongSelectAdapter.this.notifyDataSetChanged();
                    }
                    if (mClickListener != null) {
                        mClickListener!!.OnMyItemClickListener(mAdapter.booleanList, mParentMap)
                    }
                }
            })
        }
    }

    // Generate palette synchronously and return it


    override// 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    fun onCreateViewHolder(arg0: ViewGroup, arg1: Int): MyHolder {
        if (arg1 == ITEM_TYPE_HEADER) {
            return MyHolder(mHeaderView!!, ITEM_TYPE_HEADER)
        } else if (arg1 == ITEM_TYPE_EMPTY) {
            return MyHolder(mEmptyView!!, ITEM_TYPE_EMPTY)
        } else if (arg1 == ITEM_TYPE_FOOTER) {
            return MyHolder(mFooterView!!, ITEM_TYPE_FOOTER)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.item_hetong_select, arg0, false)
            return MyHolder(view, ITEM_TYPE_NORMAL)
        }
    }

    // 定义内部类继承ViewHolder
    inner class MyHolder(view: View, type: Int) : RecyclerView.ViewHolder(view) {

        var addHtLay: CardView? = null
        var mHetongCodeTv: TextView? = null
        var mHetongDateTv: TextView? = null
        var mHetongMoneyTv: TextView? = null
        var mContentLay: LinearLayout? = null
        var mShowTv: TextView? = null
        var mRelativeTv: TextView? = null
        var mRecyclerView: RecyclerView? = null
        var mCheckBox: CheckBox? = null

        init {
            when (type) {
                ITEM_TYPE_HEADER -> {
                }
                ITEM_TYPE_FOOTER -> addHtLay = view.findViewById(R.id.hetong_add_lay)
                ITEM_TYPE_EMPTY -> {
                }
                ITEM_TYPE_NORMAL -> {
                    mHetongCodeTv = view.findViewById(R.id.hetong_code_tv)
                    mHetongDateTv = view.findViewById(R.id.hetong_date_tv)
                    mHetongMoneyTv = view.findViewById(R.id.hetong_money_tv)
                    mContentLay = view.findViewById(R.id.content_lay)
                    mCheckBox = view.findViewById(R.id.check_box)
                    mShowTv = view.findViewById(R.id.tv_show)
                    mRecyclerView = view.findViewById(R.id.rcv)
                    mRelativeTv = view.findViewById(R.id.tv_relative)
                }
            }
        }
    }


    interface OnChangeBankCardListener {
        fun onButClickListener(type: String, map: MutableMap<String, Any>)
    }

    interface OnItemClickListener {
        fun OnMyItemClickListener(list: List<MutableMap<String, Any>>?, mParentMap: MutableMap<String, Any>)
    }
}