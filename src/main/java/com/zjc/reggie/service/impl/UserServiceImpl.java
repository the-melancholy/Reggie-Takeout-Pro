package com.zjc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.dto.LoginDTO;
import com.zjc.reggie.entity.User;
import com.zjc.reggie.exception.CommonException;
import com.zjc.reggie.service.UserService;
import com.zjc.reggie.mapper.UserMapper;
import com.zjc.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
* @author 86187
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2023-08-18 11:52:00
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public Result<String> sendMsg(User user, HttpSession session) {
        String phone = user.getPhone();
        if(StringUtils.isEmpty(phone))
            return Result.error("短信发送失败");
       String code = ValidateCodeUtils.generateValidateCode(4).toString();
       log.info("code = {}",code);
       //以手机号作为键，既可以比对手机号，也可以比对验证码，还可以保证key的唯一性
       session.setAttribute(phone,code);
        return Result.success("短信验证码发送成功");
    }

    @Override
    public Result<User> login(Map<String, String> map, HttpSession session) {
        //获取提交的手机号
        String phone = map.get("phone");
        //获取提交的验证码
        String code = map.get("code");

        String codeInSession = session.getAttribute(phone).toString();
        //验证码对比
        if(codeInSession!=null && codeInSession.equals(code)){
            //判断是否为新用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);
            User user = this.getOne(wrapper);
            //新用户，自动注册
            if(user==null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                this.save(user);
            }
            LoginDTO clientDTO = new LoginDTO(user.getId(), null, user.getStatus());
            session.setAttribute("user",clientDTO);
            return Result.success(user);
        }

        return Result.error("登录失败");

    }
}




