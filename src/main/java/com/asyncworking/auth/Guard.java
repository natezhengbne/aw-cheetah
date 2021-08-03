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

    //Return false if the authentication is anonymous authentication
    public boolean checkAnonymousAuthentication(Authentication authentication) {
        if (authentication.getPrincipal().equals("anonymousUser")) {
            log.info("Anonymous user, access denied");
            return false;
        }
        return true;
    }

    //Check if the user belongs to the company
    public boolean checkCompanyId(Authentication authentication, Long companyId) {
        var details = (Map<String, List<Long>>) authentication.getDetails();
        List<Long> companyIds = details.get("companyIds");
        if (!companyIds.contains(companyId)) {
            log.info("User does not belong to this company!");
            return false;
        }
        return true;
    }

    //Check if the user belongs to the project
    public boolean checkProjectId(Authentication authentication, Long projectId) {
        var details = (Map<String, List<Long>>) authentication.getDetails();
        List<Long> projectIds = details.get("projectIds");
        if (!projectIds.contains(projectId)) {
            log.info("User does not belong to this project!");
            return false;
        }
        return true;
    }

    //Check if the user is the Company Manager of given company Id
    public boolean checkCompanyManager(Authentication authentication, Long companyId) {
        Set<AwcheetahGrantedAuthority> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> (AwcheetahGrantedAuthority) grantedAuthority)
                .collect(Collectors.toSet());

        for (AwcheetahGrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("Company Manager") && authority.getTargetId() == companyId) {
                return true;
            }
        }
        return false;
    }

    public boolean checkCompanyAccess(Authentication authentication, Long companyId) {
        return checkAnonymousAuthentication(authentication)
                && checkCompanyId(authentication, companyId);
    }

    //Only for temporary use, will be deleted after APIs updating.
    public boolean checkProjectAccess(Authentication authentication, Long projectId) {
        Set<String> roleNames = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());
        return checkAnonymousAuthentication(authentication)
                && checkProjectId(authentication, projectId)
                || roleNames.contains("Company Manager");
    }

    public boolean checkAccessGetMethod(Authentication authentication, Long companyId, Long projectId) {
        return checkAnonymousAuthentication(authentication)
                && checkCompanyId(authentication, companyId)
                && checkCompanyManager(authentication, companyId)
                || checkProjectId(authentication, projectId)
                || !projectRepository.findById(projectId).get().getIsPrivate();
    }

    public boolean checkAccessOtherMethods(Authentication authentication, Long companyId, Long projectId) {
        return checkAnonymousAuthentication(authentication)
                && checkCompanyId(authentication, companyId)
                && checkCompanyManager(authentication, companyId)
                || checkProjectId(authentication, projectId);
    }

    public boolean checkProjectAccessGetMethod(Authentication authentication, Long companyId, Long projectId) {
        return checkAccessGetMethod(authentication, companyId, projectId)
                && projectRepository.findProjectIdSetByCompanyId(companyId).contains(projectId);
    }

    public boolean checkProjectAccessOtherMethods(Authentication authentication, Long companyId, Long projectId) {
        return checkAccessOtherMethods(authentication, companyId, projectId)
                && projectRepository.findProjectIdSetByCompanyId(companyId).contains(projectId);
    }

    public boolean checkMessageAccessGetMethod(Authentication authentication, Long companyId, Long projectId, Long messageId) {
        return checkAccessGetMethod(authentication, companyId, projectId)
                && messageRepository.findIfMessageExists(companyId, projectId, messageId);
    }

    public boolean checkMessageAccessOtherMethods(Authentication authentication, Long companyId, Long projectId, Long messageId) {
        return checkAccessOtherMethods(authentication, companyId, projectId)
                && messageRepository.findIfMessageExists(companyId, projectId, messageId);
    }
}
