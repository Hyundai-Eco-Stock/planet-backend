package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.department_store.response.DepartmentStoreListResponse;
import org.phoenix.planet.dto.department_store.response.OfflineShopListResponse;
import org.phoenix.planet.service.department_store.DepartmentStoreService;
import org.phoenix.planet.service.offline.OfflineShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/department-stores")
public class DepartmentStoreController {

    private final DepartmentStoreService departmentStoreService;
    private final OfflineShopService offlineShopService;

    @GetMapping
    public ResponseEntity<List<DepartmentStoreListResponse>> searchAllDepartmentStores() {

        List<DepartmentStoreListResponse> departmentStoreList =
            departmentStoreService.searchAllDepartmentStoreList();
        return ResponseEntity.ok(departmentStoreList);
    }

    @GetMapping("/shops")
    public ResponseEntity<List<OfflineShopListResponse>> searchAllDepartmentStoreShops() {

        List<OfflineShopListResponse> shopList =
            offlineShopService.searchAll();
        return ResponseEntity.ok(shopList);
    }
}
