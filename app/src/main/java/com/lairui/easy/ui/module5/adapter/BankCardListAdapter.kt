package com.lairui.easy.ui.module5.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LightingColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lairui.easy.R
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools

import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.lairui.easy.ui.temporary.adapter.ListBaseAdapter
import de.hdodenhof.circleimageview.CircleImageView


class BankCardListAdapter(context: Context) : ListBaseAdapter() {


    private var mOnCheckedBankCardListener: OnCheckedBankCardListener? = null
    private val mScreenW: Int = 0


    private val ITEM_TYPE_NORMAL = 0
    private val ITEM_TYPE_HEADER = 1
    private val ITEM_TYPE_FOOTER = 2
    private val ITEM_TYPE_EMPTY = 3
    private var mHeaderView: View? = null
    private var mFooterView: View? = null //底部添加布局
    private var mEmptyView: View? = null //内容为空布局

    var patncode = ""

    private val mParentPosition: Int = 0

    private val mLayoutInflater: LayoutInflater

    fun setOnBankCardListener(onCheckedBankCardListener: OnCheckedBankCardListener) {
        mOnCheckedBankCardListener = onCheckedBankCardListener
    }

    init {
        mContext =context
        mLayoutInflater = LayoutInflater.from(mContext)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        var itemCount = mDataList.size
        if (null != mEmptyView && itemCount == 0) {
            itemCount++
        }
        if (null != mHeaderView) {
            itemCount++
        }
        if (null != mFooterView) {
            itemCount++
        }
        return itemCount
    }

    override fun getItemViewType(position: Int): Int {
        if (null != mHeaderView && position == 0) {
            return ITEM_TYPE_HEADER
        }
        if (null != mFooterView && position == itemCount - 1) {
            return ITEM_TYPE_FOOTER
        }
        return if (null != mEmptyView && mDataList.size == 0) {
            ITEM_TYPE_EMPTY
        } else ITEM_TYPE_NORMAL
    }

    fun addHeaderView(view: View) {
        mHeaderView = view
        notifyItemInserted(0)
    }

    fun addFooterView(view: View) {
        mFooterView = view
        notifyItemInserted(itemCount - 1)
    }

