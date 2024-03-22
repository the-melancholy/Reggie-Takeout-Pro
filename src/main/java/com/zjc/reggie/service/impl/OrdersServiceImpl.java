package com.zjc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zjc.reggie.common.BaseContext;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.*;
import com.zjc.reggie.exception.CommonException;
import com.zjc.reggie.service.*;
import com.zjc.reggie.mapper.OrdersMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
* @author 86187
* @description 针对表【orders(订单表)】的数据库操作Service实现
* @createDate 2023-08-19 17:30:14
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper,Orders>
    implements OrdersService{

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     */
    @Transactional
    @Override
    public Result<String> submit(Orders orders) {
        //获取当前用户id
        Long userId = BaseContext.get().getId();
        //查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if(shoppingCarts==null || shoppingCarts.size()==0){
            throw new CommonException("下单异常，购物车为空！！！");
        }
        //查询用户数据
        User user = userService.getById(userId);
        //查询地址数据
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook==null){
            throw new CommonException("下单异常，地址信息为空！！！");
        }

        long orderId = IdWorker.getId(); //订单号
        orders.setNumber(String.valueOf(orderId));

        //进行购物车的金额数据计算 顺便把订单明细给计算出来
        AtomicInteger amount = new AtomicInteger(0);//使用原子类来保存计算的金额结果
        //这个item是集合中的每一个shoppingCarts对象,是在变化的
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item)->{
            //每对item进行一次遍历就产生一个新的orderDetail对象,然后对orderDetail进行设置,然后返回被收集,被封装成一个集合
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());//单份的金额
            //addAndGet进行累加 item.getAmount()单份的金额  multiply乘  item.getNumber()份数
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;

        }).collect(Collectors.toList());

        //向订单插入数据,一条数据  因为前端传过来的数据太少了,所以我们需要对相关的属性进行填值

        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        //Amount是指订单总的金额
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        if (user.getName() != null){
            orders.setUserName(user.getName());
        }
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);


        //先明细表插入数据,多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据  queryWrapper封装了userId我们直接使用这个条件来进行删除就行
        shoppingCartService.remove(queryWrapper);

        return Result.success("下单成功！");

    }

    @Override
    public Result<Page> pageShow(Integer page, Integer pageSize) {
        //分页插件
        Page pageInfo= new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId,BaseContext.get().getId());
        //按更新时间排序
        wrapper.orderByAsc(Orders::getCheckoutTime);
        page(pageInfo,wrapper);
        return Result.success(pageInfo);
    }

    @Override
    public Result<Page> pageShowBack(Integer page, Integer pageSize,String number) {
        //分页插件
        Page pageInfo= new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> wrapper= new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(number),Orders::getAddressBookId,number);
        //按更新时间排序
        wrapper.orderByAsc(Orders::getCheckoutTime);
        page(pageInfo,wrapper);
        return Result.success(pageInfo);
    }
}




