package cn.iwehdio.sell.utils;

import java.util.Random;

public class KeyUtil {
    //生成唯一主键：时间+6位随机数
    public static synchronized String genUniqueKey() {
        Random random = new Random();
        Integer num = random.nextInt(900000) + 100000;
        return System.currentTimeMillis() + String.valueOf(num);
    }
}
