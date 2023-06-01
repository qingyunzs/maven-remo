package com.zrg.commons.wechat;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.UnhandledException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Base64;

/**
 * @author zrg
 * Date: 2021/10/29 18:21
 */
public class WeixinHelper {
    /**
     * AES-256-ECB(PKCS7Padding)解密
     *
     * @param secretInfo 加密信息
     * @param rawKey 商户的秘钥
     * @return
     * @throws UnhandledException
     */
    public static String aesDecrypt(String secretInfo, String rawKey) throws UnhandledException {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(DigestUtils.md5Hex(getContentBytes(rawKey, "utf-8")).toLowerCase().getBytes(), "AES");
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(secretInfo)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误" + charset);
        }
    }
}
