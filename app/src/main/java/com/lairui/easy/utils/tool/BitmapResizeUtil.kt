package com.lairui.easy.utils.tool

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * bitmap对象操作工具类，最大可能性的降低OOM风险<br></br>
 * 提供的操作包括：获得一定宽高的缩略图、旋转图片、压缩保存图片、获得图片文件的旋转角度属性
 *
 *
 */
object BitmapResizeUtil {

    /**
     * 获得重置大小后的bitmap图片,同时解决OOM问题
     *
     * @param path
     * 文件在本地的路径
     * @param width
     * 重置后的宽度
     * @param height
     * 重置后的高度
     * @return bitmap 返回重置后的bmp图像
     */
    fun getResizeBitmap(path: String, width: Int, height: Int): Bitmap {

        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = true
        var bm = BitmapFactory.decodeFile(path, opt)
        val picWidth = opt.outWidth
        val picHeight = opt.outHeight

        opt.inSampleSize = 1
        if (picWidth > picHeight) {
            if (picWidth > width)
                opt.inSampleSize = picWidth / width
        } else {
            if (picHeight > height)
                opt.inSampleSize = picHeight / height
        }
        opt.inJustDecodeBounds = false
        bm = BitmapFactory.decodeFile(path, opt)
        return bm
    }

    /**
     * 获得重置大小后的bitmap图片,同时解决OOM问题
     *
     * @param path
     * 文件在本地的路径
     * @return bitmap 返回重置后的bmp图像
     */
    fun getResizeBitmap(path: String): Bitmap? {

        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = true
        var bm: Bitmap? = BitmapFactory.decodeFile(path, opt)
        if (null != bm) {
            opt.inSampleSize = 2
        } else {
            opt.inSampleSize = 1
        }

        opt.inJustDecodeBounds = false
        bm = BitmapFactory.decodeFile(path, opt)
        return bm
    }

    /**
     * 获得重置大小后的bitmap图片,同时解决OOM问题
     *
     * @param data
     * 图片文件的字节数据
     * @param width
     * 重置后的宽度
     * @param height
     * 重置后的高度
     * @return bitmap 返回重置后的bmp图像
     */
    fun getResizeBitmap(data: ByteArray, width: Int, height: Int): Bitmap {

        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = true
        var bm = BitmapFactory.decodeByteArray(data, 0, data.size, opt)
        val picWidth = opt.outWidth
        val picHeight = opt.outHeight

        opt.inSampleSize = 1
        if (picWidth > picHeight) {
            if (picWidth > width)
                opt.inSampleSize = picWidth / width
        } else {
            if (picHeight > height)

                opt.inSampleSize = picHeight / height
        }
        opt.inJustDecodeBounds = false
        bm = BitmapFactory.decodeByteArray(data, 0, data.size, opt)
        return bm
    }

