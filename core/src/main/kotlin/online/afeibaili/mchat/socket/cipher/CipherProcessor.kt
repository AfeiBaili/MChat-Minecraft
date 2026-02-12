package online.afeibaili.mchat.socket.cipher

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


/**
 * 密码工具类
 *
 *@author AfeiBaili
 *@version 2025/11/3 14:37
 */

class CipherProcessor(token: String) {
    private val keySpec = SecretKeySpec(
        MessageDigest.getInstance("sha-256").digest(token.toByteArray()), "AES"
    )

    private val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")

    fun encrypt(content: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val final: ByteArray = cipher.doFinal(content.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(final)
    }

    fun decrypt(encrypted: String): String {
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val final: ByteArray = cipher.doFinal(Base64.getDecoder().decode(encrypted))
        return String(final, StandardCharsets.UTF_8)
    }
}