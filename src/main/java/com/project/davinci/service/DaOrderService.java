package com.project.davinci.service;

import com.project.davinci.domain.*;
import com.project.davinci.utils.OrderUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 订单服务
 *
 * <p>
 * 订单状态：
 * 101 订单生成，未支付；102，下单后未支付用户取消；103，下单后未支付超时系统自动取消
 * 201 支付完成，商家未发货；202，订单生产，已付款未发货，但是退款取消；
 * 301 商家发货，用户未确认；
 * 401 用户确认收货； 402 用户没有确认收货超过一定时间，系统自动确认收货；
 *
 * <p>
 * 用户操作：
 * 当101用户未付款时，此时用户可以进行的操作是取消订单，或者付款操作
 * 当201支付完成而商家未发货时，此时用户可以取消订单并申请退款
 * 当301商家已发货时，此时用户可以有确认收货的操作
 * 当401用户确认收货以后，此时用户可以进行的操作是删除订单，评价商品，或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除订单，评价商品，或者再次购买
 *
 * <p>
 * 注意：目前不支持订单退货和售后服务
 */
@Service
public class DaOrderService {
    private final Log logger = LogFactory.getLog(DaOrderService.class);

    @Resource
    private AccountService accountService;

    @Resource
    private OrderService orderService;
    @Resource
    private OrderGoodsService orderGoodsService;
    @Resource
    private AddressService addressService;
    @Resource
    private CartService cartService;
    @Resource
    private GoodsService goodsService;
//    @Resource
//    private RegionService regionService;
    @Resource
    private GoodsProductService productService;
//    @Resource
//    private WxPayService wxPayService;
//    @Resource
//    private NotifyService notifyService;
//    @Resource
//    private UserFormIdService formIdService;

//    @Resource
//    private QCodeService qCodeService;
//    @Resource
//    private ExpressService expressService;
//    @Resource
//    private CouponVerifyService couponVerifyService;

//    /**
//     * 订单列表
//     *
//     * @param userId   用户ID
//     * @param showType 订单信息：
//     *                 0，全部订单；
//     *                 1，待付款；
//     *                 2，待发货；
//     *                 3，待收货；
//     *                 4，待评价。
//     * @param page     分页页数
//     * @param limit     分页大小
//     * @return 订单列表
//     */
    public Map<String,Object> list(Integer userId, Integer showType, Integer page, Integer limit, String sort, String order) throws MalformedURLException {

        List<Short> orderStatus = OrderUtil.orderStatus(showType);
        List<Order> orderList = orderService.queryByOrderStatus(userId, orderStatus, page, limit, sort, order);

        List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
        for (Order o : orderList) {
            Map<String, Object> orderVo = new HashMap<>();
            orderVo.put("id", o.getId());
            orderVo.put("orderSn", o.getOrderSn());
            orderVo.put("actualPrice", o.getActualPrice());
            orderVo.put("orderStatusText", OrderUtil.orderStatusText(o));
            orderVo.put("handleOption", OrderUtil.build(o));

            List<OrderGoods> orderGoodsList = orderGoodsService.queryByOid(o.getId());
            List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
            for (OrderGoods orderGoods : orderGoodsList) {
                Map<String, Object> orderGoodsVo = new HashMap<>();
                orderGoodsVo.put("id", orderGoods.getId());
                orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
                orderGoodsVo.put("number", orderGoods.getNumber());
                orderGoodsVo.put("picUrl",orderGoods.getPicUrl());
                orderGoodsVo.put("specifications", orderGoods.getSpecifications());
                orderGoodsVoList.add(orderGoodsVo);
            }
            orderVo.put("goodsList", orderGoodsVoList);

            orderVoList.add(orderVo);
        }
        Map<String, Object> result = new HashMap<>();

        result.put("orderList",orderVoList);
        return result;
    }
//
//    /**
//     * 订单详情
//     *
//     * @param userId  用户ID
//     * @param orderId 订单ID
//     * @return 订单详情
//     */

