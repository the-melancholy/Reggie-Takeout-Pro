package com.zjc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.dto.DishDTO;
import com.zjc.reggie.entity.*;
import com.zjc.reggie.exception.CommonException;
import com.zjc.reggie.mapper.DishMapper;
import com.zjc.reggie.service.CategoryService;
import com.zjc.reggie.service.DishFlavorService;
import com.zjc.reggie.service.DishService;
import com.zjc.reggie.service.SetmealDishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品涉及口味，需要操作两张表,开启事务
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;




    /**
     * 菜品信息分页查询
     * 难点： 数据库中dish表中存储的是category_id，需要将其转为category_name，显示在前端
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Result<Page> pageShow(Integer page, Integer pageSize, String name) {
        //分页插件
        Page<Dish> dishPage= new Page<>(page, pageSize);
        Page<DishDTO> dishDtoPage= new Page<>(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Dish> wrapper= new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        //按更新时间排序
        wrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        page(dishPage,wrapper);
        //属性拷贝 点击方法可以查看形参含义
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDTO> dishDtoList = records.stream().map((item)->{
                DishDTO dishDto = new DishDTO();
                BeanUtils.copyProperties(item,dishDto);
                Long categoryId = item.getCategoryId(); //分类Id
                //根据分类Id查询分类名称
                Category category = categoryService.getById(categoryId);
                if(category!=null){
                    String categoryName  = category.getName();
                    dishDto.setCategoryName(categoryName);
                }

                return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return Result.success(dishDtoPage);
    }


    /**
     * 新增菜品，同时保留对应的口味数据
     * 操作两张表，dish_flavor表的dish_id = dish表的id
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDto) {
        this.save(dishDto);         //保存到数据库，由雪花算法生成id
        Long id = dishDto.getId(); //菜品id
        List<DishFlavor> flavorList = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavorList) {
            dishFlavor.setDishId(id); //为口味赋予对应的菜品id
        }
        //dish_flavor表
        dishFlavorService.saveBatch(flavorList);
    }




    /**
     * 根据id查询菜品信息和对应口味的信息,进行回显
     * @param id
     * @return
     */
    @Transactional
    @Override
    public DishDTO getByIdWithFlavor(Long id) {
        DishDTO dishDto = new DishDTO();
        //从dish表中查询菜品基本信息
        Dish dish = getById(id);
        BeanUtils.copyProperties(dish,dishDto);
        //从dish_flavor表中查询口味信息
        LambdaQueryWrapper<DishFlavor> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(wrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    //更新菜品信息和口味信息
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDto) {
        //更新dish表基本数据
        this.updateById(dishDto);
        //清除菜品原有口味数据
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        Long id = dishDto.getId();
        wrapper.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(wrapper);
        //添加新增菜品口味数据
        List<DishFlavor> flavorList = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavorList) {
            dishFlavor.setDishId(id);
        }
        //dish_flavor表
        dishFlavorService.saveBatch(flavorList);


    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @Override
    public Result<List<Dish>> dishList(Dish dish) {
        Long categoryId = dish.getCategoryId();
        //根据类别id查询
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        //状态为1，表明在售，0表示停售
        wrapper.eq(Dish::getStatus,1);
        //添加排序条件
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = this.list(wrapper);

        return Result.success(list);
    }

    @Override
    public Result<List<DishDTO>> dishDtoList(Dish dish) {
        Long categoryId = dish.getCategoryId();
        //根据类别id查询
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        //状态为1，表明在售，0表示停售
        wrapper.eq(Dish::getStatus,1);
        //添加排序条件
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = this.list(wrapper);
        List<DishDTO> dishDtoList = list.stream().map((item)->{
            DishDTO dishDto = new DishDTO();
            BeanUtils.copyProperties(item,dishDto);
            Long id = item.getCategoryId(); //分类Id
            //根据分类Id查询分类名称
            Category category = categoryService.getById(id);
            if(category!=null){
                String categoryName  = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(flavors);

            return dishDto;
        }).collect(Collectors.toList());

        return Result.success(dishDtoList);
    }


    @Override
    @Transactional
    public Result<String> removeWithFlavour(List<Long> ids) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Dish::getId,ids);
        wrapper.eq(Dish::getStatus,1);
        long count = this.count(wrapper);
        if(count>0){
            throw new CommonException("菜品正在售卖中，不能删除");
        }
        //删除菜品数据
        this.removeByIds(ids);
        //删除套餐中关联的菜品
        LambdaQueryWrapper<SetmealDish> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.in(SetmealDish::getDishId,ids);
        setmealDishService.remove(wrapper1);
        return Result.success("套餐数据删除成功!");



    }

    @Override
    public Result<String> updateStatus(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Dish::getId,ids);
        List<Dish> dishes = this.list(wrapper);
        //对要更新的菜品进行状态检测，主要是批量操作
        for (Dish dish : dishes) {
            //如果某菜品已经启售, 仍进行启售操作, 就报错, 停售同理
            if(status.equals(dish.getStatus()))
                throw new CommonException("菜品状态更新错误，请重新尝试！");
        }
        Dish dish = new Dish();
        dish.setStatus(status);
        boolean success = this.update(dish, wrapper);
        if(!success){
            throw new CommonException("未知错误，状态更新失败!");
        }
        return Result.success("菜品状态更新成功!");

    }

}
