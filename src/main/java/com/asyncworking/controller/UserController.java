package com.asyncworking.controller;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserInfoDto> createUser(@RequestBody UserInfoDto userInfoDto) {
        UserInfoDto userInfoDto1 = userService.createUser(userInfoDto);
        return ResponseEntity.ok(userInfoDto1);
    }

    @GetMapping("/signup")
    public ResponseEntity<String> validEmail(@RequestBody UserInfoDto user) {
        if(userService.isEmailExist(user.getEmail())){
            return new ResponseEntity<>( "Email has taken",
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Email does not exist" ,
                HttpStatus.OK);
    }
}
