package com.project.davinci.service;

import com.project.davinci.domain.GoodsProduct;
import com.project.davinci.domain.GoodsProductExample;
import com.project.davinci.persistence.GoodsProductDAO;
import com.project.davinci.persistence.DaGoodsProductDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoodsProductService {
    @Resource
    private DaGoodsProductDAO litemallGoodsProductMapper;
    @Resource
    private GoodsProductDAO goodsProductDAO;

    public List<GoodsProduct> queryByGid(Integer gid) {
        GoodsProductExample example = new GoodsProductExample();
        example.or().andGoodsIdEqualTo(gid).andDeletedEqualTo(false);
        return litemallGoodsProductMapper.selectByExample(example);
    }

    public GoodsProduct findById(Integer id) {
        return litemallGoodsProductMapper.selectByPrimaryKey(id);
    }

    public void deleteById(Integer id) {
        litemallGoodsProductMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(GoodsProduct goodsProduct) {
        goodsProduct.setAddTime(LocalDateTime.now());
        goodsProduct.setUpdateTime(LocalDateTime.now());
        litemallGoodsProductMapper.insertSelective(goodsProduct);
    }

    public int count() {
        GoodsProductExample example = new GoodsProductExample();
        example.or().andDeletedEqualTo(false);
        return (int) litemallGoodsProductMapper.countByExample(example);
    }

    public void deleteByGid(Integer gid) {
        GoodsProductExample example = new GoodsProductExample();
        example.or().andGoodsIdEqualTo(gid);
        litemallGoodsProductMapper.logicalDeleteByExample(example);
    }

    public int addStock(Integer id, Short num){
        return goodsProductDAO.addStock(id, num);
    }

    public int reduceStock(Integer id, Short num){
        return goodsProductDAO.reduceStock(id, num);
    }
}