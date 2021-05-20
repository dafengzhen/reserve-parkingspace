package com.example.reserveparkingspace.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 车辆
 *
 * @author dafengzhen
 */
@Data
@Entity
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 车号牌照
     */
    private String licensePlate;

    /**
     * 停车位预约列表
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ParkingReservationEntity> parkingReservationList;

}
