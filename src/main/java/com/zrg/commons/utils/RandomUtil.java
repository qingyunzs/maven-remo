package com.zrg.commons.utils;

import org.apache.commons.lang.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RandomUtil {
    public static final String generator() {
        //时间（精确到毫秒）
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String dayTime = sdf.format(new Date());

        String randomNumeric = RandomStringUtils.randomNumeric(4);
        return dayTime + randomNumeric;
    }
}
