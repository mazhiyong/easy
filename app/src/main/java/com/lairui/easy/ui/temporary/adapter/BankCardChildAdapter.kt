package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.viewpager.widget.ViewPager
import androidx.palette.graphics.Palette
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.BankQianyueActivity
import com.lairui.easy.ui.temporary.activity.SelectBankListActivity
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.lairui.easy.mywidget.view.ScaleTransformer

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import de.hdodenhof.circleimageview.CircleImageView


class BankCardChildAdapter(private val mContext: Context, private val mDatas: List<MutableMap<String, Any>>, mType: Int,
                           private val mOnChangeBankCardListener: BankCardAdapter.OnChangeBankCardListener?, private val mParentPosition: Int) : RecyclerView.Adapter<BankCardChildAdapter.MyHolder>() {
    private val mScreenW: Int = 0
    private var mType = 0

    private val ITEM_TYPE_NORMAL = 0
    private val ITEM_TYPE_HEADER = 1
    private val ITEM_TYPE_FOOTER = 2
    private val ITEM_TYPE_EMPTY = 3
    private var mHeaderView: View? = null
    private var mFooterView: View? = null
    private var mEmptyView: View? = null

    var patncode = ""

    private val mLayoutInflater: LayoutInflater

    init {
        this.mType = mType
        mLayoutInflater = LayoutInflater.from(mContext)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        var itemCount = mDatas.size
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
        return if (null != mEmptyView && mDatas.size == 0) {
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


    override// 填充onCreateViewHolder方法返回的holder中的控件
    fun onBindViewHolder(holder: MyHolder, position: Int) {
        val type = getItemViewType(position)
        if (type == ITEM_TYPE_HEADER) {
            holder.mBankBindTv?.setOnClickListener {
                val map = HashMap<String, Any>()
                map["patncode"] = patncode
                mOnChangeBankCardListener?.onButClickListener("1", map, this@BankCardChildAdapter)
            }
        } else if (type == ITEM_TYPE_FOOTER) {

        } else if (type == ITEM_TYPE_EMPTY) {

        } else {
            val map = mDatas[position]

            val options = RequestOptions()
                    .placeholder(R.color.body_bg) //占位图
                    .error(R.color.body_bg)       //错误图

            holder.mViewPager?.id = mParentPosition + 1000
            val showViewPager = map["bindShow"]!!.toString() + ""
            if (showViewPager == "1") {
                holder.mViewPagerLay?.visibility = View.VISIBLE


                val mViews = ArrayList<View>()
                var mBindCardList: List<MutableMap<String, Any>>?
                if (map["bindCard"] != null) {
                    mBindCardList = map["bindCard"] as ArrayList<MutableMap<String, Any>>?
                    if (mBindCardList == null) {
                        mBindCardList = ArrayList()
                    }
                } else {
                    mBindCardList = ArrayList()
                }
                for (i in mBindCardList.indices) {
                    val bindMap = mBindCardList[i]
                    //View view=LayoutInflater.from(mContext).inflate(R.layout.item_pay_plan, viewHolder.mViewPager,false);
                    val view = LayoutInflater.from(mContext).inflate(R.layout.item_card_viewpager, null)
                    val mBindBankImgView = view.findViewById<CircleImageView>(R.id.image_view2)
                    val mBindBankNameTv = view.findViewById<TextView>(R.id.bank_name_tv2)
                    val mBindCardNumTv = view.findViewById<TextView>(R.id.bank_card_value_tv2)
                    val mBindCardTypeTv = view.findViewById<TextView>(R.id.bank_type_tv2)
                    val mBindCardLay = view.findViewById<CardView>(R.id.bank_card_lay2)
                    mBindCardLay.background.alpha = (0.8 * 255).toInt()

                    val bindBankNum = bindMap["acctNo"]!!.toString() + ""
                    val s = UtilTools.getShowBankIdCard(bindBankNum)

                    mBindBankNameTv.text = bindMap["othrBnkNm"]!!.toString() + ""
                    mBindCardNumTv.text = s

                    Glide.with(mContext)
                            .load(bindMap["logopath"]!!.toString() + "")
                            .apply(options)
                            .into(object : SimpleTarget<Drawable>() {
                                override fun onLoadFailed(errorDrawable: Drawable?) {
                                    mBindBankImgView.setImageDrawable(errorDrawable)
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
                                                mBindCardLay.setCardBackgroundColor(maxSwatch.rgb)
                                                mBindCardLay.background.alpha = (0.8 * 255).toInt()

                                            }
                                        }
                                    })
                                    mBindBankImgView.setImageBitmap(bm)
                                }
                            })
                    mViews.add(view)
                }
                val adapter = CardViewPagerAdapter(mContext, mViews)
                holder.mViewPager?.adapter = adapter
                holder.mViewPager?.pageMargin = UtilTools.dip2px(mContext, 8)
                holder.mViewPager?.offscreenPageLimit = 3
                holder.mViewPager?.currentItem = 0
                holder.mViewPager?.setPageTransformer(false, ScaleTransformer(mContext))
                //holder.mViewPager.setPageTransformer(false, new ScaleTransformer(this));

                holder.mViewPagerLay?.setOnTouchListener { v, event -> holder.mViewPager!!.dispatchTouchEvent(event) }


            } else {
                holder.mViewPagerLay?.visibility = View.GONE
            }

            holder.mContentLay?.background?.alpha = (0.8 * 255).toInt()

            map.put("indexPos", mParentPosition)
            val wxStatus = map["secstatus"]!!.toString() + ""
            if (UtilTools.empty(map["bankname"]!!.toString() + "")) {
                holder.mBankNameTv?.text = "暂无相关信息"
            } else {
                holder.mBankNameTv?.text = map["bankname"]!!.toString() + ""
            }

            holder.mBankCardValueTv?.text = UtilTools.getIDXing(map["accid"]!!.toString() + "")
            //GlideUtils.loadImage(mContext,map.get("logopath")+"",holder.mCircleImageView);

            Glide.with(mContext)
                    .load(map["logopath"]!!.toString() + "")
                    .apply(options)
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            holder.mCircleImageView?.setImageDrawable(errorDrawable)
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
                                        holder.mContentLay?.setCardBackgroundColor(maxSwatch!!.rgb)
                                        holder.mContentLay?.background?.alpha = (0.8 * 255).toInt()

                                    }
                                }
                            })
                            holder.mCircleImageView?.setImageBitmap(bm)
                        }
                    })
            holder.mModifyBankCardTv?.setOnClickListener {
                val intent = Intent(mContext, SelectBankListActivity::class.java)
                //intent.putExtra("DATA",(Serializable) map);
                intent.putExtra("TYPE", "1")
                intent.putExtra("patncode", map["patncode"]!!.toString() + "")
                mContext.startActivity(intent)
            }
            if (wxStatus == "2") {
                holder.mQianYeWyTv?.visibility = View.VISIBLE

                holder.mQianYeWyTv?.setOnClickListener {
                    val intent = Intent(mContext, BankQianyueActivity::class.java)
                    //intent.putExtra("DATA",(Serializable) map);
                    intent.putExtra("DATA", map as Serializable)
                    mContext.startActivity(intent)
                }
            } else {
                holder.mQianYeWyTv?.visibility = View.GONE
            }

            val show = map["isShow"]!!.toString() + ""
            if (show == "1") {
                holder.mMoneyTv?.text = MbsConstans.RMB + " " + UtilTools.getNormalMoney(map["money"]!!.toString() + "")
                holder.mShowMoneyBut?.isSelected = true
            } else {
                holder.mMoneyTv?.text = "****"
                holder.mShowMoneyBut?.isSelected = false
            }

            holder.mShowMoneyBut?.setOnClickListener {
                val b = holder.mShowMoneyBut?.isSelected
                if (!b!!) {
                    mOnChangeBankCardListener?.onButClickListener("2", map, this@BankCardChildAdapter)
                } else {
                    map.put("isShow", "0")
                    this@BankCardChildAdapter.notifyDataSetChanged()
                    if (mOnChangeBankCardListener != null) {
                        // mOnChangeBankCardListener.onButClickListener("3",map);
                    }
                }
            }

            holder.mShowLay?.setOnClickListener {
                if (!holder.mViewPagerLay?.isShown!!) {
                    var mBindCardList: List<MutableMap<String, Any>>?
                    if (map.containsKey("bindCard")) {
                        mBindCardList = map["bindCard"] as ArrayList<MutableMap<String, Any>>?
                        if (mBindCardList == null) {
                            mBindCardList = ArrayList()
                        }
                        if (mBindCardList!!.size == 0) {
                            TipsToast.showToastMsg(mContext.resources.getString(R.string.bind_card_no))
                            holder.mViewPagerLay?.visibility = View.GONE
                            map.put("bindShow", "0")
                        } else {
                            holder.mViewPagerLay?.visibility = View.VISIBLE
                            map.put("bindShow", "1")
                            this@BankCardChildAdapter.notifyDataSetChanged()
                        }
                    } else {
                        mOnChangeBankCardListener?.onButClickListener("4", map, this@BankCardChildAdapter)
                    }

                } else {
                    holder.mViewPagerLay?.visibility = View.GONE
                    map.put("bindShow", "0")
                    this@BankCardChildAdapter.notifyDataSetChanged()
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

    // 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    override fun onCreateViewHolder(arg0: ViewGroup, arg1: Int): MyHolder {
        // 填充布局
        //        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bank_card_child, null);
        //        MyHolder holder = new MyHolder(view);

        if (arg1 == ITEM_TYPE_HEADER) {
            return MyHolder(mHeaderView!!, ITEM_TYPE_HEADER)
        } else if (arg1 == ITEM_TYPE_EMPTY) {
            return MyHolder(mEmptyView!!, ITEM_TYPE_EMPTY)
        } else if (arg1 == ITEM_TYPE_FOOTER) {
            return MyHolder(mFooterView!!, ITEM_TYPE_FOOTER)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.item_bank_card_child, arg0, false)
            return MyHolder(view, ITEM_TYPE_NORMAL)
        }
    }

    // 定义内部类继承ViewHolder
    inner class MyHolder(view: View, type: Int) : RecyclerView.ViewHolder(view) {
        var mBankNameTv: TextView? = null
        var mModifyBankCardTv: TextView? = null
        var mBankCardValueTv: TextView? = null
        var mBankBindTv: TextView? = null
        var mQianYeWyTv: TextView? = null
        var mMoneyTv: TextView? = null
        var mShowMoneyBut: ImageView? = null
        var mCircleImageView: CircleImageView? = null
        var mContentLay: CardView? = null
        var mViewPager: ViewPager? = null
        var mAllLay: LinearLayout? = null
        var mShowLay: LinearLayout? = null
        var mViewPagerLay: RelativeLayout? = null

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
                    mShowMoneyBut = view.findViewById(R.id.toggle_money)
                    mViewPager = view.findViewById(R.id.viewpager)
                    mAllLay = view.findViewById(R.id.all_content_view)
                    mShowLay = view.findViewById(R.id.bind_account_lay)
                    mViewPagerLay = view.findViewById(R.id.viewPager_container)
                }
            }
        }

    }
}