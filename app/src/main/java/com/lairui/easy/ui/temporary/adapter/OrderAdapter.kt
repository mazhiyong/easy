package com.lairui.easy.ui.temporary.adapter

import android.content.Context

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.utils.tool.UtilTools

/**
 * Created by Administrator on 2016/8/30 0030.
 */
class OrderAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.layout_list_item_order, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder

        val listMap = item["goods_list"] as List<MutableMap<String, Any>>?

        if (listMap != null && listMap.size > 0) {
            val map = listMap[0]
            viewHolder.mGoodsNameTv.text = map["goods_name"]!!.toString() + ""
        } else {
            viewHolder.mGoodsNameTv.text = ""
        }

        viewHolder.mTimeTv.text = item["update_date"]!!.toString() + ""

        val price = java.lang.Double.valueOf(item["total_fee"]!!.toString() + "")
        val priceStr = UtilTools.fromDouble(price)
        viewHolder.mMoneyTv.text = UtilTools.fromDouble(price)
        /*  Glide.with(mContext)
                .load(item.get("url")+"").asBitmap()
                .placeholder(R.drawable.ic_launcher)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                       LinearLayout.LayoutParams lp = ( LinearLayout.LayoutParams)viewHolder.mImageView.getLayoutParams();
                        lp.width = mW;
                        float tempHeight=height * ((float)  viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });lp.width / width);
                        lp.height =(int)tempHeight ;
                        viewHolder.mImageView.setLayoutParams(lp);
                        viewHolder.mImageView.setImageBitmap(bitmap);
                    }
                });*/


        // Glide.with(mContext).load(item.get("url")+"").asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.mImageView);


        /*Glide.with(mContext)
                .load(item.get("url")+"")
                //.centerCrop()
                .placeholder(R.drawable.ic_launcher)
                .crossFade()
                .into(viewHolder.mImageView);*/
    }


    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var mGoodsNameTv: TextView
        internal var mMemoTv: TextView
        internal var mMoneySignTv: TextView
        internal var mTimeTv: TextView
        internal var mMoneyTv: TextView
        internal var mDealSign: TextView


        init {
            mGoodsNameTv = itemView.findViewById<View>(R.id.goods_name_tv) as TextView
            mMemoTv = itemView.findViewById<View>(R.id.memo_tv) as TextView
            mMoneySignTv = itemView.findViewById<View>(R.id.money_sign_tv) as TextView
            mTimeTv = itemView.findViewById<View>(R.id.time_tv) as TextView
            mDealSign = itemView.findViewById<View>(R.id.deal_sign) as TextView
            mMoneyTv = itemView.findViewById<View>(R.id.money_tv) as TextView
        }
    }
}
