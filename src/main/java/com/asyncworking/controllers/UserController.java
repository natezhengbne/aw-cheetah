package com.asyncworking.controllers;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
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
