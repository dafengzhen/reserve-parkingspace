package com.example.reserveparkingspace.controller;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 预约停车位请求
 *
 * @author dafengzhen
 */
@Data
public class ParkingRequest {

    /**
     * 车号牌照
     */
    @NotBlank
    private String licensePlate;

    /**
     * 手机
     */
    @NotBlank
    private String phone;

    /**
     * 开始时间
     */
    @NotNull
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull
    private LocalDateTime endTime;

}
