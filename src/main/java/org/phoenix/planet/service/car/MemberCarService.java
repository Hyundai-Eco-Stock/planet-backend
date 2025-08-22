package org.phoenix.planet.service.car;

import org.phoenix.planet.dto.car.request.CarRegisterRequest;
import org.phoenix.planet.dto.car.response.MemberCarResponse;

public interface MemberCarService {

    MemberCarResponse searchByMemberId(long memberId);

    MemberCarResponse searchByCarNumber(String carNumber);

    void registerCar(long memberId, CarRegisterRequest carRegisterRequest);

}
