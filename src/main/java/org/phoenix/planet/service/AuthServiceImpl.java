package org.phoenix.planet.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.member.request.SignUpRequest;
import org.phoenix.planet.mapper.MemberMapper;
import org.phoenix.planet.provider.TokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;
    private final TokenProvider tokenProvider;

    @Override
    public void signUp(long loginMemberId, SignUpRequest request,
        MultipartFile profileImage) {

        // 패스워드 해시화
        String pwdHash = passwordEncoder.encode(request.password());

        // 프로필 저장
        String profileUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String ext = getExtension(profileImage.getOriginalFilename());
                String storedName = "profile-" + UUID.randomUUID() + (ext != null ? "." + ext : "");
                Path uploadDir = Path.of("uploads/profile");
                Files.createDirectories(uploadDir);
                Path target = uploadDir.resolve(storedName);
                profileImage.transferTo(target.toFile());
                profileUrl = "/static/profile/" + storedName; // Nginx/스프링 정적경로에 매핑해 사용
            } catch (Exception e) {
                throw new RuntimeException("프로필 업로드 실패", e);
            }
        }

        // 멤버 정보 수정
        memberMapper.updateProfileUrl(loginMemberId, profileUrl);
        memberMapper.updatePwdHash(loginMemberId, pwdHash);
    }

    private String getExtension(String filename) {

        if (filename == null) {
            return null;
        }
        int idx = filename.lastIndexOf('.');
        return idx > -1 ? filename.substring(idx + 1) : null;
    }
}
