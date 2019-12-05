package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.ModifyFileActivity
import com.lairui.easy.ui.temporary.activity.ShowDetailPictrue
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.utils.tool.UtilTools

import java.io.Serializable
import java.util.ArrayList

class ModifyFileDateAdapter(private val mContext: Context, private val mDataList: List<MutableMap<String, Any>>) : RecyclerView.Adapter<ModifyFileDateAdapter.ViewHolder>() {

    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(mContext)
    }


    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mDataList[position]

        if (UtilTools.empty(item["opttime"])) {
            holder.mTitleTv.text = ""
            holder.mTitleTv.visibility = View.GONE
        } else {
            holder.mTitleTv.text = item["opttime"]!!.toString() + ""
            holder.mTitleTv.visibility = View.VISIBLE
        }


        val images = item["optFiles"] as List<MutableMap<String, Any>>?

        val strings = ArrayList<String>()
        if (images == null) {

        } else {
            for (i in images.indices) {
                strings.add(images[i]["remotepath"]!!.toString() + "")
            }

            val manager = GridLayoutManager(mContext, 4, RecyclerView.VERTICAL, false)
            holder.mRecyclerView.layoutManager = manager
            val pool = holder.mRecyclerView.recycledViewPool
            pool.setMaxRecycledViews(0, 10)
            holder.mRecyclerView.setRecycledViewPool(pool)

            val mGridImageAdapter = ModifyShowImageAdapter(mContext, images)
            holder.mRecyclerView.adapter = mGridImageAdapter
            mGridImageAdapter.onMyItemClickListener = object : OnMyItemClickListener {
                override fun OnMyItemClickListener(view: View, position: Int) {

                    val intent = Intent(mContext, ShowDetailPictrue::class.java)
                    intent.putExtra("position", position)
                    intent.putExtra("DATA", images as Serializable?)
                    mContext.startActivity(intent)
                    //通过淡入淡出的效果关闭和显示Activity
                    (mContext as ModifyFileActivity).overridePendingTransition(R.anim.zoomin, R.anim.zoomout)

                    //((AppCompatActivity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        }

    }

    /**
     * 创建ViewHolder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ModifyFileDateAdapter.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_mofify_file_date, viewGroup, false)
        return ViewHolder(view)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mTitleTv: TextView
        val mRecyclerView: RecyclerView

        init {
            mTitleTv = itemView.findViewById(R.id.titile)
            mRecyclerView = itemView.findViewById(R.id.list_view)

        }
    }


}
