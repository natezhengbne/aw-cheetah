package com.asyncworking.controller;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String getTestMessage() {
        return "CREATE PASSWORD";
    }

    @PostMapping
    public ResponseEntity<UserInfoDto> createPassword(@RequestBody UserInfoDto userInfoDto){
        UserInfoDto userInfoDtoPassword = userService.createUser(userInfoDto);
        return ResponseEntity.ok(userInfoDtoPassword);
    }

}
