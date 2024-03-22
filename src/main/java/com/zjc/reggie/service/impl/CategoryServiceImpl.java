package com.zjc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.Category;
import com.zjc.reggie.entity.Dish;
import com.zjc.reggie.entity.Setmeal;
import com.zjc.reggie.exception.CommonException;
import com.zjc.reggie.mapper.CategoryMapper;
import com.zjc.reggie.service.CategoryService;
import com.zjc.reggie.service.DishService;
import com.zjc.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    @Lazy
    private DishService dishService;

    @Autowired
    @Lazy
    private SetmealService setmealService;





    @Override
    public Result<Page> pageShow(Integer page, Integer pageSize) {
        //分页插件
        Page pageInfo= new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> wrapper= new LambdaQueryWrapper<>();
        //按更新时间排序
        wrapper.orderByAsc(Category::getSort);
        page(pageInfo,wrapper);
        return Result.success(pageInfo);
    }

    @Override
    public Result<String> remove(Long id) {
        //判断是否关联菜品
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(id != null, Dish::getCategoryId, id);
        long count1 = dishService.count(dishWrapper);
        if (count1 > 0) {
            //抛出异常
            throw new CommonException("当前分类下关联了菜品，不能删除");
        }
        //判断是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(id != null, Setmeal::getCategoryId, id);
        long count2 = setmealService.count(setmealWrapper);
        if (count2 > 0) {
            //抛出异常
            throw new CommonException("当前分类下关联了套餐，不能删除");
        }
        //正常删除
        removeById(id);
        return Result.success("分类信息删除成功");
    }


}
