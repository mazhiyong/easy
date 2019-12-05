package com.lairui.easy.mywidget.photoview

import android.content.Context
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView


class PhotoView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attr, defStyle), IPhotoView {


    private val mAttacher: PhotoViewAttacher?

    private var mPendingScaleType: ImageView.ScaleType? = null

    override val displayRect: RectF
        get() = mAttacher!!.displayRect

    override var minScale: Float
        get() = mAttacher!!.minScale
        set(minScale) {
            mAttacher!!.minScale = minScale
        }

    override var midScale: Float
        get() = mAttacher!!.midScale
        set(midScale) {
            mAttacher!!.midScale = midScale
        }

    override var maxScale: Float
        get() = mAttacher!!.maxScale
        set(maxScale) {
            mAttacher!!.maxScale = maxScale
        }

    override val scale: Float
        get() = mAttacher!!.scale

    init {
        super.setScaleType(ImageView.ScaleType.MATRIX)
        mAttacher = PhotoViewAttacher(this)

        if (null != mPendingScaleType) {
            setScaleType(mPendingScaleType!!)
            mPendingScaleType = null
        }
    }

    override fun canZoom(): Boolean {
        return mAttacher!!.canZoom()
    }


    override fun setAllowParentInterceptOnEdge(allow: Boolean) {
        mAttacher!!.setAllowParentInterceptOnEdge(allow)
    }

    override// setImageBitmap calls through to this method
    fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mAttacher?.update()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mAttacher?.update()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        mAttacher?.update()
    }

    override fun setOnMatrixChangeListener(listener: PhotoViewAttacher.OnMatrixChangedListener) {
        mAttacher!!.setOnMatrixChangeListener(listener)
    }

    override fun setOnLongClickListener(l: View.OnLongClickListener) {
        mAttacher!!.setOnLongClickListener(l!!)
    }

    override fun setOnPhotoTapListener(listener: PhotoViewAttacher.OnPhotoTapListener) {
        mAttacher!!.setOnPhotoTapListener(listener)
    }

    override fun setOnViewTapListener(listener: PhotoViewAttacher.OnViewTapListener) {
        mAttacher!!.setOnViewTapListener(listener)
    }


    override fun setZoomable(zoomable: Boolean) {
        mAttacher!!.setZoomable(zoomable)
    }

    override fun zoomTo(scale: Float, focalX: Float, focalY: Float) {
        mAttacher!!.zoomTo(scale, focalX, focalY)
    }

    override fun onDetachedFromWindow() {
        mAttacher!!.cleanup()
        super.onDetachedFromWindow()
    }

   /* override var scaleType: ScaleType
        get() = mAttacher!!.scaleType
        set(value) {
            if (null != mAttacher) {
                mAttacher.scaleType = scaleType
            } else {
                mPendingScaleType = scaleType
            }
        }*/

    /*override fun getScaleType(): ScaleType {
        return mAttacher!!.scaleType
    }


    override fun setScaleType(scaleType:ScaleType ) {

    }*/

    override fun getScaleType(): ScaleType {
        return mAttacher!!.scaleType
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (null != mAttacher) {
            mAttacher.scaleType = scaleType
        } else {
            mPendingScaleType = scaleType
        }
    }

}