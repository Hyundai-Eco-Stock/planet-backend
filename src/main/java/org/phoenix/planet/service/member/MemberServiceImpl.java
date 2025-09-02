package org.phoenix.planet.service.member;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.member.raw.Member;
import org.phoenix.planet.dto.member.request.ProfileUpdateRequest;
import org.phoenix.planet.dto.member.request.SignUpRequest;
import org.phoenix.planet.dto.member.response.MemberListResponse;
import org.phoenix.planet.dto.member.response.MemberProfileResponse;
import org.phoenix.planet.dto.member.response.MyEcoDealResponse;
import org.phoenix.planet.dto.member.response.MyOrderResponse;
import org.phoenix.planet.dto.member.response.MyRaffleResponse;
import org.phoenix.planet.dto.member.response.SignUpResponse;
import org.phoenix.planet.mapper.MemberMapper;
import org.phoenix.planet.util.file.CloudFrontFileUtil;
import org.phoenix.planet.util.file.S3FileUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    private final CloudFrontFileUtil cloudFrontFileUtil;
    private final PasswordEncoder passwordEncoder;
    private final S3FileUtil s3FileUtil;


    @Override
    public MemberProfileResponse searchProfile(long memberId) {

        Member member = memberMapper.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return MemberProfileResponse.builder()
            .email(member.getEmail())
            .name(member.getName())
            .sex(member.getSex())
            .birth(member.getBirth())
            .profileUrl(
                member.getProfileUrl() != null ?
                    member.getProfileUrl().startsWith("http") ?
                        member.getProfileUrl()
                        : cloudFrontFileUtil.generateUrl(member.getProfileUrl())
                    : null)
            .address(member.getAddress())
            .detailAddress(member.getDetailAddress())
            .build();
    }

    @Override
    @Transactional
    public void updateMemberInfo(
        long loginMemberId,
        ProfileUpdateRequest request,
        MultipartFile profileImageFile) {

        Member member = memberMapper.findById(loginMemberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 member id를 가진 멤버가 없습니다."));

        if (!passwordEncoder.matches(request.oldPassword(), member.getPwdHash())) {
            throw new IllegalArgumentException("틀린 비밀번호 입니다.");
        }

        memberMapper.updateProfile(
            loginMemberId,
            request.sex(),
            request.birth(),
            request.address(),
            request.detailAddress());

        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            String profileFilePath = s3FileUtil.uploadMemberProfile(profileImageFile,
                loginMemberId);
            memberMapper.updateProfileUrl(loginMemberId, profileFilePath);
        }
    }

    @Override
    @Transactional
    public SignUpResponse signUp(
        long loginMemberId,
        SignUpRequest request,
        MultipartFile profileImage) {

        // 패스워드 해시화
        String pwdHash = passwordEncoder.encode(request.password());

        // 프로필 저장
        String savedFilePath = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            savedFilePath = s3FileUtil.uploadMemberProfile(profileImage, loginMemberId);
            memberMapper.updateProfileUrl(loginMemberId, savedFilePath);
        }

        // 멤버 정보 수정
        memberMapper.update(
            loginMemberId,
            pwdHash,
            request.sex(),
            request.birth(),
            request.address(),
            request.detailAddress()
        );

        return SignUpResponse.builder()
            .profileUrl(
                (savedFilePath != null) ?
                    cloudFrontFileUtil.generateUrl(savedFilePath) : null)
            .build();
    }

    @Override
    public List<MemberListResponse> searchAllMembers() {

        return memberMapper.findAll();
    }

    @Override
    public void updatePassword(long memberId, String password) {

        String pwdHash = passwordEncoder.encode(password);
        memberMapper.updatePassword(memberId, pwdHash);
    }

    /* 마이페이지 - 구매내역 */
    public List<MyOrderResponse> getMyOrders(Long memberId) {

        return memberMapper.findMyOrders(memberId);
    }

    /* 마이페이지 - 에코딜 */
    public List<MyEcoDealResponse> getMyEcoDeals(Long memberId) {

        List<MyEcoDealResponse> myEcoDealResponseList = memberMapper.reservedEcoDeal(memberId);
        myEcoDealResponseList.forEach(myEcoDealResponse -> {
            String filePath = myEcoDealResponse.getEcoDealQrUrl();
            String fileUrl = cloudFrontFileUtil.generateSignedUrl(filePath, 24 * 60 * 60);
            myEcoDealResponse.setEcoDealQrUrl(fileUrl);
        });
        return memberMapper.reservedEcoDeal(memberId);
    }

    /* 마이페이지 - 래플 응모내역 */
    public List<MyRaffleResponse> getMyRaffles(Long memberId) {

        return memberMapper.getMyRaffles(memberId);
    }
}
