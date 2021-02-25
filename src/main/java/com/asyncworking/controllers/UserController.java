package com.asyncworking.controllers;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public ResponseEntity<String> validEmail(@RequestBody UserInfoDto user) {
        if (userService.isEmailExist(user.getEmail())){
            return new ResponseEntity<>("Email has taken",
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Email does not exist" ,
                HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserInfoDto userInfoDto) {
        log.info(userInfoDto.getEmail());
        try {
            userService.login(userInfoDto.getEmail().toLowerCase(), userInfoDto.getPassword());
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity createUser(@RequestBody UserInfoDto userInfoDto) {
        log.info("email: " + userInfoDto.getEmail());
        log.info("name: " + userInfoDto.getName());

        try {

            UserInfoDto userInfoDtoPassword = userService.createUser(userInfoDto);
            return ResponseEntity.ok(userInfoDtoPassword);
        } catch (Exception e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
