package com.lairui.easy.mywidget.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.SlideEnter.SlideBottomEnter
import com.flyco.animation.SlideExit.SlideBottomExit
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.MyWheelAdapter
import com.lairui.easy.listener.SelectBackListener
import com.wx.wheelview.adapter.BaseWheelAdapter
import com.wx.wheelview.widget.WheelView

import java.util.ArrayList

class MySelectDialog : BaseDialog {


    private var mWheelView1: WheelView<Any>? = null
    private val mWheelView2: WheelView<Any>? = null
    private val mWheelView3: WheelView<Any>? = null

    private var mCancleTv: TextView? = null
    private var mTitleTv: TextView? = null
    private var mSureTv: TextView? = null

    override var mContext: Context
    private var mTitleStr = ""

    private var mType = 0
    var selectBackListener: SelectBackListener? = null


    private var mList = ArrayList<MutableMap<String, Any>>()

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, isStyle: Boolean, list: List<MutableMap<String, Any>>, title: String, type: Int) : super(context, isStyle) {
        mContext = context
        mList = list as ArrayList<MutableMap<String, Any>>
        mTitleStr = title
        mType = type
    }

    override fun onCreateView(): View {

        val view = View.inflate(context, R.layout.dialog_select_layout, null)
        mWheelView1 = view.findViewById(R.id.one_wheelview)
        /* mWheelView2 = view.findViewById(R.id.two_wheelview);
            mWheelView3 = view.findViewById(R.id.three_wheelview);*/

        mSureTv = view.findViewById(R.id.sure_tv)
        mTitleTv = view.findViewById(R.id.title_tv)
        mCancleTv = view.findViewById(R.id.cancel_tv)
        mWheelView1!!.setWheelAdapter(MyWheelAdapter(context!!) as BaseWheelAdapter<Any>)
        mWheelView1!!.setWheelSize(5)
        mWheelView1!!.skin = WheelView.Skin.Holo

        try {
            mWheelView1!!.setWheelData(mList as List<Any>?)
        } catch (e: Exception) {

        }

        //mWheelView1.setSelection(2);
        val style = WheelView.WheelViewStyle()
        style.selectedTextColor = Color.parseColor("#0288ce")
        //style.holoBorderColor = Color.RED;
        style.textColor = Color.GRAY
        // style.selectedTextSize = 20;
        mWheelView1!!.style = style

        return view
    }


    override fun setUiBeforShow() {
        mTitleTv!!.text = mTitleStr
        mSureTv!!.setOnClickListener {
            val map = mWheelView1!!.selectionItem as MutableMap<String, Any>
            if (selectBackListener != null) {
                selectBackListener!!.onSelectBackListener(map, mType)
            }
            dismiss()
        }

        mCancleTv!!.setOnClickListener { dismiss() }

        mWheelView1!!.setOnWheelItemSelectedListener(object : WheelView.OnWheelItemSelectedListener<Any> {
            override fun onItemSelected(position: Int, o: Any) {
                val map = o as MutableMap<String, Any>
            }
        })

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
}
