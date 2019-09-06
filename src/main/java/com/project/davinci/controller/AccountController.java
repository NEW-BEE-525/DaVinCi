package com.project.davinci.controller;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.project.davinci.domain.Account;
import com.project.davinci.domain.Student;
import com.project.davinci.service.AccountService;
import com.project.davinci.utils.CCPRestSDK;
import com.project.davinci.utils.utils.encoder.CharacterEncoder;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.io.*;


@Controller
public class AccountController {

    @Resource
    private AccountService accountService;


    @GetMapping("/")
    public String view(){
        return "main/index";
    }

    @GetMapping("/login")
    public String viewLogin(){
        return "account/login";
    }

    @GetMapping("/register")
    public String viewRegister(){
        return "account/register";
    }

    @GetMapping("/showPersonalCenter")
    public String viewCenter(Model model){
        model.addAttribute("title","个人中心");
        return "account/account_fragments";
    }

    @GetMapping("/showAllOrders")
    public String viewAllOrders(Model model){
        model.addAttribute("title","我的订单");
        return "account/account_fragments";
    }

    @GetMapping("/showAllBills")
    public String viewAllBills(Model model){
        model.addAttribute("title","我的账单");
        return "account/account_fragments";
    }

    @GetMapping("/showSafetyCenter")
    public String viewCafetyCenter(Model model){
        model.addAttribute("title","安全中心");
        return "account/account_fragments";
    }

