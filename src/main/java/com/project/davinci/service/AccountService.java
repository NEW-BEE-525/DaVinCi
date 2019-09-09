package com.project.davinci.service;

import com.project.davinci.domain.Account;
import com.project.davinci.domain.Student;
import com.project.davinci.persistence.AccountDAO;
import com.project.davinci.utils.CCPRestSDK;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class AccountService {
    @Resource
    private AccountDAO accountDAO;

    public Account checkLogin(String mobile) {
        Account account = accountDAO.getAccountByMobile(mobile);
        Date date = new Date();
        Timestamp nousedate = new Timestamp(date.getTime());
        if (account != null) {
            account.setLast_login_time(nousedate);
            accountDAO.updateLoginTime(account);
        }
        return account;
    }

    public int editAvatar(Account account){
        return accountDAO.updateAvatarById(account);
    }

    public int registerAccount(String mobile,String password){
        Account account = new Account();
        account.setUsername(mobile);
        account.setPassword(password);
        account.setGender(0);
        account.setLast_login_ip("0:0:0:0:0:0:0:1");
        account.setMobile(mobile);
        BigDecimal balabce = new BigDecimal(5000.00);
        account.setBalance(balabce);
        account.setAvatar("/upload/t0170bebbfa858561f0.jpg");
        account.setStatus(0);
        Date date = new Date();
        Timestamp nousedate = new Timestamp(date.getTime());
        account.setAdd_time(nousedate);
        account.setUpdate_time(nousedate);
        return accountDAO.insertAccount(account);
    }

    public int sendVerificat(String mobile,String verification){
        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount("8a216da86cdb6950016ce1aedecb04ab", "fe3eb9071778413da9d181cb6b9739f7");// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId("8a216da86cdb6950016ce1aedf1b04b1");// 初始化应用ID
        result = restAPI.sendTemplateSMS(mobile, "1", new String[]{verification, "10"});

        System.out.println("SDKTestSendTemplateSMS result=" + result);

        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
            return 1;//验证码发送成功
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
        }
        return  0;//验证码发送失败
    }

    public Account getAccount(int id){
        return accountDAO.getAccountById(id);
    }

    public int studentCertification(Student student){
        return accountDAO.insertSthdent(student);
    }

    public Student getStudentCertification(int id){
        return accountDAO.getStudentById(id);
    }

    public int changeMobile(Account account){
        return accountDAO.updateMobile(account);
    }

    public int changePassword(Account account){
        return accountDAO.upfatePassword(account);
    }

    public int updatBalance(Account account){
        return accountDAO.updatBalance(account);
    }

    public List<Account> getAccountList(){
        return accountDAO.selectAllAccount();
    }

    public int banAccount(int id){
        return accountDAO.banAccount(id);
    }
}

