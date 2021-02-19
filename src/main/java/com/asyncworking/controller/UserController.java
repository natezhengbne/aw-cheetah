package com.asyncworking.controller;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserInfoDto> createUser(@RequestBody UserInfoDto userInfoDto) {
        UserInfoDto userInfoDto1 = userService.createUser(userInfoDto);
        return ResponseEntity.ok(userInfoDto1);
    }

//    @PostMapping
//    public ResponseEntity<String> createUserEntity(@RequestBody UserInfoDto userInfoDto) {
//        UserInfoDto userInfoDto1 = new UserInfoDto();
//        userInfoDto1.setName("Steven");
//        userInfoDto1.setEmail("skykk0128@gmail.com");
//        return ResponseEntity.ok("success");
//    }

//    @GetMapping
//    public ResponseEntity<String> createUserEntity() {
//        UserEntity userEntity = new UserEntity();
//        userEntity.setName("Steven");
//        userEntity.setEmail("skykk0128@gmail.com");
//        return ResponseEntity.ok(userEntity.toString());
//    }
}
