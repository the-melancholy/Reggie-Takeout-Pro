package com.zjc.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.dto.DishDTO;
import com.zjc.reggie.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {

     void saveWithFlavor(DishDTO dishDto);

     Result<Page> pageShow(Integer page, Integer pageSize, String name);

     DishDTO getByIdWithFlavor(Long id);

     void updateWithFlavor(DishDTO dishDto);



    Result<List<DishDTO>> dishDtoList(Dish dish);

    Result<String> removeWithFlavour(List<Long> ids);

    Result<String> updateStatus(Integer status, List<Long> ids);
}
