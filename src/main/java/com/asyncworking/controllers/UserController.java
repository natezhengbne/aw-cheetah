package com.asyncworking.controllers;

import com.asyncworking.dtos.*;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public ResponseEntity verifyStatus(@RequestParam(value = "email") String email) {
        log.info("email: {}", email);
        if (userService.ifUnverified(email)) {
            return new ResponseEntity<>("Unverified user", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        }
        return ResponseEntity.ok("success");
    }

    @GetMapping("/company")
    public ResponseEntity verifyCompany(@RequestParam(value = "email") String email) {
        log.info(email);
        if (userService.ifCompanyExits(email)) {
            return ResponseEntity.ok(userService.fetchCompanyId(email));
        }
        return new ResponseEntity<>("first login", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/signup")
    public ResponseEntity<String> verifyEmailExists(@RequestParam(value = "email") String email) {
        if (userService.ifEmailExists(email)) {
            return new ResponseEntity<>("Email has taken", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Email does not exist", HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity createUser(@Valid @RequestBody AccountDto accountDto) {
        log.info("email: {}, name: {}", accountDto.getEmail(), accountDto.getName());
        userService.createUserAndGenerateVerifyLink(accountDto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/invitations/companies")
    @PreAuthorize("hasPermission(#companyId, 'Company Manager')")
    public ResponseEntity getInvitationLink(@RequestParam(value = "companyId") Long companyId,
                                            @RequestParam(value = "email") String email,
                                            @RequestParam(value = "name") String name,
                                            @RequestParam(value = "title") String title) {
        return ResponseEntity.ok(userService.generateInvitationLink(companyId, email, name, title));
    }

    @GetMapping("/invitations/info")
    public ResponseEntity getInvitationInfo(@RequestParam(value = "code") String code) throws URISyntaxException {
        log.info("The code is " + code);
        ExternalEmployeeDto externalEmployeeDto = userService.getUserInfo(code);
        return ResponseEntity.ok(externalEmployeeDto);
    }

    @PostMapping("/invitations/register")
    public ResponseEntity createInvitationsUser(@Valid @RequestBody InvitedAccountPostDto accountDto) {
        InvitedAccountGetDto user = userService.createUserViaInvitationLink(accountDto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/resend")
    public ResponseEntity resendActivationLink(@Valid @RequestBody UserInfoDto userInfoDto) {
        userService.generateVerifyLink(userInfoDto.getEmail());
        return ResponseEntity.ok("success");
    }

    @PostMapping("/verify")
    public ResponseEntity verifyActiveUser(@RequestParam(value = "code") String code) throws URISyntaxException {
        log.info(code);
        boolean isVerified = userService.isAccountActivated(code);
        if (isVerified) {
            return ResponseEntity.ok("success");
        }
        return new ResponseEntity<>("Inactivated", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }

    @PutMapping("/password")
    public ResponseEntity resetPassword(@RequestParam(value = "email") String email) {
        // Todo: add password rest function
        if (userService.ifUnverified(email)) {
            return new ResponseEntity<>("Email is unactivated", HttpStatus.CONFLICT);
        }

        if (!userService.ifEmailExists(email)) {
            return new ResponseEntity<>("Email is not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
