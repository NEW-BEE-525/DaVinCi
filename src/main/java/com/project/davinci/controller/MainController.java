package com.project.davinci.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public String showAccount(){
        return "account/personal_center";
    }
}
