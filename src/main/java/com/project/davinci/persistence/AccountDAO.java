package com.project.davinci.persistence;

import com.project.davinci.domain.Account;
import com.project.davinci.domain.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountDAO {
    Account getAccountByMobile(String mobile);

    int updateAvatarById(Account account);

    int insertAccount(Account account);

    Account getAccountById(int id);

    int updateLoginTime(Account account);

    int insertSthdent(Student student);

    Student getStudentById(int id);

    int updateMobile(Account account);

    int upfatePassword(Account account);

    int updatBalance(Account account);

    List<Account> selectAllAccount();

    int banAccount(int id);
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
