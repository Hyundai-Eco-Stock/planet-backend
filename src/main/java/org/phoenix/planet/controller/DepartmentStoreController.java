package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.department_store.response.DepartmentStoreListResponse;
import org.phoenix.planet.dto.offline.response.OfflineProductListResponse;
import org.phoenix.planet.dto.offline.response.OfflineShopListResponse;
import org.phoenix.planet.service.department_store.DepartmentStoreService;
import org.phoenix.planet.service.offline.OfflineProductService;
import org.phoenix.planet.service.offline.OfflineShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/department-stores")
public class DepartmentStoreController {

    private final OfflineShopService offlineShopService;
    private final OfflineProductService offlineProductService;
    private final DepartmentStoreService departmentStoreService;

    /**
     * 에코딜 화면에서 사용할 예정
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<List<DepartmentStoreListResponse>> searchAllDepartmentStores() {

        List<DepartmentStoreListResponse> departmentStoreList =
            departmentStoreService.searchAllDepartmentStoreList();
        return ResponseEntity.ok(departmentStoreList);
    }


    /**
     * 가상의 포스기에서 사용
     *
     * @return
     */
    @GetMapping("/shops")
    public ResponseEntity<List<OfflineShopListResponse>> searchAllDepartmentStoreShops() {

        List<OfflineShopListResponse> shopList = offlineShopService.searchAll();
        return ResponseEntity.ok(shopList);
    }

    /**
     * 가상의 포스기에서 사용
     *
     * @param shopId
     * @return
     */
    @GetMapping("/shops/{shop-id}/products")
    public ResponseEntity<List<OfflineProductListResponse>> searchAllDepartmentStoreShopProducts(
        @PathVariable("shop-id") long shopId
    ) {

        List<OfflineProductListResponse> shopList = offlineProductService.searchAllByShopId(shopId);
        return ResponseEntity.ok(shopList);
    }
}
