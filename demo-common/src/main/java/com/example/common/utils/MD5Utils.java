package com.example.common.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {

    public static String md5(String str) {
        byte[] bytes = DigestUtils.md5(str);
        return Hex.encodeHexString(bytes, false);
    }

    public static void main(String[] args) {
        String md5 = MD5Utils.md5("123");
        System.out.println(md5);
    }
}
