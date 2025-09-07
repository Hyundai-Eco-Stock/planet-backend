package org.phoenix.planet.configuration.mybatis;

import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.MappedTypes;
import org.phoenix.planet.constant.member.Role;

@MappedTypes(Role.class)
public class RoleTypeHandler extends EnumTypeHandler<Role> {

    public RoleTypeHandler() {

        super(Role.class);
    }
}