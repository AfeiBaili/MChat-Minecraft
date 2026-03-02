package cn.afeibaili.mchat.socket.cipher;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 加密处理
 *
 * @author AfeiBaili
 * @version 2026/3/3 00:26
 */

public class CipherProcessor {
    String token;
    private final SecretKeySpec keySpec;
    private final Cipher cipher;

    public CipherProcessor(String token) {
        this.token = token;
        try {
            keySpec = new SecretKeySpec(MessageDigest.getInstance("sha-256").digest(token.getBytes(StandardCharsets.UTF_8)), "AES");
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String encrypt(String content) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] aFinal = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(aFinal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String encrypted) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] aFinal = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(aFinal, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
