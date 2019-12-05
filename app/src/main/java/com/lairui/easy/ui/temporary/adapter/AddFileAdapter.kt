package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.luck.picture.lib.entity.LocalMedia

class AddFileAdapter(context: Context, mSign: String) : ListBaseAdapter() {


    private var mSign = ""

    var onAddPicClickListene: GridImageAdapter.onAddPicClickListener? = null
    var onItemClickListener: GridImageAdapter.OnItemClickListener? = null


    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
        this.mSign = mSign
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_add_file, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val viewHolder = holder as ViewHolder

        viewHolder.mTitleTv.text = item["name"]!!.toString() + ""
        val bixuan = item["isreq"]!!.toString() + ""
        if (bixuan == "1") {
            viewHolder.mBixuanTv.visibility = View.VISIBLE
        } else {
            viewHolder.mBixuanTv.visibility = View.GONE
        }

        //文件类型  然后里面可以添加图片  和显示图片
        val manager = mContext?.let { FullyGridLayoutManager(it, 4, GridLayoutManager.VERTICAL, false) }
        viewHolder.mRecyclerView.layoutManager = manager
        var code = ""
        if (mSign == "1") {
            code = item["connpk"]!!.toString() + ""
        } else {
            code = item["filetype"]!!.toString() + ""
        }
        val mGridImageAdapter = mContext?.let { onAddPicClickListene?.let { it1 -> GridImageAdapter(it, it1, code) } }
        mGridImageAdapter!!.setSelectMax(200)
        if (item["selectPicList"] == null) {

        } else {
            val selectPicList = item["selectPicList"] as List<LocalMedia>?
            if (selectPicList != null && selectPicList.size > 0) {
                mGridImageAdapter.setList(selectPicList as MutableList<LocalMedia>)
                mGridImageAdapter.notifyDataSetChanged()
            }
        }

        onItemClickListener?.let { mGridImageAdapter.setOnItemClickListener(it) }
        viewHolder.mRecyclerView.adapter = mGridImageAdapter
    }

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mTitleTv: TextView
        val mRecyclerView: RecyclerView
        val mBixuanTv: TextView

        init {
            mTitleTv = itemView.findViewById(R.id.titile)
            mBixuanTv = itemView.findViewById(R.id.bixuan_tv)
            mRecyclerView = itemView.findViewById(R.id.list_view)
        }
    }

}
