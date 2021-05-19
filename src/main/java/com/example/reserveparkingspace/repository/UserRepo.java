package com.example.reserveparkingspace.repository;

import com.example.reserveparkingspace.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * user repository
 *
 * @author dafengzhen
 */
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    /**
     * 通过 wxOpenId 查找用户
     *
     * @param wxOpenId wxOpenId
     * @return Optional<UserEntity>
     */
    Optional<UserEntity> findByWxOpenId(String wxOpenId);

}
