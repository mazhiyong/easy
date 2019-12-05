package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.mywidget.view.SwipeMenuView

import butterknife.BindView
import butterknife.ButterKnife


class SwipeMenuAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    var isSwipeEnable = true

    private lateinit var mOnSwipeListener: onSwipeListener

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SuperViewHolder(mLayoutInflater.inflate(R.layout.list_item_swipe, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as SuperViewHolder

        val map = mDataList[position]

        //这句话关掉IOS阻塞式交互效果 并依次打开左滑右滑
        //((SwipeMenuView) holder.itemView).setIos(false).setLeftSwipe(position % 2 == 0 ? true : false);
        (holder.itemView as SwipeMenuView).setIos(false).setLeftSwipe(true).isSwipeEnable = isSwipeEnable

        viewHolder.mTitle!!.text = map["gtfirmname"]!!.toString() + ""
        // viewHolder.mGuanxiTv.setText(map.get("gtfirmname") + "");
        viewHolder.mIdcardValueTv!!.text = map["gtjkzjno"]!!.toString() + ""

        val guanxi = map["gtjkrel"]!!.toString() + ""
        var guanxiStr = ""
        when (guanxi) {
            "0" -> guanxiStr = "配偶"
            "1" -> guanxiStr = "父母"
            "2" -> guanxiStr = "子女"
            "3" -> guanxiStr = "其他"
        }
        viewHolder.mGuanxiTv!!.text = "($guanxiStr)"

        viewHolder.mBtnDelete!!.setOnClickListener {
            if (null != mOnSwipeListener) {
                //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                //((CstSwipeDelMenu) holder.itemView).quickClose();
                mOnSwipeListener!!.onDel(position)
            }
        }
        //注意事项，设置item点击，不能对整个holder.itemView设置咯，只能对第一个子View，即原来的content设置，这算是局限性吧。
        viewHolder.mSwipeContent!!.setOnClickListener { }
    }

    /**
     * 和Activity通信的接口
     */
    interface onSwipeListener {
        fun onDel(pos: Int)

        fun onTop(pos: Int)
    }

    fun setOnDelListener(mOnDelListener: onSwipeListener) {
        this.mOnSwipeListener = mOnDelListener
    }

    /**
     * ViewHolder基类
     */
    inner class SuperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.title)
        lateinit var mTitle: TextView
        @BindView(R.id.swipe_content)
        lateinit var mSwipeContent: LinearLayout
        @BindView(R.id.btnDelete)
        lateinit var mBtnDelete: Button
        @BindView(R.id.guanxi_tv)
        lateinit var mGuanxiTv: TextView
        @BindView(R.id.idcard_value_tv)
        lateinit var mIdcardValueTv: TextView

        init {
            ButterKnife.bind(this, itemView)
        }

    }

}

