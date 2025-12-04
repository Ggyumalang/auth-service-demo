package com.example.auth.security.oauth2.user;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private static final String KEY_RESPONSE = "response";
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String PROVIDER_NAVER = "naver";

    private Map<String, Object> attributes;
    private Map<String, Object> response;

    @SuppressWarnings("unchecked")
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.response = (Map<String, Object>) attributes.get(KEY_RESPONSE);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return (String) response.get(KEY_ID);
    }

    @Override
    public String getProvider() {
        return PROVIDER_NAVER;
    }

    @Override
    public String getEmail() {
        return (String) response.get(KEY_EMAIL);
    }

    @Override
    public String getName() {
        return (String) response.get(KEY_NAME);
    }
}
