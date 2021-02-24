package com.asyncworking.controllers;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;


@Slf4j
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity login(@RequestBody UserInfoDto userInfoDto) {
        log.info(userInfoDto.getEmail());
        try {
            userService.login(userInfoDto.getEmail().toLowerCase(), userInfoDto.getPassword());
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
