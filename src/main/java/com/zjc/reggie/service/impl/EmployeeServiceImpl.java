package com.zjc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.dto.LoginDTO;
import com.zjc.reggie.entity.Employee;
import com.zjc.reggie.exception.CommonException;
import com.zjc.reggie.mapper.EmployeeMapper;
import com.zjc.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;


@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Override
    public Result<Employee> login(HttpServletRequest request, Employee employee) {
        String password = employee.getPassword();
        String username = employee.getUsername();
        //页面提交的密码进行MD5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //数据库中查询用户
        LambdaQueryWrapper<Employee> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, username);
        Employee emp = getOne(queryWrapper);
        if(emp==null){
            return Result.error("登录失败，用户不存在！！！");
        }
        //用户存在，密码比对
        if(!emp.getPassword().equals(password)){
            return Result.error("密码错误，请重新输入");
        }
        //查看员工状态是否被禁用
        if(emp.getStatus()==0){
            return Result.error("员工已被禁止登录！！！");
        }

        LoginDTO employeeDTO = new LoginDTO(emp.getId(), username, emp.getStatus());

        //登录成功
        request.getSession().setAttribute("employee",employeeDTO);
        return Result.success(emp);

    }

    @Override
    public Result<String> addEmployee(HttpServletRequest request,Employee employee) {
        //获取当前用户
        LoginDTO empDTO = (LoginDTO) request.getSession().getAttribute("employee");
        //查询username
        String username = empDTO.getUsername();
        //判断是否是管理员
        if(!"admin".equals(username)){
            throw new CommonException("权限不足，只有管理员才能执行此操作！！！");
        }
        //设置初始密码，用md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //其他属性自动填充
        save(employee);
        return Result.success("新增用户成功！");
    }

    @Override
    public Result<Page> pageShow(Integer page, Integer pageSize, String name) {
        //分页插件
        Page pageInfo= new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> wrapper= new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //按更新时间排序
        wrapper.orderByDesc(Employee::getUpdateTime);
        page(pageInfo,wrapper);
        return Result.success(pageInfo);
    }

    @Override
    public Result<String> updateInfo(HttpServletRequest request, Employee employee) {

        //获取当前用户
        LoginDTO empDTO = (LoginDTO) request.getSession().getAttribute("employee");
        Long id = empDTO.getId();
        if(!("admin".equals(empDTO.getUsername())|| id.equals(employee.getId())))
            throw new CommonException("无权修改他人信息");

        employee.setUpdateUser(id);
        //employee.setUpdateTime(LocalDateTime.now());
        updateById(employee);
        return Result.success("更新成功！");

    }

    //编辑信息
    @Override
    public Result<Employee> editInfo(HttpServletRequest request, Long id) {

        Employee employee = getById(id);
        if(employee == null){
            return Result.error("没有查询到对应员工信息");
        }
        return Result.success(employee);
    }
}
