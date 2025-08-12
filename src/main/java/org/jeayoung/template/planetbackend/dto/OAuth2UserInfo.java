package org.jeayoung.template.planetbackend.dto;

import java.util.Map;
import lombok.Builder;
import org.jeayoung.template.planetbackend.constant.AuthenticationError;
import org.jeayoung.template.planetbackend.constant.Role;
import org.jeayoung.template.planetbackend.entity.Member;
import org.jeayoung.template.planetbackend.error.AuthException;

@Builder
public record OAuth2UserInfo(
    String name,
    String email,
    String profile
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {

        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new AuthException(AuthenticationError.ILLEGAL_REGISTRATION_ID);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {

        return OAuth2UserInfo.builder()
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .profile((String) attributes.get("picture"))
            .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {

        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
            .name((String) profile.get("nickname"))
            .email((String) account.get("email"))
            .profile((String) profile.get("profile_image_url"))
            .build();
    }

    public Member toEntity() {

        return Member.builder()
            .email(email)
            .profile(profile)
            .name(name)
            .role(Role.USER)
            .build();
    }
}
