package com.asyncworking.services;

import com.asyncworking.utility.DateTimeUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LinkGeneratorTest {

    private LinkGenerator linkGenerator;

    private String siteUrl = "http://localhost:3000";

    private String jwtSecret = "securesecuresecuresecuresecuresecuresecure";

    @BeforeEach
    public void setup() {
        linkGenerator = new LinkGenerator(siteUrl, jwtSecret);
    }

//    @Test //TODO
//    public void test_generateResetPasswordLink_ok() {
//        String expectedLink = siteUrl
//                .concat("/reset-password?code=")
//                .concat("eyJhbGciOiJIUzI1NiJ9." +
//                        "eyJzdWIiOiJpbnZpdGF0aW9uIiwiY29tcGFueUlkIjoxLCJlbWFpbCI6InVz" +
//                        "ZXIxQGdtYWlsLmNvbSIsIm5hbWUiOiJ1c2VyMSIsInRpdGxlIjoiZGV2ZWxvcGVyIn0." +
//                        "FsfFrxlLeCjcSBV1cWp6D_VstygnaSr9EWSqZKKX1dU");
//
//        String invitationLink = linkGenerator.generateResetPasswordLink(1L, "user1@gmail.com", "user1", "developer");
//
//        assertEquals(expectedLink, invitationLink);
//    }

    @Test
    public void test_GenerateUserInvitationLink_ok() {
        String expectedLink = siteUrl
                .concat("/invitations/info?code=")
                .concat("eyJhbGciOiJIUzI1NiJ9." +
                        "eyJzdWIiOiJpbnZpdGF0aW9uIiwiY29tcGFueUlkIjoxLCJlbWFpbCI6InVz" +
                        "ZXIxQGdtYWlsLmNvbSIsIm5hbWUiOiJ1c2VyMSIsInRpdGxlIjoiZGV2ZWxvcGVyIn0." +
                        "FsfFrxlLeCjcSBV1cWp6D_VstygnaSr9EWSqZKKX1dU");

        String invitationLink = linkGenerator.generateUserInvitationLink(1L, "user1@gmail.com", "user1", "developer");

        assertEquals(expectedLink, invitationLink);
    }

    @Test
    public void test_generateCompanyInvitationLink_ok() {
        String expectedLink = siteUrl
                .concat("/company-invitations/info?code=")
                .concat("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb21wYW55SW52aXRhdGlvbiIsImNvbXBhbn");

        String companyInvitationLink = linkGenerator.generateCompanyInvitationLink(
                1L,
                "user2@gmail",
                "user2",
                DateTimeUtility.MILLISECONDS_IN_DAY);

        assertEquals(expectedLink.substring(0, 70), companyInvitationLink.substring(0, 70));
    }
}