    @GetMapping("/showHelp")
    public String viewHelp(Model model){
        model.addAttribute("title","帮助");
        return "account/account_fragments";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("account");
        return "main/index";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public String loginPage(@RequestBody Map<String, String> map, HttpServletResponse response, HttpSession session) {
        Account account = accountService.checkLogin(map.get("userPhone"));
        if (account == null) {
            return "0";
        }
        else {
            if (account.getPassword().equals(map.get("password"))) {
                session.setAttribute("account",account);
                return "1";//登录成功
            } else {
                return "-1";//用户名或密码错误
            }
        }
    }


    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public String registerPage(@RequestBody Map<String, String> map, HttpServletResponse response, HttpSession session) {
        String sessionVerification = (String)session.getAttribute("verification");
        String sessionMobile = (String)session.getAttribute("mobile");
        String mobile = map.get("userPhone");
        String verification = map.get("phoneCheck");
        String passwored = map.get("password");
        if (sessionVerification.equals(verification)&&sessionMobile.equals(mobile)) {
            session.removeAttribute("verification");
            session.removeAttribute("mobile");
            if (accountService.registerAccount(mobile,passwored)==1)
                return "1";//注册成功
            else
                return "-1";//该手机号已绑定账号
        }
        return "0";//验证码错误
    }


    @RequestMapping(value = "sendVerificat", method = RequestMethod.POST)
    @ResponseBody
    public String sendVerificatPage1(@RequestBody Map<String, String> map, HttpServletResponse response, HttpSession session) {
        String verification = String.valueOf((int)((Math.random()*9+1)*100000));
        String mobile = map.get("userPhone");
        session.setAttribute("verification",verification);
        session.setAttribute("mobile",mobile);
        session.setMaxInactiveInterval(600);
        if(mobile!=null){
            if (accountService.sendVerificat(mobile,verification)==1){
                return "1";//验证码发送成功
            }
            else
                return "0";//验证码发送失败
        }
        else {
            return "2";
        }
    }


    @RequestMapping(value = "sendVerificat", method = RequestMethod.GET)
    @ResponseBody
    public String sendVerificatPage2(HttpSession session) {
        String verification = String.valueOf((int)((Math.random()*9+1)*100000));
        Account account=(Account) session.getAttribute("account");
        String mobile = account.getMobile();
        session.setAttribute("verification",verification);
        session.setAttribute("mobile",mobile);
        session.setMaxInactiveInterval(600);
        if (accountService.sendVerificat(mobile,verification)==1){
            return "1";//验证码发送成功
        }
        else
            return "0";//验证码发送失败
    }

    @PostMapping(value="upload",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String upload(HttpSession session, @RequestParam("name") String username,
                         @RequestParam("pic") MultipartFile file,
                         @RequestParam("gender")String sex_str,
                         @RequestParam("email") String emile,
                         @RequestParam("birthday") String birthady_str) throws Exception{
        // 接收参数description
        int sex = Integer.parseInt(sex_str);
        Account account = (Account) session.getAttribute("account");
        account.setUsername(username);
        account.setGender(sex);
        account.setEmail(emile);
        account.setBirthday(birthady_str);
        if(!file.isEmpty()){
            // 上传文件路径
            String pathName = "src/main/resources/static/upload/";//想要存储文件的地址
            String pname = file.getOriginalFilename();//获取文件名（包括后缀）
            pathName += pname;
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(pathName);
                fos.write(file.getBytes()); // 写入文件
                account.setAvatar("/upload/"+pname);
                System.out.println(account.getAvatar());
                session.setAttribute("account",account);
                accountService.editAvatar(account);
                return "1";
            } catch (Exception e) {
                e.printStackTrace();
                return "0";
            }
        }else{
            return "0";
        }

    }

    @RequestMapping(value = "judgeVerificat", method = RequestMethod.POST)
    @ResponseBody
    public String judgeVerificat(@RequestBody Map<String, String> map,HttpSession session){
        String sessionVerification = (String)session.getAttribute("verification");
        String sessionMobile = (String)session.getAttribute("mobile");
        Account account=(Account) session.getAttribute("account");
        String mobile;
        if(map.containsKey("userPhone")){
            mobile = map.get("userPhone");
        }else {
            mobile = account.getMobile();
        }
        String verification = map.get("verification");
        if (sessionVerification.equals(verification)&&sessionMobile.equals(mobile)) {
            session.removeAttribute("verification");
            session.removeAttribute("mobile");
            if(map.containsKey("userPhone")){
                account.setMobile(mobile);
                accountService.changeMobile(account);
            }
            return "1";
        }
        else {
            return "0";
        }
    }

    @GetMapping(value = "personalCenter")
    @ResponseBody
    public Account showPersonalCenter(HttpSession session) throws IOException {
        Account account=(Account) session.getAttribute("account");
        Account account1 = new Account();
        account1.setUsername(account.getUsername());
        account1.setGender(account.getGender());
        account1.setUser_level(account.getUser_level());
        account1.setEmail(account.getEmail());
        account1.setMobile(account.getMobile());
        account1.setAvatar(account.getAvatar());
        account1.setBirthday(account.getBirthday());
        String avatar = account1.getAvatar();
        if (avatar != null){
            avatar="src/main/resources/static"+avatar;
            File f = new File(avatar);
            BufferedImage bi;
            try {
                bi = ImageIO.read(f);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "jpg", baos);  //经测试转换的图片是格式这里就什么格式，否则会失真
                byte[] bytes = baos.toByteArray();
                String img_str = new String(Base64.encodeBase64(bytes));
                account1.setAvatar(img_str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return account1;
    }


    @GetMapping(value = "safetyCenter")
    @ResponseBody
    public Map<String,String> showSafetyCenter(HttpSession session) throws IOException {
        Account account=(Account) session.getAttribute("account");
        Student student = accountService.getStudentCertification(account.getId());
        Map<String,String> map = new HashMap<>();
        map.put("mobile",account.getMobile());
        if (student!=null){
            map.put("name",student.getName());
            map.put("studentNumber",student.getStudentNumber());
            map.put("school",student.getSchool());
        }
            return map;
    }

    @PostMapping(value = "studentCertification")
    @ResponseBody
    public int studentCertification(@RequestBody Map<String, String> map,HttpSession session){
        Account account=(Account) session.getAttribute("account");
        Student student = new Student();
        student.setUserId(account.getId());
        student.setStudentNumber(map.get("studentNumber"));
        student.setSchool(map.get("school"));
        student.setName(map.get("name"));
        return accountService.studentCertification(student);
    }

    @PostMapping(value = "changePassword")
    @ResponseBody
    public int changePassword(@RequestBody Map<String, String> map,HttpSession session){
        Account account=(Account) session.getAttribute("account");
        account.setPassword(map.get("password"));
        return accountService.changePassword(account);
    }


    @PostMapping(value = "changeMobile")
    @ResponseBody
    public int changeMobile(@RequestBody Map<String, String> map,HttpSession session){
        Account account=(Account) session.getAttribute("account");
        account.setMobile(map.get("mobile"));
        return accountService.changeMobile(account);
    }
    //测试
    public static void main(String[] args) throws IOException {

    }


}
