package com.asyncworking.controllers;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import com.asyncworking.utility.SiteUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public ResponseEntity<String> validateEmail(@RequestParam("email") String email) {
        if (userService.ifEmailExists(email)) {
            return new ResponseEntity<>("Email has taken", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Email does not exist", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserInfoDto userInfoDto) {
        log.info(userInfoDto.getEmail());
        try {
            userService.login(userInfoDto.getEmail().toLowerCase(), userInfoDto.getPassword());
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody UserInfoDto userInfoDto, HttpServletRequest request) {
        log.info("email: {}, name: {}", userInfoDto.getEmail(), userInfoDto.getName());
        try {
            userService.createUserAndGenerateVerifyLink(userInfoDto, SiteUrl.getSiteUrl(request));

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity resendActivationLink(@RequestBody UserInfoDto userInfoDto, HttpServletRequest request) {
        try {
            userService.generateVerifyLink(userInfoDto, SiteUrl.getSiteUrl(request));
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity verifyAccountAndActiveUser(@Param("code") String code) {
        try {
           userService.verifyAccountAndActiveUser(code);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/signup")
    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }

    @GetMapping("/company_check")
    public ResponseEntity<?> companyCheck(@RequestParam("email") String email) {
        log.info(email);
        if (userService.ifCompanyExits(email)){
            return ResponseEntity.ok("success");
        }
        return new ResponseEntity<>("first login", HttpStatus.NOT_FOUND);
    }

}
