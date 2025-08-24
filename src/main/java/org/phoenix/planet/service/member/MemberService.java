package org.phoenix.planet.service.member;

import java.util.List;
import org.phoenix.planet.dto.member.request.SignUpRequest;
import org.phoenix.planet.dto.member.response.MemberListResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    void signUp(long loginMemberId, SignUpRequest request, MultipartFile profileImage);

    List<MemberListResponse> searchAllMembers();

    void updatePassword(long memberId, String password);
}
