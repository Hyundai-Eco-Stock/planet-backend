package org.phoenix.planet.mapper;

import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.member.raw.Member;

@Mapper
public interface MemberMapper {

    Optional<Member> findById(@Param("memberId") long memberId);

    Optional<Member> findByEmail(@Param("email") String email);

    void insert(Member member);

    void updateProfileUrl(
        @Param("memberId") long memberId,
        @Param("profileUrl") String profileUrl
    );

    void updatePwdHash(
        @Param("memberId") long memberId,
        @Param("pwdHash") String pwdHash
    );
}
