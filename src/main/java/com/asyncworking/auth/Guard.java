package com.asyncworking.auth;

import com.asyncworking.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class Guard {
    private final ProjectRepository projectRepository;
    private final MessageRepository messageRepository;

    public boolean checkAnonymousAuthentication(Authentication authentication) {
        return authentication.getPrincipal().equals("anonymousUser");
    }

    //Check if the user belongs to the company
    public boolean checkCompanyId(Authentication authentication, Long companyId) {
        if (checkAnonymousAuthentication(authentication)) {
            log.info("Anonymous user, access denied");
            return false;
        }

        var details = (Map<String, List<Long>>) authentication.getDetails();
        List<Long> companyIds = details.get("companyIds");

        return companyIds.contains(companyId);
    }

    //Check if the user belongs to the project
    public boolean checkProjectId(Authentication authentication, Long projectId) {
        if (checkAnonymousAuthentication(authentication)) {
            log.info("Anonymous user, access denied");
            return false;
        }

        Set<String> roleNames = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());
        if (roleNames.contains("Company Manager")) {
            return true;
        }

        var details = (Map<String, List<Long>>) authentication.getDetails();
        List<Long> projectIds = details.get("projectIds");

        return projectIds.contains(projectId);
    }

    public boolean checkProjectAccessGetMethod(Authentication authentication, Long companyId, Long projectId) {
        if (checkAnonymousAuthentication(authentication)) {
            log.info("Anonymous user, access denied");
            return false;
        }

        if (!checkCompanyId(authentication, companyId)) {
            log.info("User does not belong to this company!");
            return false;
        }

        //Check if the project belongs to the company
        Set<Long> projectIds = projectRepository.findProjectIdSetByCompanyId(companyId);
        if (!projectIds.contains(projectId)) {
            return false;
        }

        if (!projectRepository.findById(projectId).get().getIsPrivate()) {
            return true;
        }

        return checkProjectId(authentication, projectId);
    }

    public boolean checkProjectAccessOtherMethods(Authentication authentication, Long companyId, Long projectId) {
        if (checkAnonymousAuthentication(authentication)) {
            log.info("Anonymous user, access denied");
            return false;
        }

        if (!checkCompanyId(authentication, companyId)) {
            log.info("User does not belong to this company!");
            return false;
        }

        //Check if the project belongs to the company
        Set<Long> projectIds = projectRepository.findProjectIdSetByCompanyId(companyId);
        if (!projectIds.contains(projectId)) {
            return false;
        }

        return checkProjectId(authentication, projectId);
    }

    public boolean checkMessageAccessGetMethod(Authentication authentication, Long companyId, Long projectId, Long messageId) {
        if (checkAnonymousAuthentication(authentication)) {
            log.info("Anonymous user, access denied");
            return false;
        }

        if (!checkCompanyId(authentication, companyId)) {
            log.info("User does not belong to this company!");
            return false;
        }

        if (!messageRepository.findIfMessageExists(companyId, projectId, messageId)) {
            return false;
        }

        if (!projectRepository.findById(projectId).get().getIsPrivate()) {
            return true;
        }

        return checkProjectId(authentication, projectId);
    }

    public boolean checkMessageAccessOtherMethods(Authentication authentication, Long companyId, Long projectId, Long messageId) {
        if (checkAnonymousAuthentication(authentication)) {
            log.info("Anonymous user, access denied");
            return false;
        }

        if (!checkCompanyId(authentication, companyId)) {
            log.info("User does not belong to this company!");
            return false;
        }

        if (!messageRepository.findIfMessageExists(companyId, projectId, messageId)) {
            return false;
        }

        return checkProjectId(authentication, projectId);
    }
}
