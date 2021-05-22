package com.example.reserveparkingspace.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

/**
 * 车辆
 *
 * @author dafengzhen
 */
@Data
@Entity
@EqualsAndHashCode(exclude = "parkingReservationList")
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
    @OneToMany(mappedBy = "car", fetch = FetchType.EAGER)
    private List<ParkingReservationEntity> parkingReservationList;

    /**
     * 用户
     */
    @JsonIgnore
    @ManyToOne
    private UserEntity user;

}
