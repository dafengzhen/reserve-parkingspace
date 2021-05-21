package com.example.reserveparkingspace.service.impl;

import com.example.reserveparkingspace.entity.CarEntity;
import com.example.reserveparkingspace.entity.ParkingReservationEntity;
import com.example.reserveparkingspace.entity.UserEntity;
import com.example.reserveparkingspace.other.ParkingRequest;
import com.example.reserveparkingspace.repository.CarRepo;
import com.example.reserveparkingspace.repository.ParkingReservationRepo;
import com.example.reserveparkingspace.repository.UserRepo;
import com.example.reserveparkingspace.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * parking service implement
 *
 * @author dafengzhen
 */
@Transactional
@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    private ParkingReservationRepo parkingReservationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CarRepo carRepo;

    @Override
    public void addParkingReservation(int parkingSpaceNumber, ParkingRequest parkingRequest) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(user.getPhone(), parkingRequest.getPhone())) {
            user.setPhone(parkingRequest.getPhone());
        }

        ParkingReservationEntity parking = new ParkingReservationEntity();
        parking.setParkingSpaceNumber(parkingSpaceNumber);
        parking.setStartTime(parkingRequest.getStartTime());
        parking.setEndTime(parkingRequest.getEndTime());

        Optional<CarEntity> first = user.getCarList().stream()
                .filter(carEntity -> carEntity.getLicensePlate().equals(parkingRequest.getLicensePlate()))
                .findFirst();

        if (first.isPresent()) {
            parking.setCar(first.get());
            parkingReservationRepo.save(parking);
        } else {
            CarEntity car = new CarEntity();
            car.setLicensePlate(parkingRequest.getLicensePlate());

            parking.setCar(car);
            car.setUser(user);

            parkingReservationRepo.save(parking);
            carRepo.save(car);
        }

        userRepo.save(user);
    }
}
