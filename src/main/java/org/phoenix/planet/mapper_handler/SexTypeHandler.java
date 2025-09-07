package org.phoenix.planet.mapper_handler;

import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.MappedTypes;
import org.phoenix.planet.constant.member.Sex;

@MappedTypes(Sex.class)
public class SexTypeHandler extends EnumTypeHandler<Sex> {

    public SexTypeHandler() {

        super(Sex.class);
    }
}