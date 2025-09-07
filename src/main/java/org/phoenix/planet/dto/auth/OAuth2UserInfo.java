package org.phoenix.planet.dto.auth;

import java.util.Map;
import lombok.Builder;
import org.phoenix.planet.constant.auth.AuthenticationError;
import org.phoenix.planet.constant.member.Role;
import org.phoenix.planet.dto.member.raw.Member;
import org.phoenix.planet.error.auth.AuthException;

@Builder
public record OAuth2UserInfo(
    String name,
    String email,
    String profile,
    String provider
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
            .provider("GOOGLE")
            .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {

        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
            .name((String) profile.get("nickname"))
            .email((String) account.get("email"))
            .profile((String) profile.get("profile_image_url"))
            .provider("KAKAO")
            .build();
    }

    public Member toDto() {

        return Member.builder()
            .email(email)
            .profileUrl(profile)
            .name(name)
            .role(Role.USER)
            .provider(provider)
            .build();
    }
}
