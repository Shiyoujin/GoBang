package com.example.fivegame.mapper;


import com.example.fivegame.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author white matter
 */
@Mapper
@Component
public interface LoginMapper {
    /**
     * 登录
     * @param user
     * @return
     */
    @Select("select ifnull((select u_id from user where u_id =#{u_id} and u_pass=#{u_pass}),0)")
    int login(User user);




}
