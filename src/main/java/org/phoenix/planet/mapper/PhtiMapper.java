package org.phoenix.planet.mapper;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.phti.raw.Phti;

@Mapper
public interface PhtiMapper {

    Optional<Phti> selectByName(@Param("phtiName") String phtiName);

    List<Phti> selectAll();

}
