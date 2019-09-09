package com.project.davinci.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.project.davinci.domain.Order;
import com.project.davinci.service.OrderService;
import com.project.davinci.utils.AlipayConfig;
import com.project.davinci.utils.OrderUtil;
import com.project.davinci.utils.QrCodeCreate;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@Controller
public class PayController {

    @Autowired
    private OrderService orderService;

    @PostMapping("fastadd/alipay.trade.pay")
    @ResponseBody
    public String pay(@RequestBody Map<String,String> request_param,
                      HttpSession session) {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradePrecreateRequest alipayRequest = new AlipayTradePrecreateRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        String out_trade_no=request_param.get("out_trade_no");
        String total_amount=request_param.get("total_amount");
        String subject=request_param.get("subject");
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":"+"\""+ out_trade_no+"\""+ "," +
                "    \"total_amount\":" +total_amount+ "," +
                "    \"subject\":"+ "\"" + subject +"\""+ "," +
                "    \"store_id\":\"NJ_001\"," +
                "    \"timeout_express\":\"90m\"}");//订单允许的最晚付款时间);

        String img_str=null;
        try {
            AlipayTradePrecreateResponse response = alipayClient.execute(alipayRequest);
            BufferedImage bufferedImage = QrCodeCreate.createQrCode(response.getQrCode());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            byte[] bytes = baos.toByteArray();
            img_str = new String(Base64.encodeBase64(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img_str;
    }


    @RequestMapping("/notify")
    @ResponseBody
    public void notify(HttpServletRequest request, HttpServletResponse response,HttpSession session) throws Exception{
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
       // boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
            System.out.println(trade_status);
            if (trade_status.equals("TRADE_SUCCESS")){
                Order order = orderService.findBySn(out_trade_no);
                order.setPayId(trade_no);
                order.setPayTime(LocalDateTime.now());
                order.setOrderStatus(OrderUtil.STATUS_PAY);
                orderService.updateWithOptimisticLocker(order);
                response.getWriter().write("success");
            }

    }
}
