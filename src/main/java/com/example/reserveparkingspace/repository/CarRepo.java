package com.example.reserveparkingspace.repository;

import com.example.reserveparkingspace.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * car repository
 *
 * @author dafengzhen
 */
public interface CarRepo extends JpaRepository<CarEntity, Long> {

    /**
     * 通过 licensePlate 查找实体
     *
     * @param licensePlate licensePlate
     * @return Optional<CarEntity>
     */
    Optional<CarEntity> findByLicensePlate(String licensePlate);

}
