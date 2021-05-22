package com.example.reserveparkingspace.controller;

import com.example.reserveparkingspace.entity.CarEntity;
import com.example.reserveparkingspace.entity.ParkingReservationEntity;
import com.example.reserveparkingspace.entity.UserEntity;
import com.example.reserveparkingspace.other.ParkingRequest;
import com.example.reserveparkingspace.repository.ParkingReservationRepo;
import com.example.reserveparkingspace.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

/**
 * parking restController
 *
 * @author dafengzhen
 */
@RestController
public class ParkingRestController {

    @Value("${parkingSpaces}")
    private int parkingSpaces;

    @Autowired
    private ParkingReservationRepo parkingReservationRepo;

    @Autowired
    private ParkingService parkingService;

    @PostMapping("/parking")
    public ResponseEntity<String> parking(@RequestBody @Valid ParkingRequest parkingRequest, Authentication authentication) {
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

        // 判断该时间段是否存在预约冲突
        UserEntity user = (UserEntity) authentication.getPrincipal();
        Optional<CarEntity> carFirst = user.getCarList().stream()
                .filter(carEntity -> carEntity.getLicensePlate().equals(parkingRequest.getLicensePlate()))
                .findFirst();

        if (carFirst.isPresent()) {
            Optional<ParkingReservationEntity> parkingFirst = carFirst.get().getParkingReservationList().stream()
                    .filter(entity -> checkParkingSpaceReservation(entity.getParkingSpaceNumber(), startTime, endTime))
                    .findFirst();

            if (parkingFirst.isPresent()) {
                return ResponseEntity.badRequest().body("抱歉预约失败");
            }
        }

        // 判断是否有空余时间段的停车位
        List<Integer> remainingNumbers = new ArrayList<>(parkingSpaces);
        for (int i = 0; i < parkingSpaces; i++) {
            if (!checkParkingSpaceReservation(i, startTime, endTime)) {
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

    @GetMapping("/myParkingList")
    public ResponseEntity<Set<CarEntity>> myParkingList(Authentication authentication) {
        return ResponseEntity.ok().body(((UserEntity) authentication.getPrincipal()).getCarList());
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancelReservation(@RequestParam Long id) {
        try {
            parkingReservationRepo.deleteById(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("取消预约失败");
        }
        return ResponseEntity.ok().body("取消预约完成");
    }

    /**
     * 检查车位预约
     *
     * @param parkingSpaceNumber parkingSpaceNumber
     * @param startTime          startTime
     * @param endTime            endTime
     * @return boolean
     */
    private boolean checkParkingSpaceReservation(int parkingSpaceNumber, LocalDateTime startTime, LocalDateTime endTime) {
        return parkingReservationRepo.existsByParkingSpaceNumberAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(parkingSpaceNumber, startTime, startTime) ||
                parkingReservationRepo.existsByParkingSpaceNumberAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(parkingSpaceNumber, endTime, endTime);
    }

}
