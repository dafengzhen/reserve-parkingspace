package com.example.reserveparkingspace.controller;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
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
    @Future
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Future
    private LocalDateTime endTime;

}
