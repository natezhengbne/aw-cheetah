package com.asyncworking.services;

import com.asyncworking.utility.DateTimeUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LinkGeneratorTest {

    private LinkGenerator linkGenerator;

    private final String siteUrl = "http://localhost:3000";

    private final String jwtSecret = "securesecuresecuresecuresecuresecuresecure";

    @BeforeEach
    public void setup() {
        linkGenerator = new LinkGenerator(siteUrl, jwtSecret);
    }

    @Test
    public void test_generateUserVerificationLink_ok() {
        String expectedLink = siteUrl
                .concat("/verifylink/verify?code=")
                .concat("eyJhbGciOiJIUzI1NiJ9." +
                        "eyJzdWIiOiJyZXNldC1wYXNzd29yZCIsImVtYWlsIjoidXNlcjFAZ21haWwu" +
                        "Y29tIiwiaWF0IjoxNjQ3MzQ0NjQwLCJleHAiOjE2NDc0MzEwNDB9." +
                        "vzt8X_P9fdmiu9yQF2Dq2Zyz_Lbzx5r87zjEt2p0wyA");

        String verificationLink = linkGenerator.generateUserVerificationLink("user1@gmail.com", DateTimeUtility.MILLISECONDS_IN_DAY);

        assertEquals(expectedLink.substring(0, 70), verificationLink.substring(0, 70));
    }

    @Test
    public void test_generateResetPasswordLink_ok() {
        String expectedLink = siteUrl
                .concat("/reset-password?code=")
                .concat("eyJhbGciOiJIUzI1NiJ9." +
                        "eyJzdWIiOiJyZXNldC1wYXNzd29yZCIsImVtYWlsIjoidXNlcjFAZ21haWwu" +
                        "Y29tIiwiaWF0IjoxNjQ3MzQ0NjQwLCJleHAiOjE2NDc0MzEwNDB9." +
                        "vzt8X_P9fdmiu9yQF2Dq2Zyz_Lbzx5r87zjEt2p0wyA");

        String invitationLink = linkGenerator.generateResetPasswordLink("user1@gmail.com", DateTimeUtility.MILLISECONDS_IN_DAY);

        assertEquals(expectedLink.substring(0, 70), invitationLink.substring(0, 70));
    }

    @Test
    public void test_GenerateInvitationLink_ok() {
        String expectedLink = siteUrl
                .concat("/invitations/info?code=")
                .concat("eyJhbGciOiJIUzI1NiJ9." +
                        "eyJzdWIiOiJpbnZpdGF0aW9uIiwiY29tcGFueUlkIjoxLCJlbWFpbCI6InVz" +
                        "ZXIxQGdtYWlsLmNvbSIsIm5hbWUiOiJ1c2VyMSIsInRpdGxlIjoiZGV2ZWxvcGVyIn0." +
                        "FsfFrxlLeCjcSBV1cWp6D_VstygnaSr9EWSqZKKX1dU");

        String invitationLink = linkGenerator.generateInvitationLink(1L, "user1@gmail.com", "user1", "developer");

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
                "title",
                DateTimeUtility.MILLISECONDS_IN_DAY);

        assertEquals(expectedLink.substring(0, 70), companyInvitationLink.substring(0, 70));
    }
}
