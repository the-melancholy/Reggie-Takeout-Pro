package com.zjc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.dto.SetmealDto;
import com.zjc.reggie.entity.Category;
import com.zjc.reggie.entity.Setmeal;
import com.zjc.reggie.entity.SetmealDish;
import com.zjc.reggie.exception.CommonException;
import com.zjc.reggie.mapper.SetmealMapper;
import com.zjc.reggie.service.CategoryService;
import com.zjc.reggie.service.SetmealDishService;
import com.zjc.reggie.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐，同时保留套餐和菜品的关联关系,操作两张表
     * @param setmealDto
     * @return
     */
    @Override
    @Transactional
    public Result<String> saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，setmeal表,save执行后才会生成setmeal_id
        this.save(setmealDto);
        //保存套餐和菜品映射关系，setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
        return Result.success("新增套餐成功!");
    }

    /**
     * 套餐分页查询展示,需要将类别id转换为类别名称
     * 操作两张表，setmeal表 和 category表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Result<Page> pageShow(Integer page, Integer pageSize, String name) {
        //分页插件
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Setmeal> wrapper= new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        //按更新时间排序
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        page(setmealPage,wrapper);
        //属性拷贝
        BeanUtils.copyProperties(setmealPage,dtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return Result.success(dtoPage);
    }



    /**
     * 删除套餐，同时删除套餐和菜品关联的数据
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public Result<String> removeWithDish(List<Long> ids) {
        //查询套餐状态，确认是否可以删除
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        //状态1表示在售
        wrapper.eq(Setmeal::getStatus,1);
        long count = this.count(wrapper);
        //不能删除，抛出异常
        if(count>0){
            throw new CommonException("套餐正在售卖中，不能删除");
        }

        //删除套餐表中的数据
        this.removeByIds(ids);
        //删除套餐菜品关联表中的数据
        LambdaQueryWrapper<SetmealDish> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(wrapper1);
        return Result.success("套餐数据删除成功!");
    }

    /**
     * 更新套餐状态
     */
    @Override
    public Result<String> updateStatus(Integer status, List<Long> ids) {

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmeals = this.list(wrapper);
        //对要更新的套餐进行状态检测，主要是批量操作
        for (Setmeal setmeal : setmeals) {
            //如果某套餐已经启售, 仍进行启售操作, 就报错, 停售同理
            if(status.equals(setmeal.getStatus()))
                throw new CommonException("套餐状态更新错误，请重新尝试！");
        }
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        boolean success = this.update(setmeal, wrapper);
        if(!success){
            throw new CommonException("未知错误，状态更新失败!");
        }
        return Result.success("套餐状态更新成功!");

    }

    /**
     * 移动端展示套餐
     * @param setmeal
     * @return
     */
    @Override
    public Result<List<Setmeal>> setmealList(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        Long categoryId = setmeal.getCategoryId();
        queryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId);
        Integer status = setmeal.getStatus();
        queryWrapper.eq(status!=null,Setmeal::getStatus,1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = this.list(queryWrapper);
        return Result.success(setmealList);
    }

    /**
     * 根据id进行套餐信息回显
     *
     * @param id
     * @return
     */
    @Override
    public Result<SetmealDto> getSetMeal(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        setmealDto.setSetmealDishes(list);
        return Result.success(setmealDto);
    }

    /**
     * 更新套餐信息
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        //消除原套餐关联菜品 setmeal_dish表
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,id);
        setmealDishService.remove(wrapper);
        //新增套餐关联菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }
        setmealDishService.saveBatch(setmealDishes);

    }
}