    fun setEmptyView(view: View) {
        mEmptyView = view
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = getItemViewType(position)
        val viewholder = holder as MyHolder
        val map = mDataList[position]

        if (type == ITEM_TYPE_HEADER) {

        } else if (type == ITEM_TYPE_FOOTER) {

        } else if (type == ITEM_TYPE_EMPTY) {

        } else {

            val co = mContext?.let { ContextCompat.getColor(it, R.color.font_c) }?.let { getColorWithAlpha(0.7f, it) }
            val colors = mContext?.let { ContextCompat.getColor(it, R.color.font_c) }?.let { co?.let { it1 -> intArrayOf(it1, it) } }
            val gd = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
            gd.cornerRadius = UtilTools.dip2px(mContext!!,10).toFloat()
            viewholder.mContentLay?.background = gd

            val options = RequestOptions()
                    .placeholder(R.color.body_bg) //占位图
                    .error(R.color.body_bg)       //错误图


            if (UtilTools.empty(map["opnbnknm"]!!.toString() + "")) {
                viewholder.mBankNameTv?.text = "暂无相关信息"
            } else {
                viewholder.mBankNameTv?.text = map["opnbnknm"]!!.toString() + ""
            }
            viewholder.mBankCardValueTv?.text = "**** **** **** " + UtilTools.getCardNoFour(map["accid"]!!.toString() + "")


            Glide.with(this.mContext!!)
                    .load(map["logopath"]!!.toString() + "")
                    .apply(options)
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            viewholder.mCircleImageView?.setImageDrawable(mContext?.let { ContextCompat.getDrawable(it, R.drawable.default_bank) })
                            val co = mContext?.let { ContextCompat.getColor(it, R.color.font_c) }?.let { getColorWithAlpha(0.7f, it) }
                            val colors = mContext?.let { ContextCompat.getColor(it, R.color.font_c) }?.let { co?.let { it1 -> intArrayOf(it1, it) } }
                            val gd = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
                            gd.cornerRadius = UtilTools.dip2px(mContext!!,10).toFloat()
                            viewholder.mContentLay?.background = gd
                            //                            viewholder.mContentLay.setBackgroundColor(ContextCompat.getColor(mContext,R.color.bank_bg));

                        }

                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            val bd = resource as BitmapDrawable
                            val bm = bd.bitmap
                            Palette.from(bm).maximumColorCount(10).generate(object : Palette.PaletteAsyncListener {
                                override fun onGenerated(palette: Palette?) {
                                    val list = palette?.swatches
                                    var colorSize = 0
                                    var maxSwatch: Palette.Swatch? = null
                                    for (i in list!!.indices) {
                                        val swatch = list[i]
                                        if (swatch != null) {
                                            val population = swatch.population
                                            if (colorSize < population) {
                                                colorSize = population
                                                maxSwatch = swatch
                                            }
                                        }
                                    }
                                    if (maxSwatch != null) {
                                        //viewholder.mContentLay.setCardBackgroundColor((int)(0.9*maxSwatch.getRgb()));
                                        //viewholder.mContentLay.getBackground().setAlpha((int)(0.8*255));
                                        //设置渐变色
                                        val co = getColorWithAlpha(0.7f, maxSwatch.rgb)
                                        val colors = intArrayOf(co, maxSwatch.rgb)
                                        val gd = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
                                        gd.cornerRadius = mContext?.let { UtilTools.dip2px(it, 8).toFloat() }!!
                                        viewholder.mContentLay?.background = gd


                                    }
                                }
                            })


                            val matrix = ColorMatrix()
                            matrix.setSaturation(0f)//饱和度 0灰色 100过度彩色，50正常
                            val filter = ColorMatrixColorFilter(matrix)
                            // holder.mShuiyinView.setColorFilter(filter);
                            //holder.mShuiyinView.setImageBitmap(grey(bm));


                            val smallBitmap = Bitmap.createBitmap(bm, 0, 0, bm.width * 3 / 4, bm.width * 3 / 4)
                            //将获取后的图像显示在ImageView组件中
                            val drawable = BitmapDrawable(mContext?.getResources(), bm)
                            val filter2 = mContext?.let { ContextCompat.getColor(it, R.color.white) }?.let { LightingColorFilter(it, ContextCompat.getColor(mContext!!, R.color.white)) }
                            drawable.colorFilter = filter2

                            viewholder.mShuiyinView?.setImageDrawable(drawable)

                            viewholder.mCircleImageView?.setImageBitmap(bm)
                        }
                    })

       /*     val show = map["isShow"]!!.toString() + ""
            if (show == "1") {
                viewholder.mMoneyTv?.text = MbsConstans.RMB + " " + UtilTools.getNormalMoney(map["money"]!!.toString() + "")
                viewholder.mShowMoneyBut?.isSelected = true
            } else {
                viewholder.mMoneyTv?.text = "****"
                viewholder.mShowMoneyBut?.isSelected = false
            }*/

            //删除
            viewholder.mDeleteBankCardTv.setOnClickListener {
                if (mOnCheckedBankCardListener != null) {
                    mOnCheckedBankCardListener!!.onButClickListener("6", map, this@BankCardListAdapter)
                }
            }
            //修改
            viewholder.mModifyBankCardTv.setOnClickListener {
                if (mOnCheckedBankCardListener != null) {
                    mOnCheckedBankCardListener!!.onButClickListener("5", map, this@BankCardListAdapter)
                }
            }
        }
    }


    // Generate palette synchronously and return it

    /**
     *
     * @param bitmap
     * @return
     */
    fun createPaletteSync(bitmap: Bitmap): Palette {
        return Palette.from(bitmap).generate()
    }

    // Generate palette asynchronously and use it on a different
    // thread using onGenerated()
    fun createPaletteAsync(bitmap: Bitmap) {
        Palette.from(bitmap).generate {
            // Use generated instance
        }
    }

    override// 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    fun onCreateViewHolder(arg0: ViewGroup, arg1: Int): MyHolder {
        // 填充布局
        //        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bank_card_child, null);
        //        MyHolder holder = new MyHolder(view);

        if (arg1 == ITEM_TYPE_HEADER) {
            return mHeaderView?.let { MyHolder(it, ITEM_TYPE_HEADER) }!!
        } else if (arg1 == ITEM_TYPE_EMPTY) {
            return mEmptyView?.let { MyHolder(it, ITEM_TYPE_EMPTY) }!!
        } else if (arg1 == ITEM_TYPE_FOOTER) {
            return mFooterView?.let { MyHolder(it, ITEM_TYPE_FOOTER) }!!
        } else {
            val view = mLayoutInflater.inflate(R.layout.item_bank_item, arg0, false)
            return MyHolder(view, ITEM_TYPE_NORMAL)
        }
    }

    // 定义内部类继承ViewHolder
    inner class MyHolder(view: View, type: Int) : RecyclerView.ViewHolder(view) {
        lateinit var mBankNameTv: TextView  //开户行
        lateinit var mModifyBankCardTv: TextView //变更
        lateinit var mDeleteBankCardTv: TextView//变更
        lateinit var mBankCardValueTv: TextView //卡号
        lateinit var mBankBindTv: TextView //绑卡
        lateinit var mQianYeWyTv: TextView //签约网银
        lateinit var mMoneyTv: TextView  //余额
        lateinit var mTypeTv: TextView //类型
        lateinit var mShowMoneyBut: ImageView //显示余额
        lateinit var mCircleImageView: CircleImageView //银行卡图标
        lateinit var mContentLay: LinearLayout
        lateinit var mShuiyinView: ImageView


        init {
            when (type) {
                ITEM_TYPE_HEADER -> mBankBindTv = view.findViewById(R.id.bind_tv)
                ITEM_TYPE_FOOTER -> {
                }
                ITEM_TYPE_EMPTY -> {
                }
                ITEM_TYPE_NORMAL -> {
                    mBankNameTv = view.findViewById(R.id.bank_name_tv)
                    mModifyBankCardTv = view.findViewById(R.id.modify_bank_tv)
                    mBankCardValueTv = view.findViewById(R.id.bank_card_value_tv)
                    mCircleImageView = view.findViewById(R.id.image_view)
                    mContentLay = view.findViewById(R.id.bank_card_lay)
                    mQianYeWyTv = view.findViewById(R.id.qianyue_tv)
                    mMoneyTv = view.findViewById(R.id.money_tv)
                    mTypeTv = view.findViewById(R.id.type_tv)
                    mShowMoneyBut = view.findViewById(R.id.toggle_money)
                    mShuiyinView = view.findViewById(R.id.shuiyin_view)
                    mDeleteBankCardTv = view.findViewById(R.id.delete_bank_tv)
                }
            }
        }

    }


    interface OnCheckedBankCardListener {
        /*void onAddNewBanCardListener(Map<String,Object> map);
        void onShowMoney(Map<String,Object> map);*/
        fun onButClickListener(type: String, map: MutableMap<String, Any>, bankCardChildAdapter: BankCardListAdapter)
    }

    companion object {


        /**
         * 对rgb色彩加入透明度
         * @param alpha     透明度，取值范围 0.0f -- 1.0f.
         * @param baseColor
         * @return a color with alpha made from base color
         */
        fun getColorWithAlpha(alpha: Float, baseColor: Int): Int {
            val a = Math.min(255, Math.max(0, (alpha * 255).toInt())) shl 24
            val rgb = 0x00ffffff and baseColor
            return a + rgb
        }
    }
}