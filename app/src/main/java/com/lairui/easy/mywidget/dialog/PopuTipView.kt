package com.lairui.easy.mywidget.dialog

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView

import com.flyco.dialog.utils.CornerUtils
import com.flyco.dialog.view.TriangleView
import com.lairui.easy.R
import com.lairui.easy.utils.tool.UtilTools



class PopuTipView(context: Context, msg: String, id: Int) : PopupWindow() {

    private val mHeight: Int
    private val mWidth: Int

    private val popView: View

    private val mJianTouLay: LinearLayout
    private val mLlContent: LinearLayout
    private val mTriangleView: TriangleView

    init {
        popView = View.inflate(context, id, null)
        popView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        mTriangleView = popView.findViewById(R.id.triangle_view)
        mTriangleView.color = Color.parseColor("#A6000000")
        val mTriangleLayoutParams = mTriangleView.layoutParams as LinearLayout.LayoutParams
        mTriangleLayoutParams.width = UtilTools.dip2px(context, 6)
        mTriangleLayoutParams.height = UtilTools.dip2px(context, 9)
        mTriangleView.layoutParams = mTriangleLayoutParams


        mJianTouLay = popView.findViewById(R.id.jiantou_lay)


        val mTextView2 = popView.findViewById<TextView>(R.id.tv_bubble)
        mTextView2.text = msg


        mLlContent = popView.findViewById(com.flyco.dialog.R.id.ll_content)
        mLlContent.background = CornerUtils.cornerDrawable(Color.parseColor("#A6000000"), 15f)

        /*	RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) mLlContent.getLayoutParams();
		// mLlContent.setBackgroundDrawable( CornerUtils.cornerDrawable(Color.parseColor("#A6000000"), 15));
		mLlContent.setBackground(CornerUtils.cornerDrawable(Color.parseColor("#A6000000"), 15));
		mLayoutParams.setMargins(0, 0, 20, 0);
		mLlContent.setLayoutParams(mLayoutParams);*/


        //popView.requestFocus();//pop设置不setBackgroundDrawable情况，把焦点给popView，添加popView.setOnKeyListener。可实现点击外部不消失，点击反键才消失
        //			mPopupWindow.showAtLocation(mCityTv, Gravity.TOP|Gravity.RIGHT, 0, 0); //设置layout在PopupWindow中显示的位置
        // mPopupWindow.showAsDropDown(mCityLay);
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        popView.measure(w, h)
        val height = popView.measuredHeight
        val width = popView.measuredWidth
        println("measure width=$width height=$height")

        mHeight = height + popView.paddingTop + popView.paddingBottom
        mWidth = width + popView.paddingLeft + popView.paddingRight


        val mLayoutParams = mLlContent.layoutParams as RelativeLayout.LayoutParams
        mLlContent.background = CornerUtils.cornerDrawable(Color.parseColor("#A6000000"), 15f)
        mLayoutParams.setMargins(0, 0, 0, 0)
        //mLayoutParams.addRule(RelativeLayout.ABOVE, R.id.jiantou_lay);
        mLlContent.layoutParams = mLayoutParams

        this.update()
        this.isTouchable = true
        this.isFocusable = true

        this.width = LayoutParams.WRAP_CONTENT
        this.height = LayoutParams.WRAP_CONTENT
        this.contentView = popView
    }


    /**
     * 显示已经改进度对话框
     *
     * @param parent
     * 为该进度对话框设置从界面中的什么位置开始显示
     */
    fun show(parent: View, type: Int) {
        val location = IntArray(2)
        parent.getLocationOnScreen(location)
        val layoutParams: RelativeLayout.LayoutParams
        when (type) {
            1//上面靠view右边显示
            -> {
                mTriangleView.gravity = Gravity.BOTTOM
                layoutParams = mJianTouLay.layoutParams as RelativeLayout.LayoutParams
                layoutParams.width = parent.width
                layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                mJianTouLay.layoutParams = layoutParams
                this.showAtLocation(parent, Gravity.TOP or Gravity.LEFT, location[0], location[1] - mHeight)
            }
            2//上面靠view左边显示
            -> {
                mTriangleView.gravity = Gravity.BOTTOM
                layoutParams = mJianTouLay.layoutParams as RelativeLayout.LayoutParams
                layoutParams.width = parent.width
                layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                //layoutParams.addRule(RelativeLayout.BELOW, R.id.ll_content);
                layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.ll_content)
                //				mJianTouLay.setGravity(Gravity.RIGHT);
                mJianTouLay.layoutParams = layoutParams
                this.showAtLocation(parent, Gravity.TOP or Gravity.LEFT, location[0] - mWidth + parent.width, location[1] - mHeight)
            }
            3//下面靠view右边显示
            -> {
                mTriangleView.gravity = Gravity.TOP
                layoutParams = mJianTouLay.layoutParams as RelativeLayout.LayoutParams
                layoutParams.width = parent.width
                layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                //layoutParams.addRule(RelativeLayout.ABOVE, R.id.ll_content);
                mJianTouLay.layoutParams = layoutParams
                this.showAtLocation(parent, Gravity.TOP or Gravity.LEFT, location[0], location[1] + parent.height)
            }
            4//下面靠view左边显示
            -> {
                mTriangleView.gravity = Gravity.TOP
                layoutParams = mJianTouLay.layoutParams as RelativeLayout.LayoutParams
                layoutParams.width = parent.width
                layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.ll_content)
                mJianTouLay.layoutParams = layoutParams
                this.showAtLocation(parent, Gravity.TOP or Gravity.LEFT, location[0] - mWidth + parent.width, location[1] + parent.height)
            }
        }

    }
}
