package com.zjc.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.Orders;

/**
* @author 86187
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2023-08-19 17:30:14
*/
public interface OrdersService extends IService<Orders> {

    Result<String> submit(Orders orders);

    Result<Page> pageShow(Integer page, Integer pageSize);

    Result<Page> pageShowBack(Integer page, Integer pageSize,String number);

    Result<Orders> updatePatch(Orders orders);
}
