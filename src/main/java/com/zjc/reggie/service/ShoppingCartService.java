package com.zjc.reggie.service;

import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 86187
* @description 针对表【shopping_cart(购物车)】的数据库操作Service
* @createDate 2023-08-19 13:47:50
*/
public interface ShoppingCartService extends IService<ShoppingCart> {

    Result<ShoppingCart> add(ShoppingCart shoppingCart);

    Result<List<ShoppingCart>> show();

    Result<String> clean();

    Result<Object> sub(ShoppingCart shoppingCart);
}
