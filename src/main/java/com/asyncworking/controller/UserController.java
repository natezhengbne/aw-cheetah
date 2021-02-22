package com.asyncworking.controller;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserInfoDto> createUser(@RequestBody UserInfoDto userInfoDto) {
        UserInfoDto userInfoDto1 = userService.createUser(userInfoDto);
        return ResponseEntity.ok(userInfoDto1);
    }

//    @PostMapping("/email")
//    public ResponseEntity<Boolean> checkEmailExists(@RequestBody UserInfoDto userInfoDto) {
//        Boolean ifEmailExists = userService.ifEmailExists(userInfoDto);
//        return ResponseEntity.ok(ifEmailExists);
//    }

//    @PostMapping("/register")
//    public String save(@Validated UserInfoDto userInfoDto, BindingResult bindingResult) {
//
//        if (userService.emailExists(userInfoDto.getEmail())) {
//            bindingResult.addError(new FieldError("userInfoDto",
//                    "email",
//                    "Error: Email address already exists"));
//        }
//
//        if (bindingResult.hasErrors()) {
//            return "register";
//        }
//        return "redirect/login";
//    }
}
