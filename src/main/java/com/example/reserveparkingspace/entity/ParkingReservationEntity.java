package com.example.reserveparkingspace.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 停车位预约
 *
 * @author dafengzhen
 */
@Data
@Entity
public class ParkingReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 车辆
     */
    @OneToOne
    private CarEntity car;

}
