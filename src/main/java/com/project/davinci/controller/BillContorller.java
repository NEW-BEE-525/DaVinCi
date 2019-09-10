package com.project.davinci.controller;

import com.project.davinci.domain.*;
import com.project.davinci.service.*;
import com.project.davinci.utils.OrderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BillContorller {

    @Resource
    private OrderService orderService;
    @Resource
    private OrderGoodsService orderGoodsService;
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private BillService billService;

    @RequestMapping(value = {"fastadd/balancePay","cartBuy/balancePay"}, method = RequestMethod.POST)
    @ResponseBody
    public String balancePay(@RequestBody Map<String, String> map, HttpSession session) {
        Account account = (Account) session.getAttribute("account");
        String password = map.get("password");
        if (account.getPassword().equals(password)) {
            String order_sn = map.get("order_id");
            int fq = Integer.valueOf(map.get("fq")) * 3;
            Order order = orderService.findBySn(order_sn);
            BigDecimal newPrice = new BigDecimal(0.00);
            newPrice = newPrice.add(order.getActualPrice());
            double rate = 0;
            switch (account.getUser_level()) {
                case 1:
                    rate = 0.004;
                    break;
                case 2:
                    rate = 0.005;
                    break;
                case 3:
                    rate = 0.006;
                    break;
                case 4:
                    rate = 0.007;
                    break;
                case 5:
                    rate = 0.008;
                    break;
            }

            newPrice = newPrice.add(newPrice.multiply(new BigDecimal(rate)).multiply(new BigDecimal(fq)));
            if (account.getBalance().compareTo(newPrice.divide(new BigDecimal(fq), RoundingMode.HALF_DOWN)) < 0) {
                return "-1";
            }
            account.setBalance(account.getBalance().subtract(newPrice.divide(new BigDecimal(fq),RoundingMode.HALF_DOWN)));
            List<OrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
            accountService.updatBalance(account);
            if (orderGoodsList != null) {
                OrderGoods orderGoods = orderGoodsList.get(0);
                Goods goods = goodsService.findById(orderGoods.getGoodsId());
                Bill bill = new Bill();
                bill.setUser_id(account.getId());
                bill.setGoods_id(goods.getId());
                bill.setGoods_name(goods.getName());
                bill.setBrief(goods.getBrief());
                bill.setTotal_amount(newPrice);
                bill.setStage_months(fq);
                bill.setRepayment_months(0);
                bill.setIs_payment(0);
                order.setPayTime(LocalDateTime.now());
                order.setOrderStatus(OrderUtil.STATUS_PAY);
                if(billService.addBill(bill)==1&&orderService.updateWithOptimisticLocker(order)==1) {
                    return "1";
                }
                else
                    return "-2";
            } else
                return "-2";
        }
        else
            return "0";
    }


    @RequestMapping(value = "allBills",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> userBill(HttpSession session){
        Account account = (Account) session.getAttribute("account");
        List<Bill> billList = billService.getBills(account.getId());
        Map<String,Object> map = new HashMap<>(2);
        map.put("account",account);
        map.put("billList",billList);
        return map;
    }
}
