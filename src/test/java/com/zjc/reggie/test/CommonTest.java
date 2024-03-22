package com.zjc.reggie.test;

import org.junit.jupiter.api.Test;



public class CommonTest {

    @Test
    public void demo1(){
        String origin = "hello.jpg";
        System.out.println(origin.lastIndexOf("."));
        String suffix = origin.substring(origin.lastIndexOf("."));
        System.out.println(suffix);

    }
}
