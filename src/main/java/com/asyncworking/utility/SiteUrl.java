package com.asyncworking.utility;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
public class SiteUrl {

    public static String getSiteUrl(HttpServletRequest request) {
        String activationLink = request.getRequestURL().toString();
        return activationLink.replace(request.getServletPath(), "");
    }
}
