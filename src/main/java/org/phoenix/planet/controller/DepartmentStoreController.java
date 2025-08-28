package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.department_store.response.DepartmentStoreListResponse;
import org.phoenix.planet.service.department_store.DepartmentStoreService;
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

}
