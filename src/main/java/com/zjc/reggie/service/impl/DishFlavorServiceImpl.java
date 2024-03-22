package com.zjc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjc.reggie.entity.DishFlavor;
import com.zjc.reggie.service.DishFlavorService;
import com.zjc.reggie.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author 86187
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2023-08-09 17:15:28
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




