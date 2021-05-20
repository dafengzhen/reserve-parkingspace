package com.example.reserveparkingspace.controller;

import com.example.reserveparkingspace.repository.ParkingReservationRepo;
import com.example.reserveparkingspace.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * parking restController
 *
 * @author dafengzhen
 */
@RequestMapping("/api/public")
@RestController
public class ParkingRestController {

    @Value("${parkingSpaces}")
    private int parkingSpaces;

    @Autowired
    private ParkingReservationRepo parkingReservationRepo;

    @Autowired
    private ParkingService parkingService;

    @PostMapping("/parking")
    public ResponseEntity<String> parking(@RequestBody @Valid ParkingRequest parkingRequest) {
        final LocalDateTime startTime = parkingRequest.getStartTime();
        final LocalDateTime endTime = parkingRequest.getEndTime();
        final int year = LocalDateTime.now().getYear();

        if (startTime.isEqual(endTime)) {
            return ResponseEntity.badRequest().body("时间不能相等");
        }

        if (startTime.isAfter(endTime)) {
            return ResponseEntity.badRequest().body("时间无效");
        }

        if (startTime.getYear() < year || endTime.getYear() < year) {
            return ResponseEntity.badRequest().body("无法预约过去时间");
        }

        if (parkingSpaces == 0) {
            return ResponseEntity.badRequest().body("目前暂不开放预约");
        }

        // 判断是否有空余时间段的停车位
        List<Integer> remainingNumbers = new ArrayList<>(parkingSpaces);
        for (int i = 0; i < parkingSpaces; i++) {
            if (!parkingReservationRepo.existsByParkingSpaceNumberAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(i, startTime, startTime)) {
                // 记录该时间段可以停车的车位号
                remainingNumbers.add(i);
            }
        }

        if (remainingNumbers.isEmpty()) {
            return ResponseEntity.badRequest().body("没有空余停车位");
        }

        // 随机取一个车位号预约停车
        Integer num = remainingNumbers.get(new Random().nextInt(remainingNumbers.size()));
        parkingService.addParkingReservation(num, parkingRequest);

        return ResponseEntity.ok().body("预约停车位成功");
    }

}
