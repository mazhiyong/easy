package com.lairui.easy.utils.imageload

import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

import android.graphics.Bitmap
import android.graphics.BitmapFactory

object BitmapUtil {
    /**
     * 压缩图片之后保存为文件
     *
     * @param filePath
     * 原始图片的完整路径
     * @param storeImgPath
     * 压缩之后要存储的图片的完整路径
     * @return boolean
     * @author Doraemon
     * @time 2014年6月27日下午5:10:19
     */
    fun saveCompressImg(filePath: String, storeImgPath: String): Boolean {
        val bm = getSmallBitmap(filePath)
        var stream: OutputStream? = null
        try {
            stream = FileOutputStream(storeImgPath)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return bm.compress(Bitmap.CompressFormat.JPEG, 20, stream)//质量压缩
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @return
     */
    private fun getSmallBitmap(filePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)

        // Calculate inSampleSize
        //options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inSampleSize = 1

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(filePath, options)
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }
}