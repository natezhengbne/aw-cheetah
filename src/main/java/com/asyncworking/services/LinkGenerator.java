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

    @Value("${url}")
    private String baseUrl;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateUserVerificationLink(String email, long expiryTimeInMilliseconds) {
        String link = generateLink(email, "/verifylink/verify?code=", "signUp", expiryTimeInMilliseconds);
        log.info("User Verification Link={}", link);
        return link;
    }

    public String generateResetPasswordLink(String email, long expiryTimeInMilliseconds) {
        String link = generateLink(email, "/reset-password?code=", "reset-password", expiryTimeInMilliseconds);
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

    public String generateUserInvitationLink(Long companyId, String email, String name, String title) {
        String invitationJwt = Jwts.builder()
                .setSubject("invitation")
                .claim("companyId", companyId)
                .claim("email", email)
                .claim("name", name)
                .claim("title", title)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
        log.info("invitationJwt=" + invitationJwt);

        String invitationLink = baseUrl + "/invitations/info?code=" + invitationJwt;
        log.info("User Invitation Link={}", invitationLink);
        return invitationLink;
    }

    public String generateCompanyInvitationLink(Long companyId, String email, String name, long expiryTimeInMilliseconds) {
        Date expireDate = new Date(System.currentTimeMillis() + expiryTimeInMilliseconds);
        String invitationJwt = Jwts.builder()
                .setSubject("companyInvitation")
                .claim("companyId", companyId)
                .claim("email", email)
                .claim("name", name)
                .claim("date", expireDate)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .compact();
        log.info("invitationJwt=" + invitationJwt);

        String invitationLink = baseUrl + "/company-invitations/info?code=" + invitationJwt;
        log.info("Company Invitation Link={}", invitationLink);
        return invitationLink;
    }


}
