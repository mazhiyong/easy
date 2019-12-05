package com.lairui.easy.utils.secret

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

import javax.crypto.Cipher

/**
 * Created by LG on 2018/9/21.
 */

object RSAUtils {
    val RSA = "RSA"// 非对称加密密钥算法
    val ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding"//加密填充方式


    val publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDWT4AnorEBOW0RWQnyL8RIs6cC" +
            "WYncvo5yGVxWKZx7GEHg9C2UrxcAOZUm4qDUbYcmsSI8Y1NazqF3FjjYCxDmSwcD" +
            "6ijXuJavVVaSwM8mF4AoGUZ32+BrozH+4iIHa41T5a3LVCRcRMoin88zIrpPl00R" +
            "z8HnmRY/5y608MEHBwIDAQAB"

    /**
     *
     * @param content 加密内容
     * @param key 加密公钥
     * @return
     */
    fun encryptContent(content: String, key: String): String {
        try {
            val publicKey1 = loadPublicKey(key)

            val encryptBytes = RSAUtils.encryptByPublicKeyForSpilt(content.toByteArray(), publicKey1.encoded)
            //                    encryptBytes = RSAUtils.encryptByPublicKeyForSpilt(text.getBytes(), imgs);
            return Base64.encode(encryptBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength 密钥长度，范围：512～2048
     * 一般1024
     * @return
     */
    fun generateRSAKeyPair(keyLength: Int): KeyPair? {
        try {
            val kpg = KeyPairGenerator.getInstance(RSA)
            kpg.initialize(keyLength)
            return kpg.genKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 用公钥对字符串进行加密
     *
     * @param data 原文
     */
    @Throws(Exception::class)
    fun encryptByPublicKey(data: ByteArray, publicKey: ByteArray): ByteArray {
        // 得到公钥
        val keySpec = X509EncodedKeySpec(publicKey)
        val kf = KeyFactory.getInstance(RSA)
        val keyPublic = kf.generatePublic(keySpec)
        // 加密数据
        val cp = Cipher.getInstance(ECB_PKCS1_PADDING)
        cp.init(Cipher.ENCRYPT_MODE, keyPublic)
        return cp.doFinal(data)
    }

    /**
     * 使用私钥进行解密
     */
    @Throws(Exception::class)
    fun decryptByPrivateKey(encrypted: ByteArray, privateKey: ByteArray): ByteArray {
        // 得到私钥
        val keySpec = PKCS8EncodedKeySpec(privateKey)
        val kf = KeyFactory.getInstance(RSA)
        val keyPrivate = kf.generatePrivate(keySpec)

        // 解密数据
        val cp = Cipher.getInstance(ECB_PKCS1_PADDING)
        cp.init(Cipher.DECRYPT_MODE, keyPrivate)
        return cp.doFinal(encrypted)
    }


    /**
     * 私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 密钥
     * @return byte[] 加密数据
     */
    @Throws(Exception::class)
    fun encryptByPrivateKey(data: ByteArray, privateKey: ByteArray): ByteArray {
        // 得到私钥
        val keySpec = PKCS8EncodedKeySpec(privateKey)
        val kf = KeyFactory.getInstance(RSA)
        val keyPrivate = kf.generatePrivate(keySpec)
        // 数据加密
        val cipher = Cipher.getInstance(ECB_PKCS1_PADDING)
        cipher.init(Cipher.ENCRYPT_MODE, keyPrivate)
        return cipher.doFinal(data)
    }

    /**
     * 公钥解密
     *
     * @param data      待解密数据
     * @param publicKey 密钥
     * @return byte[] 解密数据
     */
    @Throws(Exception::class)
    fun decryptByPublicKey(data: ByteArray, publicKey: ByteArray): ByteArray {
        // 得到公钥
        val keySpec = X509EncodedKeySpec(publicKey)
        val kf = KeyFactory.getInstance(RSA)
        val keyPublic = kf.generatePublic(keySpec)
        // 数据解密
        val cipher = Cipher.getInstance(ECB_PKCS1_PADDING)
        cipher.init(Cipher.DECRYPT_MODE, keyPublic)
        return cipher.doFinal(data)
    }


    /**
     * 用公钥对字符串进行分段加密
     *
     */
    @Throws(Exception::class)
    fun encryptByPublicKeyForSpilt(data: ByteArray, publicKey: ByteArray): ByteArray {

        // 得到公钥
        val keySpec = X509EncodedKeySpec(publicKey)
        val kf = KeyFactory.getInstance(RSA)
        val keyPublic = kf.generatePublic(keySpec)
        // 加密数据
        val cp = Cipher.getInstance(ECB_PKCS1_PADDING)
        cp.init(Cipher.ENCRYPT_MODE, keyPublic)


        val inputLen = data.size
        var offLen = 0//偏移量
        var i = 0
        val bops = ByteArrayOutputStream()
        while (inputLen - offLen > 0) {
            val cache: ByteArray
            if (inputLen - offLen > 117) {
                cache = cp.doFinal(data, offLen, 117)
            } else {
                cache = cp.doFinal(data, offLen, inputLen - offLen)
            }
            bops.write(cache)
            i++
            offLen = 117 * i
        }
        bops.close()

        return bops.toByteArray()
    }


    /**
     * 使用私钥分段解密
     *
     */
    @Throws(Exception::class)
    fun decryptByPrivateKeyForSpilt(encrypted: ByteArray, privateKey: ByteArray): ByteArray {
        // 得到私钥
        val keySpec = PKCS8EncodedKeySpec(privateKey)
        val kf = KeyFactory.getInstance(RSA)
        val keyPrivate = kf.generatePrivate(keySpec)

        // 解密数据
        val cp = Cipher.getInstance(ECB_PKCS1_PADDING)
        cp.init(Cipher.DECRYPT_MODE, keyPrivate)

        val inputLen = encrypted.size
        var offLen = 0
        var i = 0
        val byteArrayOutputStream = ByteArrayOutputStream()
        while (inputLen - offLen > 0) {
            val cache: ByteArray
            if (inputLen - offLen > 128) {
                cache = cp.doFinal(encrypted, offLen, 128)
            } else {
                cache = cp.doFinal(encrypted, offLen, inputLen - offLen)
            }
            byteArrayOutputStream.write(cache)
            i++
            offLen = 128 * i

        }
        byteArrayOutputStream.close()

        return byteArrayOutputStream.toByteArray()
    }


    /**
     * 私钥 分段加密
     *
     * @param data       要加密的原始数据
     * @param privateKey 秘钥
     */
    @Throws(Exception::class)
    fun encryptByPrivateKeyForSpilt(data: ByteArray, privateKey: ByteArray): ByteArray {

        // 得到私钥
        val keySpec = PKCS8EncodedKeySpec(privateKey)
        val kf = KeyFactory.getInstance(RSA)
        val keyPrivate = kf.generatePrivate(keySpec)
        // 数据加密
        val cipher = Cipher.getInstance(ECB_PKCS1_PADDING)
        cipher.init(Cipher.ENCRYPT_MODE, keyPrivate)

        val inputLen = data.size
        var offLen = 0//偏移量
        var i = 0
        val bops = ByteArrayOutputStream()
        while (inputLen - offLen > 0) {
            val cache: ByteArray
            if (inputLen - offLen > 117) {
                cache = cipher.doFinal(data, offLen, 117)
            } else {
                cache = cipher.doFinal(data, offLen, inputLen - offLen)
            }
            bops.write(cache)
            i++
            offLen = 117 * i
        }
        bops.close()
        return bops.toByteArray()
    }

    /**
     * 公钥分段解密
     *
     * @param encrypted 待解密数据
     * @param publicKey 密钥
     */
    @Throws(Exception::class)
    fun decryptByPublicKeyForSpilt(encrypted: ByteArray, publicKey: ByteArray): ByteArray {

        // 得到公钥
        val keySpec = X509EncodedKeySpec(publicKey)
        val kf = KeyFactory.getInstance(RSA)
        val keyPublic = kf.generatePublic(keySpec)
        // 数据解密
        val cipher = Cipher.getInstance(ECB_PKCS1_PADDING)
        cipher.init(Cipher.DECRYPT_MODE, keyPublic)

        val inputLen = encrypted.size
        var offLen = 0
        var i = 0
        val byteArrayOutputStream = ByteArrayOutputStream()
        while (inputLen - offLen > 0) {
            val cache: ByteArray
            if (inputLen - offLen > 128) {
                cache = cipher.doFinal(encrypted, offLen, 128)
            } else {
                cache = cipher.doFinal(encrypted, offLen, inputLen - offLen)
            }
            byteArrayOutputStream.write(cache)
            i++
            offLen = 128 * i

        }
        byteArrayOutputStream.close()
        return byteArrayOutputStream.toByteArray()
    }


    /**
     * 读取密钥信息
     *
     * @param in
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readKey(`in`: InputStream): String {
        val br = BufferedReader(InputStreamReader(`in`))
        var readLine: String? = null
        val sb = StringBuilder()



        readLine = br.readLine()
        while (readLine != null) {
            if (readLine!![0] == '-') {
                continue
            } else {
                sb.append(readLine)
                sb.append('\r')
            }
        }

        return sb.toString()
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     * 公钥数据字符串
     * @throws Exception
     * 加载公钥时产生的异常
     */
    @Throws(Exception::class)
    fun loadPublicKey(publicKeyStr: String): PublicKey {
        try {
            val buffer = Base64.decode(publicKeyStr)
            val keyFactory = KeyFactory.getInstance(RSA)
            val keySpec = X509EncodedKeySpec(buffer)
            return keyFactory.generatePublic(keySpec) as RSAPublicKey
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("无此算法")
        } catch (e: InvalidKeySpecException) {
            throw Exception("公钥非法")
        } catch (e: NullPointerException) {
            throw Exception("公钥数据为空")
        }

    }

    /**
     * 从字符串中加载私钥<br></br>
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun loadPrivateKey(privateKeyStr: String): PrivateKey {
        try {
            val buffer = Base64.decode(privateKeyStr)
            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            val keySpec = PKCS8EncodedKeySpec(buffer)
            val keyFactory = KeyFactory.getInstance(RSA)
            return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("无此算法")
        } catch (e: InvalidKeySpecException) {
            throw Exception("私钥非法")
        } catch (e: NullPointerException) {
            throw Exception("私钥数据为空")
        }

    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param in
     * 公钥输入流
     * @throws Exception
     * 加载公钥时产生的异常
     */
    @Throws(Exception::class)
    fun loadPublicKey(`in`: InputStream): PublicKey {
        try {
            return loadPublicKey(readKey(`in`))
        } catch (e: IOException) {
            throw Exception("公钥数据流读取错误")
        } catch (e: NullPointerException) {
            throw Exception("公钥输入流为空")
        }

    }

    /**
     * 从文件中加载私钥
     *
     * 私钥文件名
     * @return 是否成功
     * @throws Exception
     */
    @Throws(Exception::class)
    fun loadPrivateKey(`in`: InputStream): PrivateKey {
        try {
            return loadPrivateKey(readKey(`in`))
        } catch (e: IOException) {
            throw Exception("私钥数据读取错误")
        } catch (e: NullPointerException) {
            throw Exception("私钥输入流为空")
        }

    }
}
