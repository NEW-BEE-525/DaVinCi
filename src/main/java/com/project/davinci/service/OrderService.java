package com.project.davinci.service;

import com.github.pagehelper.PageHelper;
import com.project.davinci.domain.Order;
import com.project.davinci.domain.OrderExample;
import com.project.davinci.persistence.DaOrderDAO;
import com.project.davinci.persistence.OrderDAO;
import com.project.davinci.utils.OrderUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OrderService {
    @Resource
    private DaOrderDAO daOrderDAO;
    @Resource
    private OrderDAO orderDAO;

    public int add(Order order) {
        order.setAddTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return daOrderDAO.insertSelective(order);
    }

    public int count(Integer userId) {
        OrderExample example = new OrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return (int) daOrderDAO.countByExample(example);
    }

    public Order findById(Integer orderId) {
        return daOrderDAO.selectByPrimaryKey(orderId);
    }

    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public int countByOrderSn(Integer userId, String orderSn) {
        OrderExample example = new OrderExample();
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return (int) daOrderDAO.countByExample(example);
    }

    // TODO 这里应该产生一个唯一的订单，但是实际上这里仍然存在两个订单相同的可能性
    public String generateOrderSn(Integer userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now());
        String orderSn = now + getRandomNum(6);
        while (countByOrderSn(userId, orderSn) != 0) {
            orderSn = now + getRandomNum(6);
        }
        return orderSn;
    }

    public List<Order> queryByOrderStatus(Integer userId, List<Short> orderStatus, Integer page, Integer limit, String sort, String order) {
        OrderExample example = new OrderExample();
        example.setOrderByClause(Order.Column.addTime.desc());
        OrderExample.Criteria criteria = example.or();
        criteria.andUserIdEqualTo(userId);
        if (orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return daOrderDAO.selectByExample(example);
    }

    public List<Order> querySelective(Integer userId, String orderSn, List<Short> orderStatusArray, Integer page, Integer limit, String sort, String order) {
        OrderExample example = new OrderExample();
        OrderExample.Criteria criteria = example.createCriteria();

        if (userId != null) {
            criteria.andUserIdEqualTo(userId);
        }
        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnEqualTo(orderSn);
        }
        if (orderStatusArray != null && orderStatusArray.size() != 0) {
            criteria.andOrderStatusIn(orderStatusArray);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return daOrderDAO.selectByExample(example);
    }

    public int updateWithOptimisticLocker(Order order) {
        LocalDateTime preUpdateTime = order.getUpdateTime();
        order.setUpdateTime(LocalDateTime.now());
        return orderDAO.updateWithOptimisticLocker(preUpdateTime, order);
    }

    public void deleteById(Integer id) {
        daOrderDAO.logicalDeleteByPrimaryKey(id);
    }

    public int count() {
        OrderExample example = new OrderExample();
        example.or().andDeletedEqualTo(false);
        return (int) daOrderDAO.countByExample(example);
    }

    public List<Order> queryUnpaid(int minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusMinutes(minutes);
        OrderExample example = new OrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CREATE).andAddTimeLessThan(expired).andDeletedEqualTo(false);
        return daOrderDAO.selectByExample(example);
    }

    public List<Order> queryUnconfirm(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(days);
        OrderExample example = new OrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_SHIP).andShipTimeLessThan(expired).andDeletedEqualTo(false);
        return daOrderDAO.selectByExample(example);
    }

    public Order findBySn(String orderSn) {
        OrderExample example = new OrderExample();
        example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return daOrderDAO.selectOneByExample(example);
    }

    public Map<Object, Object> orderInfo(Integer userId) {
        OrderExample example = new OrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        List<Order> orders = daOrderDAO.selectByExampleSelective(example, Order.Column.orderStatus, Order.Column.comments);

        int unpaid = 0;
        int unship = 0;
        int unrecv = 0;
        int uncomment = 0;
        for (Order order : orders) {
            if (OrderUtil.isCreateStatus(order)) {
                unpaid++;
            } else if (OrderUtil.isPayStatus(order)) {
                unship++;
            } else if (OrderUtil.isShipStatus(order)) {
                unrecv++;
            } else if (OrderUtil.isConfirmStatus(order) || OrderUtil.isAutoConfirmStatus(order)) {
                uncomment += order.getComments();
            } else {
                // do nothing
            }
        }

        Map<Object, Object> orderInfo = new HashMap<Object, Object>();
        orderInfo.put("unpaid", unpaid);
        orderInfo.put("unship", unship);
        orderInfo.put("unrecv", unrecv);
        orderInfo.put("uncomment", uncomment);
        return orderInfo;

    }

    public List<Order> queryComment(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(days);
        OrderExample example = new OrderExample();
        example.or().andCommentsGreaterThan((short) 0).andConfirmTimeLessThan(expired).andDeletedEqualTo(false);
        return daOrderDAO.selectByExample(example);
    }
}