    fun saveImg(baos: ByteArrayOutputStream, savePath: String,
                name: String): String {
        val mediaFile = File(savePath + name)
        if (mediaFile.exists()) {
            mediaFile.delete()
        }
        if (!File(savePath).exists()) {
            File(savePath).mkdirs()
        }
        try {
            mediaFile.createNewFile()

            var fos: FileOutputStream? = FileOutputStream(mediaFile)
            val isBm = ByteArrayInputStream(
                    baos.toByteArray())// 把压缩后的数据baos存放到ByteArrayInputStream中
            var bitmap = BitmapFactory.decodeStream(isBm, null, null)
            bitmap!!.compress(CompressFormat.JPEG, 80, fos)
            fos!!.flush()
            fos.close()
            fos = null
            bitmap = null
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return mediaFile.path
    }

    fun getThumbUploadPath(oldPath: String, newPath: String,
                           bitmapMaxWidth: Int, name: String): String {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        BitmapFactory.decodeFile(oldPath, options)
        val height = options.outHeight
        val width = options.outWidth

        val reqHeight: Int
        val reqWidth: Int
        if (height > width) {
            reqHeight = bitmapMaxWidth
            reqWidth = width * bitmapMaxWidth / height
        } else {
            reqWidth = bitmapMaxWidth
            reqHeight = height * bitmapMaxWidth / width
        }

        // // 在内存中创建bitmap对象，这个对象按照缩放大小创建的
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight)
        options.inJustDecodeBounds = false

        val degree = RotatePictureUtil.getBitmapDegree(oldPath)

        val bmpSelected = RotatePictureUtil.rotateBitmapByDegree(
                BitmapResizeUtil.getResizeBitmap(oldPath, reqWidth, reqHeight),
                degree)

        // Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
        val baos = compressImage(Bitmap.createScaledBitmap(
                bmpSelected!!, reqWidth, reqHeight, false))
        return saveImg(baos, newPath, name)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options,
                                      reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round(height.toFloat() / reqHeight.toFloat())
            } else {
                inSampleSize = Math.round(width.toFloat() / reqWidth.toFloat())
            }
        }
        return inSampleSize
    }


    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    fun compressImage2(image: Bitmap): Bitmap? {

        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true


        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset() // 重置baos即清空baos
            options -= 10// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中

        }
        val isBm = ByteArrayInputStream(baos.toByteArray())// 把压缩后的数据baos存放到ByteArrayInputStream中
        val bitmap = BitmapFactory.decodeStream(isBm, null, null)// 把ByteArrayInputStream数据生成图片

        opts.inJustDecodeBounds = false
        return bitmap
    }

    fun compressImage(image: Bitmap): ByteArrayOutputStream {
        var image = image

        val baos = ByteArrayOutputStream()
        image.compress(CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset()// 重置baos即清空baos
            options -= 10// 每次都减少10
            image.compress(CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
        }

        image.recycle()
        // 把ByteArrayInputStream数据生成图片
        return baos
    }

    // 将指定路径的图片压缩到200kb以内并存储在原路径
    fun compressBitmap(oldPath: String, newPath: String,
                       filename: String) {

        var bitmap: Bitmap? = null
        try {
            var file: File? = File(newPath, filename)
            var path: File? = File(newPath)
            if (!path!!.exists()) {
                path.mkdirs()
            }
            if (!file!!.exists()) {
                file.createNewFile()
            }

            // bitmap = BitmapFactory.decodeFile(path); //方式一,某些手机会OOM,如:S5
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(oldPath, opts)
            opts.inSampleSize = computeSampleSize(opts, -1, 960 * 960)
            opts.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeFile(oldPath, opts) // 方式二,解决OOM问题

            var baos: ByteArrayOutputStream? = ByteArrayOutputStream()
            // 100不压缩;
            var options = 90
            bitmap!!.compress(CompressFormat.JPEG, options, baos)// 质量压缩方法，这里options表示压缩，把数据存放到baos中
            while (baos!!.toByteArray().size / 1024 > 200) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
                baos.reset()// 重置baos即清空baos
                options -= 10// 每次都减少10
                bitmap.compress(CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
            }
            var fOut: FileOutputStream? = FileOutputStream(file)
            fOut!!.write(baos.toByteArray())
            fOut.flush()
            fOut.close()
            fOut = null
            baos.flush()
            baos.close()
            baos = null
            bitmap.recycle()
            bitmap = null
            file = null
            path = null
            System.gc()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

    }

    // 将指定路径的图片压缩到200kb以内并存储在原路径
    fun compressBitmap2(oldPath: String): Bitmap? {

        var bitmap: Bitmap? = null
        try {
            // bitmap = BitmapFactory.decodeFile(path); //方式一,某些手机会OOM,如:S5
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(oldPath, opts)
            opts.inSampleSize = computeSampleSize(opts, -1, 960 * 960)
            opts.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeFile(oldPath, opts) // 方式二,解决OOM问题

            val baos = ByteArrayOutputStream()
            // 100不压缩;
            var options = 90
            bitmap!!.compress(CompressFormat.JPEG, options, baos)// 质量压缩方法，这里options表示压缩，把数据存放到baos中
            while (baos.toByteArray().size / 1024 > 200) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
                baos.reset()// 重置baos即清空baos
                options -= 10// 每次都减少10
                bitmap.compress(CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
            }
            baos.flush()
            baos.close()
            bitmap.recycle()
            bitmap = null
            System.gc()
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 图片压缩方法（该压缩方法能够最大限度的节省内存并最大限度的保证图片质量）<br></br>
     * 压缩的最大压力测试:11张 8K分辨率的大图共603MB连续进行压缩通过
     *
     * @param oldPath
     * 图片原始路径
     * @param newPath
     * 图片要保存的路径
     * @param filename
     * 图片的保存名称
     * @param degree
     * 图片的旋转角度
     */
    fun compressBitmap(oldPath: String, newPath: String,
                       filename: String, degree: Int) {
        var bitmap: Bitmap? = null
        try {
            val file = File(newPath, filename)
            val path = File(newPath)
            if (!path.exists()) {
                path.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }

            // bitmap = BitmapFactory.decodeFile(path); //方式一,某些手机会OOM,如:S5
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(oldPath, opts)
            opts.inSampleSize = computeSampleSize(opts, -1, 1080 * 1920)
            opts.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeFile(oldPath, opts) // 方式二,解决OOM问题

            // 旋转图片
            bitmap = rotateBitmapByDegree(bitmap, degree)

            var baos: ByteArrayOutputStream? = ByteArrayOutputStream()
            // 100不压缩;
            var options = 90
            bitmap.compress(CompressFormat.JPEG, options, baos)// 质量压缩方法，这里options表示压缩，把数据存放到baos中
            while (baos!!.toByteArray().size / 1024 > 200) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
                baos.reset()// 重置baos即清空baos
                options -= 10// 每次都减少10
                bitmap.compress(CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
            }
            var fOut: FileOutputStream? = FileOutputStream(file)
            fOut!!.write(baos.toByteArray())
            fOut.flush()
            fOut.close()
            fOut = null
            baos.flush()
            baos.close()
            baos = null
            bitmap.recycle()
            bitmap = null
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

    }

    fun getBitmapFromFile(path: String): Bitmap {

        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, opts)
        opts.inSampleSize = computeSampleSize(opts, -1, 960 * 960)
        opts.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(path, opts)
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm
     * 需要旋转的图片
     * @param degree
     * 旋转角度
     * @return 旋转后的图片
     */
    fun rotateBitmapByDegree(bm: Bitmap?, degree: Int): Bitmap {
        var bm = bm
        var returnBm: Bitmap? = null

        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm!!, 0, 0, bm.width,
                    bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
        }

        if (returnBm == null) {
            returnBm = bm
        }
        if (bm != returnBm) {
            bm!!.recycle()
            bm = null
        }
        return returnBm!!
    }

    // Android提供了一种动态计算图片的inSampleSize方法
    fun computeSampleSize(options: BitmapFactory.Options,
                          minSideLength: Int, maxNumOfPixels: Int): Int {
        val initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels)
        var roundedSize: Int
        if (initialSize <= 8) {
            roundedSize = 1
            while (roundedSize < initialSize) {
                roundedSize = roundedSize shl 1
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8
        }
        return roundedSize
    }

    private fun computeInitialSampleSize(options: BitmapFactory.Options,
                                         minSideLength: Int, maxNumOfPixels: Int): Int {
        val w = options.outWidth.toDouble()
        val h = options.outHeight.toDouble()
        val lowerBound = if (maxNumOfPixels == -1)
            1
        else
            Math.ceil(Math
                    .sqrt(w * h / maxNumOfPixels)).toInt()
        val upperBound = if (minSideLength == -1)
            128
        else
            Math.min(
                    Math.floor(w / minSideLength), Math.floor(h / minSideLength)).toInt()
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound
        }
        return if (maxNumOfPixels == -1 && minSideLength == -1) {
            1
        } else if (minSideLength == -1) {
            lowerBound
        } else {
            upperBound
        }
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
            val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
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
