package com.lairui.easy.ui.temporary.activity


import android.graphics.Bitmap
import android.graphics.Color

import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.ViewPagerAdapter
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.utils.imageload.CircleProgressView
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil
import com.komi.slider.SliderConfig
import com.komi.slider.SliderUtils
import com.komi.slider.position.SliderPosition

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import java.util.Date


/**
 *
 * @Description:显示大图界面
 */
class ShowDetailPictrue : BasicActivity(), View.OnClickListener {
    private var mViewPager: ViewPager? = null
    /**得到上一个界面点击图片的位置 */
    private var position = 0
    private var mPagerAdapter: ViewPagerAdapter? = null
    private var mViews: MutableList<View>? = null
    //装ViewPager中ImageView的数组
    //	private ImageView[] mYuanImageView;
    private var mDataList: List<MutableMap<String, Any>> = ArrayList()
    private var mFileList: List<MutableMap<String, Any>>? = ArrayList()
    private var mTipTv: TextView? = null
    private var mBackTv: ImageView? = null
    private var mSaveTv: TextView? = null


    private var mLayoutInflater: LayoutInflater? = null

    override val contentView: Int
        get() = R.layout.show_detail_pictrue_a

    override fun isSupportSwipeBack(): Boolean {
        return false
    }

    override fun init() {

        initSilder()
        //StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, R.color.black), 0);
        //StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.black), 0);
        StatusBarUtil.setTranslucentForImageView(this, 0, null)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) //隐藏状态栏

        mLayoutInflater = LayoutInflater.from(this)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            position = bundle.getInt("position", 0)
            mDataList = bundle.getSerializable("DATA") as List<MutableMap<String, Any>>
        }

        /*position = 0;

		Map<String,Object> map = new HashMap<>();
		map.put("remotepath","https://gagayi.oss-cn-beijing.aliyuncs.com/video/article.jpg");
        mDataList.add(map);

        map = new HashMap<>();
        map.put("remotepath","https://gagayi.oss-cn-beijing.aliyuncs.com/video/bear.png");
        mDataList.add(map);

        map = new HashMap<>();
        map.put("remotepath","https://gagayi.oss-cn-beijing.aliyuncs.com/video/river.jpg");
        mDataList.add(map);*/

        initViewPager()
    }

    //实现界面上下滑动退出界面效果
    private fun initSilder() {
        val mConfig = SliderConfig.Builder()
                .primaryColor(Color.BLACK)
                .secondaryColor(Color.TRANSPARENT)
                .position(SliderPosition.VERTICAL)
                .slideEnter(true)
                .sensitivity(0.3f)
                .distanceThreshold(0.2f)
                //.edgePercent(1f)
                .edge(false)
                .build()
        SliderUtils.attachActivity(this, mConfig)
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout)

    }

    private fun initViewPager() {

        /*SlideCloseLayout swipeableLayout = (SlideCloseLayout) findViewById(R.id.swipableLayout);
		swipeableLayout.setGradualBackground(getWindow().getDecorView().getBackground());
		//设置监听，滑动一定距离后让Activity结束
		swipeableLayout.setLayoutScrollListener(new SlideCloseLayout.LayoutScrollListener() {
			@Override
			public void onLayoutClosed() {
				onBackPressed();
			}
		});*/

        mTipTv = findViewById<View>(R.id.position_tv) as TextView
        mBackTv = findViewById<View>(R.id.btn_back) as ImageView
        mBackTv!!.setOnClickListener(this)
        mSaveTv = findViewById<View>(R.id.btn_save) as TextView
        mSaveTv!!.setOnClickListener(this)
        mViewPager = findViewById<View>(R.id.viewPager_show_bigPic) as ViewPager
        mFileList = mDataList
        initView()
    }

    private fun initView() {

        //mLinearLayout.removeAllViews();
        if (mFileList == null || mFileList!!.size <= 0) {
            return
        }

        mViews = ArrayList()
        for (i in 0..3) {
            /*PhotoView imageView = new PhotoView(this);
			//imageView.setScaleType(ScaleType.CENTER_CROP);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			imageView.setTag(R.id.glide_tag,mFileList.get(i));

			imageView.setLayoutParams(params);*/

            val view = mLayoutInflater!!.inflate(R.layout.item_viewpager_pic, null)
            val relativeLayout = view.findViewById<RelativeLayout>(R.id.content_lay)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            relativeLayout.layoutParams = params
            /*SubsamplingScaleImageView imageView = new SubsamplingScaleImageView(this);
			//imageView.setScaleType(ScaleType.CENTER_CROP);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			imageView.setTag(R.id.glide_tag,mFileList.get(i));

			imageView.setLayoutParams(params);*/
            mViews!!.add(view)
        }

        mPagerAdapter = ViewPagerAdapter(this, mViews as ArrayList<View>, mFileList!!)
        mViewPager!!.adapter = mPagerAdapter
        //mViewPager.setOffscreenPageLimit(3);
        mViewPager!!.addOnPageChangeListener(MyPageChangeListener())
        mViewPager!!.currentItem = position
        mTipTv!!.text = (position + 1).toString() + "/" + mFileList!!.size
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back -> this.finish()
            R.id.btn_save -> {
            }
        }
    }

    private fun saveCroppedImage() {
        val imageView = mViews!![mViewPager!!.currentItem] as ImageView
        imageView.isDrawingCacheEnabled = true
        val bmp = imageView.drawingCache

        val map = mFileList!![mViewPager!!.currentItem]
        try {
            val saveFile = File(MbsConstans.PIC_SAVE)

            val filepath = MbsConstans.PIC_SAVE + Date().time + ".png"
            val file = File(filepath)
            if (!saveFile.exists()) {
                saveFile.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            val saveFile2 = File(filepath)

            val fos = FileOutputStream(saveFile2)
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, fos)
            fos.flush()
            fos.close()
            imageView.isDrawingCacheEnabled = false
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {

    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun disimissProgress() {

    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }

    /**
     * 当ViewPager中页面的状态发生改变时调用
     *
     * @author Administrator
     */
    private inner class MyPageChangeListener : OnPageChangeListener {

        /**
         * This method will be invoked when a new page becomes selected.
         * position: Position index of the new selected page.
         */
        override fun onPageSelected(position: Int) {

            mSwipeBackHelper!!.setSwipeBackEnable(position == 0)
            setImageBackground(position)
            mTipTv!!.text = (position + 1).toString() + "/" + mFileList!!.size

        }

        override fun onPageScrollStateChanged(arg0: Int) {

        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

        }
    }

    /**
     * 设置选中的tip的背景
     *
     * @param selectItemsIndex
     */
    private fun setImageBackground(selectItemsIndex: Int) {
        //		for (int i = 0; i < mYuanImageView.length; i++) {
        //			if (i == selectItemsIndex) {
        //				mYuanImageView[i].setBackgroundResource(R.drawable.app_button_white_checked);
        //			} else {
        //				mYuanImageView[i].setBackgroundResource(R.drawable.app_button_white_normal);
        //			}
        //		}
    }

    override fun finish() {
        super.finish()
        if (mViews != null) {
            for (i in mViews!!.indices) {
                var mRootView: View? = mViews!![i]
                var mCircleProgressView: CircleProgressView? = mRootView!!.findViewById(R.id.progressView)
                var view: SubsamplingScaleImageView? = mRootView.findViewById<View>(R.id.big_image_view) as SubsamplingScaleImageView
                Glide.with(view!!.context).clear(view)
                view.recycle()
                view = null
                mCircleProgressView = null
                mRootView = null
            }
        }
        //Runtime.getRuntime().gc();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout)

    }

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout)
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
