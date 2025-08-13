package org.phoenix.planet.service;

import org.phoenix.planet.dto.member.request.SignUpRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

    void signUp(long loginMemberId, SignUpRequest request, MultipartFile profileImage);
}
