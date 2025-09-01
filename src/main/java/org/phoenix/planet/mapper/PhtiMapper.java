package org.phoenix.planet.mapper;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.phti.raw.Phti;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str;

@Mapper
public interface PhtiMapper {

    Optional<Phti> selectByName(@Param("phtiName") Str phtiName);

    List<Phti> selectAll();

}
