package com.example.fivegame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 我们都知道需要通过控制器跳转才能访问到templates下的html文件
 * 因为springboot默认访问static,resources,public这些文件夹下的文件，而没有默认访问templates下的
 * 我们需要在配置加上
 *
 * spring.resources.static-locations=classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/,classpath:/public/,classpath:/templates/
 *
 * 就可以直接访问了
 * ---------------------
 */
@Controller
public class AllController {

    //运用控制器跳转到html页面
    @GetMapping("/login")
    public String login() {
        return "webSocketTest";
    }
}
