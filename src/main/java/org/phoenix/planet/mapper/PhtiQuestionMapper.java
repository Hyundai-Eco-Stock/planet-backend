package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.phti.response.PhtiQuestionWithChoicesResponse;

@Mapper
public interface PhtiQuestionMapper {

    List<PhtiQuestionWithChoicesResponse> selectQuestionWithChoices();
}
