package com.example.fivegame.controller;

import com.example.fivegame.entity.User;
import com.example.fivegame.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author white matter
 */
@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String u_id,String u_pass){
        User user = new User();
        user.setU_id(u_id);
        user.setU_pass(u_pass);
        if (loginService.login(u_id,u_pass)){
            return "webSocketTest";
        }else {
            return "login";
        }
    }
}
