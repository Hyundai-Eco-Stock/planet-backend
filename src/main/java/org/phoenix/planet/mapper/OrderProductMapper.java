package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.constant.order.CancelStatus;
import org.phoenix.planet.dto.order.raw.OrderProduct;

@Mapper
public interface OrderProductMapper {

    void insert(OrderProduct orderProduct);

    List<OrderProduct> findOrderProductsByOrderHistoryId(Long orderHistoryId);

    void updateAllOrderProductsCancelStatus(
        @Param("orderHistoryId") Long orderHistoryId,
        @Param("cancelStatus") CancelStatus cancelStatus
    );

    OrderProduct findByOrderProductId(Long orderProductId);

    void updateCancelStatus(
        @Param("orderProductId") Long orderProductId,
        @Param("cancelStatus") CancelStatus cancelStatus
    );

    List<OrderProduct> findActiveOrderProducts(Long orderHistoryId);

    void updateCancelStatusToProcessing(
        @Param("orderProductId") Long orderProductId,
        @Param("cancelStatus") CancelStatus cancelStatus
    );

}
