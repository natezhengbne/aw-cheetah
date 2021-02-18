package com.asyncworking.controller;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class UserController {

    private  final  UserService userService;

    @PostMapping
    public ResponseEntity login(@RequestBody UserInfoDto userInfoDto) {
        log.info(userInfoDto.getEmail());
        log.info(userInfoDto.getPassword());
        try {
            userService.login(userInfoDto.getName(), userInfoDto.getPassword());
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
