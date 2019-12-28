package com.lairui.easy.mywidget.dialog


import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.SlideEnter.SlideBottomEnter
import com.flyco.animation.SlideExit.SlideBottomExit
import com.lairui.easy.R
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.ui.module5.adapter.KindSelectAdapter
import java.util.*

/**
 * 客户类型选择：个人    企业/个体工商户
 */
class KindSelectDialog : BaseDialog {
    override var mContext: Context
    private var mSelectedMap: MutableMap<String, Any>? = null
    private var mDatas = ArrayList<MutableMap<String, Any>>()
    private var mType = 0

    private var mAdapter: KindSelectAdapter? = null
    private var mRecyclerView: RecyclerView? = null

    var selectBackListener: SelectBackListener? = null

    fun setClickListener(clickListener: SelectBackListener) {
        selectBackListener = clickListener
    }

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, isPopupStyle: Boolean, datas: List<MutableMap<String, Any>>, type: Int) : super(context, isPopupStyle) {
        mContext = context
        mDatas = datas as ArrayList<MutableMap<String, Any>>
        mType = type
    }

    override fun onCreateView(): View {
        val view = View.inflate(mContext, R.layout.dialong_kind_layout, null)
        mRecyclerView = view.findViewById(R.id.rcv_bankcard)
        init()

        return view
    }

    @SuppressLint("WrongConstant")
    private fun init() {
        if (mAdapter == null) {
            mAdapter = KindSelectAdapter(mContext!!, mDatas, 10)
            val manager = LinearLayoutManager(mContext)
            manager.orientation = LinearLayout.VERTICAL
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

        val bas_in: BaseAnimatorSet
        val bas_out: BaseAnimatorSet
        bas_in = SlideBottomEnter()
        bas_in.duration(400)
        bas_out = SlideBottomExit()
        bas_out.duration(200)

        widthScale(1f)
        dimEnabled(true)
        //heightScale(0.45f)
        showAnim(bas_in)
        dismissAnim(bas_out)
    }
}
