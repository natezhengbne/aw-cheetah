package com.asyncworking.controllers;

import com.asyncworking.config.EmailConfig;
import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import com.asyncworking.utility.SiteUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    private final EmailConfig emailConfig;

    @GetMapping("/signup")
    public ResponseEntity<String> validateEmail(@RequestParam(value = "email") String email) {
        if (userService.ifEmailExists(email)) {
            return new ResponseEntity<>("Email has taken", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Email does not exist", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AccountDto accountDto) {
        log.info(accountDto.getEmail());
        UserInfoDto userInfoDto = userService.login(
            accountDto.getEmail().toLowerCase(),
            accountDto.getPassword());
        return ResponseEntity.ok(userInfoDto);
    }

    @PostMapping("/signup")
    public ResponseEntity createUser(@Valid @RequestBody AccountDto accountDto) {
        log.info("email: {}, name: {}", accountDto.getEmail(), accountDto.getName());
        userService.createUserAndGenerateVerifyLink(accountDto);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/invitationsregister")
    public ResponseEntity createInvitationsUser(@Valid @RequestBody AccountDto accountDto) {
        log.info("email: {}, name: {}", accountDto.getEmail(), accountDto.getName());
        userService.createUserViaInvitationLink(accountDto);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/resend")
    public ResponseEntity resendActivationLink(@Valid @RequestBody UserInfoDto userInfoDto) {
        userService.generateVerifyLink(userInfoDto.getEmail());
        return ResponseEntity.ok("success");
    }

    @PostMapping("/verify")
    public ResponseEntity verifyAccountAndActiveUser(@RequestParam(value = "code") String code) throws URISyntaxException {
        log.info(code);
        boolean isVerified = userService.isAccountActivated(code);
        if (isVerified) {
            return ResponseEntity.ok("success");
        }
        return new ResponseEntity<>("Inactivated", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }

    @GetMapping("/company")
    public ResponseEntity companyCheck(@RequestParam(value = "email") String email) {
        log.info(email);
        if (userService.ifCompanyExits(email)){
            return ResponseEntity.ok(userService.fetchCompanyId(email));
        }
        return new ResponseEntity<>("first login", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/login")
    public ResponseEntity statusCheck(@RequestParam(value = "email") String email) {
        log.info("email: {}", email);
        if (userService.ifUnverified(email)) {
            return new ResponseEntity<>("Unverified user", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        }
        return ResponseEntity.ok("success");
    }
}
