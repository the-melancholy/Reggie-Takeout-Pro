package com.zjc.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.Category;


public interface CategoryService extends IService<Category> {

    Result<Page> pageShow(Integer page, Integer pageSize);

    Result<String> remove(Long id);
}
