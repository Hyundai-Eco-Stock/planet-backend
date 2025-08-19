package org.phoenix.planet.dto.department_store.response;

public record DepartmentStoreListResponse(
    Long departmentStoreId,
    String name,
    String lat,
    String lng
) {

}