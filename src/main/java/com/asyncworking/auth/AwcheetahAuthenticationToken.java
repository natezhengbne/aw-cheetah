package com.asyncworking.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwcheetahAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final List<Long> companyIds;

    private final List<Long> projectIds;

    public AwcheetahAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities,
                                        List<Long> companyIds, List<Long> projectIds) {
        super(principal, credentials, authorities);
        this.companyIds = companyIds;
        this.projectIds = projectIds;
    }

    @Override
    public Object getDetails() {
        Map<String, List<Long>> details = new HashMap();
        details.put("companyIds", companyIds);
        details.put("projectIds", projectIds);
        return details;
    }

}
