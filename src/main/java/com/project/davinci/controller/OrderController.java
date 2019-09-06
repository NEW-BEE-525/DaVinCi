package com.project.davinci.controller;

import com.project.davinci.domain.*;
import com.project.davinci.service.*;
import com.project.davinci.utils.OrderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller

public class OrderController {
    private final Log logger = LogFactory.getLog(OrderController.class);

    @Resource
    private DaOrderService daOrderService;
//    /**
//     * 订单列表
//     *
//     * @param userId   用户ID
//     * @param showType 订单信息
//     * @param page     分页页数
//     * @param limit     分页大小
//     * @return 订单列表
//     */
//    @GetMapping("list")
//    public Object list( Integer userId,
//                       @RequestParam(defaultValue = "0") Integer showType,
//                       @RequestParam(defaultValue = "1") Integer page,
//                       @RequestParam(defaultValue = "10") Integer limit,
//                        @RequestParam(defaultValue = "add_time") String sort,
//                        @RequestParam(defaultValue = "desc") String order) {
//        return daOrderService.list(userId, showType, page, limit, sort, order);
//    }
//
//    /**
//     * 订单详情
//     *
//     * @param userId  用户ID
//     * @param orderId 订单ID
//     * @return 订单详情
//     */
//    @GetMapping("detail")
//    public Object detail( Integer userId, @NotNull Integer orderId) {
//        return daOrderService.detail(userId, orderId);
//    }
//
//    /**
//     * 提交订单
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ cartId：xxx, addressId: xxx, couponId: xxx, message: xxx, grouponRulesId: xxx,  grouponLinkId: xxx}
//     * @return 提交订单操作结果
//     */
    @RequestMapping(value = "fastadd/submit", method = RequestMethod.POST)
    @ResponseBody
    public String submit(@RequestBody Map<String, String> map, HttpSession session) {
        Account account = (Account)session.getAttribute("account");
        Integer cartId = Integer.valueOf(map.get("cartId"));
        Integer productId = Integer.valueOf(map.get("productId"));
        String addressDesc = map.get("addressDesc");
        String name = map.get("name");
        String mobile = map.get("mobile");
        String message = map.get("message");
        Short num = Short.parseShort(map.get("num"));
        Integer fq = Integer.valueOf(map.get("fq"));
        //收货地址
        Address address = new Address();
        address.setName(name);
        address.setAddressDesc(addressDesc);
        address.setMobile(mobile);
        Cart cart = new Cart();
        cart.setNumber(num);
        cart.setProductId(productId);
        cart.setId(cartId);
        return daOrderService.submit(account,address,cart,message);
    }
//
//    /**
//     * 取消订单
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 取消订单操作结果
//     */
//    @PostMapping("cancel")
//    public Object cancel( Integer userId, @RequestBody String body) {
//        return daOrderService.cancel(userId, body);
//    }
//
//    /**
//     * 付款订单的预支付会话标识
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 支付订单ID
//     */
//    @PostMapping("prepay")
//    public Object prepay( Integer userId, @RequestBody String body, HttpServletRequest request) {
//        return daOrderService.prepay(userId, body, request);
//    }
//
//    /**
//     * 微信付款成功或失败回调接口
//     * <p>
//     *  TODO
//     *  注意，这里pay-notify是示例地址，建议开发者应该设立一个隐蔽的回调地址
//     *
//     * @param request 请求内容
//     * @param response 响应内容
//     * @return 操作结果
//     */
//    @PostMapping("pay-notify")
//    public Object payNotify(HttpServletRequest request, HttpServletResponse response) {
//        return daOrderService.payNotify(request, response);
//    }
//
//    /**
//     * 订单申请退款
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 订单退款操作结果
//     */
//    @PostMapping("refund")
//    public Object refund( Integer userId, @RequestBody String body) {
//        return daOrderService.refund(userId, body);
//    }
//
//    /**
//     * 确认收货
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 订单操作结果
//     */
//    @PostMapping("confirm")
//    public Object confirm( Integer userId, @RequestBody String body) {
//        return daOrderService.confirm(userId, body);
//    }
//
//    /**
//     * 删除订单
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 订单操作结果
//     */
//    @PostMapping("delete")
//    public Object delete( Integer userId, @RequestBody String body) {
//        return daOrderService.delete(userId, body);
//    }
//
//    /**
//     * 待评价订单商品信息
//     *
//     * @param userId  用户ID
//     * @param orderId 订单ID
//     * @param goodsId 商品ID
//     * @return 待评价订单商品信息
//     */
//    @GetMapping("goods")
//    public Object goods( Integer userId,
//                        @NotNull Integer orderId,
//                        @NotNull Integer goodsId) {
//        return daOrderService.goods(userId, orderId, goodsId);
//    }
//
//    /**
//     * 评价订单商品
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 订单操作结果
//     */
//    @PostMapping("comment")
//    public Object comment( Integer userId, @RequestBody String body) {
//        return daOrderService.comment(userId, body);
//    }

}