package com.zjc.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.dto.DishDTO;
import com.zjc.reggie.dto.SetmealDto;
import com.zjc.reggie.entity.Setmeal;
import com.zjc.reggie.service.SetmealDishService;
import com.zjc.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保留套餐和菜品的关联关系
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        return setmealService.saveWithDish(setmealDto);
    }

    /**
     * 套餐分页查询展示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageShow(Integer page, Integer pageSize, String name){

        return setmealService.pageShow(page,pageSize,name);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> removeWithDish(@RequestParam List<Long> ids){
        return setmealService.removeWithDish(ids);

    }

    /**
     * 更新套餐状态
     */
    @PostMapping("/status/{id}")
    public Result<String> updateStatus(@PathVariable("id") Integer status,@RequestParam List<Long> ids){
        return setmealService.updateStatus(status,ids);

    }

    /**
     * 移动端展示
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public Result<List<Setmeal>> setmealList(Setmeal setmeal){
        return setmealService.setmealList(setmeal);

    }

    /**
     * 根据id进行信息回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> getSetMeal(@PathVariable("id") Long id){
        return setmealService.getSetMeal(id);

    }

    /**
     * 更新菜品信息和口味信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return Result.success("套餐更新成功");

    }




}
