package com.zjc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjc.reggie.common.BaseContext;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.ShoppingCart;
import com.zjc.reggie.exception.CommonException;
import com.zjc.reggie.service.ShoppingCartService;
import com.zjc.reggie.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86187
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2023-08-19 13:47:50
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService{

    /**
     * 添加菜品或套餐到购物车
     * 菜品虽然有很多口味组合，但是数据库只维护菜品总数并显示在前端，否则多条记录业务逻辑太复杂
     * @param shoppingCart
     * @return
     */
    @Override
    public Result<ShoppingCart> add(ShoppingCart shoppingCart) {
        //设置用户id，指定当前是哪个用户的购物车数据
        Long id = BaseContext.get().getId();
        shoppingCart.setUserId(id);
        //查询当前套餐或菜品是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,id);
        Long dishId = shoppingCart.getDishId();
        if(dishId!=null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            //queryWrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        // 目前相同菜品/套餐在数据库中的记录只能有一条
        ShoppingCart shoppingCart1 = this.getOne(queryWrapper);
        //数据库中已有数据
        if(shoppingCart1!=null){
            Integer number = shoppingCart1.getNumber();
            shoppingCart1.setNumber(number+1);
            this.updateById(shoppingCart1);
        }else {
            //数据库中没有，添加数据
            shoppingCart.setNumber(1);
            this.save(shoppingCart);
            shoppingCart1 = shoppingCart;

        }
        return Result.success(shoppingCart1);
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public Result<List<ShoppingCart>> show() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.get().getId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = this.list(queryWrapper);
        return Result.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @Override
    public Result<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.get().getId());
        this.remove(queryWrapper);
        return Result.success("购物车清空成功!");
    }

    /**
     * 删除菜品/套餐
     * @param shoppingCart
     * @return
     */
    @Override
    public Result<Object> sub(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        Long userId = BaseContext.get().getId();
        Long dishId = shoppingCart.getDishId();
        //判断删除的是菜品还是套餐
        if(dishId!=null){
            wrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        wrapper.eq(ShoppingCart::getUserId,userId);
        ShoppingCart shoppingCart1 = this.getOne(wrapper);
        if(shoppingCart1==null || shoppingCart1.getNumber()<1){
            throw new CommonException("删除菜品异常!");
        }
        else if(shoppingCart1.getNumber()>1){
            shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
            this.updateById(shoppingCart1);
        }else{
            this.removeById(shoppingCart1);
            return Result.success("删除成功");
        }
        return Result.success(shoppingCart1);
    }
}




