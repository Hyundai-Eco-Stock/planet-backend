package org.jeayoung.template.planetbackend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jeayoung.template.planetbackend.constant.AuthenticationError;
import org.jeayoung.template.planetbackend.dto.member.request.SignUpRequest;
import org.jeayoung.template.planetbackend.entity.Member;
import org.jeayoung.template.planetbackend.error.AuthException;
import org.jeayoung.template.planetbackend.provider.TokenProvider;
import org.jeayoung.template.planetbackend.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    // private final RedisService redisService; // refresh 블랙리스트/화이트리스트 운영 시

    @Override
    public void signUp(long loginMemberId, SignUpRequest request,
        MultipartFile profileImage) {

        // 1) 요청한 회원 정보 찾기
        Member member = memberRepository.findById(loginMemberId)
            .orElseThrow(() -> new AuthException(AuthenticationError.NOT_EXIST_MEMBER_ID));

        // 2) 패스워드 해시
        String pwdHash = passwordEncoder.encode(request.password());

        // 3) 프로필 저장 (예시: 로컬 디스크. 실제로는 S3/MinIO 등 사용 권장)
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

        // 4) 멤버 정보 수정
        member.updateProfile(profileUrl);
        member.setPwdHash(pwdHash);

        memberRepository.save(member);
    }

    private String getExtension(String filename) {

        if (filename == null) {
            return null;
        }
        int idx = filename.lastIndexOf('.');
        return idx > -1 ? filename.substring(idx + 1) : null;
    }
}
