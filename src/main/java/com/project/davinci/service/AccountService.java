package com.project.davinci.service;

import com.project.davinci.domain.Account;
import com.project.davinci.persistence.AccountDAO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AccountService {
    @Resource
    private AccountDAO accountDAO;

    public Account checkLogin(String mobile) {
        Account account = accountDAO.getAccountByMobile(mobile);
        return account;
    }
    public int editAvatar(Account account){
        return accountDAO.updateAvatarById(account);
    }

    public int registerAccount(Account account){
        return accountDAO.insertAccount(account);
    }
}

