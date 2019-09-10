package com.project.davinci.service;

import com.project.davinci.domain.Order;
import com.project.davinci.domain.OrderGoods;
import com.project.davinci.utils.OrderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service

public class AdminOrderService {
    private final Log logger = LogFactory.getLog(AdminOrderService.class);

    @Autowired
    private OrderGoodsService orderGoodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsProductService productService;

    public List<Order> list(Integer userId, String orderSn, List<Short> orderStatusArray,
                       Integer page, Integer limit, String sort, String order) {
        List<Order> orderList = orderService.querySelective(userId, orderSn, orderStatusArray, page, limit, sort, order);
        return orderList;
    }

    public String detail(Integer id) {
        Order order = orderService.findById(id);
        List<OrderGoods> orderGoods = orderGoodsService.queryByOid(id);
//        UserVo user = userService.findUserVoById(order.getUserId());
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderGoods", orderGoods);

        return null;
    }

    public Object ship(Integer orderId) {
        if (orderId == null) {
            return null;
        }

        Order order = orderService.findById(orderId);
        if (order == null) {
            return null;
        }

        // 如果订单不是已付款状态，则不能发货
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)) {
            return null;
        }

        order.setOrderStatus(OrderUtil.STATUS_SHIP);
        order.setShipSn("1111");
        order.setShipChannel("顺风");
        order.setShipTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return null;
        }

        return "1";
    }




}
