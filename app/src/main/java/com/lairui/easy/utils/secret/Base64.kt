package com.lairui.easy.utils.secret

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream


object Base64 {


    private val legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray()
    /**
     * data[]进行编码
     * @param data
     * @return
     */
    fun encode(data: ByteArray): String {
        val start = 0
        val len = data.size
        val buf = StringBuffer(data.size * 3 / 2)

        val end = len - 3
        var i = start
        var n = 0

        while (i <= end) {
            val d = (data[i].toInt() and 0x0ff shl 16
                    or (data[i + 1].toInt() and 0x0ff shl 8)
                    or (data[i + 2].toInt() and 0x0ff))

            buf.append(legalChars[d shr 18 and 63])
            buf.append(legalChars[d shr 12 and 63])
            buf.append(legalChars[d shr 6 and 63])
            buf.append(legalChars[d and 63])

            i += 3

            if (n++ >= 14) {
                n = 0
                buf.append(" ")
            }
        }

        if (i == start + len - 2) {
            val d = data[i].toInt() and 0x0ff shl 16 or (data[i + 1].toInt() and 255 shl 8)

            buf.append(legalChars[d shr 18 and 63])
            buf.append(legalChars[d shr 12 and 63])
            buf.append(legalChars[d shr 6 and 63])
            buf.append("=")
        } else if (i == start + len - 1) {
            val d = data[i].toInt() and 0x0ff shl 16

            buf.append(legalChars[d shr 18 and 63])
            buf.append(legalChars[d shr 12 and 63])
            buf.append("==")
        }

        return buf.toString()
    }

    private fun decode(c: Char): Int {
        return if (c >= 'A' && c <= 'Z')
            c.toInt() - 65
        else if (c >= 'a' && c <= 'z')
            c.toInt() - 97 + 26
        else if (c >= '0' && c <= '9')
            c.toInt() - 48 + 26 + 26
        else
            when (c) {
                '+' -> 62
                '/' -> 63
                '=' -> 0
                else -> throw RuntimeException("unexpected code: $c")
            }
    }

    /**
     * Decodes the given Base64 encoded String to a new byte array. The byte
     * array holding the decoded data is returned.
     */

    fun decode(s: String): ByteArray {

        var bos: ByteArrayOutputStream? = ByteArrayOutputStream()
        try {
            bos?.let { decode(s, it) }
        } catch (e: IOException) {
            throw RuntimeException()
        }

        val decodedBytes = bos!!.toByteArray()
        try {
            bos.close()
            bos = null
        } catch (ex: IOException) {
            System.err.println("Error while decoding BASE64: $ex")
        }

        return decodedBytes
    }

    @Throws(IOException::class)
    private fun decode(s: String, os: OutputStream) {
        var i = 0

        val len = s.length

        while (true) {
            while (i < len && s[i] <= ' ')
                i++

            if (i == len)
                break

            val tri = ((decode(s[i]) shl 18)
                    + (decode(s[i + 1]) shl 12)
                    + (decode(s[i + 2]) shl 6)
                    + decode(s[i + 3]))

            os.write(tri shr 16 and 255)
            if (s[i + 2] == '=')
                break
            os.write(tri shr 8 and 255)
            if (s[i + 3] == '=')
                break
            os.write(tri and 255)

            i += 4
        }
    }

}