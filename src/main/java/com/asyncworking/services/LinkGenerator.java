package com.asyncworking.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@NoArgsConstructor
@AllArgsConstructor
public class LinkGenerator {

    private static final String VERIFICATION_SUBJECT = "signUp";
    private static final String PASSWORD_RESET_SUBJECT = "reset-password";
    private static final String INVITATION_SUBJECT = "invitation";
    private static final String COMPANY_INVITATION_SUBJECT = "companyInvitation";

    private static final String VERIFICATION_LINK_PREFIX = "/verifylink/verify?code=";
    private static final String PASSWORD_RESET_LINK_PREFIX = "/reset-password?code=";
    private static final String INVITATION_LINK_PREFIX = "/invitations/info?code=";
    private static final String COMPANY_INVITATION__LINK_PREFIX = "/company-invitations/info?code=";

    @Value("${url}")
    private String baseUrl;
    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateUserVerificationLink(String email, long expiryTimeInMilliseconds) {
        String link = generateLink(email, VERIFICATION_LINK_PREFIX, VERIFICATION_SUBJECT, expiryTimeInMilliseconds);
        log.info("User Verification Link={}", link);
        return link;
    }

    public String generateResetPasswordLink(String email, long expiryTimeInMilliseconds) {
        String link = generateLink(email, PASSWORD_RESET_LINK_PREFIX, PASSWORD_RESET_SUBJECT, expiryTimeInMilliseconds);
        log.info("Password Reset Link={}", link);
        return link;
    }

    private String generateLink(String email, String subApi, String subject, long expiryTimeInMilliseconds) {
        Date expireDate = new Date(System.currentTimeMillis() + expiryTimeInMilliseconds);

        String jwts = Jwts.builder()
                .setSubject(subject)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
        log.info("jwt=" + jwts);

        String link = baseUrl + subApi + jwts;
        return link;
    }

    public String generateInvitationLink(Long companyId, String email, String name, String title) {
        String invitationJwt = Jwts.builder()
                .setSubject(INVITATION_SUBJECT)
                .claim("companyId", companyId)
                .claim("email", email)
                .claim("name", name)
                .claim("title", title)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
        log.info("invitationJwt=" + invitationJwt);

        String invitationLink = baseUrl + INVITATION_LINK_PREFIX + invitationJwt;
        log.info("User Invitation Link={}", invitationLink);
        return invitationLink;
    }

    public String generateCompanyInvitationLink(Long companyId, String email, String name, String title, long expiryTimeInMilliseconds) {
        Date expireDate = new Date(System.currentTimeMillis() + expiryTimeInMilliseconds);
        String invitationJwt = Jwts.builder()
                .setSubject(COMPANY_INVITATION_SUBJECT)
                .claim("companyId", companyId)
                .claim("email", email)
                .claim("name", name)
                .claim("date", expireDate)
                .claim("title", title)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .compact();
        log.info("companyInvitationLink=" + invitationJwt);

        String invitationLink = baseUrl + COMPANY_INVITATION__LINK_PREFIX + invitationJwt;
        log.info("Company Invitation Link={}", invitationLink);
        return invitationLink;
    }
}
