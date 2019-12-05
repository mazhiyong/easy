package com.lairui.easy.utils.tool

import android.text.TextUtils

/**
 * 身份证的工具类
 */
class IdCardUtil
/**
 * 构造方法。
 *
 * @param idCardNum
 */
(idCardNum: String) {

    private var idCardNum: String? = null

    private val errMsg = arrayOf("身份证完全正确！", "身份证为空！", "身份证长度不正确！", "身份证有非法字符！", "身份证中出生日期不合法！", "身份证校验位错误！")

    private var error = 0

    /**
     * 是否为空。
     *
     * @return true: null  false: not null;
     */
    val isEmpty: Boolean
        get() = if (this.idCardNum == null)
            true
        else
            if (this.idCardNum!!.trim { it <= ' ' }.length > 0) false else true

    /**
     * 身份证长度。
     *
     * @return
     */
    val length: Int
        get() = if (this.isEmpty) 0 else this.idCardNum!!.length

    /**
     * 是否是15位身份证。
     *
     * @return true: 15位  false：其他。
     */
    val is15: Boolean
        get() = this.length == 15

    /**
     * 是否是18位身份证。
     *
     * @return true: 18位  false：其他。
     */
    val is18: Boolean
        get() = this.length == 18

    /**
     * 得到身份证的省份代码。
     *
     * @return 省份代码。
     */
    val province: String
        get() = if (this.isCorrect == 0) this.idCardNum!!.substring(0, 2) else ""

    /**
     * 得到身份证的城市代码。
     *
     * @return 城市代码。
     */
    val city: String
        get() = if (this.isCorrect == 0) this.idCardNum!!.substring(2, 4) else ""

    /**
     * 得到身份证的区县代码。
     *
     * @return 区县代码。
     */
    val country: String
        get() = if (this.isCorrect == 0) this.idCardNum!!.substring(4, 6) else ""

    /**
     * 得到身份证的出生年份。
     *
     * @return 出生年份。
     */
    val year: String
        get() {
            if (this.isCorrect != 0)
                return ""

            return if (this.length == 15) {
                "19" + this.idCardNum!!.substring(6, 8)
            } else {
                this.idCardNum!!.substring(6, 10)
            }
        }

    /**
     * 得到身份证的出生月份。
     *
     * @return 出生月份。
     */
    val month: String
        get() {
            if (this.isCorrect != 0)
                return ""

            return if (this.length == 15) {
                this.idCardNum!!.substring(8, 10)
            } else {
                this.idCardNum!!.substring(10, 12)
            }
        }

    /**
     * 得到身份证的出生日子。
     *
     * @return 出生日期。
     */
    val day: String
        get() {
            if (this.isCorrect != 0)
                return ""

            return if (this.length == 15) {
                this.idCardNum!!.substring(10, 12)
            } else {
                this.idCardNum!!.substring(12, 14)
            }
        }

    /**
     * 得到身份证的出生日期。
     *
     * @return 出生日期。
     */
    val birthday: String
        get() {
            if (this.isCorrect != 0)
                return ""

            return if (this.length == 15) {
                "19" + this.idCardNum!!.substring(6, 12)
            } else {
                this.idCardNum!!.substring(6, 14)
            }
        }

    /**
     * 得到身份证的出生年月。
     *
     * @return 出生年月。
     */
    val birthMonth: String
        get() = birthday.substring(0, 6)

    /**
     * 得到身份证的顺序号。
     *
     * @return 顺序号。
     */
    val order: String
        get() {
            if (this.isCorrect != 0)
                return ""

            return if (this.length == 15) {
                this.idCardNum!!.substring(12, 15)
            } else {
                this.idCardNum!!.substring(14, 17)
            }
        }

    /**
     * 得到性别。
     *
     * @return 性别：1－男  2－女
     */
    val sex: String
        get() {
            if (this.isCorrect != 0)
                return ""

            val p = Integer.parseInt(order)
            return if (p % 2 == 1) {
                "男"
            } else {
                "女"
            }
        }

    /**
     * 得到性别值。
     *
     * @return 性别：1－男  2－女
     */
    val sexValue: String
        get() {
            if (this.isCorrect != 0)
                return ""

            val p = Integer.parseInt(order)
            return if (p % 2 == 1) {
                "1"
            } else {
                "2"
            }
        }

    /**
     * 得到校验位。
     *
     * @return 校验位。
     */
    val check: String
        get() {
            if (!this.isLenCorrect)
                return ""

            var lastStr = this.idCardNum!!.substring(this.idCardNum!!.length - 1)
            if ("x" == lastStr) {
                lastStr = "X"
            }
            return lastStr
        }

    /**
     * 校验身份证是否正确
     *
     * @return 0：正确
     */
    val isCorrect: Int
        get() {
            if (this.isEmpty) {
                this.error = IdCardUtil.IS_EMPTY
                return this.error
            }

            if (!this.isLenCorrect) {
                this.error = IdCardUtil.LEN_ERROR
                return this.error
            }

            if (!this.isCharCorrect) {
                this.error = IdCardUtil.CHAR_ERROR
                return this.error
            }

            if (!this.isDateCorrect) {
                this.error = IdCardUtil.DATE_ERROR
                return this.error
            }

            if (this.is18) {
                if (this.check != this.checkBit) {
                    this.error = IdCardUtil.CHECK_BIT_ERROR
                    return this.error
                }
            }

            return 0
        }


    private val isLenCorrect: Boolean
        get() = this.is15 || this.is18

    /**
     * 判断身份证中出生日期是否正确。
     *
     * @return
     */
    private/*非闰年天数*//*闰年天数*/ val isDateCorrect: Boolean
        get() {
            val monthDayN = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            val monthDayL = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

            val month: Int
            if (this.is15) {
                month = Integer.parseInt(this.idCardNum!!.substring(8, 10))
            } else {
                month = Integer.parseInt(this.idCardNum!!.substring(10, 12))
            }

            val day: Int
            if (this.is15) {
                day = Integer.parseInt(this.idCardNum!!.substring(10, 12))
            } else {
                day = Integer.parseInt(this.idCardNum!!.substring(12, 14))
            }

            if (month > 12 || month <= 0) {
                return false
            }

            if (this.isLeapyear) {
                if (day > monthDayL[month - 1] || day <= 0)
                    return false
            } else {
                if (day > monthDayN[month - 1] || day <= 0)
                    return false
            }

            return true
        }

    /**
     * 得到校验位。
     *
     * @return
     */
    private val checkBit: String
        get() {
            if (!this.isLenCorrect)
                return ""

            var temp: String? = null
            if (this.is18)
                temp = this.idCardNum
            else
                temp = this.idCardNum!!.substring(0, 6) + "19" + this.idCardNum!!.substring(6)


            val checkTable = arrayOf("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2")
            val wi = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1)
            var sum = 0

            for (i in 0..16) {
                val ch = temp!!.substring(i, i + 1)
                sum = sum + Integer.parseInt(ch) * wi[i]
            }

            val y = sum % 11

            return checkTable[y]
        }


    /**
     * 身份证号码中是否存在非法字符。
     *
     * @return true: 正确  false：存在非法字符。
     */
    private val isCharCorrect: Boolean
        get() {
            var iRet = true

            if (this.isLenCorrect) {
                val temp = this.idCardNum!!.toByteArray()

                if (this.is15) {
                    for (i in temp.indices) {
                        if (temp[i] < 48 || temp[i] > 57) {
                            iRet = false
                            break
                        }
                    }
                }

                if (this.is18) {
                    for (i in temp.indices) {
                        if (temp[i] < 48 || temp[i] > 57) {
                            if (i == 17 && temp[i].toInt() != 88) {
                                iRet = false
                                break
                            }
                        }
                    }
                }
            } else {
                iRet = false
            }
            return iRet
        }

    /**
     * 判断身份证的出生年份是否未闰年。
     *
     * @return true ：闰年  false 平年
     */
    private val isLeapyear: Boolean
        get() {
            val temp: String

            if (this.is15) {
                temp = "19" + this.idCardNum!!.substring(6, 8)
            } else {
                temp = this.idCardNum!!.substring(6, 10)
            }

            val year = Integer.parseInt(temp)

            return if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                true
            else
                false
        }

    init {
        // super();
        this.idCardNum = idCardNum.trim { it <= ' ' }
        if (!TextUtils.isEmpty(this.idCardNum)) {
            this.idCardNum = this.idCardNum!!.replace("x", "X")
        }
    }

    fun getIdCardNum(): String? {
        return idCardNum
    }

    fun setIdCardNum(idCardNum: String) {
        this.idCardNum = idCardNum
        if (!TextUtils.isEmpty(this.idCardNum)) {
            this.idCardNum = this.idCardNum!!.replace("x", "X")
        }
    }

    /**
     * 得到身份证详细错误信息。
     *
     * @return 错误信息。
     */
    fun getErrMsg(): String {
        return this.errMsg[this.error]
    }

    /**
     * 身份证长度。
     *
     * @return
     */
    fun getLength(str: String): Int {
        return if (this.isEmpty) 0 else str.length
    }

    /**
     * 得到15位身份证。
     *
     * @return 15位身份证。
     */
    fun to15(): String? {
        if (this.isCorrect != 0)
            return ""

        return if (this.is15)
            this.idCardNum
        else
            this.idCardNum!!.substring(0, 6) + this.idCardNum!!.substring(8, 17)
    }

    /**
     * 得到18位身份证。
     *
     * @return 18位身份证。
     */
    fun to18(): String? {
        if (this.isCorrect != 0)
            return ""

        return if (this.is18)
            this.idCardNum
        else
            this.idCardNum!!.substring(0, 6) + "19" + this.idCardNum!!.substring(6) + this.checkBit
    }

    companion object {

        private val IS_EMPTY = 1
        private val LEN_ERROR = 2
        private val CHAR_ERROR = 3
        private val DATE_ERROR = 4
        private val CHECK_BIT_ERROR = 5

        /**
         * 得到18位身份证。
         *
         * @return 18位身份证。
         */
        fun toNewIdCard(tempStr: String): String {
            return if (tempStr.length == 18)
                tempStr.substring(0, 6) + tempStr.substring(8, 17)
            else
                tempStr.substring(0, 6) + "19" + tempStr.substring(6) + getCheckBit(tempStr)
        }


        /**
         * 得到校验位。
         *
         * @return
         */
        private fun getCheckBit(str: String): String {

            var temp: String? = null
            if (str.length == 18)
                temp = str
            else
                temp = str.substring(0, 6) + "19" + str.substring(6)


            val checkTable = arrayOf("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2")
            val wi = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1)
            var sum = 0

            for (i in 0..16) {
                val ch = temp.substring(i, i + 1)
                sum = sum + Integer.parseInt(ch) * wi[i]
            }

            val y = sum % 11

            return checkTable[y]
        }
    }
}
