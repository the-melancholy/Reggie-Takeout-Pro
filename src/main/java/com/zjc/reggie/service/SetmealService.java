package com.zjc.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.dto.SetmealDto;
import com.zjc.reggie.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    Result<String> saveWithDish(SetmealDto setmealDto);

    Result<Page> pageShow(Integer page, Integer pageSize, String name);


    Result<String> removeWithDish(List<Long> ids);

    Result<String> updateStatus(Integer status, List<Long> ids);

    Result<List<Setmeal>> setmealList(Setmeal setmeal);

    Result<SetmealDto> getSetMeal(Long id);

    void updateWithDish(SetmealDto setmealDto);
}
