package com.example.reserveparkingspace.other;

import lombok.Data;

/**
 * 登录响应
 *
 * @author dafengzhen
 */
@Data
public class LoginResponse {

    /**
     * 令牌
     */
    private String token;

    /**
     * 用户 Id
     */
    private Long userId;

}
