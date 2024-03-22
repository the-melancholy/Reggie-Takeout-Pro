package com.zjc.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjc.reggie.common.Result;
import com.zjc.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;


public interface EmployeeService extends IService<Employee> {
    Result<Employee> login(HttpServletRequest request, Employee employee);

    Result<String> addEmployee(HttpServletRequest request,Employee employee);

    Result<Page> pageShow(Integer page, Integer pageSize, String name);

    Result<String> updateInfo(HttpServletRequest request,Employee employee);

    Result<Employee> editInfo(HttpServletRequest request, Long id);
}
