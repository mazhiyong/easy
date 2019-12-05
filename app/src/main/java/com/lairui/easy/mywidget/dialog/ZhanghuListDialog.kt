package com.lairui.easy.mywidget.dialog

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View

import com.flyco.dialog.widget.popup.base.BasePopup
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.ZhangHuListAdapter
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.SelectBackListener

import java.util.ArrayList

class ZhanghuListDialog(context: Context, mlist: MutableList<MutableMap<String, Any>>, private val mType: Int) : BasePopup<ZhanghuListDialog>(context) {


    private var mRecyclerView: RecyclerView? = null
    private var mHezuoListAdapter: ZhangHuListAdapter? = null

    var selectBackListener: SelectBackListener? = null
    private var mList = ArrayList<MutableMap<String, Any>>()

    init {
        //            setCanceledOnTouchOutside(false);
        mList = mlist as ArrayList<MutableMap<String, Any>>
    }

    override fun onCreatePopupView(): View {
        val inflate = View.inflate(mContext, R.layout.item_hezuo_popu, null)
        mRecyclerView = inflate.findViewById(R.id.hezuo_recyclerview)
        return inflate
    }

    override fun setUiBeforShow() {
        initData(null)
    }


    fun initData(mdata: List<MutableMap<String, Any>>?) {
        if (mdata != null) {
            mList.clear()
            mList.addAll(mdata)
        }
        if (mHezuoListAdapter == null) {
            mHezuoListAdapter = ZhangHuListAdapter(mContext, mList)
            val linearLayoutManager = LinearLayoutManager(mContext)
            linearLayoutManager.orientation = RecyclerView.VERTICAL
            mRecyclerView!!.layoutManager = linearLayoutManager
            mRecyclerView!!.adapter = mHezuoListAdapter

            mHezuoListAdapter!!.onMyItemClickListener = object : OnMyItemClickListener {
                override fun OnMyItemClickListener(view: View, position: Int) {
                    val mSelectMap = mHezuoListAdapter!!.datas!![position]
                    if (selectBackListener != null) {
                        selectBackListener!!.onSelectBackListener(mSelectMap, mType)
                    }
                    dismiss()

                }
            }
        } else {
            mHezuoListAdapter!!.notifyDataSetChanged()
        }
    }

}