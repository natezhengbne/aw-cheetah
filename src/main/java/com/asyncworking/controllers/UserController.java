package com.asyncworking.controllers;

import com.asyncworking.dtos.CompanyColleagueDto;
import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.UserInfoDto;
import java.net.URI;
import java.net.URISyntaxException;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
@Validated
public class UserController {

    private final UserService userService;
    private final CompanyService companyService;

    @GetMapping("/signup")
    public ResponseEntity<String> validateEmail(@RequestParam(value = "email", required = true) String email) {
        if (userService.ifEmailExists(email)) {
            return new ResponseEntity<>("Email has taken", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Email does not exist", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody UserInfoDto userInfoDto) {
        log.info(userInfoDto.getEmail());
        UserInfoDto userLoginInfoDto
                = userService.login(userInfoDto.getEmail().toLowerCase(), userInfoDto.getPassword());
        return ResponseEntity.ok(userLoginInfoDto);
    }

    @PostMapping("/signup")
    public ResponseEntity createUser(@Valid @RequestBody AccountDto accountDto,
                                     HttpServletRequest request) {
        log.info("email: {}, name: {}", accountDto.getEmail(), accountDto.getName());
        userService.createUserAndGenerateVerifyLink(accountDto, SiteUrl.getSiteUrl(request));
        return ResponseEntity.ok("success");
    }

    @PostMapping("/resend")
    public ResponseEntity resendActivationLink(@Valid @RequestBody UserInfoDto userInfoDto,
                                               HttpServletRequest request) {
        userService.generateVerifyLink(userInfoDto.getEmail(), SiteUrl.getSiteUrl(request));
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
    public ResponseEntity companyCheck(@RequestParam(value = "email", required = true) String email) {
        log.info(email);
        if (userService.ifCompanyExits(email)){
            CompanyColleagueDto companyInfoDto = companyService.getCompanyInfoDto(email);
            return ResponseEntity.ok(companyInfoDto);
        }
        return new ResponseEntity<>("first login", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/login")
    public ResponseEntity statusCheck(@RequestParam(value = "email", required = true) String email) {
        log.info("email: {}", email);
        if (userService.ifUnverified(email)) {
            return new ResponseEntity<>("Unverified user", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        }
        return ResponseEntity.ok("success");
    }
}
