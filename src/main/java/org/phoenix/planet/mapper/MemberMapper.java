package org.phoenix.planet.mapper;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.constant.Sex;
import org.phoenix.planet.dto.member.raw.Member;
import org.phoenix.planet.dto.member.response.MemberListResponse;
import org.phoenix.planet.dto.member.response.MyEcoDealResponse;
import org.phoenix.planet.dto.member.response.MyOrderResponse;
import org.phoenix.planet.dto.member.response.MyRaffleResponse;

@Mapper
public interface MemberMapper {

    Optional<Member> findById(@Param("memberId") long memberId);

    Optional<Member> findByEmail(@Param("email") String email);

    List<MemberListResponse> findAll();

    void insert(Member member);

    void updateProfileUrl(
        @Param("memberId") long memberId,
        @Param("profileUrl") String profileUrl
    );

    void update(
        @Param("memberId") long memberId,
        @Param("pwdHash") String pwdHash,
        @Param("sex") Sex sex,
        @Param("birth") String birth,
        @Param("address") String address,
        @Param("detailAddress") String detailAddress);

    int deductPointsByMemberId(@Param("memberId") Long memberId, @Param("points") int points);

    void updatePassword(
        @Param("memberId") long memberId,
        @Param("pwdHash") String pwdHash);

    void updateProfile(
        @Param("memberId") long memberId,
        @Param("sex") Sex sex,
        @Param("birth") String birth,
        @Param("address") String address,
        @Param("detailAddress") String detailAddress);


    List<MyOrderResponse> findMyOrders(Long memberId);

    List<MyEcoDealResponse> reservedEcoDeal(Long memberId);

    List<MyRaffleResponse> getMyRaffles(Long memberId);

    int restorePointsByMemberId(@Param("memberId") Long memberId, @Param("points") int points);

    List<Long> selectUnsurveyPhtiMemberList();
}
