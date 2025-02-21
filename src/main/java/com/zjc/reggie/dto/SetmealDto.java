package com.zjc.reggie.dto;

import com.zjc.reggie.entity.Setmeal;
import com.zjc.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
