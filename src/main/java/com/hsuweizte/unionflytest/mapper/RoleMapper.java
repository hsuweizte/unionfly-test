package com.hsuweizte.unionflytest.mapper;

import com.hsuweizte.unionflytest.pojo.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper {
    @Select("SELECT * FROM t_roles WHERE name = #{name}")
    public Role getByName(String name);
}
