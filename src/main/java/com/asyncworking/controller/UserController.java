package com.asyncworking.controller;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.User;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserInfoDto> createUser(@RequestBody UserInfoDto userInfoDto) {
        UserInfoDto userInfoDto1 = userService.createUser(userInfoDto);
        return ResponseEntity.ok(userInfoDto1);
    }
}
