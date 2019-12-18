package com.lairui.easy.utils.tool


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.hardware.Camera
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.lairui.easy.basic.BasicApplication
import com.lairui.easy.basic.MbsConstans
import java.io.File
import java.io.FileOutputStream
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 *
 *
 * @Description: 辅助类用于一些常规方法
 *
 */
class UtilTools {


    fun isLongImage(width: Int, height: Int): Boolean {
        return if (width == 0 || height == 0 || width >= height) {
            false
        } else height / width >= 4
    }

    companion object {
        fun setListViewHeightBasedOnChildren(listView: ListView) {
            val listAdapter = listView.adapter
                    ?: // pre-condition
                    return

            var totalHeight = 0
            for (i in 0 until listAdapter.count) {
                val listItem = listAdapter.getView(i, null, listView)
                listItem.measure(0, 0)
                //int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
                //listItem.measure(desiredWidth, 0);
                totalHeight += listItem.measuredHeight
            }

            val params = listView.layoutParams
            params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
            listView.layoutParams = params

        }


        /**
         * 获得状态栏的高度
         *
         * @param context
         * @return
         */
        fun getStatusHeight2(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        /**
         * 得到状态栏高度信息
         * @param
         * @return > 0 success; <= 0 fail
         */
        fun getStatusHeight(context: Context): Int {
            var statusHeight = 0
            val localRect = Rect()
            (context as Activity).window.decorView.getWindowVisibleDisplayFrame(localRect)
            statusHeight = localRect.top
            if (0 == statusHeight) {
                val localClass: Class<*>
                try {
                    localClass = Class.forName("com.android.internal.R\$dimen")
                    val localObject = localClass.newInstance()
                    val i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString())
                    statusHeight = context.getResources().getDimensionPixelSize(i5)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: SecurityException) {
                    e.printStackTrace()
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                }

            }
            return statusHeight
        }

        @JvmStatic
        fun getScreenWidth(context: Context?): Int {
            if (context != null) {
                var dm = DisplayMetrics()
                dm = context.resources.displayMetrics
                return dm.widthPixels
            } else {
                return 0
            }

        }

        fun getScreenHeight(context: Context?): Int {
            if (context != null) {
                var dm = DisplayMetrics()
                dm = context.resources.displayMetrics
                return dm.heightPixels
            } else {
                return 0
            }
        }

        fun getScreenDensity(context: Context, i: Int): Float {
            var dp = 0
            var dm = DisplayMetrics()
            dm = context.resources.displayMetrics
            dp = (i * dm.density).toInt()
            return dp.toFloat()
        }


        /**
         * 判断是否有闪光灯
         * @param context
         * @return
         */
        fun isFlashLight(context: Context): Boolean {
            val pm = context.packageManager
            val features = pm.systemAvailableFeatures
            for (f in features) {
                if (PackageManager.FEATURE_CAMERA_FLASH == f.name)
                //判断设备是否支持闪光灯
                {
                    return true
                }
            }
            return false
        }

        /**
         * 判断一个对象是否为空
         * @param o
         * @return
         */
        @JvmStatic
        fun empty(o: Any?): Boolean {
            return (o == null
                    || "" == o.toString().trim { it <= ' ' }
                    || "null".equals(o.toString().trim { it <= ' ' }, ignoreCase = true)
                    || "nullnull".equals(o.toString().trim { it <= ' ' }, ignoreCase = true) //判断地址内容（省市+详细地址）是否为空

                    || "NULL".equals(o.toString().trim { it <= ' ' }, ignoreCase = true)
                    || "undefined".equals(o.toString().trim { it <= ' ' }, ignoreCase = true))
        }

        /**
         * 判断输入框是否有输入，并提示信息
         * @param w
         * @param displayStr
         * @return
         */
        fun isEmpty(w: TextView, displayStr: String): Boolean {
            var displayStr = displayStr
            if (empty(w.text.toString().trim { it <= ' ' })) {
                displayStr = displayStr + "不能为空!"
                val fgcspan = ForegroundColorSpan(Color.RED)
                val ssbuilder = SpannableStringBuilder(displayStr)
                ssbuilder.setSpan(fgcspan, 0, displayStr.length, 0)
                w.error = ssbuilder
                w.isFocusable = true
                w.requestFocus()
                return true
            }
            return false
        }

        /**
         * 提供精确的加法运算。
         * @param   v1   被加数
         * @param   v2   加数
         * @return   两个参数的和
         */
        fun add(v1: Double, v2: Double): Double {
            val b1 = BigDecimal(java.lang.Double.toString(v1))
            val b2 = BigDecimal(java.lang.Double.toString(v2))
            return b1.add(b2).toDouble()
        }

        /**
         * 提供精确的减法运算。
         * @param   v1   被减数
         * @param   v2   减数
         * @return   两个参数的差
         */

        fun sub(v1: Double, v2: Double): Double {
            val b1 = BigDecimal(java.lang.Double.toString(v1))
            val b2 = BigDecimal(java.lang.Double.toString(v2))
            return b1.subtract(b2).toDouble()
        }

        /**
         * 提供精确的乘法运算。
         * @param v1 被乘数
         * @param v2 乘数
         * @return 两个参数的积
         */
        fun mul(v1: Double, v2: Double): Double {
            val b1 = BigDecimal(java.lang.Double.toString(v1))
            val b2 = BigDecimal(java.lang.Double.toString(v2))
            return b1.multiply(b2).toDouble()
        }

        /**
         * 提供精确的除法运算。
         */
        fun divide(v1: Double, v2: Double): Double {
            //注意需要使用BigDecimal(String val)构造方法
            val bigDecimal = BigDecimal(java.lang.Double.toString(v1))
            val bigDecimal2 = BigDecimal(java.lang.Double.toString(v2))
            val scale = 2//保留2位小数
            val bigDecimalDivide = bigDecimal.divide(bigDecimal2, scale, BigDecimal.ROUND_HALF_UP)
            return bigDecimalDivide.toDouble()
        }


        /**
         * 把double转化为带有二位小数点的字符串
         * @param d 要转化的double，并保留小数点两位，多的按照四舍五入
         * @return
         */
        fun fromDouble(d: Double): String {
            val a = BigDecimal(d)
            return (a.setScale(2, BigDecimal.ROUND_HALF_UP)).toString()
        }

        fun fromDouble4(d: Double): String {
            val a = BigDecimal(d)
            return (a.setScale(4, BigDecimal.ROUND_HALF_UP) ).toString()
        }

        fun fromDouble6(d: Double): String {
            val a = BigDecimal(d)
            return (a.setScale(6, BigDecimal.ROUND_HALF_UP)).toString()
        }

        fun getIntFromStr(str: String): Int {
            try {
                var i = 0
                i = Integer.valueOf(str)
                return i
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }

        }

        fun getDoubleFromStr(str: String): Double? {
            try {
                var i = 0.0
                i = java.lang.Double.valueOf(str)
                return i
            } catch (e: Exception) {
                e.printStackTrace()
                return 0.0
            }

        }


        fun getDianType(context: Context, money: String): Spannable {
            var dian = money.length
            var ff = 0
            if (money.contains(MbsConstans.RMB)) {
                ff = money.indexOf(MbsConstans.RMB)
                ff = ff + 1
            }
            if (money.contains(".")) {
                dian = money.indexOf(".")
            } else {
                dian = money.length
            }
            val mSpan = SpannableString(money)
            mSpan.setSpan(AbsoluteSizeSpan(UtilTools.sp2px(context, 26)), 0, ff, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mSpan.setSpan(AbsoluteSizeSpan(UtilTools.sp2px(context, 36)), ff, dian, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mSpan.setSpan(AbsoluteSizeSpan(UtilTools.sp2px(context, 16)), dian, money.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            return mSpan
        }

        fun getDianType2(context: Context, money: String): Spannable {
            val dian = money.length
            var ff = 0
            if (money.contains(MbsConstans.RMB)) {
                ff = money.indexOf(MbsConstans.RMB)
                ff = ff + 1
            }
            /*if (money.contains(".")) {
			dian = money.indexOf(".");
		}else {
			dian = money.length();
		}*/
            val mSpan = SpannableString(money)
            mSpan.setSpan(AbsoluteSizeSpan(UtilTools.sp2px(context, 20)), 0, ff, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mSpan.setSpan(AbsoluteSizeSpan(UtilTools.sp2px(context, 36)), ff, dian, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            //mSpan.setSpan(new AbsoluteSizeSpan(UtilTools.sp2px(context,16)), dian, money.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            return mSpan
        }

        fun getRMBMoney(s: String): String {
            var s = s
            try {
                if (empty(s)) {
                    s = "0"
                }
                val d = java.lang.Double.valueOf(s)
                val dd = divide(d, 100.0)

                val myformat = DecimalFormat()
                myformat.applyPattern("##,###.00")
                var ss = myformat.format(dd)
                if (ss.startsWith(".")) {
                    ss = "0$ss"
                } else if (ss.startsWith("-.")) {
                    ss = ss.replace("-.", "-0.")
                }
                return MbsConstans.RMB + " " + ss
            } catch (e: Exception) {
                e.printStackTrace()
                return "0.00"
            }

        }

        fun getRMBMoneyZF(s: String): String {
            var s = s
            try {
                if (empty(s)) {
                    s = "0"
                }
                val d = java.lang.Double.valueOf(s)
                val dd = divide(d, 100.0)

                val myformat = DecimalFormat()
                myformat.applyPattern("##,###.00")
                var ss = myformat.format(dd)
                if (ss.startsWith(".")) {
                    ss = "+ " + MbsConstans.RMB + "0" + ss
                } else if (ss.startsWith("-.")) {
                    ss = ss.replace("-.", "- " + MbsConstans.RMB + "0.")
                } else if (ss.startsWith("-")) {
                    ss = ss.replace("-", "- " + MbsConstans.RMB + "")
                } else {
                    ss = "+ " + MbsConstans.RMB + "" + ss
                }
                val result = MbsConstans.RMB + " " + ss
                return ss
            } catch (e: Exception) {
                e.printStackTrace()
                return "0.00"
            }

        }

        fun getMoney(s: String): String {
            var s = s
            try {
                if (empty(s)) {
                    s = "0"
                }
                val d = java.lang.Double.valueOf(s)
                val dd = divide(d, 100.0)

                val myformat = DecimalFormat()
                myformat.applyPattern("##,###.00")
                var ss = myformat.format(dd)
                if (ss.startsWith(".")) {
                    ss = "0$ss"
                }
                return ss
            } catch (e: Exception) {
                e.printStackTrace()
                return "0.00"
            }

        }

        fun getNormalMoney(s: String): String {
            var s = s
            try {

                if (empty(s)) {
                    s = "0"
                }
                val d = java.lang.Double.valueOf(s)
                val myformat = DecimalFormat()
                myformat.applyPattern("##,###.00")
                var ss = myformat.format(d)
                if (ss.startsWith(".")) {
                    ss = "0$ss"
                }
                return ss
            } catch (e: Exception) {
                e.printStackTrace()
                return "0.00"
            }

        }

        fun getShuziMoney(s: String): String {
            var s = s
            try {
                if (empty(s)) {
                    s = "0"
                }
                val d = java.lang.Double.valueOf(s)
                val dd = divide(d, 100.0)
                return fromDouble(dd)
            } catch (e: Exception) {
                e.printStackTrace()
                return "0.00"
            }

        }

        //(    [     {    /    ^    -    $     ¦    }    ]    )    ?    *    +    .
        //转义方法为字符前面加上"\\"，这样在split、replaceAll时就不会报错了；
        fun getlilv(s: String): String {
            var s = s

            try {
                if (empty(s)) {
                    s = "0"
                }
                val d = java.lang.Double.valueOf(s)
                val str = doubleTrans1(d)

                var lastStr = ""
                if (str.contains(".")) {
                    val shuzu = str.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    lastStr = shuzu[1]
                }
                val result: String
                if (lastStr.length > 2) {
                    result = "$str%"
                } else {
                    result = fromDouble(d) + "%"
                }
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                return "0.00"
            }

        }


        fun getlilvT(s: String): String {
            var s = s
            try {
                if (empty(s)) {
                    s = "0"
                }
                val d = java.lang.Double.valueOf(s)
                //Double dd = divide(d,100);

                return fromDouble(d) + "%"
            } catch (e: Exception) {
                e.printStackTrace()
                return "0"
            }

        }

        /**
         * 去掉小数点后面无用的0
         *
         * @param num
         * @return
         */
        fun doubleTrans1(num: Double): String {
            return if (num % 1.0 == 0.0) {
                num.toLong().toString()
            } else num.toString()
        }


        fun getPhoneXing(s: String): String {
            return if (!empty(s)) {
                s.substring(0, 3) + "****" + s.substring(7, s.length)
            } else {
                ""
            }
        }

        /**
         * 银行卡显示
         * @param s
         * @return
         */
        fun getIDXing(s: String): String {
            return getShowBankIdCard(s)
            /*if (!empty(s) && s.length()>5){
			String maskNumber =  s.substring(0,6)+"******"+ s.substring(s.length()-4, s.length());
			return  maskNumber;
		}else {
			return  "******";
		}*/
        }

        /**
         * s身份证显示
         * @param s
         * @return
         */
        fun getIDCardXing(s: String): String {
            return if (!empty(s) && s.length > 7) {
                s.substring(0, 6) + "******" + s.substring(s.length - 6, s.length)
            } else {
                "******"
            }
        }

        fun getShowBankIdCard(string: String): String {
            var str = StringBuilder(string.replace(" ", ""))

            val i = str.length / 4
            val j = str.length % 4

            for (x in (if (j == 0) i - 1 else i) downTo 1) {
                str = str.insert(x * 4, " ")
            }

            return str.toString()
        }

        /**
         * 隐藏银行卡号中间的字符串（使用*号），显示前四后四
         */
        fun getHideCardNo(cardNo: String): String {
            try {
                if (empty(cardNo)) {
                    return ""
                }
                /*if(StringUtils.isBlank(cardNo)) {
			return cardNo;
		}*/

                val length = cardNo.length
                val beforeLength = 4
                val afterLength = 4
                //替换字符串，当前使用“*”
                val replaceSymbol = "*"
                val sb = StringBuffer()
                for (i in 0 until length) {
                    if (i < beforeLength || i >= length - afterLength) {
                        sb.append(cardNo[i])
                    } else {
                        sb.append(replaceSymbol)
                    }
                }

                return sb.toString()
            } catch (e: Exception) {
                return ""
            }


        }

        /**
         * 后四
         */
        fun getCardNoFour(cardNo: String): String {
            var cardNo = cardNo
            /*if(StringUtils.isBlank(cardNo)) {
			return cardNo;
		}*/
            try {
                cardNo = cardNo.substring(cardNo.length - 4, cardNo.length)

                return cardNo
            } catch (e: Exception) {
                return ""
            }

        }


        /**
         * 得到日志的详细存放路径
         * @param mContext
         * @return
         */
        fun getLogPath(mContext: Context): String {
            val mLogFilePath = StringBuffer()
            // 检测到如果系统中存在SD卡，则将Log文件写入SD卡路径
            // 否则，将Log文件写入程序内部存储路径：./data/data/com.hwttnew.xx.xx/files/
            mLogFilePath.append(getBaseLogPath(mContext))
            mLogFilePath.append(File.separator)
            mLogFilePath.append("LogsDay")
            mLogFilePath.append(File.separator)
            mLogFilePath.append(getStringFromDate(Date(), "yyyy_MM_dd"))
            mLogFilePath.append(".log")

            return mLogFilePath.toString()
        }

        /**
         * 根据手机配置 获取错误日志存放路径
         */
        fun getBaseLogPath(mContext: Context?): String {
            var mLogFilePath = MbsConstans.DATA_PATH
            if (existsSD()) {
                mLogFilePath = MbsConstans.BASE_PATH
            } else {
                if (mContext != null)
                    mLogFilePath = mContext.filesDir.absolutePath
                else {
                    mLogFilePath = MbsConstans.DATA_PATH
                }
            }
            return mLogFilePath
        }

        fun getBaseCutPicPath(mContext: Context?): String {
            var mPicPath = MbsConstans.DATA_PATH
            if (existsSD()) {
                mPicPath = MbsConstans.BASE_PATH
            } else {
                if (mContext != null)
                    mPicPath = mContext.filesDir.absolutePath
                else {
                    mPicPath = MbsConstans.DATA_PATH
                }
            }
            return mPicPath
        }

        /**
         * 判断是否存在Sdcard
         * @return
         */
        fun existsSD(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

        /**
         * @Description::将date类型转换为字符串
         * @param date 传入的date
         * @param style 要变化成的字符串类型  比如：yyyy-MM-dd HH:mm:ss
         * @return
         */
        fun getStringFromDate(date: Date?, style: String): String {
            try {
                val simpleDateFormat = SimpleDateFormat(style)
                return simpleDateFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }

        }

        /**
         * @Description:将字符串转换为date类型的方法
         * @param dateStr 要转换的字符串
         * @param style 要转换的类型
         * @return
         */
        fun getDateFromString(dateStr: String, style: String): Date? {

            val simpleDateFormat = SimpleDateFormat(style)
            var date: Date? = null
            try {
                date = simpleDateFormat.parse(dateStr)
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return date
        }

        fun getStringFromSting(dateStr: String, style: String): String {
            try {
                if (empty(dateStr)) {
                    return ""
                }
                val date = getDateFromString(dateStr, style)
                return getStringFromDate(date, "yyyy-MM-dd")
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }

        }

        //yyyyMMddHHmmss   yyyy-MM-dd HH:mm:ss
        fun getStringFromSting2(dateStr: String, style: String, returnStyle: String): String {
            try {
                if (empty(dateStr)) {
                    return ""
                }
                val date = getDateFromString(dateStr, style)
                return getStringFromDate(date, returnStyle)
            } catch (e: Exception) {
                return ""
            }

        }


        /**
         * 获取一个月前的日期
         * @param date 传入的日期
         * @return
         */
        fun getMonthAgo(date: Date, i: Int): String {
            val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.MONTH, i)//-1
            return simpleDateFormat.format(calendar.time)
        }

        /**
         * 获取i个周前的日期
         * @param date 传入的日期
         * @return
         */
        fun getWeekAgo(date: Date, i: Int): String {
            val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DAY_OF_MONTH, i)//-1
            return simpleDateFormat.format(calendar.time)
        }


        /**
         * 根据手机app下载
         */
        fun getAppDownPath(mContext: Context?): String {
            var appPath = MbsConstans.DATA_PATH
            if (existsSD()) {
                appPath = MbsConstans.BASE_PATH + "apk"
            } else {
                if (mContext != null)
                    appPath = mContext.filesDir.absolutePath
                else {
                    appPath = MbsConstans.DATA_PATH
                }
            }

            return appPath
        }

        /**
         * 截取全屏的方法
         * @param mContext
         */
        fun saveCurrentImage(mContext: Context) {
            val windowManager = (mContext as Activity).windowManager
            val display = windowManager.defaultDisplay
            val w = display.width
            val h = display.height
            var Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

            val decorview = mContext.window.decorView
            decorview.isDrawingCacheEnabled = true
            Bmp = decorview.drawingCache

            val SavePath = getBaseCutPicPath(mContext) + "/pic/"
            try {
                val path = File(SavePath)

                val filepath = SavePath + Date().time + ".png"
                val file = File(filepath)
                if (!path.exists()) {
                    path.mkdirs()
                }
                if (!file.exists()) {
                    file.createNewFile()
                }
                var fos: FileOutputStream? = null
                fos = FileOutputStream(file)
                if (fos != null) {
                    Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos)
                    fos.flush()
                    fos.close()
                    Toast.makeText(mContext, "截屏文件已保存至SDCard目录下", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**
         * 跳到打电话的界面，但是没有拨出
         * @param phoneNum
         */
        fun startTel(mContext: Context, phoneNum: String) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNum"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(intent)
        }

        //判断手机格式是否正确
        fun isMobileNO(mobiles: String): Boolean {
            val p = Pattern.compile("^[1][0-9][0-9]{9}$") // 验证手机号
            //		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[^4,\\D])|(17[0,8]))\\d{8}$");
            val m = p.matcher(mobiles)
            return m.matches()
        }

        //判断email格式是否正确
        fun isEmail(email: String): Boolean {
            val str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
            val p = Pattern.compile(str)
            val m = p.matcher(email)

            return m.matches()
        }

        /* 获得一个UUID
	 * @return String UUID
	 */
        //去掉“-”符号
        //		return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
        val uuid: String
            get() {
                val s = UUID.randomUUID().toString()
                return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
            }

        //判断应用是否在运行
        fun isRunning(context: Context): Boolean {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val list = am.getRunningTasks(100)
            var isAppRunning = false
            val MY_PKG_NAME = "cn.gagakeji.gagayi.activity"
            for (info in list) {
                if (info.topActivity.packageName == MY_PKG_NAME || info.baseActivity.packageName == MY_PKG_NAME) {
                    isAppRunning = true
                    break
                }
            }
            return isAppRunning
        }

        /**
         * 是否包含中文
         *
         * @param str
         * @return 找到了true
         */
        fun isContainsChinese(str: String): Boolean {

            val regEx = "[\u4e00-\u9fa5]"
            val pat = Pattern.compile(regEx)

            val matcher = pat.matcher(str)
            var flg = false
            if (matcher.find()) {
                flg = true
            }
            return flg
        }

        /**
         * 是否全是中文
         *
         * @param str
         * @return 找到了true
         */
        fun isAllChinese(str: String): Boolean {

            val regEx = "[\u4e00-\u9fa5]+"

            return if (!TextUtils.isEmpty(str)) {
                str.matches(regEx.toRegex())
            } else false

        }

        private fun checkCameraFacing(facing: Int): Boolean {
            if (sdkVersion < Build.VERSION_CODES.GINGERBREAD) {
                return false
            }
            val cameraCount = Camera.getNumberOfCameras()
            val info = Camera.CameraInfo()
            for (i in 0 until cameraCount) {
                Camera.getCameraInfo(i, info)
                if (facing == info.facing) {
                    return true
                }
            }
            return false
        }

        fun hasBackFacingCamera(): Boolean {
            val CAMERA_FACING_BACK = 0
            return checkCameraFacing(CAMERA_FACING_BACK)
        }

        fun hasFrontFacingCamera(): Boolean {
            val CAMERA_FACING_BACK = 1
            return checkCameraFacing(CAMERA_FACING_BACK)
        }

        val sdkVersion: Int
            get() = android.os.Build.VERSION.SDK_INT


        fun showSoftInput(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            //imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }

        fun hideSoftInput(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0) //强制隐藏键盘
        }

        fun isShowSoftInput(context: Context): Boolean {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //获取状态信息
            return imm.isActive//true 打开
        }


        fun saveMedioPic(path: String?) {

            if (path == null || path == "") {
                return
            } else {
                //String path  = Environment.getExternalStorageDirectory().getPath();
                val media = MediaMetadataRetriever()
                media.setDataSource(path)
                val bitmap = media.frameAtTime
                try {
                    val SavePath = Environment.getExternalStorageDirectory().path + "/viedoPic/"
                    val saveFile = File(SavePath)

                    val filepath = SavePath + Date().time + ".png"
                    val file = File(filepath)
                    if (!saveFile.exists()) {
                        saveFile.mkdirs()
                    }
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    var fos: FileOutputStream? = null
                    fos = FileOutputStream(file)
                    if (fos != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
                        fos.flush()
                        fos.close()
                        //Toast.makeText(mContext, "截屏文件已保存至SDCard目录下", 0).show();
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }


        /**
         * 将时间戳转为代表"距现在多久之前"的字符串
         * @param t   时间戳
         * @return
         */
        fun getStandardDate(t: Long): String {

            val sb = StringBuffer()
            val time = System.currentTimeMillis() - t * 1000
            val mill = Math.ceil((time / 1000).toDouble()).toLong()//秒前

            val minute = Math.ceil((time.toFloat() / 60f / 1000.0f).toDouble()).toLong()// 分钟前

            val hour = Math.ceil((time.toFloat() / 60f / 60f / 1000.0f).toDouble()).toLong()// 小时

            val day = Math.ceil((time.toFloat() / 24f / 60f / 60f / 1000.0f).toDouble()).toLong()// 天前
            if (day - 1 > 0) {
                if (day >= 10) {
                    return ""
                }
                sb.append(day.toString() + "天")
            } else if (hour - 1 > 0) {
                if (hour >= 24) {
                    sb.append("1天")
                } else {
                    sb.append(hour.toString() + "小时")
                }
            } else if (minute - 1 > 0) {
                if (minute == 60L) {
                    sb.append("1小时")
                } else {
                    sb.append(minute.toString() + "分钟")
                }
            } else if (mill - 1 > 0) {
                if (mill == 60L) {
                    sb.append("1分钟")
                } else {
                    sb.append(mill.toString() + "秒")
                }
            } else {
                sb.append("刚刚")
            }
            if (sb.toString() != "刚刚") {
                sb.append("前")
            }
            return sb.toString()
        }

        /**
         * 请求参数加密方法（先使用URLEncoder进行转码，后进行Base64加密）
         * @param params
         * @return
         */
        fun gagaJiami(params: String): String {
            var params = params
            try {
                params = URLEncoder.encode(params, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            LogUtil.i("show", params)

            val chars = params.toCharArray()

            val l = chars.size

            val key = 0x12
            var s = ""
            for (i in 0 until l) {
                s += (chars[i].toInt() xor key + i % 9).toChar()
            }
            return String(Base64.encode(s.toByteArray(), Base64.DEFAULT))
        }

        /**
         * 参数也可以解密
         * @param params
         * @return
         */
        fun gagaJiemi(params: String): String {
            val strBase64 = String(Base64.decode(params.toByteArray(), Base64.DEFAULT))

            val chars = strBase64.toCharArray()

            val l = chars.size

            val key = 0x12
            var s = ""
            for (i in 0 until l) {
                s += (chars[i].toInt() xor key + i % 9).toChar()
            }
            try {
                s = URLDecoder.decode(s, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return s
        }


        fun setMoneyEdit(mMoneyEdit: EditText, maxNum: Double) {
            mMoneyEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    var s = s
                    //输入的内容包含小数点
                    if (s.toString().contains(".")) {
                        //小数点后面数字超过两位
                        if (s.length - 1 - s.toString().indexOf(".") > 2) {
                            s = s.toString().subSequence(0,
                                    s.toString().indexOf(".") + 3)
                            mMoneyEdit.setText(s)
                            mMoneyEdit.setSelection(s.length)
                        }
                    }
                    //输入内容以0开头
                    if (s.toString().startsWith("0") && s.toString().trim { it <= ' ' }.length > 1) {
                        if (s.toString().substring(1, 2) != ".") {
                            mMoneyEdit.setText(s.subSequence(0, 1))
                            mMoneyEdit.setSelection(1)
                            return
                        }
                    }
                    //输入内容以小数点开头
                    if (s.toString().startsWith(".")) {
                        mMoneyEdit.setText("")
                    }

                    /*if (!empty(s) && maxNum != 0){
					double d = Double.valueOf(s.toString());
					if (d > maxNum){
						s = s.toString().subSequence(0, s.length()-1);
						mMoneyEdit.setText(s);
						mMoneyEdit.setSelection(s.length());
					}
				}*/

                }

                override fun afterTextChanged(s: Editable) {}
            })
        }


        /**
         * 获得该月第一天
         * @param year
         * @param month
         * @return
         */
        fun getFirstDayOfMonth(year: Int, month: Int): String {
            val cal = Calendar.getInstance()
            //设置年份
            cal.set(Calendar.YEAR, year)
            //设置月份
            cal.set(Calendar.MONTH, month - 1)
            //获取某月最小天数
            val firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH)
            //设置日历中月份的最小天数
            cal.set(Calendar.DAY_OF_MONTH, firstDay)
            //格式化日期
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            return sdf.format(cal.time)
        }

        /**
         * 获得该月第一天
         * @return
         */
        fun getFirstDayOfMonthByStr(str: String, type: String): String {
            val date = getDateFromString(str, type)
            val cal = Calendar.getInstance()
            cal.time = date
            //获取某月最小天数
            val firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH)
            //设置日历中月份的最小天数
            cal.set(Calendar.DAY_OF_MONTH, firstDay)
            //格式化日期
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            return sdf.format(cal.time)
        }

        /**
         * 获得该月第一天
         * @return
         */
        fun getFirstDayOfMonthByDate(date: Date): String {
            val cal = Calendar.getInstance()
            cal.time = date
            //获取某月最小天数
            val firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH)
            //设置日历中月份的最小天数
            cal.set(Calendar.DAY_OF_MONTH, firstDay)
            //格式化日期
            val sdf = SimpleDateFormat("yyyyMMdd")
            return sdf.format(cal.time)
        }

        /**
         * 获得该月最后一天
         * @param year
         * @param month
         * @return
         */
        fun getLastDayOfMonth(year: Int, month: Int): String {
            val cal = Calendar.getInstance()
            //设置年份
            cal.set(Calendar.YEAR, year)
            //设置月份
            cal.set(Calendar.MONTH, month - 1)
            //获取某月最大天数
            val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            //设置日历中月份的最大天数
            cal.set(Calendar.DAY_OF_MONTH, lastDay)
            //格式化日期
            val sdf = SimpleDateFormat("yyyyMMdd")
            return sdf.format(cal.time)
        }

        /**
         * 获得该月最后一天
         * @return
         */
        fun getLastDayOfMonthByStr(str: String, type: String): String {
            val date = getDateFromString(str, type)
            val cal = Calendar.getInstance()
            cal.time = date
            //获取某月最大天数
            val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            //设置日历中月份的最大天数
            cal.set(Calendar.DAY_OF_MONTH, lastDay)
            //格式化日期
            val sdf = SimpleDateFormat("yyyyMMdd")
            return sdf.format(cal.time)
        }

        /**
         * 获得该月最后一天
         * @return
         */
        fun getLastDayOfMonthByDate(date: Date): String {
            val cal = Calendar.getInstance()
            cal.time = date
            //获取某月最大天数
            val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            //设置日历中月份的最大天数
            cal.set(Calendar.DAY_OF_MONTH, lastDay)
            //格式化日期
            val sdf = SimpleDateFormat("yyyyMMdd")
            return sdf.format(cal.time)
        }

        /**
         * 比较两个日期的大小，日期格式为yyyy-MM-dd
         */
        fun isDateOneBigger(str1: String, str2: String, type: String): Boolean {
            var isBigger = false
            val sdf = SimpleDateFormat(type)
            var dt1: Date? = null
            var dt2: Date? = null
            try {
                dt1 = sdf.parse(str1)
                dt2 = sdf.parse(str2)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (dt1!!.time > dt2!!.time) {
                isBigger = true
            } else if (dt1.time < dt2.time) {
                isBigger = false
            }
            return isBigger
        }

        fun isGif(url: String): Boolean {
            return "gif" == getPathFormat(url)
        }

        fun getPathFormat(path: String): String {
            if (!TextUtils.isEmpty(path)) {
                val lastPeriodIndex = path.lastIndexOf('.')
                if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < path.length) {
                    val format = path.substring(lastPeriodIndex + 1)
                    if (!TextUtils.isEmpty(format)) {
                        return format.toLowerCase()
                    }
                }
            }
            return ""
        }


        /**
         * 1.现在目前市面的手机屏幕尺寸
         *
         * 480*800       dp表示：   hdpi    密实系数：1.5
         *
         * 720*1280     dp表示：   xhdpi    密实系数：2
         *
         * 1080*1920   dp表示：   xxhdpi   密实系数：3  
         *
         * 1440*2560   dp表示：   xxxhdpi   密实系数：3.5  
         *
         * 但是有的UI真的是懒惰，为适配Android 和 ios 而做一套ios的规格尺寸（1242 x 2208）图片去适配两种系统，这种是最烦人的。怎么办？
         *
         * 可以这样认为：1242 x 2208        密实系数：3.5
         * ---------------------
         */

        /**
         * * dp转px
         * @param dp
         * @return
         */
        @JvmStatic
        fun dip2px(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density + 0.5).toInt()
        }

        /** px转换dip  */
        fun px2dip(context: Context, px: Int): Int {
            val scale = context.resources.displayMetrics.density
            return (px / scale + 0.5f).toInt()
        }

        /** px转换sp  */
        fun px2sp(context: Context, pxValue: Int): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        /** sp转换px  */
        fun sp2px(context: Context, spValue: Int): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }


        /*
	 * 将当前时间换为时间戳
	 */
        @Throws(ParseException::class)
        fun dateToStamp(): String {
            val res: String
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val s = simpleDateFormat.format(Date())
            val date = simpleDateFormat.parse(s)
            val ts = date.time
            res = ts.toString()
            return res
        }


        /*
	 * 将时间换为时间戳
	 */
        @Throws(ParseException::class)
        fun dateToStamp(s: String): String {
            val res: String
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = simpleDateFormat.parse(s)
            val ts = date.time
            res = ts.toString()
            return res
        }


        /*
	 * 将时间戳转换为时间
	 */
        @SuppressLint("SimpleDateFormat")
        fun stampToDate(s: String): String {
            val res: String
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val lt = java.lang.Long.valueOf(s)
            val date = Date(lt)
            res = simpleDateFormat.format(date)
            return res
        }


        /**
         * 检测输入金额是否有效的（至少包含1-9的任意数字）
         * @param money
         * @return
         */
        fun CheckMoneyValid(money: String): Boolean {

            for (i in 1..9) {
                if (money.contains(i.toString() + "")) {
                    return true
                }
            }
            return false
        }

        /**
         * 分转万元并保留两位小数
         * @param money
         * @return
         */
        fun fenToWanYuan(money: String): Float {
            var money = money
            if (empty(money)) {
                money = "0"
            }
            val bigDecimal = BigDecimal(money)
            //转换为万元
            val decimal = bigDecimal.divide(BigDecimal(10000 * 100))
            //保留两位小数
            val format = DecimalFormat("0.00")
            //四舍五入
            format.roundingMode = RoundingMode.HALF_UP
            val formatNum = format.format(decimal)
            try {
                return java.lang.Float.valueOf(formatNum)
            } catch (e: Exception) {
                e.printStackTrace()
                return 0f
            }

        }


        /**
         * 将20190101  格式日期转换成 “01月01日”
         * @return
         */
        fun dateTypeTo(strDate: String): String {
            try {
                val date = getDateFromString(strDate, "yyyyMMdd")
                return getStringFromDate(date, "yyyy年MM月dd日").substring(5)
            } catch (e: Exception) {
                return "01月01日"
            }

        }


        /**
         * 计算两个日期相差多少天
         * @param startTime
         * @param endTime
         * @param format
         * @return
         */
        fun dateDiff(startTime: String, endTime: String, format: String): Long {
            // 按照传入的格式生成一个simpledateformate对象
            val sd = SimpleDateFormat(format)
            val nd = (1000 * 24 * 60 * 60).toLong()// 一天的毫秒数
            val nh = (1000 * 60 * 60).toLong()// 一小时的毫秒数
            val nm = (1000 * 60).toLong()// 一分钟的毫秒数
            val ns: Long = 1000// 一秒钟的毫秒数
            val diff: Long
            var day: Long = 0
            try {
                // 获得两个时间的毫秒时间差异
                diff = sd.parse(endTime).time - sd.parse(startTime).time
                day = diff / nd// 计算差多少天
                val hour = diff % nd / nh// 计算差多少小时
                val min = diff % nd % nh / nm// 计算差多少分钟
                val sec = diff % nd % nh % nm / ns// 计算差多少秒
                // 输出结果
                println("时间相差：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒。")
                return if (day >= 1) {
                    day
                } else {
                    if (day == 0L) {
                        1
                    } else {
                        0
                    }

                }

            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return 0

        }

        /**
         * 浮点数格式化
         *
         * @param isPercentage 是否为分数
         * @param needSign     是否需要正号
         * @param isMoney      是否使用金钱格式(每3位用","分隔)
         * @param digit        需要保留的位数
         * @param num          需要格式化的值(int、float、double、String、byte均可)
         */
        fun format(isPercentage: Boolean, needSign: Boolean, isMoney: Boolean, digit: Int, num: Any?): String {
            var digit = digit
            var num = num
            digit = if (digit < 0) 0 else digit
            if (num == null) num = 0

            var converted: Double//需求大多四舍五入  float保留位数会舍去后面的
            try {
                converted = java.lang.Double.parseDouble(num.toString())
            } catch (e: Throwable) {
                Log.e("FormatErr", "Input number is not kind number")
                converted = 0.0
            }

            val sb = StringBuilder("%")

            if (needSign && converted > 0) {
                sb.append("+")
            }

            if (isMoney) {
                sb.append(",")
            }

            sb.append(".").append(digit).append("f")

            if (isPercentage) {
                sb.append("%%")
            }

            return String.format(Locale.getDefault(), sb.toString(), converted)
        }

        fun numFormat(num: Any, digit: Int): String {
            return numFormat(false, digit, num)
        }

        fun numFormat(needSign: Boolean, digit: Int, num: Any): String {
            return format(false, needSign, false, digit, num)
        }

        /**
         * 带%格式化
         */
        fun percentageFormat(num: Any): String {
            return percentageFormat(true, num)
        }

        fun percentageFormat(needSign: Boolean, num: Any): String {
            return percentageFormat(needSign, 2, num)
        }

        fun percentageFormat(digit: Int, num: Any): String {
            return percentageFormat(false, digit, num)
        }

        fun percentageFormat(needSign: Boolean, digit: Int, num: Any): String {
            return format(true, needSign, false, digit, num)
        }

        /**
         * 以金钱格式表示的数字
         */
        fun moneyFormat(num: Any): String {
            return moneyFormat(true, num)
        }

        fun moneyFormat(isPercentage: Boolean, num: Any): String {
            return moneyFormat(isPercentage, 2, num)
        }

        fun moneyFormat(isPercentage: Boolean, digit: Int, num: Any): String {
            return format(isPercentage, false, true, digit, num)
        }

        fun stringAppend(`object`: Any?): String {
            return if (`object` != null)
                String.format(Locale.getDefault(), "%s", `object`)
            else
                "- -"
        }

        /**
         * 不四舍五入取n位小数
         */
        fun formatBySubString(obj: Any?, digit: Int): String {
            var digit = digit
            if (obj == null) return "0"
            var num = obj.toString()
            digit = if (digit < 0) 0 else digit
            var i = num.indexOf(".")
            if (i >= 0) {
                if (num.length - ++i > digit) {
                    num = num.substring(0, i + digit)
                }
            }
            return num
        }

        /**
         * 进位处理
         *
         * @param scale 保留几位
         */
        fun roundUp(scale: Int, num: Any): String {
            val decimal = BigDecimal(num.toString())
            return decimal.setScale(scale, BigDecimal.ROUND_UP).toString()
        }

        /**
         * 直接舍弃多余小数
         */
        fun roundDown(scale: Int, num: Any): String {
            val decimal = BigDecimal(num.toString())
            return decimal.setScale(scale, BigDecimal.ROUND_DOWN).toString()
        }

        //获取虚拟按键的高度
        fun getNavigationBarHeight(context: Context): Int {
            var result = 0
            if (isShowNavBar(context)) {
                val res = context.resources
                val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    result = res.getDimensionPixelSize(resourceId)
                }
            }
            return result
        }

        /**
         * 判断是否显示了导航栏
         * (说明这里的context 一定要是activity的context 否则类型转换失败)
         *
         * @param context
         * @return
         */
        fun isShowNavBar(context: Context?): Boolean {
            if (null == context) {
                return false
            }
            /**
             * 获取应用区域高度
             */
            val outRect1 = Rect()
            try {
                (context as Activity).window.decorView.getWindowVisibleDisplayFrame(outRect1)
            } catch (e: ClassCastException) {
                e.printStackTrace()
                return false
            }
            val activityHeight = outRect1.height()
            /**
             * 获取状态栏高度
             */
            val statuBarHeight: Int =getStatusBarHeight()
            /**
             * 屏幕物理高度 减去 状态栏高度
             */
            val remainHeight: Int =getRealHeight() - statuBarHeight
            /**
             * 剩余高度跟应用区域高度相等 说明导航栏没有显示 否则相反
             */
            return activityHeight != remainHeight
        }

        /**
         * 获取状态栏高度
         *
         * @return
         */
        fun getStatusBarHeight(): Int {
            var result = 0
            val resourceId: Int = BasicApplication.context!!.getResources().getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = BasicApplication.context!!.getResources().getDimensionPixelSize(resourceId)
            }
            return result
        }

        /**
         * 活动屏幕信息
         */
        private var wm: WindowManager? = null
        /**
         * 获取真实屏幕高度
         *
         * @return
         */
        fun getRealHeight(): Int {
            if (null == wm) {
                wm = BasicApplication.context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            }
            val point = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm!!.getDefaultDisplay().getRealSize(point)
            } else {
                wm!!.getDefaultDisplay().getSize(point)
            }
            return point.y
        }

        fun parseLayout(context: Context?, resLayout: Int): View? {
            return View.inflate(context, resLayout, null)
        }


    }




}
