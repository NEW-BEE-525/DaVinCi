package com.project.davinci.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.project.davinci.domain.Account;
import com.project.davinci.service.AccountService;
import com.project.davinci.utils.CCPRestSDK;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@CrossOrigin(origins= {"http://localhost:8080","null"})
@Controller
public class AccountController {

    @Resource
    private AccountService accountService;


    @RequestMapping("/")
    public String view(){
        return "main/index";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public String loginPage(@RequestBody Map<String, String> map, HttpServletResponse response, HttpSession session) {
        Account account = accountService.checkLogin(map.get("username"));
        if (account == null) {
            return "0";
        }
        else {
            if (account.getPassword().equals(map.get("password"))) {
                session.setAttribute("Account",account);
                return "1";//登录成功
            } else {
                return "-1";//用户名或密码错误
            }
        }
    }

    @RequestMapping("register")
    public String registerForm(){
        return "register";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public String registerPage(@RequestBody Map<String, String> map, HttpServletResponse response, HttpSession session) {
        String sessionVerification = (String)session.getAttribute("verification");
        String sessionMobile = (String)session.getAttribute("mobile");
        if (sessionVerification.equals(map.get("verification"))&&sessionMobile.equals(map.get("mobile"))) {
            session.removeAttribute("verification");
            session.removeAttribute("mobile");
            Account account = new Account();
            account.setUsername(map.get("mobile"));
            account.setPassword(map.get("password"));
            account.setGender(0);
            account.setLast_login_ip("0:0:0:0:0:0:0:1");
            account.setMobile(map.get("mobile"));
            account.setAvatar("D:\\java\\新建文件夹\\DaVinCi\\src\\main\\resources\\static\\upload");
            account.setStatus(0);
            Date date = new Date();
            Timestamp nousedate = new Timestamp(date.getTime());
            account.setAdd_time(nousedate);
            account.setUpdate_time(nousedate);
            if (accountService.registerAccount(account)==1)
                return "1";//注册成功
            else
                return "-1";//该手机号已绑定账号
        }
        return "0";//验证码错误
    }


    @RequestMapping(value = "sendVerificat", method = RequestMethod.POST)
    @ResponseBody
    public String sendVerificatPage(@RequestBody Map<String, String> map, HttpServletResponse response, HttpSession session) {
        String verification = String.valueOf((int)((Math.random()*9+1)*100000));
        String mobile = map.get("mobile");
        session.setAttribute("verification",verification);
        session.setAttribute("mobile",mobile);
        session.setMaxInactiveInterval(600);
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
            return "2";//验证码发送成功
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
        }
        return  "0";//验证码发送失败
    }


    @PostMapping(value="upload")
    @ResponseBody
    public String upload(HttpServletRequest request,
                         HttpSession session,
                         @RequestParam("file") MultipartFile file) throws Exception{
        // 接收参数description
        if(!file.isEmpty()){
            // 上传文件路径
            String path = "D:\\java\\新建文件夹\\DaVinCi\\src\\main\\resources\\static\\upload";
//            String path = request.getServletContext().getRealPath(
//                    "/upload/");
            System.out.println("path = " + path);
            // 上传文件名
            String filename = file.getOriginalFilename();
            File filepath = new File(path,filename);
            // 判断路径是否存在，如果不存在就创建一个
            if (!filepath.getParentFile().exists()) {
                filepath.getParentFile().mkdirs();
            }
            // 将上传文件保存到一个目标文件当中
            file.transferTo(new File(path+File.separator+ filename));
            Account account = (Account) session.getAttribute("Account");
//            Account account = new Account();
//            account.setAvatar(path);
//            account.setId(1);
            if (accountService.editAvatar(account)==1){
                return "success";
            }
            else
                return "error";
        }else{
            return "error";
        }

    }
    public static void main(String[] args) {
        String verification = String.valueOf((int)((Math.random()*9+1)*100000));

        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount("8a216da86cdb6950016ce1aedecb04ab", "fe3eb9071778413da9d181cb6b9739f7");// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId("8a216da86cdb6950016ce1aedf1b04b1");// 初始化应用ID
        result = restAPI.sendTemplateSMS("18890099801","1" ,new String[]{verification,"10"});

        System.out.println("SDKTestSendTemplateSMS result=" + result);

        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }

}
