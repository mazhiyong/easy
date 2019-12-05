package com.lairui.easy.utils.tool

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface

import java.io.IOException

object RotatePictureUtil {

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm
     * 需要旋转的图片
     * @param degree
     * 旋转角度
     * @return 旋转后的图片
     */
    fun rotateBitmapByDegree(bm: Bitmap?, degree: Int): Bitmap? {
        var returnBm: Bitmap? = null

        if (null == bm) {
            return null
        }
        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
        }

        if (returnBm == null) {
            returnBm = bm
        }
        return returnBm
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path
     * 图片绝对路径
     * @return 图片的旋转角度
     */
    fun getBitmapDegree(path: String): Int {
        var degree = 0
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            val exifInterface = ExifInterface(path)
            // 获取图片的旋转信息
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }
}
