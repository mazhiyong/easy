package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.lairui.easy.R
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.DateUtils
import com.luck.picture.lib.tools.StringUtils

import java.io.File
import java.util.ArrayList

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.pictureselector.adapter
 * email：893855882@qq.com
 * data：16/7/27
 */
class GridImageAdapter(private val context: Context,
                       /**
                        * 点击添加图片跳转
                        */
                       private val mOnAddPicClickListener: onAddPicClickListener, mType: String) : RecyclerView.Adapter<GridImageAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private var list: MutableList<LocalMedia> = ArrayList()
    private var selectMax = 9

    private var mType = ""

    protected var mItemClickListener: OnItemClickListener? = null

    interface onAddPicClickListener {
        fun onAddPicClick(mType: String)

        fun onDeleClick(position: Int, mType: String)
    }

    init {
        mInflater = LayoutInflater.from(context)
        this.mType = mType
    }

    fun setSelectMax(selectMax: Int) {
        this.selectMax = selectMax
    }

    fun setList(list: MutableList<LocalMedia>) {
        this.list = list
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var mImg: ImageView
        internal var ll_del: LinearLayout
        internal var tv_duration: TextView

        init {
            mImg = view.findViewById<View>(R.id.fiv) as ImageView
            ll_del = view.findViewById<View>(R.id.ll_del) as LinearLayout
            tv_duration = view.findViewById<View>(R.id.tv_duration) as TextView
        }
    }

    override fun getItemCount(): Int {
        return if (list.size < selectMax) {
            list.size + 1
        } else {
            list.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isShowAddItem(position)) {
            TYPE_CAMERA
        } else {
            TYPE_PICTURE
        }
    }

    /**
     * 创建ViewHolder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.gv_filter_image,
                viewGroup, false)
        return ViewHolder(view)
    }

    private fun isShowAddItem(position: Int): Boolean {
        val size = if (list.size == 0) 0 else list.size
        return position == size
    }

    /**
     * 设置值
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        //少于8张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.mImg.setImageResource(R.drawable.addimg_1x)
            viewHolder.mImg.setOnClickListener {
                //点击 某个 文件类型下的添加按钮   添加图片  把文件类型传递到前面 进行标记
                mOnAddPicClickListener.onAddPicClick(mType)
            }
            viewHolder.ll_del.visibility = View.INVISIBLE
        } else {
            viewHolder.ll_del.visibility = View.VISIBLE
            viewHolder.ll_del.setOnClickListener {
                val index = viewHolder.adapterPosition
                // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
                // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
                if (index != RecyclerView.NO_POSITION) {
                    list.removeAt(index)
                    notifyItemRemoved(index)
                    notifyItemRangeChanged(index, list.size)
                    mOnAddPicClickListener.onDeleClick(position, mType)
                }
            }
            val media = list[position]
            val mimeType = media.mimeType
            var path = ""
            if (media.isCut && !media.isCompressed) {
                // 裁剪过
                path = media.cutPath
            } else if (media.isCompressed || media.isCut && media.isCompressed) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                path = media.compressPath
            } else {
                // 原图
                path = media.path
            }
            // 图片
            if (media.isCompressed) {
                Log.i("compress image result:", (File(media.compressPath).length() / 1024).toString() + "k")
                Log.i("压缩地址::", media.compressPath)
            }

            Log.i("原图地址::", media.path)
            val pictureType = PictureMimeType.isPictureType(media.pictureType)
            if (media.isCut) {
                Log.i("裁剪地址::", media.cutPath)
            }
            val duration = media.duration
            viewHolder.tv_duration.visibility = if (pictureType == PictureConfig.TYPE_VIDEO)
                View.VISIBLE
            else
                View.GONE
            if (mimeType == PictureMimeType.ofAudio()) {
                viewHolder.tv_duration.visibility = View.VISIBLE
                val drawable = ContextCompat.getDrawable(context, R.drawable.picture_audio)
                StringUtils.modifyTextViewDrawable(viewHolder.tv_duration, drawable!!, 0)
            } else {
                val drawable = ContextCompat.getDrawable(context, R.drawable.video_icon)
                StringUtils.modifyTextViewDrawable(viewHolder.tv_duration, drawable!!, 0)
            }
            viewHolder.tv_duration.text = DateUtils.timeParse(duration)
            if (mimeType == PictureMimeType.ofAudio()) {
                viewHolder.mImg.setImageResource(R.drawable.audio_placeholder)
            } else {
                Log.i("图片显示地址::", path)
                val options = RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                Glide.with(viewHolder.itemView.context)
                        .load(path)
                        .apply(options)
                        .thumbnail(0.2f)
                        .into(viewHolder.mImg)
            }
            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener { v ->
                    val adapterPosition = viewHolder.adapterPosition
                    mItemClickListener!!.onItemClick(adapterPosition, v, mType)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, v: View, mType: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mItemClickListener = listener
    }

    companion object {
        val TYPE_CAMERA = 1
        val TYPE_PICTURE = 2
    }
}
