package com.example.fivegame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AllController { @GetMapping("/login")
  public String login(){
        return "webSocketText";
    }
}
