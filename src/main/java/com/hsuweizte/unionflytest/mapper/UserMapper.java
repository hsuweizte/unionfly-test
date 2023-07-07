package com.hsuweizte.unionflytest.mapper;

import com.hsuweizte.unionflytest.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("Select * FROM t_user where username= #{username}and password = #{password}")
    User findUserbyUsernameandPassword(String username, String password);

    @Select("Select * FROM t_user where username= #{username}")
    User findByUsername(String username);

    @Select("Select email From t_user where username= #{username}")
    Boolean existByUsername(String username);

    @Select("Select * FROM t_user where email = #{email}")
    Boolean existByEmail(String email);

    @Insert("INSERT INTO t_user (`email`,`username`,`password`) " +
            "value( #{email},#{username},#{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public void save(User user);

}
