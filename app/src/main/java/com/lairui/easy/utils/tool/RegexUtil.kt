package com.lairui.easy.utils.tool

import java.util.regex.Pattern

/**
 * 格式验证
 */
object RegexUtil {

    /**
     * 判断是否是字母+数字 混合
     * @param str
     * @return
     */
    fun isLetterDigit(str: String): Int {
        //是否含有数字
        var isDigit = false
        //是否含有字母
        var isLetter = false
        for (i in 0 until str.length) {
            if (Character.isDigit(str[i])) {
                isDigit = true
            }
            if (Character.isLetter(str[i])) {
                isLetter = true
            }
        }
        //正则判断
        val regex = "^[a-zA-Z0-9]{6,16}$"

        val b = str.matches(regex.toRegex())
        if (!isDigit) {
            return 1
        }

        if (!isLetter) {
            return 2
        }

        return if (!b) {
            3
        } else 0

    }

    fun isSiCard(tel: String): Boolean {//^[0-9]+(.[0-9]{1,3})?$  验证有1-3位小数的正实数   String regEx2 = "^[0-9_]+$";//纯数字
        val p = Pattern.compile("^[0-9]{14,19}$") //
        val m = p.matcher(tel)
        LogUtil.i("###############111   ", tel + " " + m.matches())

        return m.matches()
    }

    fun isGongCard(tel: String): Boolean {//^[0-9]+(.[0-9]{1,3})?$  验证有1-3位小数的正实数   String regEx2 = "^[0-9_]+$";//纯数字
        LogUtil.i("###############", tel)
        val p = Pattern.compile("^[0-9]{4,32}$") //
        val m = p.matcher(tel)
        LogUtil.i("###############222   ", tel + " " + m.matches())

        return m.matches()
    }

    fun isPhone(tel: String): Boolean {
        val p = Pattern.compile("^[1][0-9][0-9]{9}$") // 验证手机号
        //		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[^4,\\D])|(17[0,8]))\\d{8}$");
        val m = p.matcher(tel)
        return m.matches()
    }

    fun validatePhonePass(pass: String): Boolean {
        val passRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$"
        return !UtilTools.empty(pass) && pass.matches(passRegex.toRegex())
    }

    fun isAmount(tel: String): Boolean {
        val p = Pattern.compile("^(0(?:[.](?:[1-9]\\d?|0[1-9]))|[1-9]\\d*(?:[.]\\d{1,2}|$))$") // 验证金额
        val m = p.matcher(tel)
        return m.matches()
    }

    fun isBankCard(tel: String): Boolean {
        val p = Pattern.compile("^\\d{15,21}$") // 验证银行卡信息
        val m = p.matcher(tel)
        return m.matches()
    }

    fun isCode(tel: String): Boolean {
        val p = Pattern.compile("^\\d{6}$") // 验证验证码
        val m = p.matcher(tel)
        return m.matches()
    }


    //判断是否符合身份证号码的规范
    fun isIDCard(IDCard: String?): Boolean {
        if (IDCard != null) {
            val IDCardRegex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x|Y|y)$)"
            return IDCard.matches(IDCardRegex.toRegex())
        }
        return false
    }


    /**
     * 比较真实完整的判断身份证号码的工具
     *
     * @param idCard 用户输入的身份证号码
     * @return [true符合规范, false不符合规范]
     */
    fun isRealIDCard(idCard: String?): Boolean {
        if (idCard != null) {
            val idCardUtil = IdCardUtil(idCard)
            val correct = idCardUtil.isCorrect
            val msg = idCardUtil.getErrMsg()
            if (0 == correct) {// 符合规范
                return true
            }
        }
        return false
    }


    fun getPureDouble(str: String?): Float {
        if (str == null || str.length == 0) return 0f
        var result = 0f
        try {
            val compile = Pattern.compile("(\\d+\\.\\d+)|(\\d+)")//如何提取带负数d ???
            val matcher = compile.matcher(str)
            matcher.find()
            val string = matcher.group()//提取匹配到的结果
            result = java.lang.Float.parseFloat(string)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        return result
    }

    @JvmStatic
    fun main(args: Array<String>) {

        test()
    }

    fun test() {
        println(getPureDouble("12"))
        println(getPureDouble("wew3423.36"))
        println(getPureDouble("wewsf"))
        println(getPureDouble("000"))
        println(getPureDouble(null))
    }


}
