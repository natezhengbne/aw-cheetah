package com.asyncworking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final String TEST_MESSAGE = "Test";

    @GetMapping
    public String getTestMessage() {
        return TEST_MESSAGE;
    }

}
