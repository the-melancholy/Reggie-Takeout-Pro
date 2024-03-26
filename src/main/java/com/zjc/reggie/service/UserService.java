package com.zjc.reggie.service;

import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
* @author 86187
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2023-08-18 11:52:00
*/
public interface UserService extends IService<User> {

    Result<String> sendMsg(User user);

    Result<User> login(Map<String, String> map,HttpSession session);
}
