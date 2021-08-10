package com.asyncworking.auth;

import com.asyncworking.services.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

import static com.asyncworking.auth.AwcheetahAuthenticationToken.COMPANY_IDS;
import static com.asyncworking.auth.AwcheetahAuthenticationToken.PROJECT_IDS;
import static com.asyncworking.models.RoleNames.COMPANY_MANAGER;

@Component
@RequiredArgsConstructor
@Slf4j
public class Guard {

    private final ProjectService projectService;

    public boolean checkCompanyAccess(Authentication authentication, Long companyId) {
        return ifNotAnonymousAuthentication(authentication)
                && ifUserBelongsToCompany(authentication, companyId);
    }

    public boolean checkProjectAccessGetMethod(Authentication authentication, Long companyId, Long projectId) {
        return ifNotAnonymousAuthentication(authentication)
                && ifUserBelongsToCompany(authentication, companyId)
                && (ifUserIsCompanyManager(authentication, companyId)
                || ifUserBelongsToProject(authentication, projectId)
                || projectService.ifProjectIsPublic(projectId));
    }

    public boolean checkProjectAccessOtherMethods(Authentication authentication, Long companyId, Long projectId) {
        return ifNotAnonymousAuthentication(authentication)
                && ifUserBelongsToCompany(authentication, companyId)
                && (ifUserIsCompanyManager(authentication, companyId)
                || ifUserBelongsToProject(authentication, projectId));
    }

    private boolean ifNotAnonymousAuthentication(Authentication authentication) {
        if (authentication.getPrincipal().equals("anonymousUser")) {
            log.info("Anonymous user, access denied");
            return false;
        }
        return true;
    }

    private boolean ifUserBelongsToCompany(Authentication authentication, Long companyId) {
        var details = (Map<String, Set<Long>>) authentication.getDetails();
        Set<Long> companyIds = details.get(COMPANY_IDS);
        if (!companyIds.contains(companyId)) {
            log.info("User does not belong to this company!");
            return false;
        }
        return true;
    }

    private boolean ifUserBelongsToProject(Authentication authentication, Long projectId) {
        var details = (Map<String, Set<Long>>) authentication.getDetails();
        Set<Long> projectIds = details.get(PROJECT_IDS);
        if (!projectIds.contains(projectId)) {
            log.info("User does not belong to this project!");
            return false;
        }
        return true;
    }

    private boolean ifUserIsCompanyManager(Authentication authentication, Long companyId) {
        Set<AwcheetahGrantedAuthority> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> (AwcheetahGrantedAuthority) grantedAuthority)
                .collect(Collectors.toSet());

        for (AwcheetahGrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(COMPANY_MANAGER.value()) && authority.getTargetId().equals(companyId)) {
                return true;
            }
        }
        return false;
    }

    //Only for temporary use, will be deleted after APIs updating.
    public boolean checkProjectAccess(Authentication authentication, Long projectId) {
        if (!ifNotAnonymousAuthentication(authentication)) {
            return false;
        }
        Set<String> roleNames = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return ifUserBelongsToProject(authentication, projectId)
                || roleNames.contains(COMPANY_MANAGER.value());
    }
}
