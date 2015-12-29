package com.mmb.framework.common;

import java.security.SecureRandom;
import java.util.UUID;

public class IdGernericHelper {

    private static SecureRandom random = new SecureRandom();

    /**
     * 封装JDK自带的UUID, 通过Random数字生成,中间有-分割
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    /**
     * 使用SecureRandom随机生成Long. 
     */
    public static long randomLong() {
        return random.nextLong();
    }
    
    public void main(String[] args){
        
    }
}
