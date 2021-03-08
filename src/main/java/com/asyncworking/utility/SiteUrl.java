package com.asyncworking.utility;

import javax.servlet.http.HttpServletRequest;

public class SiteUrl {
    public static String getSiteUrl(HttpServletRequest request) {
        String activationLink = request.getRequestURL().toString();
        return activationLink.replace(request.getServletPath(), "");
    }
}
