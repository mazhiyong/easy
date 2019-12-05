package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.lairui.easy.R

/**
 * Created by Administrator on 2016/8/30 0030.
 */
class FoodAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.layout_list_item_food, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder
        if (position == mDataList.size - 1) {
            viewHolder.textView.text = "添加"
        } else {
            viewHolder.textView.text = "牛肉$position"
        }


        /* viewHolder.mTimeTv.setText(item.get("updatetime")+"");*/
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

        val textView: TextView
        private val mImageView: ImageView? = null
        private val mPd: ProgressBar? = null
        private val mTimeTv: TextView? = null

        init {
            textView = itemView.findViewById<View>(R.id.title_text) as TextView
        }
    }
}
