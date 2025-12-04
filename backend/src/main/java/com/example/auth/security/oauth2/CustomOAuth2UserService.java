package com.example.auth.security.oauth2;

import com.example.auth.domain.user.User;
import com.example.auth.domain.user.UserRepository;
import com.example.auth.security.oauth2.user.KakaoOAuth2UserInfo;
import com.example.auth.security.oauth2.user.NaverOAuth2UserInfo;
import com.example.auth.security.oauth2.user.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final String KAKAO = "kakao";
    private final String NAVER = "naver";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = loadUserDelegate(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo;

        if (registrationId.equals(KAKAO)) {
            oAuth2UserInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals(NAVER)) {
            oAuth2UserInfo = new NaverOAuth2UserInfo(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        User user = saveOrUpdate(oAuth2UserInfo);

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(OAuth2UserInfo oAuth2UserInfo) {
        Optional<User> userOptional = userRepository.findByUsername(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update logic if needed
        } else {
            user = User.builder()
                    .username(oAuth2UserInfo.getEmail()) // Use email as username for OAuth
                    .email(oAuth2UserInfo.getEmail())
                    .password(UUID.randomUUID().toString()) // Random password
                    .build();
            userRepository.save(user);
        }
        return user;
    }

    protected OAuth2User loadUserDelegate(OAuth2UserRequest userRequest) {
        return super.loadUser(userRequest);
    }
}
