package com.lairui.easy.ui.temporary.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R

class CartAdapter(context: Context) : ListBaseAdapter() {


    private val mLayoutInflater: LayoutInflater
    private val mW = 0
    var mIsSoftKeyboardShowing = false

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.cart_item, parent, false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]

        val viewHolder = holder as ViewHolder
        viewHolder.nameTextView.text = mDataList[position]["name"]!!.toString() + ""


        if (needTitle(position)) {
            if (position == 0) {
                viewHolder.mShopLay.visibility = View.VISIBLE
            } else {
                viewHolder.mShopLay.visibility = View.VISIBLE
            }

            // 显示标题并设置内容
            viewHolder.mShopNameTv.text = mDataList[position]["shopName"]!!.toString() + ""
            viewHolder.mShopNameTv.visibility = View.VISIBLE
        } else {
            // 内容项隐藏标题
            viewHolder.mShopLay.visibility = View.GONE
        }



        viewHolder.mDeleteView.setOnClickListener {
            // TODO Auto-generated method stub
            // mCartActivity.showDialog(map,position);
        }

        /* viewHolder.checkBox.setId(position);
        viewHolder.checkBox.setChecked(checkPosition.get(position));
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                int id = buttonView.getId();
                checkPosition.set(id,isChecked); //赋值
                getAllPrice();
            }

        });*/

        viewHolder.mCheckLayout.setOnClickListener { viewHolder.checkBox.performClick() }

        viewHolder.mAddView.setOnClickListener {
            // TODO Auto-generated method stub
            // int temp = Integer.valueOf(map.get("goods_quantity")+"");
            //                goodsNum = temp + 1;
            //                mListEdit = viewHolder.mEditText;
            //                listMorePrice = viewHolder.morePricetTextView;
            //                updateMap = map;
            //                mCartActivity.butAction(Integer.valueOf(map.get("id")+""),goodsNum,map.get("buyType")+"");
        }
        viewHolder.mCutView.setOnClickListener {
            // TODO Auto-generated method stub
            //                int temp = Integer.valueOf(map.get("goods_quantity")+"");
            //                if (temp <=1) {
            //                    return;
            //                }else{
            //                    goodsNum = temp - 1;
            //                    mListEdit = viewHolder.mEditText;
            //                    listMorePrice = viewHolder.morePricetTextView;
            //                    updateMap = map;
            //                    mCartActivity.butAction(Integer.valueOf(map.get("id")+""),goodsNum,map.get("buyType")+"");
            //                }
        }

        viewHolder.mEditText.clearFocus()
        viewHolder.mEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                viewHolder.mEditText.isFocusable = true
                viewHolder.mEditText.isFocusableInTouchMode = true
                viewHolder.mEditText.requestFocus()
                // viewHolder.mEditText.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            false
        }


        viewHolder.convertView.rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            viewHolder.convertView.getWindowVisibleDisplayFrame(r)
            val screenHeight = viewHolder.convertView.rootView
                    .height
            val heightDifference = screenHeight - r.bottom
            if (heightDifference > 200) {
                //软键盘显示
                mIsSoftKeyboardShowing = true
                Log.e("TAG", "显示")
            } else {
                //软键盘隐藏
                Log.e("TAG", "隐藏")
                mIsSoftKeyboardShowing = false

                viewHolder.mEditText.isFocusable = false
                viewHolder.mEditText.isFocusableInTouchMode = false
                viewHolder.mEditText.clearFocus()


            }
        }

    }

    inner class ViewHolder(var convertView: View) : RecyclerView.ViewHolder(convertView) {
        var nameTextView: TextView
        var imageView: ImageView
        var priceTextView: TextView
        var checkBox: CheckBox
        var mEditText: EditText
        var mAddView: ImageView
        var mCutView: ImageView
        var morePricetTextView: TextView
        var mCheckLayout: LinearLayout
        var mDeleteView: ImageView

        val mShopLay: LinearLayout
        val mShopNameTv: TextView

        init {
            nameTextView = convertView.findViewById<View>(R.id.cart_item_name) as TextView
            priceTextView = convertView.findViewById<View>(R.id.cart_item_price) as TextView
            imageView = convertView.findViewById<View>(R.id.cart_item_image) as ImageView
            checkBox = convertView.findViewById<View>(R.id.cart_item_checkbox) as CheckBox
            mAddView = convertView.findViewById<View>(R.id.cart_item_add) as ImageView
            mCutView = convertView.findViewById<View>(R.id.cart_item_des) as ImageView
            mEditText = convertView.findViewById<View>(R.id.cart_item_edit) as EditText
            morePricetTextView = convertView.findViewById<View>(R.id.more_prcie) as TextView
            mCheckLayout = convertView.findViewById<View>(R.id.check) as LinearLayout
            mDeleteView = convertView.findViewById<View>(R.id.delete_view) as ImageView

            mShopLay = convertView.findViewById<View>(R.id.shop_lay) as LinearLayout
            mShopNameTv = convertView.findViewById<View>(R.id.shop_name) as TextView
        }
    }


    /**
     * 判断是否需要显示标题
     *
     * @param position
     * @return
     */
    private fun needTitle(position: Int): Boolean {
        // 第一个肯定是分类
        if (position == 0) {
            return true
        }

        // 异常处理
        if (position < 0) {
            return false
        }

        // 当前  // 上一个
        val currentEntity = mDataList[position] as MutableMap<String, Any>
        val previousEntity = mDataList[position - 1] as MutableMap<String, Any>
        if (null == currentEntity || null == previousEntity) {
            return false
        }

        val currentTitle = currentEntity["code"]!!.toString() + ""
        val previousTitle = previousEntity["code"]!!.toString() + ""
        if (null == previousTitle || null == currentTitle) {
            return false
        }

        // 当前item分类名和上一个item分类名不同，则表示两item属于不同分类
        return if (currentTitle == previousTitle) {
            false
        } else true
    }


    private fun isMove(position: Int): Boolean {
        // 获取当前与下一项
        val currentEntity = mDataList[position] as MutableMap<String, Any>
        val nextEntity = mDataList[position + 1] as MutableMap<String, Any>
        if (null == currentEntity || null == nextEntity) {
            return false
        }

        // 获取两项header内容
        val currentTitle = currentEntity["code"]!!.toString() + ""
        val nextTitle = nextEntity["code"]!!.toString() + ""
        if (null == currentTitle || null == nextTitle) {
            return false
        }

        // 当前不等于下一项header，当前项需要移动了
        return if (currentTitle != nextTitle) {
            true
        } else false

    }
}
