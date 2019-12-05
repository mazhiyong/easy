package com.lairui.easy.mywidget.dialog

import android.content.Context

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.SlideEnter.SlideBottomEnter
import com.flyco.animation.SlideExit.SlideBottomExit
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.HezuoListAdapter
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.SelectBackListener

import java.util.ArrayList

class HeZuoFangDialog : BaseDialog {

    private var mCancleTv: TextView? = null
    private var mTitleTv: TextView? = null
    private var mSureTv: TextView? = null

    override var mContext: Context

    private var mTitleStr = ""

    private var mType = 0
    var selectBackListener: SelectBackListener? = null

    private var mRecyclerView: RecyclerView? = null
    private var mHezuoListAdapter: HezuoListAdapter? = null

    private var mSelectMap: MutableMap<String, Any>? = null


    private val mList = ArrayList<MutableMap<String, Any>>()

    constructor(context: Context) : super(context) {
        mContext= context
    }

    constructor(context: Context, isStyle: Boolean, list: List<MutableMap<String, Any>>, title: String, type: Int) : super(context, isStyle) {
        mContext = context
        mList.clear()
        mList.addAll(list)
        mTitleStr = title
        mType = type
    }

    override fun onCreateView(): View {

        val view = View.inflate(context, R.layout.dialog_hezuo_layout, null)

        mRecyclerView = view.findViewById(R.id.hezuo_recyclerview)

        mSureTv = view.findViewById(R.id.sure_tv)
        mTitleTv = view.findViewById(R.id.title_tv)
        mCancleTv = view.findViewById(R.id.cancel_tv)
        initData()
        return view
    }

    override fun setUiBeforShow() {
        mTitleTv!!.text = mTitleStr
        mSureTv!!.setOnClickListener {
            if (selectBackListener != null) {
                selectBackListener!!.onSelectBackListener(mSelectMap!!, mType)
            }
            dismiss()
        }

        mCancleTv!!.setOnClickListener { dismiss() }


        val bas_in: BaseAnimatorSet
        val bas_out: BaseAnimatorSet
        bas_in = SlideBottomEnter()
        bas_in.duration(400)
        bas_out = SlideBottomExit()
        bas_out.duration(200)

        widthScale(1f)
        dimEnabled(true)
        // baseDialog.heightScale(1f);
        showAnim(bas_in)
        dismissAnim(bas_out)

    }


    fun initData() {

        if (mHezuoListAdapter == null) {
            mHezuoListAdapter = HezuoListAdapter(context!!, mList)
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = RecyclerView.VERTICAL
            mRecyclerView!!.layoutManager = linearLayoutManager
            mRecyclerView!!.adapter = mHezuoListAdapter
            mHezuoListAdapter!!.onMyItemClickListener = object : OnMyItemClickListener {
                override fun OnMyItemClickListener(view: View, position: Int) {
                    mSelectMap = mHezuoListAdapter!!.datas!![position]
                    if (selectBackListener != null) {
                        selectBackListener!!.onSelectBackListener(mSelectMap!!, mType)
                    }
                    dismiss()

                }
            }

        } else {
            mHezuoListAdapter!!.notifyDataSetChanged()
        }
    }
}
