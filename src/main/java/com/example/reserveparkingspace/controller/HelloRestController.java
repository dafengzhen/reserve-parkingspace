package com.example.reserveparkingspace.controller;

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

    @RequestMapping
    public String hello() {
        return "hello";
    }

}
