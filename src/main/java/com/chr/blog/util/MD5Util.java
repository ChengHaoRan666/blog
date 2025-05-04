package com.chr.blog.util;

import java.security.MessageDigest;

/**
 * MD5工具类
 */
public class MD5Util {

    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 将MD5字符数组转为十六进制字符串
     *
     * @param b MD5数组
     * @return 十六进制字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte value : b) resultSb.append(byteToHexString(value));
        return resultSb.toString();
    }

    /**
     * 将一个字节拆成两个十六进制字符
     *
     * @param b 字节
     * @return 十六进制字符
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 将字符串加密
     *
     * @param origin      原字符串
     * @param charsetName 字符集
     * @return 加密后的字符串
     */
    public static String MD5Encode(String origin, String charsetName) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetName == null || charsetName.isEmpty())
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetName)));
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return resultString;
    }
}
