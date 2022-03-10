package com.asyncworking.services;

import com.asyncworking.config.FrontEndUrlConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LinkGeneratorTest {

    private LinkGenerator linkGenerator;

    @Value("${url}")
    private String siteUrl;

    @BeforeEach
    public void setup() {
        linkGenerator = new LinkGenerator();
    }

    @Test
    public void testGenerateUserInvitationLink_GivenDetail_ok() {
        String invitationLink = linkGenerator.generateUserInvitationLink(1L, "user1@gmail.com", "user1", "developer");
        assertEquals(
                siteUrl.concat("/invitations/info?code=")
                        .concat("eyJhbGciOiJIUzI1NiJ9." +
                                "eyJzdWIiOiJpbnZpdGF0aW9uIiwiY29tcGFueUlkIjoxLCJlbWFpbCI6InVz" +
                                "ZXIxQGdtYWlsLmNvbSIsIm5hbWUiOiJ1c2VyMSIsInRpdGxlIjoiZGV2ZWxvcGVyIn0." +
                                "FsfFrxlLeCjcSBV1cWp6D_VstygnaSr9EWSqZKKX1dU"),
                invitationLink
        );
    }
}
