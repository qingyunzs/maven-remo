package com.zrg.commons.utils;

import java.io.*;
import java.nio.file.Files;

public class Base64Util {
    /**
     * 将inputstream转为Base64
     *
     * @param is
     * @return
     * @throws Exception
     */
    public static String inputStreamToBase64(InputStream is) throws Exception {
        byte[] data = null;
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = is.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new Exception("输入流关闭异常");
                }
            }
        }
        return org.apache.commons.codec.binary.Base64.encodeBase64String(data);
    }

    /**
     * base64 转 InputStream
     *
     * @param base64string
     * @return
     */
    public static InputStream base64ToInputStream(String base64string) {
        ByteArrayInputStream stream = null;
        try {
            byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(base64string);
            stream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    /**
     * File转成编码成BASE64
     *
     * @param path
     * @return
     */
    public static String fileToBase64(String path) {
        String base64 = null;
        InputStream in = null;
        try {
            File file = new File(path);
            in = Files.newInputStream(file.toPath());
            byte[] bytes = new byte[(int) file.length()];
            in.read(bytes);
            base64 = java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    /**
     * base64编码转换为文件
     *
     * @param path
     * @param base64encrypt
     * @param filename
     */
    public static void base64ToFile(String path, String base64encrypt, String filename) {
        File file = null;
        // 创建文件目录
        String filePath = path;
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        java.io.FileOutputStream fos = null;
        try {
            byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(base64encrypt);
            file = new File(filePath + filename);
            fos = new java.io.FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
