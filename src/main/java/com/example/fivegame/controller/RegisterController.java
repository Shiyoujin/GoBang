package com.example.fivegame.controller;

import com.example.fivegame.mapper.RegisterMapper;
import com.example.fivegame.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author white matter
 */
@Controller
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @RequestMapping (value = "/register",method = RequestMethod.POST)
    public String register(String u_id,String u_name,String u_pass){
        if (registerService.register(u_id,u_name,u_pass)){
            System.out.println("注册成功跳转到登录页面！");
            return "/signIn";
        }else {
            return "/signUp";
        }
    }
}
