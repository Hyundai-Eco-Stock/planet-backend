package org.phoenix.planet.service.member;

import org.phoenix.planet.dto.member.request.SignUpRequest;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    void signUp(long loginMemberId, SignUpRequest request, MultipartFile profileImage);
}
