package com.lairui.easy.mywidget.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.listener.ReLoadingData


class PageView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs), View.OnClickListener {

    private var mLoading: View? = null
    private var mNetworkError: View? = null

    private var mEmpty: View? = null
    private var mEmptyText: TextView? = null
    private var mContent: View? = null
    private var mOther: View? = null

    private var mSubscriber: Any? = null

    var reLoadingData: ReLoadingData? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.page_view, this)

        initView()
    }

    private fun initView() {
        mLoading = findViewById(R.id.page_view_loading)
        //ImageView mImageView = (ImageView) mLoading.findViewById(R.id.page_view_loading);
        /*AnimationDrawable aniDrawable;              android:src="@drawable/loading_anim"
		aniDrawable = (AnimationDrawable) ((ImageView)mLoading).getDrawable();
		aniDrawable.start();*/

        mEmpty = findViewById(R.id.page_view_empty)
        mEmptyText = findViewById<View>(R.id.page_view_empty_text) as TextView
        mNetworkError = findViewById(R.id.page_view_network_error)
        mEmpty!!.setOnClickListener(this)
        mNetworkError!!.setOnClickListener(this)

        hideAll()
    }

    private fun hideAll() {
        val childCount = childCount
        for (i in 0 until childCount) {
            getChildAt(i).visibility = View.GONE
        }
    }

    fun subscribRefreshEvent(subscriber: Any) {
        mSubscriber = subscriber
    }

    fun setEmptyText(text: String): PageView {
        mEmptyText!!.text = text
        return this
    }

    fun setEmptyText(resId: Int): PageView {
        mEmptyText!!.setText(resId)
        return this
    }

    fun setEmptyIcon(resId: Int): PageView {
        mEmptyText!!.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0)
        return this
    }

    fun setEmptyView(resId: Int) {
        mEmpty = findViewById(resId)
        hideView(mEmpty)
    }

    fun setOther(resId: Int) {
        mOther = findViewById(resId)
        hideView(mOther)
    }

    fun showOther() {
        hideAll()
        showView(mOther)
    }

    fun setContentView(content: View) {
        mContent = content
        hideView(mContent)
    }

    fun showLoading() {
        hideAll()
        showView(mLoading)
    }

    fun hideLoading() {
        hideView(mLoading)
    }

    fun showNetworkError() {
        hideAll()
        showView(mNetworkError)
    }

    fun showContent(show: Boolean) {
        if (show) {
            showContent()
        }
    }

    fun showContent() {
        hideAll()
        showView(mContent)
    }

    fun showEmpty(show: Boolean) {
        if (show) {
            showEmpty()
        }
    }

    fun showEmpty() {
        hideAll()
        showView(mEmpty)
    }

    override fun onClick(v: View) {
        if (reLoadingData != null) {
            //			showLoading();
            //showEmpty();
            //EventBusUtils.postEvent(mSubscriber, mMethodName);
            reLoadingData!!.reLoadingData()
        }
    }

    companion object {
        private val mMethodName = "onEventErrorRefresh"

        private fun hideView(view: View?) {
            if (view != null) {
                view.visibility = View.GONE
            }
        }

        private fun showView(view: View?) {
            if (view != null) {
                view.visibility = View.VISIBLE
            }
        }
    }

}
