package com.example.auth.security.oauth2.user;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private static final String KEY_KAKAO_ACCOUNT = "kakao_account";
    private static final String KEY_PROFILE = "profile";
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NICKNAME = "nickname";
    private static final String PROVIDER_KAKAO = "kakao";

    private Map<String, Object> attributes;
    private Map<String, Object> attributesAccount;
    private Map<String, Object> attributesProfile;

    @SuppressWarnings("unchecked")
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesAccount = (Map<String, Object>) attributes.get(KEY_KAKAO_ACCOUNT);
        this.attributesProfile = (Map<String, Object>) attributesAccount.get(KEY_PROFILE);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get(KEY_ID));
    }

    @Override
    public String getProvider() {
        return PROVIDER_KAKAO;
    }

    @Override
    public String getEmail() {
        return (String) attributesAccount.get(KEY_EMAIL);
    }

    @Override
    public String getName() {
        return (String) attributesProfile.get(KEY_NICKNAME);
    }
}
