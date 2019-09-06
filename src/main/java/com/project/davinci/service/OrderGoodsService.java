package com.project.davinci.service;

import com.project.davinci.domain.OrderGoods;
import com.project.davinci.domain.OrderGoodsExample;
import com.project.davinci.persistence.OrderGoodsDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderGoodsService {
    @Resource
    private OrderGoodsDAO orderGoodsDAO;

    public int add(OrderGoods orderGoods) {
        orderGoods.setAddTime(LocalDateTime.now());
        orderGoods.setUpdateTime(LocalDateTime.now());
        return orderGoodsDAO.insertSelective(orderGoods);
    }

    public List<OrderGoods> queryByOid(Integer orderId) {
        OrderGoodsExample example = new OrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return orderGoodsDAO.selectByExample(example);
    }

    public List<OrderGoods> findByOidAndGid(Integer orderId, Integer goodsId) {
        OrderGoodsExample example = new OrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return orderGoodsDAO.selectByExample(example);
    }

    public OrderGoods findById(Integer id) {
        return orderGoodsDAO.selectByPrimaryKey(id);
    }

    public void updateById(OrderGoods orderGoods) {
        orderGoods.setUpdateTime(LocalDateTime.now());
        orderGoodsDAO.updateByPrimaryKeySelective(orderGoods);
    }

    public Short getComments(Integer orderId) {
        OrderGoodsExample example = new OrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        long count = orderGoodsDAO.countByExample(example);
        return (short) count;
    }

    public boolean checkExist(Integer goodsId) {
        OrderGoodsExample example = new OrderGoodsExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return orderGoodsDAO.countByExample(example) != 0;
    }
}