    public Map<String,Object> detail(Account account,Integer orderId) {
        if (account == null) {
            return null;
        }

        // 订单信息
        Order order = orderService.findById(orderId);
        if (null == order) {
            return null;
        }
        if (!order.getUserId().equals(account.getId())) {
            return null;
        }
        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("addTime", order.getAddTime());
        orderVo.put("consignee", order.getConsignee());
        orderVo.put("mobile", order.getMobile());
        orderVo.put("address", order.getAddress());
        orderVo.put("goodsPrice", order.getGoodsPrice());
        orderVo.put("couponPrice", order.getCouponPrice());
        orderVo.put("freightPrice", order.getFreightPrice());
        orderVo.put("actualPrice", order.getActualPrice());
        orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
        orderVo.put("handleOption", OrderUtil.build(order));
        orderVo.put("expCode", order.getShipChannel());
        orderVo.put("expNo", order.getShipSn());

        List<OrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderGoods", orderGoodsList);

//        // 订单状态为已发货且物流信息不为空
//        //"YTO", "800669400640887922"
//        if (order.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) {
//            ExpressInfo ei = expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
//            result.put("expressInfo", ei);
//        }

        return result;

    }
//
//    /**
//     * 提交订单
//     * <p>
//     * 1. 创建订单表项和订单商品表项;
//     * 2. 购物车清空;
//     * 3. 优惠券设置已用;
//     * 4. 商品货品库存减少;
//     * 5. 如果是团购商品，则创建团购活动表项。
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ cartId：xxx, addressId: xxx, couponId: xxx, message: xxx, grouponRulesId: xxx,  grouponLinkId: xxx}
//     * @return 提交订单操作结果
//     */
    public String submit(Account account, Address address,Cart cart,String message) {
        if (account == null) {
            return "0";//未登录
        }
        Integer cartId = cart.getId();
        Integer productId = cart.getProductId();
        String addressDesc = address.getAddressDesc();
        String name = address.getName();
        String mobile = address.getMobile();
        Short num = cart.getNumber();


        if (addressService.addAddress(address) != 1) {
            return "0";
        }
            // 货品价格
            List<Cart> checkedGoodsList = null;
            if (cartId.equals(0)) {
                cart.setProductId(productId);
                GoodsProduct product = productService.findById(productId);
                Goods goods = goodsService.findById(product.getGoodsId());
                cart.setId(null);
                cart.setPrice(product.getPrice());
                cart.setNumber(num);
                cart.setGoodsId(goods.getId());
                cart.setGoodsName(goods.getName());
                cart.setGoodsSn(goods.getGoodsSn());
                cart.setPicUrl(goods.getPicUrl());
                cart.setSpecifications(product.getSpecifications());
                checkedGoodsList = new ArrayList<>(1);
                checkedGoodsList.add(cart);
            } else {
                checkedGoodsList = new ArrayList<>(1);
                checkedGoodsList.add(cart);
            }
            if (checkedGoodsList.size() == 0) {
                return "0";
            }
            BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
            for (Cart checkGoods : checkedGoodsList) {
                checkedGoodsPrice = checkedGoodsPrice.add(checkGoods.getPrice().multiply(new BigDecimal(checkGoods.getNumber())));
            }


            // 根据订单商品总价计算运费，满足条件（例如88元）则免运费，否则需要支付运费（例如8元）；
            BigDecimal freightPrice = new BigDecimal(0.00);
            if (checkedGoodsPrice.compareTo(new BigDecimal(88.00)) < 0) {
                freightPrice = new BigDecimal(8.00);
            }

            // 最终支付费用
            BigDecimal actualPrice = checkedGoodsPrice.add(freightPrice).max(new BigDecimal(0.00));

            Integer orderId = null;
            Order order = null;
            // 订单
            order = new Order();
            order.setUserId(account.getId());
            order.setOrderSn(orderService.generateOrderSn(account.getId()));
            order.setOrderStatus(OrderUtil.STATUS_CREATE);
            order.setConsignee(name);
            order.setMobile(mobile);
            order.setMessage(message);
            order.setAddress(addressDesc);
            order.setGoodsPrice(checkedGoodsPrice);
            order.setFreightPrice(freightPrice);
            order.setCouponPrice(new BigDecimal(0.00));
            order.setIntegralPrice(new BigDecimal(0.00));
            order.setOrderPrice(new BigDecimal(0.00));
            order.setGrouponPrice(new BigDecimal(0.00));
            order.setActualPrice(actualPrice);

            // 添加订单表项
            orderService.add(order);
            orderId = order.getId();

            // 添加订单商品表项
            for (Cart cartGoods : checkedGoodsList) {
                // 订单商品
                OrderGoods orderGoods = new OrderGoods();
                orderGoods.setOrderId(order.getId());
                orderGoods.setGoodsId(cartGoods.getGoodsId());
                orderGoods.setGoodsSn(cartGoods.getGoodsSn());
                orderGoods.setProductId(cartGoods.getProductId());
                orderGoods.setGoodsName(cartGoods.getGoodsName());
                orderGoods.setPicUrl(cartGoods.getPicUrl());
                orderGoods.setPrice(cartGoods.getPrice());
                orderGoods.setNumber(cartGoods.getNumber());
                orderGoods.setSpecifications(cartGoods.getSpecifications());
                orderGoods.setAddTime(LocalDateTime.now());

                orderGoodsService.add(orderGoods);
            }

            // 删除购物车里面的商品信息
        if (!cartId.equals(0)) {
            cartService.clearGoods(account.getId());
        }
            return order.getOrderSn();

    }
