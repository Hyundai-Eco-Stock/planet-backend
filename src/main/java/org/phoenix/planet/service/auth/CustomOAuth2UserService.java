package org.phoenix.planet.service.auth;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.auth.OAuth2UserInfo;
import org.phoenix.planet.dto.auth.PrincipalDetails;
import org.phoenix.planet.dto.member.raw.Member;
import org.phoenix.planet.mapper.MemberMapper;
import org.phoenix.planet.util.file.CloudFrontFileUtil;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final CloudFrontFileUtil cloudFrontFileUtil;
    private final MemberMapper memberMapper;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // 2. resistrationId 가져오기 (third-party id)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registrationId: {}", registrationId);
        // 3. userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
            .getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

        // 5. 회원가입 및 로그인
        Optional<Member> memberOpt = memberMapper.findByEmail(oAuth2UserInfo.email());
        Member member;
        if (memberOpt.isPresent()) {
            log.info("멤버 있음");
            member = memberOpt.get();
            if (member.getProfileUrl() != null && !member.getProfileUrl().startsWith("http")) {
                String profileUrl = cloudFrontFileUtil.generateUrl(member.getProfileUrl());
                member.setProfileUrl(profileUrl);
            }
        } else {
            log.info("멤버 없음");
            member = oAuth2UserInfo.toDto();
            memberMapper.insert(member);
        }

        // 6. OAuth2User로 반환
        return new PrincipalDetails(member, oAuth2UserAttributes, userNameAttributeName);
    }

}
