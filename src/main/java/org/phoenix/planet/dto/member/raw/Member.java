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
    @Setter
    private String profileUrl;
    @Setter
    private String pwdHash;
    private String provider;
    private String birth;
    private String address;
    private String detailAddress;
    private String zipCode;
    private Role role;
    private Sex sex;
    private Long point;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
