package com.project.davinci.service;

import com.github.pagehelper.PageHelper;
import com.project.davinci.domain.Cart;
import com.project.davinci.domain.CartExample;
import com.project.davinci.persistence.CartDAO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {
    @Resource
    private CartDAO cartDAO;

    public Cart queryExist(Integer goodsId, Integer productId, Integer userId) {
        CartExample example = new CartExample();
        example.or().andGoodsIdEqualTo(goodsId).andProductIdEqualTo(productId).andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return cartDAO.selectOneByExample(example);
    }

    public void add(Cart cart) {
        cart.setAddTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        cartDAO.insertSelective(cart);
    }

    public int updateById(Cart cart) {
        cart.setUpdateTime(LocalDateTime.now());
        return cartDAO.updateByPrimaryKeySelective(cart);
    }

    public List<Cart> queryByUid(int userId) {
        CartExample example = new CartExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return cartDAO.selectByExample(example);
    }


    public List<Cart> queryByUidAndChecked(Integer userId) {
        CartExample example = new CartExample();
        example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true).andDeletedEqualTo(false);
        return cartDAO.selectByExample(example);
    }

    public int delete(List<Integer> productIdList, int userId) {
        CartExample example = new CartExample();
        example.or().andUserIdEqualTo(userId).andProductIdIn(productIdList);
        return cartDAO.logicalDeleteByExample(example);
    }

    public Cart findById(Integer id) {
        return cartDAO.selectByPrimaryKey(id);
    }

    public int updateCheck(Integer userId, List<Integer> idsList, Boolean checked) {
        CartExample example = new CartExample();
        example.or().andUserIdEqualTo(userId).andProductIdIn(idsList).andDeletedEqualTo(false);
        Cart cart = new Cart();
        cart.setChecked(checked);
        cart.setUpdateTime(LocalDateTime.now());
        return cartDAO.updateByExampleSelective(cart, example);
    }

    public void clearGoods(Integer userId) {
        CartExample example = new CartExample();
        example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true);
        Cart cart = new Cart();
        cart.setDeleted(true);
        cartDAO.updateByExampleSelective(cart, example);
    }

    public List<Cart> querySelective(Integer userId, Integer goodsId, Integer page, Integer limit, String sort, String order) {
        CartExample example = new CartExample();
        CartExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(userId);
        }
        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(goodsId);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return cartDAO.selectByExample(example);
    }

    public void deleteById(Integer id) {
        cartDAO.logicalDeleteByPrimaryKey(id);
    }

    public boolean checkExist(Integer goodsId) {
        CartExample example = new CartExample();
        example.or().andGoodsIdEqualTo(goodsId).andCheckedEqualTo(true).andDeletedEqualTo(false);
        return cartDAO.countByExample(example) != 0;
    }
}
