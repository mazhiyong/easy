package com.lairui.easy.mywidget.dialog

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.SlideEnter.SlideBottomEnter
import com.flyco.animation.SlideExit.SlideBottomExit
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.BankCardSelectAdapter
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.SelectBackListener

import java.util.ArrayList

class BankCardSelectDialog : BaseDialog {
    override var mContext: Context
    private var mSelectedMap: MutableMap<String, Any>? = null
    private var mDatas = ArrayList<MutableMap<String, Any>>()
    private var mType = 0

    private var mAdapter: BankCardSelectAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAddTv: TextView? = null
    private var mCancelTv: TextView? = null
    private var mAllLay: RelativeLayout? = null

    private var mAddCardLay: LinearLayout? = null

    var onClickListener: View.OnClickListener? = null


    var selectBackListener: SelectBackListener? = null

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, isPopupStyle: Boolean, datas: List<MutableMap<String, Any>>, type: Int) : super(context, isPopupStyle) {
        mContext = context
        mDatas = datas as ArrayList<MutableMap<String, Any>>
        mType = type
    }

    override fun onCreateView(): View {
        val view = View.inflate(mContext, R.layout.dialong_bankcard_layout, null)
        mRecyclerView = view.findViewById(R.id.rcv_bankcard)
        mAddTv = view.findViewById(R.id.tv_add_card)
        mAddCardLay = view.findViewById(R.id.add_card_lay)

        mAllLay = view.findViewById(R.id.all_lay)
        mAllLay!!.setOnClickListener { dismiss() }
        if (mType == 20) {
            mAddCardLay!!.visibility = View.GONE
        } else {
            mAddCardLay!!.visibility = View.VISIBLE
        }
        mCancelTv = view.findViewById(R.id.tv_cancel)
        heightScale(0.6f)
        init()
        return view
    }


    private fun init() {
        if (mAdapter == null) {
            mAdapter = BankCardSelectAdapter(mContext!!, mDatas)
            val manager = LinearLayoutManager(mContext)
            manager.orientation = RecyclerView.VERTICAL
            mRecyclerView!!.layoutManager = manager
            mRecyclerView!!.adapter = mAdapter
            mAdapter!!.onMyItemClickListener = object : OnMyItemClickListener {
                override fun OnMyItemClickListener(view: View, position: Int) {
                    mSelectedMap = mAdapter!!.datas!![position]
                    if (selectBackListener != null) {
                        selectBackListener!!.onSelectBackListener(mSelectedMap!!, mType)
                    }
                    dismiss()
                }
            }
        } else {
            mAdapter!!.notifyDataSetChanged()
        }

    }

    override fun setUiBeforShow() {
        if (onClickListener != null) {
            mAddCardLay!!.setOnClickListener(onClickListener)
            mCancelTv!!.setOnClickListener(onClickListener)

        }
        /*mAddTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"添加新卡",Toast.LENGTH_SHORT).show();
            }
        });
        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
*/

        val bas_in: BaseAnimatorSet
        val bas_out: BaseAnimatorSet
        bas_in = SlideBottomEnter()
        bas_in.duration(400)
        bas_out = SlideBottomExit()
        bas_out.duration(200)

        widthScale(1f)
        dimEnabled(true)
        // baseDialog.heightScale(1f)
        showAnim(bas_in)
        dismissAnim(bas_out)
    }
}
