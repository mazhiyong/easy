package com.lairui.easy.utils.imageload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.lairui.easy.R
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.RotatePictureUtil
import com.lairui.easy.utils.tool.UtilTools

import java.util.UUID

import jp.wasabeef.glide.transformations.GrayscaleTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


/**
 *
 * 描述：Glide工具类（glide 4.x）
 * 功能包括加载图片，圆形图片，圆角图片，指定圆角图片，模糊图片，灰度图片等等。
 * 目前我只加了这几个常用功能，其他请参考glide-transformations这个开源库。
 * https://github.com/wasabeef/glide-transformations
 */
class GlideUtils {

    /**
     * Glide.with(this).asGif()    //强制指定加载动态图片
     * 如果加载的图片不是gif，则asGif()会报错， 当然，asGif()不写也是可以正常加载的。
     * 加入了一个asBitmap()方法，这个方法的意思就是说这里只允许加载静态图片，不需要Glide去帮我们自动进行图片格式的判断了。
     * 如果你传入的还是一张GIF图的话，Glide会展示这张GIF图的第一帧，而不会去播放它。
     *
     * @param context
     * @param url       例如：https://image.niwoxuexi.com/blog/content/5c0d4b1972-loading.gif
     * @param imageView
     */
    private fun loadGif(context: Context, url: String, imageView: ImageView) {
        val options = RequestOptions()
                .placeholder(placeholderSoWhite)
                .error(errorSoWhite)
        Glide.with(context)
                .load(url)
                .apply(options)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {

                        return false
                    }
                })
                .into(imageView)

    }

    fun downloadImage(context: Context, url: String) {
        Thread(Runnable {
            try {
                //String url = "http://www.guolin.tech/book.png";
                val target = Glide.with(context)
                        .asFile()
                        .load(url)
                        .submit()
                val imageFile = target.get()
                Log.d("logcat", "下载好的图片文件路径=" + imageFile.path)
                /*  runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, imageFile.getPath(), Toast.LENGTH_LONG).show();
                        }
                    });*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    companion object {


        val placeholderSoWhite = R.drawable.refresh_people_3
        val errorSoWhite = R.drawable.refresh_people_3
        // public static final int soWhite = R.color.white;

        /**
         * 加载图片(默认)
         */
        @JvmStatic
        fun loadImage(context: Context, url: String, imageView: ImageView) {
            //url = "https://gagayi.oss-cn-beijing.aliyuncs.com/video/image.jpg";
            // url = "http://172.16.1.173:8082/image22.jpg";
            // url = "http://wx2.sinaimg.cn/mw690/b1072857ly1fm2xa2am75j20rsa8xqv8.jpg";


            /* Picasso
                .with(context)
                .load(url)
                .into(imageView);*/

            val options = RequestOptions()
                    //.skipMemoryCache(true)
                    // .diskCacheStrategy(DiskCacheStrategy.NONE)
                    // .format(DecodeFormat.PREFER_RGB_565)////.format(DecodeFormat.PREFER_ARGB_8888)
                    //.timeout(1*1000*60)
                    // .centerCrop()
                    .placeholder(R.color.body_bg) //占位图
                    .error(R.color.body_bg)       //错误图
            //.disallowHardwareConfig()


            // .signature(new ObjectKey(UUID.randomUUID().toString())) ;
            // .priority(Priority.HIGH)

           /* val glideUrl = GlideUrl(url, LazyHeaders.Builder()
                    .addHeader("access_token", MbsConstans.ACCESS_TOKEN)
                    .addHeader("refresh_token", MbsConstans.REFRESH_TOKEN)
                    .build())*/
            Glide.with(context)
                    .load(url)
                    //.thumbnail(0.3f)
                    .apply(options)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            e!!.printStackTrace()
                            e.message?.let { LogUtil.i("show", it) }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(imageView)
            /* GlideApp.with(context)
                .asBitmap()
                .centerCrop()
                .load(url)
                .into(imageView);*/

        }

        /**
         * 加载图片(默认)
         */
        fun loadImage(context: Context, url: String, imageView: ImageView, defaultPic: Int) {

            val options = RequestOptions()
                    .placeholder(defaultPic) //占位图
                    .error(defaultPic)       //错误图

            Glide.with(context)
                    .load(url)
                    //.thumbnail(0.3f)
                    .apply(options)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            e!!.printStackTrace()
                            e.message?.let { LogUtil.i("show", it) }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(imageView)

        }

        /**
         * 加载图片(默认)
         */
        fun loadImage2(context: Context, url: String, imageView: ImageView, defaultPic: Int) {

            val options = RequestOptions()
                    .placeholder(defaultPic) //占位图
                    .error(defaultPic)       //错误图
                    .signature(ObjectKey(UUID.randomUUID().toString()))



            Glide.with(context)
                    .load(url)
                    //.thumbnail(0.3f)
                    .apply(options)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            e!!.printStackTrace()
                            e.message?.let { LogUtil.i("show", it) }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(imageView)

        }

        /*
     *加载图片(默认)
     */
        fun loadUUIDImage(context: Context, url: String, imageView: ImageView) {
            val options = RequestOptions()
                    .placeholder(R.color.body_bg) //占位图
                    .error(R.color.body_bg)      //错误图
                    .signature(ObjectKey(UUID.randomUUID().toString()))

            Glide.with(context)
                    .load(url)
                    //.thumbnail(0.3f)
                    .apply(options)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            e!!.printStackTrace()
                            e.message?.let { LogUtil.i("show", it) }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(imageView)

        }


        /*
     *加载图片(默认)
     */
        fun loadSmallImage(context: Context, url: String, imageView: ImageView) {
            //url = "https://gagayi.oss-cn-beijing.aliyuncs.com/video/image.jpg";
            // url = "http://172.16.1.173:8082/image22.jpg";
            // url = "http://wx2.sinaimg.cn/mw690/b1072857ly1fm2xa2am75j20rsa8xqv8.jpg";


            /* Picasso
                .with(context)
                .load(url)
                .into(imageView);*/

            val options = RequestOptions()
                    //                .skipMemoryCache(true)
                    //                .diskCacheStrategy(DiskCacheStrategy.NONE)
                    //                .format(DecodeFormat.PREFER_RGB_565)////.format(DecodeFormat.PREFER_ARGB_8888)
                    // .timeout(1*1000*60)
                    // .override(200,200)
                    .placeholder(R.color.body_bg) //占位图
                    .error(R.color.body_bg)       //错误图
                    //                .disallowHardwareConfig()
                    .centerCrop()

            // .signature(new ObjectKey(UUID.randomUUID().toString())) ;
            // .priority(Priority.HIGH)

            val glideUrl = GlideUrl(url, LazyHeaders.Builder()
                    .addHeader("access_token", MbsConstans.ACCESS_TOKEN)
                    .addHeader("refresh_token", MbsConstans.REFRESH_TOKEN)
                    .build())
            Glide.with(context)
                    .load(glideUrl)
                    .thumbnail(0.2f)
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade())//淡入淡出
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            e!!.printStackTrace()
                            e.message?.let { LogUtil.i("show", it) }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(imageView)
            /* GlideApp.with(context)
                .asBitmap()
                .centerCrop()
                .load(url)
                .into(imageView);*/

        }


        /**
         * 指定图片大小;使用override()方法指定了一个图片的尺寸。
         * Glide现在只会将图片加载成width*height像素的尺寸，而不会管你的ImageView的大小是多少了。
         * 如果你想加载一张图片的原始尺寸的话，可以使用Target.SIZE_ORIGINAL关键字----override(Target.SIZE_ORIGINAL)
         *
         * @param context
         * @param url
         * @param imageView
         * @param width
         * @param height
         */
        fun loadImageSize(context: Context, url: String, imageView: ImageView, width: Int, height: Int) {
            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(placeholderSoWhite) //占位图
                    .error(placeholderSoWhite)       //错误图
                    .override(width, height)
                    // .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(context).load(url).apply(options).into(imageView)

        }


        /**
         * 禁用内存缓存功能
         * diskCacheStrategy()方法基本上就是Glide硬盘缓存功能的一切，它可以接收五种参数：
         *
         *
         * DiskCacheStrategy.NONE： 表示不缓存任何内容。
         * DiskCacheStrategy.DATA： 表示只缓存原始图片。
         * DiskCacheStrategy.RESOURCE： 表示只缓存转换过后的图片。
         * DiskCacheStrategy.ALL ： 表示既缓存原始图片，也缓存转换过后的图片。
         * DiskCacheStrategy.AUTOMATIC： 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）。
         */

        fun loadImageSizekipMemoryCache(context: Context, url: String, imageView: ImageView) {
            val options = RequestOptions()
                    .placeholder(placeholderSoWhite) //占位图
                    .error(R.color.white)       //错误图S
                    .skipMemoryCache(true)//禁用掉Glide的内存缓存功能
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(context).load(url).apply(options).into(imageView)

        }


        /**
         * 加载圆形图片
         */
        @JvmStatic
        fun loadCircleImage(context: Context, url: String, imageView: ImageView) {
            val options = RequestOptions()
                    .centerCrop()
                    .circleCrop()//设置圆形
                    .placeholder(R.color.body_bg) //占位图
                    .error(R.color.body_bg)       //错误图
                    .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(context).load(url).apply(options).into(imageView)
        }

        /**
         * 预先加载图片
         * 在使用图片之前，预先把图片加载到缓存，调用了预加载之后，我们以后想再去加载这张图片就会非常快了，
         * 因为Glide会直接从缓存当中去读取图片并显示出来
         */
        fun preloadImage(context: Context, url: String) {
            Glide.with(context)
                    .load(url)
                    .preload()

        }

        /**
         * 加载圆角图片
         */
        fun loadRoundCircleImage(context: Context, url: String, imageView: ImageView) {
            val options = RequestOptions()
                    .centerCrop()
                    .circleCrop()//设置圆形
                    .placeholder(placeholderSoWhite)
                    .error(errorSoWhite)
                    //.priority(Priority.HIGH)
                    .transform(RoundedCornersTransformation(15, 0, RoundedCornersTransformation.CornerType.ALL))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(context).load(url).apply(options).into(imageView)

        }


        /**
         * 加载圆角图片-指定任意部分圆角（图片上、下、左、右四个角度任意定义）
         *
         * @param context
         * @param url
         * @param imageView
         * @param type
         */
        fun loadCustRoundCircleImage(context: Context, url: String, imageView: ImageView, type: RoundedCornersTransformation.CornerType) {
            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(placeholderSoWhite)
                    .error(errorSoWhite)
                    //.priority(Priority.HIGH)
                    .transform(RoundedCornersTransformation(45, 0, type))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)

            Glide.with(context).load(url).apply(options).into(imageView)
        }


        /**
         * 加载模糊图片（自定义透明度）
         *
         * @param context
         * @param url
         * @param imageView
         * @param blur      模糊度，一般1-100够了，越大越模糊
         */
        fun loadBlurImage(context: Context, url: String, imageView: ImageView, blur: Int) {
            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(placeholderSoWhite)
                    .error(errorSoWhite)
                    //.priority(Priority.HIGH)
                    //.bitmapTransform(BlurTransformation(blur))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(context).load(url).apply(options).into(imageView)
        }

        /*
     *加载灰度(黑白)图片（自定义透明度）
     */
        fun loadBlackImage(context: Context, url: String, imageView: ImageView) {
            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(placeholderSoWhite)
                    .error(errorSoWhite)
                    //.priority(Priority.HIGH)
                    .transform(GrayscaleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(context).load(url).apply(options).into(imageView)
        }


        fun dip2px(context: Context, dp: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }


        /**
         * 计算出图片初次显示需要放大倍数
         * @param imagePath 图片的绝对路径
         */
        fun getImageScale(context: Context, imagePath: String): Float {
            if (TextUtils.isEmpty(imagePath)) {
                return 2.0f
            }

            var bitmap: Bitmap? = null

            try {
                bitmap = BitmapFactory.decodeFile(imagePath)
            } catch (error: OutOfMemoryError) {
                error.printStackTrace()
            }


            val i = RotatePictureUtil.getBitmapDegree(imagePath)
            bitmap = RotatePictureUtil.rotateBitmapByDegree(bitmap, i)

            if (bitmap == null) {
                return 2.0f
            }

            // 拿到图片的宽和高
            val dw = bitmap.width
            val dh = bitmap.height

            /* WindowManager wm = ((Activity)context).getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();*/

            val width = UtilTools.getScreenWidth(context)
            val height = UtilTools.getScreenHeight(context)


            var scale = 1.0f
            //图片宽度大于屏幕，但高度小于屏幕，则缩小图片至填满屏幕宽
            if (dw > width && dh <= height) {
                scale = width * 1.0f / dw
            }
            //图片宽度小于屏幕，但高度大于屏幕，则放大图片至填满屏幕宽
            if (dw <= width && dh > height) {
                scale = width * 1.0f / dw

            }
            //图片高度和宽度都小于屏幕，则放大图片至填满屏幕宽
            if (dw < width && dh < height) {
                scale = width * 1.0f / dw

            }
            //图片高度和宽度都大于屏幕，则缩小图片至填满屏幕宽
            if (dw > width && dh > height) {
                scale = width * 1.0f / dw
            }
            bitmap.recycle()
            return scale
        }
    }

}