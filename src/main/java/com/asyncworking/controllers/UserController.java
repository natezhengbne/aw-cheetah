package com.asyncworking.controllers;

import com.asyncworking.dtos.*;
import com.asyncworking.services.EmailService;
import com.asyncworking.services.UserService;
import com.asyncworking.services.LinkService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    private final EmailService emailService;

    private final LinkService linkService;

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
    @ApiOperation(value = "Check if the email is already exist in system")
    public ResponseEntity<String> verifyEmailExists(@RequestParam(value = "email") String email) {
        if (userService.ifEmailExists(email)) {
            return new ResponseEntity<>("Email has taken", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Email does not exist", HttpStatus.OK);
    }

    @PostMapping("/signup")
    @ApiOperation(value = "Create a user and send verification mail to the user's email address")
    public ResponseEntity createUser(@Valid @RequestBody AccountDto accountDto) {
        log.info("email: {}, name: {}", accountDto.getEmail(), accountDto.getName());
        userService.createUserAndSendVerificationEmail(accountDto);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/resend")
    @ApiOperation(value = "Resend verification link by email provided")
    public ResponseEntity resendActivationLink(@Valid @RequestBody UserInfoDto userInfoDto) {
        String userEmail = userInfoDto.getEmail();
        if(!userService.ifEmailExists(userEmail)) {
            return new ResponseEntity<>("Email does not exist", HttpStatus.NOT_FOUND);
        }
        if(!userService.ifUnverified(userEmail)) {
            return new ResponseEntity<>("Email has already been verified!", HttpStatus.CONFLICT);
        }
        emailService.sendVerificationEmail(userEmail);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/invitations/companies")
    @PreAuthorize("hasPermission(#companyId, 'Company Manager')")
    public ResponseEntity getInvitationLink(@RequestParam(value = "companyId") Long companyId,
                                            @RequestParam(value = "email") String email,
                                            @RequestParam(value = "name") String name,
                                            @RequestParam(value = "title") String title) {
        return ResponseEntity.ok(linkService.generateInvitationLink(companyId, email, name, title));
    }

    @GetMapping("/invitations/info")
    public ResponseEntity getInvitationInfo(@RequestParam(value = "code") String code) {
        log.info("The code is " + code);
        ExternalEmployeeDto externalEmployeeDto = userService.getUserInfo(code);
        return ResponseEntity.ok(externalEmployeeDto);
    }

    @PostMapping("/invitations/register")
    public ResponseEntity createInvitationsUser(@Valid @RequestBody InvitedAccountPostDto accountDto) {
        InvitedAccountGetDto user = userService.createUserViaInvitationLink(accountDto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/verify")
    @ApiOperation(value = "Verify the user according to the verification code")
    public ResponseEntity verifyActiveUser(@RequestParam(value = "code") String code) {
        log.info(code);
        boolean isVerified = userService.isAccountActivated(code);
        if (isVerified) {
            return ResponseEntity.ok("success");
        }
        return new ResponseEntity<>("Inactivated", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }

    @PostMapping("/password")
    @ApiOperation(value = "Send password reset link to the email provided")
    public ResponseEntity generateResetPasswordLink(@RequestParam(value = "email")  String email) {
        if (userService.ifUnverified(email)) {
            return new ResponseEntity<>("Email is unactivated", HttpStatus.CONFLICT);
        }
        if (!userService.ifEmailExists(email)) {
            return new ResponseEntity<>("Email is not exist", HttpStatus.NOT_FOUND);
        }
        emailService.sendPasswordResetEmail(email);
        return ResponseEntity.ok("sent");
    }

    @GetMapping("/password-reset/info")
    public ResponseEntity getResetterInfo(@RequestParam(value = "code") String code) {
        log.info("The code is " + code);
        userService.getResetterInfo(code);
        return ResponseEntity.ok("valid");
    }

    @PutMapping("/password-reset")
    public ResponseEntity resetPassword(@Valid @RequestBody UserInfoDto userInfoDto) {
        userService.resetPassword(userInfoDto);
        return ResponseEntity.ok("reset");
    }
}
