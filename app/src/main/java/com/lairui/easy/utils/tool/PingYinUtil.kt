package com.lairui.easy.utils.tool

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

/**
 * 将汉字转换为拼音，得到拼音首字母
 */
object PingYinUtil {


    //获得字符串的首字母 首字符 转汉语拼音
    fun getPingYin(value: String): String {
        // 首字符
        var firstChar = value[0]
        // 首字母分类
        var first: String? = null
        // 是否是非汉字
        val print = PinyinHelper.toHanyuPinyinStringArray(firstChar)

        if (print == null) {

            // 将小写字母改成大写
            if (firstChar.toInt() >= 97 && firstChar.toInt() <= 122) {
                firstChar -= 32
            }
            if (firstChar.toInt() >= 65 && firstChar.toInt() <= 90) {
                first = firstChar.toString()
            } else {
                // 认为首字符为数字或者特殊字符
                first = "#"
            }
        } else {
            // 如果是中文 分类大写字母
            first = (print[0][0].toInt() - 32).toChar().toString()
        }
        if (first == null) {
            first = "?"
        }
        return first
    }


    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param inputString
     * @return
     */
    fun getPingYin2(inputString: String): String {
        val format = HanyuPinyinOutputFormat()
        format.caseType = HanyuPinyinCaseType.LOWERCASE
        format.toneType = HanyuPinyinToneType.WITHOUT_TONE
        format.vCharType = HanyuPinyinVCharType.WITH_V

        val input = inputString.trim { it <= ' ' }.toCharArray()
        var output = ""

        try {
            for (i in input.indices) {
                if (Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+".toRegex())) {
                    val temp = PinyinHelper.toHanyuPinyinStringArray(input[i],
                            format)
                    output += temp[0]
                } else {
                    output += Character.toString(
                            input[i])

                }

            }
        } catch (e: BadHanyuPinyinOutputFormatCombination) {
            e.printStackTrace()
        }

        return output.toUpperCase()
    }
}
