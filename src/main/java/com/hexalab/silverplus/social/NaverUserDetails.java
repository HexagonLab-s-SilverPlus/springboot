package com.hexalab.silverplus.social;

import java.util.Map;

public class NaverUserDetails implements OAuth2UserInfo {
    private Map<String, Object> attributes;
    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "Naver";
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }
}
