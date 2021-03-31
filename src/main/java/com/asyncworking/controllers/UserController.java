package com.asyncworking.controllers;

import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.UserInfoPostDto;
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
@CrossOrigin("http://localhost:3000")
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public ResponseEntity<String> validateEmail(@RequestParam(value = "email") String email) {
        if (userService.ifEmailExists(email)) {
            return new ResponseEntity<>("Email has taken", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Email does not exist", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody UserInfoPostDto userInfoPostDto) {
        log.info(userInfoPostDto.getEmail());
        UserInfoPostDto userInfoPostDto1 = userService.login(
            userInfoPostDto.getEmail().toLowerCase(),
            userInfoPostDto.getPassword());
        return ResponseEntity.ok(userInfoPostDto1);
    }

    @PostMapping("/signup")
    public ResponseEntity createUser(@Valid @RequestBody AccountDto accountDto,
                                     HttpServletRequest request) {
        log.info("email: {}, name: {}", accountDto.getEmail(), accountDto.getName());
        userService.createUserAndGenerateVerifyLink(accountDto, SiteUrl.getSiteUrl(request));
        return ResponseEntity.ok("success");
    }

    @PostMapping("/resend")
    public ResponseEntity resendActivationLink(@Valid @RequestBody AccountDto accountDto,
                                               HttpServletRequest request) {
        userService.generateVerifyLink(accountDto.getEmail(), SiteUrl.getSiteUrl(request));
        return ResponseEntity.ok("success");
    }

    @GetMapping("/verify")
    public ResponseEntity verifyAccountAndActiveUser(@Param("code") String code) throws URISyntaxException {
        boolean isVerified = userService.isAccountActivated(code);

        URI redirectPage = new URI("http://localhost:3000/verifylink?verify=" + isVerified);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectPage);

        return new ResponseEntity(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @DeleteMapping("/signup")
    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }

    @GetMapping("/company")
    public ResponseEntity companyCheck(@RequestParam(value = "email") String email) {
        log.info(email);
        if (userService.ifCompanyExits(email)){
            return ResponseEntity.ok("success");
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
