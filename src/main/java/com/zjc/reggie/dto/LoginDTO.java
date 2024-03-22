package com.zjc.reggie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户前端client以及后端employee数据传输
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    private Long id;
    private String username;
    private Integer status;
}
