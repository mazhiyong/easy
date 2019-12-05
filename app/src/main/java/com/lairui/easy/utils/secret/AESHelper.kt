package com.lairui.easy.utils.secret

import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

///** AES对称加密解密类 **/
object AESHelper {

    /** 算法/模式/填充  */
    private val CipherMode = "AES/ECB/PKCS5Padding"


    val password = "(KlJ*HY^%VGBNJH)"

    ///** 创建密钥 **/
    private fun createKey(password: String): SecretKeySpec {
        var data: ByteArray? = null
        /*if (password == null) {
			password = "";
		}
		StringBuffer sb = new StringBuffer(32);
		sb.append(password);
		while (sb.length() < 32) {
			sb.append("0");
		}
		if (sb.length() > 32) {
			sb.setLength(32);
		}*/

        try {
            //data = sb.toString().getBytes("UTF-8");
            data = password.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return SecretKeySpec(data, "AES")
    }

    // /** 加密字节数据 **/
    fun encrypt(content: ByteArray?, password: String): ByteArray? {
        try {
            val key = createKey(password)
            val cipher = Cipher.getInstance(CipherMode)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return cipher.doFinal(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /** 加密(结果为16进制字符串)  */
    fun encrypt(content: String, password: String): String {
        var data: ByteArray? = null
        try {
            data = content.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        data = encrypt(data, password)
//		String result = byte2hex(data);
        //		String result = new String(Base64.encode(data,Base64.DEFAULT));
        return Base64.encode(data!!)
    }

    // /** 解密字节数组 **/
    fun decrypt(content: ByteArray?, password: String): ByteArray? {
        try {
            val key = createKey(password)
            val cipher = Cipher.getInstance(CipherMode)
            cipher.init(Cipher.DECRYPT_MODE, key)

            return cipher.doFinal(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    ///** 解密16进制的字符串为字符串 **/
    fun decrypt(content: String, password: String): String? {
        var data: ByteArray? = null
        try {
            //data = hex2byte(content);
            data = Base64.decode(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        data = decrypt(data, password)
        if (data == null)
            return null
        var result: String? = null
        result = String(data, StandardCharsets.UTF_8)
        return result
    }

    // /** 字节数组转成16进制字符串 **/
    fun byte2hex(b: ByteArray): String { // 一个字节的数，
        val sb = StringBuffer(b.size * 2)
        var tmp = ""
        for (n in b.indices) {
            // 整数转成十六进制表示
            tmp = Integer.toHexString((b[n] and 0XFF.toByte()).toInt())
            if (tmp.length == 1) {
                sb.append("0")
            }
            sb.append(tmp)
        }
        return sb.toString().toUpperCase() // 转成大写
    }

    // /** 将hex字符串转换成字节数组 **/
    private fun hex2byte(inputString: String?): ByteArray {
        var inputString = inputString
        if (inputString == null || inputString.length < 2) {
            return ByteArray(0)
        }
        inputString = inputString.toLowerCase()
        val l = inputString.length / 2
        val result = ByteArray(l)
        for (i in 0 until l) {
            val tmp = inputString.substring(2 * i, 2 * i + 2)
            result[i] = (Integer.parseInt(tmp, 16) and 0xFF).toByte()
        }
        return result
    }
}
