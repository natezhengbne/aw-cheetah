package com.asyncworking.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AwcheetahAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public static final String COMPANY_IDS = "companyIds";

    public static final String PROJECT_IDS = "projectIds";

    private final Set<Long> companyIds;

    private final Set<Long> projectIds;

    public AwcheetahAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities,
                                        Set<Long> companyIds, Set<Long> projectIds) {
        super(principal, credentials, authorities);
        this.companyIds = companyIds;
        this.projectIds = projectIds;
    }

    @Override
    public Object getDetails() {
        Map<String, Set<Long>> details = new HashMap<>();
        details.put(COMPANY_IDS, companyIds);
        details.put(PROJECT_IDS, projectIds);
        return details;
    }

}
