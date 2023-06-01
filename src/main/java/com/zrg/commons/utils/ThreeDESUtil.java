package com.zrg.commons.utils;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 3DES 加密算法
 *
 * ## 错误记录
 * 1. given final block not properly padded. Such issues can arise if a bad key is used during decryption.
 * 原因：在用DES加密的时候，最后一位长度不足64的，它会自动填补到64，那么在我们进行字节数组到字串的转化过程中，可以把它填补的不可见字符改变了，所以引发系统抛出异常。
 * 解决：使用Base64加解码
 * 2. java.security.InvalidKeyException: Wrong key size
 *
 * 原因：
 *
 *
 * @author zrg
 * Date: 2022/1/20 22:01
 */
public class ThreeDESUtil {
    /**
     * 定义 加密算法,可用DES,DESede,Blowfish
     */
    private static final String ALGORITHM = "DESede";

    /**
     * 使用3DES算法对目标数据执行加密操作
     *
     * @param key 192位的加密key
     * @param src 需要加密的数据
     * @return byte[] 执行加密后的数据
     * @author knight
     */
    public static String encryptFor3DES(String src, String key) throws Exception {
        byte[] value = null;
        try {
            byte[] keyByte = hexToByte(key);
            byte[] srcByte = src.getBytes(StandardCharsets.UTF_8);
            /*生成秘钥key*/
            SecretKeySpec deskey = new SecretKeySpec(keyByte, ALGORITHM);
            /*对目标数据执行加密操作*/
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            value = cipher.doFinal(srcByte);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("使用3DES加密异常：" + e.getMessage());
        }
        // 注意：此处使用Base64编码返回
        return Base64.getEncoder().encodeToString(value);
    }

    /**
     * 使用3DES算法对目标数据执行解密操作
     *
     * @param key 192位的加密key
     * @param src 需要执行解密的数据
     * @return byte[] 执行解密后的数据
     * @author knight
     */
    public static String decryptFor3DES(String src, String key) throws Exception {
        byte[] value = null;
        try {
            byte[] keyByte = hexToByte(key);
            // 注意：此处使用Base64解码
            byte[] srcByte = Base64.getDecoder().decode(src);
            /*生成秘钥key*/
            SecretKeySpec deskey = new SecretKeySpec(keyByte, ALGORITHM);
            /*对目标数据执行解密操作*/
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            value = cipher.doFinal(srcByte);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("使用3DES解密异常：" + e.getMessage());
        }
        return new String(value);
    }

    public static byte[] hexToByte(String value) {
        String f = DigestUtils.md5Hex(value);
        byte[] bkeys = f.getBytes();
        byte[] enk = new byte[24];
        for (int i = 0; i < 24; i++) {
            enk[i] = bkeys[i];
        }
        return enk;
    }
}

