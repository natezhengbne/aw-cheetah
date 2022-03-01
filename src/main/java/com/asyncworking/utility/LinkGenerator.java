package com.asyncworking.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@NoArgsConstructor
public class LinkGenerator {

    @Value("${url}")
    private String baseUrl;

    @Value("${jwt.secret}")
    private String jwtSecret;


    public String generateUserVerificationLink(String email, Date expireDate) {
        return this.generateLink(email, "/verifylink/verify?code=", "signUp", expireDate);
    }

    public String generateResetPasswordLink(String email, Date expireDate) {
        return this.generateLink(email, "/reset-password?code=", "reset-password", expireDate);
    }

    public String generateCompanyInvitationLink(Long companyId, String email, String name, String title) {
        String invitationLink = this.baseUrl
                + "/invitations/info?code="
                + this.encodeInvitation(companyId, email, name, title);
        log.info("invitationLink: " + invitationLink);
        return invitationLink;
    }

    private String generateLink(String email, String subApi, String subject, Date expireDate) {
        String link = this.baseUrl + subApi + this.generateJws(email, subject, expireDate);
        log.info(subApi + "Link: {}", link);
        return link;
    }

    private String generateJws(String email, String subject, Date expireDate) {
        String jws = Jwts.builder()
                .setSubject(subject)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
        log.info("jwt token: " + jws);

        return jws;
    }

    private String encodeInvitation(Long companyId, String email, String name, String title) {
        String invitationJwt = Jwts.builder()
                .setSubject("invitation")
                .claim("companyId", companyId)
                .claim("email", email)
                .claim("name", name)
                .claim("title", title)
                .signWith(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .compact();
        log.info("invitationJwt: " + invitationJwt);
        return invitationJwt;
    }
}
