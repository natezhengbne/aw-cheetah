package com.asyncworking.controller;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/signup")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserInfoDto> createUser(@RequestBody UserInfoDto userInfoDto) {
        UserInfoDto userInfoDtoPassword = userService.createUser(userInfoDto);
        return ResponseEntity.ok(userInfoDtoPassword);
    }

}
