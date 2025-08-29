package org.phoenix.planet.service.car;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.car.request.CarRegisterRequest;
import org.phoenix.planet.dto.car.response.MemberCarResponse;
import org.phoenix.planet.mapper.MemberCarMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberMemberCarServiceImpl implements MemberCarService {

    private final MemberCarMapper memberCarMapper;


    @Override
    public MemberCarResponse searchByMemberId(long memberId) {

        return memberCarMapper.selectByMemberId(memberId)
            .orElseThrow(() -> new IllegalArgumentException("memberId에 해당하는 차량 정보가 없습니다."));
    }

    @Override
    public MemberCarResponse searchByCarNumber(String carNumber) {

        return memberCarMapper.selectByCarNumber(carNumber)
            .orElseThrow(() -> new IllegalArgumentException("carNumber에 해당하는 차량 정보가 없습니다."));
    }

    @Override
    public void registerCar(long memberId, CarRegisterRequest carRegisterRequest) {

        memberCarMapper.insert(
            memberId,
            carRegisterRequest.carNumber());
    }

}
