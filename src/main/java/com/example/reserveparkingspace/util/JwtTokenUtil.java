package com.example.reserveparkingspace.util;

import com.example.reserveparkingspace.entity.UserEntity;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * jwt token util
 *
 * @author dafengzhen
 */
@Component
public class JwtTokenUtil {

    @Value("${tokenKey}")
    private String tokenKey;

    /**
     * 检验令牌
     *
     * @param token token
     * @return boolean
     */
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(tokenKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException ignored) {
        }

        return false;
    }

    /**
     * 获取用户 Id
     *
     * @param token token
     * @return Long
     */
    public Long getUserId(String token) {
        try {
            return Long.valueOf(Jwts.parserBuilder().setSigningKey(tokenKey).build().parseClaimsJws(token).getBody().getSubject());
        } catch (JwtException ex) {
            throw new RuntimeException("令牌无效");
        }
    }

    /**
     * 生成访问令牌
     *
     * @param user user
     * @return String
     */
    public String generateAccessToken(UserEntity user) {
        return Jwts.builder().setSubject(String.valueOf(user.getId())).signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenKey))).compact();
    }

    /**
     * 生成随机密钥（妥善保管）
     */
    public static void main(String[] args) {
        System.out.println(Encoders.BASE64.encode(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()));
    }

}
