package com.example.reserveparkingspace.service;

import com.example.reserveparkingspace.controller.ParkingRequest;

/**
 * parking service
 *
 * @author dafengzhen
 */
public interface ParkingService {

    /**
     * 添加预约停车
     *
     * @param parkingSpaceNumber parkingSpaceNumber
     * @param parkingRequest     parkingRequest
     */
    void addParkingReservation(int parkingSpaceNumber, ParkingRequest parkingRequest);

}
