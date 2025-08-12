package org.jeayoung.template.planetbackend.service;

import org.jeayoung.template.planetbackend.dto.member.request.SignUpRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

    void signUp(long loginMemberId, SignUpRequest request, MultipartFile profileImage);
}
