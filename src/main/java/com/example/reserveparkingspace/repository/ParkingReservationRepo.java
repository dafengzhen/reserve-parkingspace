package com.example.reserveparkingspace.repository;

import com.example.reserveparkingspace.entity.ParkingReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * parking repository
 *
 * @author dafengzhen
 */
public interface ParkingReservationRepo extends JpaRepository<ParkingReservationEntity, Long> {

    /**
     * 判断指定停车位在 startTime 和 endTime 时间之间是否存在实体
     *
     * @param startTime startTime
     * @param endTime   endTime
     * @return boolean
     */
    boolean existsByParkingSpaceNumberAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(int parkingSpaceNumber, LocalDateTime startTime, LocalDateTime endTime);

}
