package com.zjc.reggie.controller;

import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.ShoppingCart;
import com.zjc.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        return shoppingCartService.add(shoppingCart);
    }

    @PostMapping("/sub")
    public Result<Object> sub(@RequestBody ShoppingCart shoppingCart){
        return shoppingCartService.sub(shoppingCart);
    }



    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> show(){
        return shoppingCartService.show();

    }

    @DeleteMapping("/clean")
    public Result<String> clean(){
        return shoppingCartService.clean();
    }

}
