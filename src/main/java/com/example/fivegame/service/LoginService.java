package com.example.fivegame.service;

import com.example.fivegame.entity.User;
import com.example.fivegame.mapper.LoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author white matter
 */
@Service
public class LoginService {
    @Autowired
    private LoginMapper loginMapper;

    public boolean login(String u_id,String pass){
        User user = new User();
        user.setU_id(u_id);
        user.setU_pass(pass);
        if (loginMapper.login(user)!=0){
            return true;
        } else {
            return false;
        }
    }


}
