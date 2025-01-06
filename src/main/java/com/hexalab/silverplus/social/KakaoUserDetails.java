package com.hexalab.silverplus.social;

import java.util.Map;

public class KakaoUserDetails implements OAuth2UserInfo {
    private Map<String, Object> attributes;
    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getProvider() {
        return "Kakao";
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
