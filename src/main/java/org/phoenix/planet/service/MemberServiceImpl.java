package org.phoenix.planet.service;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.member.request.SignUpRequest;
import org.phoenix.planet.mapper.MemberMapper;
import org.phoenix.planet.util.file.S3FileUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;
    private final S3FileUtil s3FileUtil;

    @Override
    @Transactional
    public void signUp(long loginMemberId, SignUpRequest request, MultipartFile profileImage) {

        // 패스워드 해시화
        String pwdHash = passwordEncoder.encode(request.password());

        // 프로필 저장
        if (profileImage != null && !profileImage.isEmpty()) {
            String savedFilePath = s3FileUtil.uploadMemberProfile(profileImage, loginMemberId);
            memberMapper.updateProfileUrl(loginMemberId, savedFilePath);
        }

        // 멤버 정보 수정
        memberMapper.updatePwdHash(loginMemberId, pwdHash);
    }
}
