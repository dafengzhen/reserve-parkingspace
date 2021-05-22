package com.example.reserveparkingspace.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

/**
 * 用户
 *
 * @author dafengzhen
 */
@Data
@Entity
@EqualsAndHashCode(exclude = "carList")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 微信 openId
     */
    @Column(unique = true)
    private String wxOpenId;

    /**
     * 微信 会话密钥
     */
    private String wxSessionKey;

    /**
     * 腾讯 unionId
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/union-id.html
     */
    @Column(unique = true)
    private String unionId;

    /**
     * 手机
     */
    @Column(unique = true)
    private String phone;

    /**
     * 车辆列表
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<CarEntity> carList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
