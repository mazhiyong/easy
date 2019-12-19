package com.lairui.easy.ui.module2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.lairui.easy.R
import com.lairui.easy.ui.module2.activity.CoinInfoActivity
import com.lairui.easy.ui.temporary.adapter.ListBaseAdapter
import java.io.Serializable

class WeituoListAdapter(context: Context) : ListBaseAdapter() {
    private var mHeaderView: View? = null
    private val mLayoutInflater: LayoutInflater
    private val ITEM_TYPE_NORMAL = 0
    private val ITEM_TYPE_HEADER = 1
    override fun onCreateViewHolder(arg0: ViewGroup, arg1: Int): RecyclerView.ViewHolder {
        return if (arg1 == ITEM_TYPE_HEADER) {
            ViewHolder(mHeaderView, ITEM_TYPE_HEADER)
        } else {
            ViewHolder(mLayoutInflater.inflate(R.layout.item_weituo, arg0, false), ITEM_TYPE_NORMAL)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        var itemCount = mDataList.size
        if (null != mHeaderView) {
            itemCount++
        }
        return itemCount
    }

    override fun getItemViewType(position: Int): Int {
        return if (null != mHeaderView && position == 0) {
            ITEM_TYPE_HEADER
        } else {
            ITEM_TYPE_NORMAL
        }
    }

    fun addHeaderView(view: View?) {
        mHeaderView = view
        notifyItemInserted(0)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = getItemViewType(position)
        if (type == ITEM_TYPE_HEADER) {
        } else {
            val item: Map<String, Any> = mDataList[position - 1]
            val viewHolder = holder as ViewHolder
            viewHolder.nameTv.text = item["name"].toString()

            if (item["type"].toString() == "买入"){
                viewHolder.priceTv.setTextColor(ContextCompat.getColor(mContext!!,R.color.colorPrimary))
            }else{
                viewHolder.priceTv.setTextColor(ContextCompat.getColor(mContext!!,R.color.font_c))
            }
            viewHolder.priceTv.text = item["type"].toString()

            viewHolder.priceCurTv.text = item["price"].toString()
            viewHolder.ratioTv.text =item["number"].toString()
            viewHolder.amountTv.text = item["time"].toString()

            viewHolder.typeTv.text = (item["short"].toString().substring(0,2)).toUpperCase()
            viewHolder.numberTv.text = item["code"].toString()
            viewHolder.lay!!.setOnClickListener {
               /* val intent = Intent(mContext,CoinInfoActivity::class.java)
                intent.putExtra("DATA", item as Serializable)
                mContext!!.startActivity(intent)*/
            }
        }
    }

    inner class ViewHolder(itemView: View?, type: Int) : RecyclerView.ViewHolder(itemView!!) {

        var nameTv: TextView = itemView!!.findViewById(R.id.nameTv)

        var priceTv: TextView = itemView!!.findViewById(R.id.priceTv)

        var ratioTv: TextView = itemView!!.findViewById(R.id.ratioTv)

        var typeTv: TextView = itemView!!.findViewById(R.id.typeTv)

        var numberTv: TextView = itemView!!.findViewById(R.id.numberTv)

        var amountTv: TextView = itemView!!.findViewById(R.id.amountTv)
        var priceCurTv: TextView = itemView!!.findViewById(R.id.priceCurTv)

        var lay: CardView? = null

        init {
            when (type) {
                ITEM_TYPE_HEADER -> {

                }
                ITEM_TYPE_NORMAL -> {
                    lay = itemView!!.findViewById(R.id.trade_Lay)
                }
            }

        }
    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}