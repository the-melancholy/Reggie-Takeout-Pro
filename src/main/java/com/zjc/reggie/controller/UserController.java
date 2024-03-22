package com.zjc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.User;
import com.zjc.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送短信验证码
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){

        return userService.sendMsg(user,session);
    }

    /**
     * 移动端用户登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String,String> map, HttpSession session){

        return userService.login(map,session);
    }

    @PostMapping("/loginout")
    public Result<String> loginOut(HttpSession session){
        session.removeAttribute("user");
        return Result.success("退出成功");
    }


}
