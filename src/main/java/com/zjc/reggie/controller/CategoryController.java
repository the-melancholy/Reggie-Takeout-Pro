package com.zjc.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.Category;
import com.zjc.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> add(@RequestBody Category category){
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageShow(Integer page, Integer pageSize){

        return categoryService.pageShow(page,pageSize);
    }

    /**
     * 删除菜品分类，删除前要进行关联查询
     * @param id
     * @return
     */
    @DeleteMapping
    public Result<String> remove(Long id){

        return categoryService.remove(id);
    }

    @PutMapping
    public Result<String> updateInfo(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.success("信息更新成功");

    }

    /**
     * 用于新增菜品的菜品分类
     * 使用实体类来接受请求参数，当参数为空时，类中属性为空，但类不为空
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        Integer type = category.getType();
        wrapper.eq(type!=null,Category::getType,type);

        //添加排序条件
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(wrapper);

        return Result.success(list);
    }


}
