package com.example.reserveparkingspace.controller;

import com.example.reserveparkingspace.repository.UserRepo;
import com.example.reserveparkingspace.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * hello restController
 *
 * @author dafengzhen
 */
@RequestMapping("/hello")
@RestController
public class HelloRestController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepo userRepo;

    @RequestMapping
    public String hello() {
        System.out.println(jwtTokenUtil.generateAccessToken(userRepo.findById(1L).get()));
        return "hello";
    }
}
