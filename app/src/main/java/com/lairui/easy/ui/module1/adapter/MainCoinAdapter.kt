package com.lairui.easy.ui.module1.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.viewpager.widget.PagerAdapter
import com.lairui.easy.R
import com.lairui.easy.basic.BasicRecycleViewAdapter
import com.lairui.easy.utils.tool.UtilTools
import java.util.*


class MainCoinAdapter(activity: Activity, data: List<Map<String, Any>>?) : PagerAdapter() {
    //上下文
    private val mContext: Context
    //数据
    private var mData: List<Map<String, Any>>?
    private var currentPosition = 0
    private var itemClickListener: ItemClickListener? = null
    private val recyclerViewList = SparseArray<RecyclerView>()
    fun setItemClickListener(itemClickListener: ItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    fun setData(listUp: List<Map<String, Any>>?, currentPosition: Int) {
        mData = listUp
        this.currentPosition = currentPosition
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onItemClickListener(map: Map<String?, Any?>?)
    }

    override fun getCount(): Int { // 根据传入数据来判断
        return if (mData != null && mData!!.size > 0) {
            if (mData!!.size % 3 > 0) {
                mData!!.size / 3 + 1
            } else {
                mData!!.size / 3
            }
        } else 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any { // 根据索引取view, 如果view为空, 则添加view
//        RecyclerView recyclerView = recyclerViewList.get(position);
//        if (recyclerView == null) {
// 创建 RecyclerView 并绑定 adapter 并设置adapter 的监听事件
        val recyclerView = UtilTools.parseLayout(mContext, R.layout.coin_gridview) as RecyclerView
        val coinInfoAdapter = CoinInfoAdapter(mContext)
        recyclerView.adapter = coinInfoAdapter
       /* val spaceWidth: Int = (UtilTools.getScreenWidth(mContext) -
                (mContext.resources.getDimension(R.dimen.dd_dimen_20px) * 2) as Int -
                (mContext.resources.getDimension(R.dimen.dd_dimen_210px) * 3) as Int) / (3 * (3 - 1))*/
        recyclerView.addItemDecoration(SpaceItemDecoration(UtilTools.dip2px(mContext,10),3))
        val start = position * 3
        // 当位于最后一部分时
        val listUpBeans: List<Map<String, Any>>
        listUpBeans = if (position == count - 1) {
            ArrayList(mData!!.subList(start, mData!!.size))
        } else {
            ArrayList(mData!!.subList(start, start + 3))
        }
        coinInfoAdapter.setList(listUpBeans)
        coinInfoAdapter.setOnItemClickListener(object : BasicRecycleViewAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View?, position: Int) {
                val map: Map<String?, Any?> = coinInfoAdapter.getCoinList().get(position)
                if (itemClickListener != null) {
                    itemClickListener!!.onItemClickListener(map)
                }
            }
        })
        recyclerViewList.put(position, recyclerView)
        container.addView(recyclerView)
        return recyclerView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        recyclerViewList.remove(position)
        container.removeView(`object` as RecyclerView)
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun getItemPosition(`object`: Any): Int {
        val coinInfoAdapter1: CoinInfoAdapter? = (`object` as RecyclerView).adapter as CoinInfoAdapter?
        if (coinInfoAdapter1 != null) {
            val start = currentPosition * 3
            // 当位于最后一部分时
            val listUpBeans: List<Map<String, Any>>
            listUpBeans = if (currentPosition == count - 1) {
                ArrayList(mData!!.subList(start, mData!!.size))
            } else {
                ArrayList(mData!!.subList(start, start + 3))
            }
            coinInfoAdapter1.setList(listUpBeans)
        }
        return -2
    }

    init {
        mContext = activity
        mData = data
    }

   inner class SpaceItemDecoration(private val space: Int, private val column: Int) : ItemDecoration() {

       override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val mod = parent.getChildAdapterPosition(view) % column
            outRect.left = space * mod
        }

    }
}