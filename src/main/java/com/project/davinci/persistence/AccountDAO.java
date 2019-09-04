package com.project.davinci.persistence;

import com.project.davinci.domain.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountDAO {
    Account getAccountByMobile(String mobile);

    int updateAvatarById(Account account);

    int insertAccount(Account account);
}

//    Account getAccountByUsernameAndPassword(Account account);
//
//    void insertAccount(Account account);
//
//    void insertProfile(Account account);
//
//    void insertSignon(Account account);
//
//    void updateAccount(Account account);
//
//    void updateProfile(Account account);
//
//    void updateSignon(Account account);
