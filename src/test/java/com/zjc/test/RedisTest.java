package com.zjc.test;

import com.zjc.reggie.ReggieApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = ReggieApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

   @Autowired
   private RedisTemplate redisTemplate;

   @Test
   public void demo1(){
      redisTemplate.opsForValue().set("hello","java");
   }



}
