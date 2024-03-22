package com.zjc.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.dto.DishDTO;
import com.zjc.reggie.entity.Dish;
import com.zjc.reggie.service.DishFlavorService;
import com.zjc.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDTO dishDto){
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");

    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageShow(Integer page, Integer pageSize, String name){

        return dishService.pageShow(page,pageSize,name);
    }

    /**
     * 根据id查询菜品信息和对应口味的信息,进行回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDTO> get(@PathVariable Long id){
        DishDTO dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);

    }

    /**
     * 更新菜品信息和口味信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDTO dishDto){
        dishService.updateWithFlavor(dishDto);
        return Result.success("新增菜品成功");

    }

    /**
     * 菜品的通用删除方法
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> remove(@RequestParam List<Long> ids){
        return dishService.removeWithFlavour(ids);
    }

    /**
     * 更新套餐状态
     */
    @PostMapping("/status/{id}")
    public Result<String> updateStatus(@PathVariable("id") Integer status,@RequestParam List<Long> ids){
        return dishService.updateStatus(status,ids);

    }

    /**
     * 前端界面，根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public Result<List<Dish>> dishList(Dish dish){
//        return dishService.dishList(dish);
//
//    }

    @GetMapping("/list")
    public Result<List<DishDTO>> dishDtoList(Dish dish){
        return dishService.dishDtoList(dish);
    }
}