//
//    /**
//     * 取消订单
//     * <p>
//     * 1. 检测当前订单是否能够取消；
//     * 2. 设置订单取消状态；
//     * 3. 商品货品库存恢复；
//     * 4. TODO 优惠券；
//     * 5. TODO 团购活动。
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 取消订单操作结果
//     */
//    @Transactional
//    public Object cancel(Integer userId, String body) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
//        if (orderId == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        Order order = orderService.findById(orderId);
//        if (order == null) {
//            return ResponseUtil.badArgumentValue();
//        }
//        if (!order.getUserId().equals(userId)) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        LocalDateTime preUpdateTime = order.getUpdateTime();
//
//        // 检测是否能够取消
//        OrderHandleOption handleOption = OrderUtil.build(order);
//        if (!handleOption.isCancel()) {
//            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能取消");
//        }
//
//        // 设置订单已取消状态
//        order.setOrderStatus(OrderUtil.STATUS_CANCEL);
//        order.setEndTime(LocalDateTime.now());
//        if (orderService.updateWithOptimisticLocker(order) == 0) {
//            throw new RuntimeException("更新数据已失效");
//        }
//
//        // 商品货品数量增加
//        List<OrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
//        for (OrderGoods orderGoods : orderGoodsList) {
//            Integer productId = orderGoods.getProductId();
//            Short number = orderGoods.getNumber();
//            if (productService.addStock(productId, number) == 0) {
//                throw new RuntimeException("商品货品库存增加失败");
//            }
//        }
//
//        return ResponseUtil.ok();
//    }
//
//    /**
//     * 付款订单的预支付会话标识
//     * <p>
//     * 1. 检测当前订单是否能够付款
//     * 2. 微信商户平台返回支付订单ID
//     * 3. 设置订单付款状态
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 支付订单ID
//     */
//    @Transactional
//    public Object prepay(Integer userId, String body, HttpServletRequest request) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
//        if (orderId == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        Order order = orderService.findById(orderId);
//        if (order == null) {
//            return ResponseUtil.badArgumentValue();
//        }
//        if (!order.getUserId().equals(userId)) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        // 检测是否能够取消
//        OrderHandleOption handleOption = OrderUtil.build(order);
//        if (!handleOption.isPay()) {
//            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能支付");
//        }
//
//        User user = userService.findById(userId);
//        String openid = user.getWeixinOpenid();
//        if (openid == null) {
//            return ResponseUtil.fail(AUTH_OPENID_UNACCESS, "订单不能支付");
//        }
//        WxPayMpOrderResult result = null;
//        try {
//            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
//            orderRequest.setOutTradeNo(order.getOrderSn());
//            orderRequest.setOpenid(openid);
//            orderRequest.setBody("订单：" + order.getOrderSn());
//            // 元转成分
//            int fee = 0;
//            BigDecimal actualPrice = order.getActualPrice();
//            fee = actualPrice.multiply(new BigDecimal(100)).intValue();
//            orderRequest.setTotalFee(fee);
//            orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));
//
//            result = wxPayService.createOrder(orderRequest);
//
//            //缓存prepayID用于后续模版通知
//            String prepayId = result.getPackageValue();
//            prepayId = prepayId.replace("prepay_id=", "");
//            UserFormid userFormid = new UserFormid();
//            userFormid.setOpenid(user.getWeixinOpenid());
//            userFormid.setFormid(prepayId);
//            userFormid.setIsprepay(true);
//            userFormid.setUseamount(3);
//            userFormid.setExpireTime(LocalDateTime.now().plusDays(7));
//            formIdService.addUserFormid(userFormid);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseUtil.fail(ORDER_PAY_FAIL, "订单不能支付");
//        }
//
//        if (orderService.updateWithOptimisticLocker(order) == 0) {
//            return ResponseUtil.updatedDateExpired();
//        }
//        return ResponseUtil.ok(result);
//    }
//
//    /**
//     * 微信付款成功或失败回调接口
//     * <p>
//     * 1. 检测当前订单是否是付款状态;
//     * 2. 设置订单付款成功状态相关信息;
//     * 3. 响应微信商户平台.
//     *
//     * @param request  请求内容
//     * @param response 响应内容
//     * @return 操作结果
//     */
//    @Transactional
//    public Object payNotify(HttpServletRequest request, HttpServletResponse response) {
//        String xmlResult = null;
//        try {
//            xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return WxPayNotifyResponse.fail(e.getMessage());
//        }
//
//        WxPayOrderNotifyResult result = null;
//        try {
//            result = wxPayService.parseOrderNotifyResult(xmlResult);
//
//            if(!WxPayConstants.ResultCode.SUCCESS.equals(result.getResultCode())){
//                logger.error(xmlResult);
//                throw new WxPayException("微信通知支付失败！");
//            }
//            if(!WxPayConstants.ResultCode.SUCCESS.equals(result.getReturnCode())){
//                logger.error(xmlResult);
//                throw new WxPayException("微信通知支付失败！");
//            }
//        } catch (WxPayException e) {
//            e.printStackTrace();
//            return WxPayNotifyResponse.fail(e.getMessage());
//        }
//
//        logger.info("处理腾讯支付平台的订单支付");
//        logger.info(result);
//
//        String orderSn = result.getOutTradeNo();
//        String payId = result.getTransactionId();
//
//        // 分转化成元
//        String totalFee = BaseWxPayResult.fenToYuan(result.getTotalFee());
//        Order order = orderService.findBySn(orderSn);
//        if (order == null) {
//            return WxPayNotifyResponse.fail("订单不存在 sn=" + orderSn);
//        }
//
//        // 检查这个订单是否已经处理过
//        if (OrderUtil.isPayStatus(order) && order.getPayId() != null) {
//            return WxPayNotifyResponse.success("订单已经处理成功!");
//        }
//
//        // 检查支付订单金额
//        if (!totalFee.equals(order.getActualPrice().toString())) {
//            return WxPayNotifyResponse.fail(order.getOrderSn() + " : 支付金额不符合 totalFee=" + totalFee);
//        }
//
//        order.setPayId(payId);
//        order.setPayTime(LocalDateTime.now());
//        order.setOrderStatus(OrderUtil.STATUS_PAY);
//        if (orderService.updateWithOptimisticLocker(order) == 0) {
//            // 这里可能存在这样一个问题，用户支付和系统自动取消订单发生在同时
//            // 如果数据库首先因为系统自动取消订单而更新了订单状态；
//            // 此时用户支付完成回调这里也要更新数据库，而由于乐观锁机制这里的更新会失败
//            // 因此，这里会重新读取数据库检查状态是否是订单自动取消，如果是则更新成支付状态。
//            order = orderService.findBySn(orderSn);
//            int updated = 0;
//            if (OrderUtil.isAutoCancelStatus(order)) {
//                order.setPayId(payId);
//                order.setPayTime(LocalDateTime.now());
//                order.setOrderStatus(OrderUtil.STATUS_PAY);
//                updated = orderService.updateWithOptimisticLocker(order);
//            }
//
//            // 如果updated是0，那么数据库更新失败
//            if (updated == 0) {
//                return WxPayNotifyResponse.fail("更新数据已失效");
//            }
//        }
//
//        //  支付成功，有团购信息，更新团购信息
//        Groupon groupon = grouponService.queryByOrderId(order.getId());
//        if (groupon != null) {
//            GrouponRules grouponRules = grouponRulesService.queryById(groupon.getRulesId());
//
//            //仅当发起者才创建分享图片
//            if (groupon.getGrouponId() == 0) {
//                String url = qCodeService.createGrouponShareImage(grouponRules.getGoodsName(), grouponRules.getPicUrl(), groupon);
//                groupon.setShareUrl(url);
//            }
//            groupon.setPayed(true);
//            if (grouponService.updateById(groupon) == 0) {
//                return WxPayNotifyResponse.fail("更新数据已失效");
//            }
//        }
//
//        //TODO 发送邮件和短信通知，这里采用异步发送
//        // 订单支付成功以后，会发送短信给用户，以及发送邮件给管理员
//        notifyService.notifyMail("新订单通知", order.toString());
//        // 这里微信的短信平台对参数长度有限制，所以将订单号只截取后6位
//        notifyService.notifySmsTemplateSync(order.getMobile(), NotifyType.PAY_SUCCEED, new String[]{orderSn.substring(8, 14)});
//
//        // 请依据自己的模版消息配置更改参数
//        String[] parms = new String[]{
//                order.getOrderSn(),
//                order.getOrderPrice().toString(),
//                DateTimeUtil.getDateTimeDisplayString(order.getAddTime()),
//                order.getConsignee(),
//                order.getMobile(),
//                order.getAddress()
//        };
//
//        notifyService.notifyWxTemplate(result.getOpenid(), NotifyType.PAY_SUCCEED, parms, "pages/index/index?orderId=" + order.getId());
//
//        return WxPayNotifyResponse.success("处理成功!");
//    }
//
//    /**
//     * 订单申请退款
//     * <p>
//     * 1. 检测当前订单是否能够退款；
//     * 2. 设置订单申请退款状态。
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 订单退款操作结果
//     */
//    public Object refund(Integer userId, String body) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
//        if (orderId == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        Order order = orderService.findById(orderId);
//        if (order == null) {
//            return ResponseUtil.badArgument();
//        }
//        if (!order.getUserId().equals(userId)) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        OrderHandleOption handleOption = OrderUtil.build(order);
//        if (!handleOption.isRefund()) {
//            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能取消");
//        }
//
//        // 设置订单申请退款状态
//        order.setOrderStatus(OrderUtil.STATUS_REFUND);
//        if (orderService.updateWithOptimisticLocker(order) == 0) {
//            return ResponseUtil.updatedDateExpired();
//        }
//
//        //TODO 发送邮件和短信通知，这里采用异步发送
//        // 有用户申请退款，邮件通知运营人员
//        notifyService.notifyMail("退款申请", order.toString());
//
//        return ResponseUtil.ok();
//    }
//
//    /**
//     * 确认收货
//     * <p>
//     * 1. 检测当前订单是否能够确认收货；
//     * 2. 设置订单确认收货状态。
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 订单操作结果
//     */
//    public Object confirm(Integer userId, String body) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
//        if (orderId == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        Order order = orderService.findById(orderId);
//        if (order == null) {
//            return ResponseUtil.badArgument();
//        }
//        if (!order.getUserId().equals(userId)) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        OrderHandleOption handleOption = OrderUtil.build(order);
//        if (!handleOption.isConfirm()) {
//            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能确认收货");
//        }
//
//        Short comments = orderGoodsService.getComments(orderId);
//        order.setComments(comments);
//
//        order.setOrderStatus(OrderUtil.STATUS_CONFIRM);
//        order.setConfirmTime(LocalDateTime.now());
//        if (orderService.updateWithOptimisticLocker(order) == 0) {
//            return ResponseUtil.updatedDateExpired();
//        }
//        return ResponseUtil.ok();
//    }
//
//    /**
//     * 删除订单
//     * <p>
//     * 1. 检测当前订单是否可以删除；
//     * 2. 删除订单。
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 订单操作结果
//     */
//    public Object delete(Integer userId, String body) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
//        if (orderId == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        Order order = orderService.findById(orderId);
//        if (order == null) {
//            return ResponseUtil.badArgument();
//        }
//        if (!order.getUserId().equals(userId)) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        OrderHandleOption handleOption = OrderUtil.build(order);
//        if (!handleOption.isDelete()) {
//            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能删除");
//        }
//
//        // 订单order_status没有字段用于标识删除
//        // 而是存在专门的delete字段表示是否删除
//        orderService.deleteById(orderId);
//
//        return ResponseUtil.ok();
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
//    public Object goods(Integer userId, Integer orderId, Integer goodsId) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//
//        List<OrderGoods> orderGoodsList = orderGoodsService.findByOidAndGid(orderId, goodsId);
//        int size = orderGoodsList.size();
//
//        Assert.state(size < 2, "存在多个符合条件的订单商品");
//
//        if (size == 0) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        OrderGoods orderGoods = orderGoodsList.get(0);
//        return ResponseUtil.ok(orderGoods);
//    }
//
//    /**
//     * 评价订单商品
//     * <p>
//     * 确认商品收货或者系统自动确认商品收货后7天内可以评价，过期不能评价。
//     *
//     * @param userId 用户ID
//     * @param body   订单信息，{ orderId：xxx }
//     * @return 订单操作结果
//     */
//    public Object comment(Integer userId, String body) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//
//        Integer orderGoodsId = JacksonUtil.parseInteger(body, "orderGoodsId");
//        if (orderGoodsId == null) {
//            return ResponseUtil.badArgument();
//        }
//        OrderGoods orderGoods = orderGoodsService.findById(orderGoodsId);
//        if (orderGoods == null) {
//            return ResponseUtil.badArgumentValue();
//        }
//        Integer orderId = orderGoods.getOrderId();
//        Order order = orderService.findById(orderId);
//        if (order == null) {
//            return ResponseUtil.badArgumentValue();
//        }
//        Short orderStatus = order.getOrderStatus();
//        if (!OrderUtil.isConfirmStatus(order) && !OrderUtil.isAutoConfirmStatus(order)) {
//            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "当前商品不能评价");
//        }
//        if (!order.getUserId().equals(userId)) {
//            return ResponseUtil.fail(ORDER_INVALID, "当前商品不属于用户");
//        }
//        Integer commentId = orderGoods.getComment();
//        if (commentId == -1) {
//            return ResponseUtil.fail(ORDER_COMMENT_EXPIRED, "当前商品评价时间已经过期");
//        }
//        if (commentId != 0) {
//            return ResponseUtil.fail(ORDER_COMMENTED, "订单商品已评价");
//        }
//
//        String content = JacksonUtil.parseString(body, "content");
//        Integer star = JacksonUtil.parseInteger(body, "star");
//        if (star == null || star < 0 || star > 5) {
//            return ResponseUtil.badArgumentValue();
//        }
//        Boolean hasPicture = JacksonUtil.parseBoolean(body, "hasPicture");
//        List<String> picUrls = JacksonUtil.parseStringList(body, "picUrls");
//        if (hasPicture == null || !hasPicture) {
//            picUrls = new ArrayList<>(0);
//        }
//
//        // 1. 创建评价
//        Comment comment = new Comment();
//        comment.setUserId(userId);
//        comment.setType((byte) 0);
//        comment.setValueId(orderGoods.getGoodsId());
//        comment.setStar(star.shortValue());
//        comment.setContent(content);
//        comment.setHasPicture(hasPicture);
//        comment.setPicUrls(picUrls.toArray(new String[]{}));
//        commentService.save(comment);
//
//        // 2. 更新订单商品的评价列表
//        orderGoods.setComment(comment.getId());
//        orderGoodsService.updateById(orderGoods);
//
//        // 3. 更新订单中未评价的订单商品可评价数量
//        Short commentCount = order.getComments();
//        if (commentCount > 0) {
//            commentCount--;
//        }
//        order.setComments(commentCount);
//        orderService.updateWithOptimisticLocker(order);
//
//        return ResponseUtil.ok();
//    }

}