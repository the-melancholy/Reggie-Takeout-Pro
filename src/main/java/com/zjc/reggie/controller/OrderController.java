package com.zjc.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.Orders;
import com.zjc.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        return ordersService.submit(orders);
    }

    /**
     * 前端展示订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page> pageShow(Integer page, Integer pageSize){
        return ordersService.pageShow(page,pageSize);
    }

    /**
     * 后端展示订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageShowBack(Integer page, Integer pageSize,String number){
        return ordersService.pageShowBack(page,pageSize,number);
    }


}
