package com.project.davinci.controller;

import com.project.davinci.domain.Account;
import com.project.davinci.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin/account")
public class AdminAccountController {


    @Resource
    private AccountService accountService;

    @GetMapping()
    public String view(Model model){
        List<Account> accountList = accountService.getAccountList();
        model.addAttribute("accountList",accountList);
        return "manage/users_manage";
    }

    @RequestMapping(value = "/ban", method = RequestMethod.POST)
    public String banAccount(@RequestBody Map<String, String> map, HttpSession session) {
       int id = Integer.valueOf(map.get("userId"));
       return String.valueOf(accountService.banAccount(id));
    }

}
