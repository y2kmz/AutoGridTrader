package com.company.order;

import org.junit.Test;

import java.util.UUID;


public class OrderIdUtils {

    /**
     * The order ids, use uuid and remove the line dividing line. <br/>
     * id length = 32
     *
     * @return String eg: 39360db0a45e41309511f4fba658b01c
     */
    public static String createUUID() {
        return UUID.randomUUID().toString().toLowerCase().replace("-", "");
    }


    public static String createTsId() {
        return "order"+System.currentTimeMillis();
    }

    @Test
    public void test() {
        System.out.println(createUUID());
        System.out.println(createTsId());
    }
}