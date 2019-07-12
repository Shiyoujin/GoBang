package com.example.fivegame.mapper;

import com.example.fivegame.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @author white matter
 */
@Mapper
@Component
public interface RegisterMapper {

    /**
     * 检验之前是否注册过
     * @param user
     * @return int 最里面那个select u_id 不能查询到多条数据，不然返回就不确定，就会报错
     */
    @Select("select ifnull((select u_id from user where u_id =#{u_id}),0)")
    int isExistRegiter(User user);

    /**
     * 注册
     * @param user
     * @return 插入成功返回1，插入失败返回0
     */
    @Insert("insert into user (u_id,u_name,u_pass) value (#{u_id},#{u_name},#{u_pass})")
    int register(User user);
}
