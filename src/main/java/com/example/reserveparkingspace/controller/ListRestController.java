package com.example.reserveparkingspace.controller;

import com.example.reserveparkingspace.entity.CarEntity;
import com.example.reserveparkingspace.entity.ParkingReservationEntity;
import com.example.reserveparkingspace.repository.ParkingReservationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * list restController
 *
 * @author dafengzhen
 */
@RequestMapping("/api/public")
@RestController
public class ListRestController {

    @Autowired
    private ParkingReservationRepo parkingReservationRepo;

    @GetMapping("/parkingList")
    public ResponseEntity<Set<CarEntity>> parkingList() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
        LocalDateTime endTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59);
        return ResponseEntity.ok().body(
                parkingReservationRepo.findAllByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(startTime, endTime).stream()
                        .map(ParkingReservationEntity::getCar)
                        .collect(Collectors.toSet())
        );
    }

}
