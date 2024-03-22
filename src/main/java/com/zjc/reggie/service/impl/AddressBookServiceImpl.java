package com.zjc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjc.reggie.entity.AddressBook;
import com.zjc.reggie.service.AddressBookService;
import com.zjc.reggie.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author 86187
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2023-08-19 09:58:09
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




