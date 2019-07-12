package com.example.fivegame.service;

import com.example.fivegame.entity.User;
import com.example.fivegame.mapper.RegisterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author white matter
 */
@Service
public class RegisterService {
    @Autowired
    private RegisterMapper registerMapper;

    public boolean isExistRegister(String u_id){
        User user = new User();
        user.setU_id(u_id);

        if (registerMapper.isExistRegiter(user)!=0){
            System.out.println("已被注册");
            return false;
        }else {
            System.out.println("新用户");
            return true;
        }
    }

    public boolean register(String u_id,String u_name,String pass){
        User user = new User();
        if (isExistRegister(u_id)){
            user.setU_id(u_id);
            user.setU_name(u_name);
            user.setU_pass(pass);
            registerMapper.register(user);
            System.out.println("注册成功");
            return true;
        } else {
//            System.out.println(registerMapper.register(user));
            return false;
        }
    }

}
