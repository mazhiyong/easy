package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.lairui.easy.R
import com.lairui.easy.utils.imageload.CircleProgressView
import com.lairui.easy.utils.imageload.ProgressInterceptor
import com.lairui.easy.utils.imageload.ProgressListener
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools

/**
 */
class ModifyShowImageAdapter(private val context: Context, private val mDataList: List<MutableMap<String, Any>>) : RecyclerView.Adapter<ModifyShowImageAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater

    var onMyItemClickListener: OnMyItemClickListener? = null

    init {
        mInflater = LayoutInflater.from(context)
    }


    override fun getItemCount(): Int {
        return mDataList.size
    }


    /**
     * 创建ViewHolder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_mofify_file_image,
                viewGroup, false)
        return ViewHolder(view)
    }


    /**
     * 设置值
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val map = mDataList[position]

        var w = UtilTools.getScreenWidth(context)
        val padd = UtilTools.dip2px(context, 5)
        val picPadd = UtilTools.dip2px(context, 2)
        w = w - 2 * 4 * picPadd - 2 * padd

        val layoutParams = RelativeLayout.LayoutParams(w / 4, w / 4)
        layoutParams.bottomMargin = picPadd
        layoutParams.topMargin = picPadd
        layoutParams.leftMargin = picPadd
        layoutParams.rightMargin = picPadd
        viewHolder.mImg!!.layoutParams = layoutParams

        viewHolder.mImg!!.setOnClickListener {
            if (onMyItemClickListener != null) {
                onMyItemClickListener!!.OnMyItemClickListener(viewHolder.mImg!!, position)
            }
        }


        var imgUrl = map["remotepath"]!!.toString() + ""
        if (imgUrl.contains("http")) {
            //GlideUtils.loadSmallImage(context,imgUrl,viewHolder.mImg);
        } else {
            imgUrl = MbsConstans.PIC_URL + imgUrl
            //GlideUtils.loadSmallImage(context,imgUrl,viewHolder.mImg);
        }

        val loadImgUrl = imgUrl
        //viewHolder.mProgressBar.setProgress(20+position);

        ProgressInterceptor.addListener(loadImgUrl, object : ProgressListener {
            override fun onProgress(progress: Int) {
                viewHolder.mProgressBar.progress = progress
                //                viewHolder.mImg.setTag(R.id.glide_tag,progress);
                //
                //                CircleProgressView ccc =(CircleProgressView) viewHolder.mImg.getTag(R.id.glide_tag);
                //                ccc.setProgress(progress);
            }
        })


        val options = RequestOptions()
                //                .skipMemoryCache(true)
                //                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //                .format(DecodeFormat.PREFER_RGB_565)////.format(DecodeFormat.PREFER_ARGB_8888)
                // .timeout(1*1000*60)
                .override(160, 160)
                .placeholder(R.color.body_bg) //占位图
                .error(R.color.body_bg)       //错误图
                //                .disallowHardwareConfig()
                .centerCrop()

        // .signature(new ObjectKey(UUID.randomUUID().toString())) ;
        // .priority(Priority.HIGH)

        Glide.with(context)
                .load(imgUrl)
                //.thumbnail(0.3f)

                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())//淡入淡出
                .into(object : DrawableImageViewTarget(viewHolder.mImg!!) {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        //隐藏加载的进度条
                        viewHolder.mProgressBar.visibility = View.GONE
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                    }

                    override fun onResourceReady(resource: Drawable, animation: Transition<in Drawable>?) {
                        super.onResourceReady(resource, animation)
                        ProgressInterceptor.removeListener(loadImgUrl)
                        viewHolder.mProgressBar.visibility = View.GONE
                    }
                })

        LogUtil.i("打印log日志", imgUrl)

    }


    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        val imageView = holder.mImg
        if (imageView != null) {
            Glide.with(context).clear(imageView)
            /*Drawable drawable = imageView.getDrawable();
            if (drawable != null && drawable instanceof BitmapDrawable){
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }*/
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var mImg: ImageView? = null
        internal var mProgressBar: CircleProgressView

        init {
            mImg = view.findViewById<View>(R.id.image_view) as ImageView
            mProgressBar = view.findViewById<View>(R.id.progressView) as CircleProgressView
        }
    }


}
