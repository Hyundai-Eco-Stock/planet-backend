package org.phoenix.planet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedScheduled {
    String lockKey(); // Redis 락 키

    long expireSeconds() default 50; // 락 만료 시간
}
