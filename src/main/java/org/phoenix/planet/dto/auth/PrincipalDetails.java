package org.phoenix.planet.dto.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.phoenix.planet.dto.member.raw.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public record PrincipalDetails(
    Member member,
    Map<String, Object> attributes,
    String attributeKey
) implements OAuth2User, UserDetails {

    public PrincipalDetails(Member member) {
        this(member, null, null);
    }

    @Override
    public String getName() {
        if (attributes == null) {
            return member.getEmail();
        }
        return attributes.get(attributeKey).toString();
    }

    @Override
    public Map<String, Object> getAttributes() {

        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Collections.singletonList(
            new SimpleGrantedAuthority(member.getRole().getValue()));
    }

    @Override
    public String getPassword() {

        return member.getPwdHash();
    }

    @Override
    public String getUsername() {

        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }
}