package org.phoenix.planet.dto.member.raw;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.phoenix.planet.constant.Role;
import org.phoenix.planet.constant.Sex;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private long id;

    private String email;

    private String name;

    private String profileUrl;

    @Setter
    private String pwdHash;

    private String provider;

    private String address;

    private String detailAddress;

    private Role role;

    private Sex sex;

    private Long point;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void updateProfile(String profileUrl) {

        this.profileUrl = profileUrl;
    }


}
