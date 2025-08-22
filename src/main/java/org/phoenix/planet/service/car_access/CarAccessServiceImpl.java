package org.phoenix.planet.service.car_access;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.car_access.request.CarEnterRequest;
import org.phoenix.planet.dto.car_access.request.CarExitRequest;
import org.phoenix.planet.dto.car_access.response.CarAccessHistoryResponse;
import org.phoenix.planet.mapper.CarAccessMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarAccessServiceImpl implements CarAccessService {

    private final CarAccessMapper carAccessMapper;

    @Override
    public void processEnterCar(CarEnterRequest carEnterRequest) {

        carAccessMapper.insertEnterCar(carEnterRequest.carNumber());
    }

    @Override
    public void processExitCar(CarExitRequest carExitRequest) {

        carAccessMapper.insertExitCar(carExitRequest.carNumber());

    }

    @Override
    public List<CarAccessHistoryResponse> searchCarAccessHistories() {

        return carAccessMapper.selectCarAccessHistories();
    }
}
