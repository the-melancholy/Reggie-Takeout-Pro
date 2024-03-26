package com.zjc.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.Employee;
import com.zjc.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

       return employeeService.login(request, employee);

    }

    /**
     * 员工退出
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        //清除session中的员工信息
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");

    }

    /**
     * 新增员工，只允许管理员操作
     * @param employee
     * @return
     */
    @PostMapping //没有指定地址，则走类上的全局地址
    public Result<String> addEmployee(HttpServletRequest request,@RequestBody Employee employee){
        return employeeService.addEmployee(request,employee);

    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageShow(Integer page,Integer pageSize,String name){

        return employeeService.pageShow(page,pageSize,name);
    }

    /**
     * 员工信息更新，管理员或本人有权更新信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> updateInfo(HttpServletRequest request,@RequestBody Employee employee){
        return employeeService.updateInfo(request,employee);
    }

    /**
     *跳转到编辑页面，回显员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> editInfo(HttpServletRequest request,@PathVariable("id") Long id){
        return employeeService.editInfo(request,id);
    }

}
