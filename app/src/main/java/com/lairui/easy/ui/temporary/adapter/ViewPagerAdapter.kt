package com.lairui.easy.ui.temporary.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri

import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.view.ViewGroup


import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.lairui.easy.R
import com.lairui.easy.utils.imageload.CircleProgressView
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.imageload.ProgressInterceptor
import com.lairui.easy.utils.imageload.ProgressListener
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.luck.picture.lib.config.PictureMimeType
import com.lairui.easy.mywidget.photoview.PhotoView

import java.io.File


class ViewPagerAdapter(private val mContext: Context, private val mViews: List<View>, private val mImgList: List<MutableMap<String, Any>>) : PagerAdapter() {

    override fun getCount(): Int {
        return mImgList.size
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        // TODO Auto-generated method stub
        return arg0 === arg1
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val i = position % 4
        val mRootView = mViews[i]

        val mCircleProgressView = mRootView.findViewById<CircleProgressView>(R.id.progressView)
        val view = mRootView.findViewById<View>(R.id.big_image_view) as SubsamplingScaleImageView
        val mPhotoView = mRootView.findViewById<PhotoView>(R.id.big_photo_image_view)

        //在此次回收图片

        //........回收代码
        Glide.with(view.context).clear(view)
        view.recycle()
        //Runtime.getRuntime().gc();
        //回收图片
        (container as ViewPager).removeView(mRootView)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val i = position % 4
        val mRootView = mViews[i]

        val mCircleProgressView = mRootView.findViewById<CircleProgressView>(R.id.progressView)
        val view = mRootView.findViewById<SubsamplingScaleImageView>(R.id.big_image_view)
        val mPhotoView = mRootView.findViewById<PhotoView>(R.id.big_photo_image_view)
        //view.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        view.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
        view.setDoubleTapZoomScale(2.0f)
        view.minScale = 0.3f//最小显示比例
        view.maxScale = 5.0f//最大显示比例（太大了图片显示会失真，因为一般微博长图的宽度不会太宽）
        //ViewCompat.setTransitionName(view, "detail:header:image" + ":" + position);
        val map = mImgList[position]
        (container as ViewPager).addView(mRootView, 0)

        var imgUrl = map["remotepath"]!!.toString() + ""
        if (imgUrl.contains("http")) {
        } else {
            imgUrl = MbsConstans.PIC_URL + imgUrl
        }

        ProgressInterceptor.addListener(imgUrl, object : ProgressListener {
            override fun onProgress(progress: Int) {
                mCircleProgressView.progress = progress
            }
        })

        Glide
                .with(view.context)
                .asFile()
                .load(imgUrl)
                .into(object : SimpleTarget<File>() {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        //隐藏加载的进度条
                        mCircleProgressView.visibility = View.GONE
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        //显示加载进度条
                        //viewHolder.mPd.setVisibility(View.VISIBLE);
                    }

                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                        val filePath = resource.path
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(filePath, options)
                        val bmpWidth = options.outWidth
                        val bmpHeight = options.outHeight


                        //outMimeType是以--”image/png”、”image/jpeg”、”image/gif”…….这样的方式返回的
                        val mimeType = options.outMimeType
                        LogUtil.i("图片类型:", "" + mimeType)
                        var isGif = false
                        if (UtilTools.empty(mimeType)) {
                            isGif = false
                        } else {
                            isGif = PictureMimeType.isGif(mimeType)
                        }
                        //自定义的判断是否为GIF的工具类

                        val scale = GlideUtils.getImageScale(mContext, resource.absolutePath)
                        if (isGif) {
                            view.visibility = View.GONE
                            mPhotoView.visibility = View.VISIBLE
                            Glide.with(view.context)
                                    .asGif()
                                    .load(resource)
                                    //监听器是RequestListener<GifDrawable>类型的，其中控制了加载进度条——loadingProgress的隐藏
                                    //imageView即 PhotoView对象
                                    .into(mPhotoView)

                        } else {

                            view.visibility = View.VISIBLE
                            mPhotoView.visibility = View.GONE
                            view.setImage(ImageSource.uri(Uri.fromFile(resource)), ImageViewState(scale, PointF(0f, 0f), SubsamplingScaleImageView.ORIENTATION_USE_EXIF))
                            //                            view.setImage(ImageSource.uri(Uri.fromFile(resource)));
                        }
                        mCircleProgressView.visibility = View.GONE
                    }
                })


        /*	Glide.with(mContext)
				.load(map.get("remotepath")+"")
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        e.printStackTrace();
                        LogUtil.i("show",e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
				.into(new SimpleTarget<Drawable>() {
					@Override
					public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
						BitmapDrawable bd = (BitmapDrawable) resource;
						Bitmap bm = bd.getBitmap();

						view.setImage(ImageSource.bitmap(bm),new ImageViewState(2.0F, new PointF(0, 0), 0));
					}
				});*/

        return mRootView
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

}
