package com.zjc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zjc.reggie.entity.OrderDetail;
import com.zjc.reggie.service.OrderDetailService;
import com.zjc.reggie.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author 86187
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2023-08-19 17:31:04
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




