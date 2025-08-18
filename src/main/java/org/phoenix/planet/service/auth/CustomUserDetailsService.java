package org.phoenix.planet.service.auth;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.auth.PrincipalDetails;
import org.phoenix.planet.dto.member.raw.Member;
import org.phoenix.planet.mapper.MemberMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberMapper.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new PrincipalDetails(member);
    }
}
