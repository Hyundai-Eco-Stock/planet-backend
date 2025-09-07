package org.phoenix.planet.constant.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CancelStatus {

    Y,  // 취소됨
    N,   // 정상
    P   // 취소중

}
