package org.phoenix.planet.service.member;

import jakarta.validation.Valid;
import java.util.List;
import org.phoenix.planet.dto.member.request.KakaoSignUpRequest;
import org.phoenix.planet.dto.member.request.ProfileUpdateRequest;
import org.phoenix.planet.dto.member.response.MemberListResponse;
import org.phoenix.planet.dto.member.response.MemberProfileResponse;
import org.phoenix.planet.dto.member.response.MyEcoDealResponse;
import org.phoenix.planet.dto.member.response.MyOrderResponse;
import org.phoenix.planet.dto.member.response.MyRaffleResponse;
import org.phoenix.planet.dto.member.response.SignUpResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    SignUpResponse signUp(long loginMemberId, KakaoSignUpRequest request,
        MultipartFile profileImage);

    List<MemberListResponse> searchAllMembers();

    void updatePassword(long memberId, String password);

    MemberProfileResponse searchProfile(long loginMemberId);

    void updateMemberInfo(long loginMemberId, @Valid ProfileUpdateRequest profileUpdateRequest,
        MultipartFile profileImageFile);

    List<MyOrderResponse> getMyOrders(Long memberId);

    List<MyEcoDealResponse> getMyEcoDeals(Long memberId);

    List<MyRaffleResponse> getMyRaffles(Long memberId);
}
