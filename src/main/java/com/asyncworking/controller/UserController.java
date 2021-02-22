package com.asyncworking.controller;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
//@RequestMapping("/signup")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
//    public ResponseEntity<UserInfoDto> createUser(@RequestBody UserInfoDto userInfoDto) {
    public ResponseEntity createUser(@RequestBody UserInfoDto userInfoDto) {
        log.info("email: " + userInfoDto.getEmail());
        log.info("name: " + userInfoDto.getName());

        try {

            UserInfoDto userInfoDtoPassword = userService.createUser(userInfoDto);
//            log.info(userInfoDtoPassword.getPassword());
            return ResponseEntity.ok(userInfoDtoPassword);
        } catch (Exception e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //AW-9 function test
    @PostMapping("/email")
    public ResponseEntity<Boolean> verifyEmail(@RequestBody String email) {
        Boolean isEmailUsed = userService.isEmailUsed(email);
        System.out.println(isEmailUsed);
        return ResponseEntity.ok(isEmailUsed);
    }
}
