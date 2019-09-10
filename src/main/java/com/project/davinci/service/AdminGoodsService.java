package com.project.davinci.service;

import com.project.davinci.domain.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AdminGoodsService {
    private final Log logger = LogFactory.getLog(AdminGoodsService.class);

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsAttributeService attributeService;
    @Autowired
    private GoodsProductService productService;
    @Autowired
    private CategoryService categoryService;
//
//    @Autowired
//    private QCodeService qCodeService;

    public List<Goods>  list(Integer catId, Integer page, Integer limit) {
        List<Goods> goodsList = goodsService.queryByCategory(catId, page, limit);
        return goodsList;
    }

    private Object validate(GoodsAllinone goodsAllinone) {
        Goods goods = goodsAllinone.getGoods();
        String name = goods.getName();
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        String goodsSn = goods.getGoodsSn();
        if (StringUtils.isEmpty(goodsSn)) {
            return null;
        }
        // 分类可以不设置，如果设置则需要验证分类存在
        Integer categoryId = goods.getCategoryId();
        if (categoryId != null && categoryId != 0) {
            if (categoryService.findById(categoryId) == null) {
                return null;
            }
        }

        GoodsAttribute[] attributes = goodsAllinone.getAttributes();
        for (GoodsAttribute attribute : attributes) {
            String attr = attribute.getAttribute();
            if (StringUtils.isEmpty(attr)) {
                return null;
            }
            String value = attribute.getValue();
            if (StringUtils.isEmpty(value)) {
                return null;
            }
        }

        GoodsProduct[] products = goodsAllinone.getProducts();
        for (GoodsProduct product : products) {
            Integer number = product.getNumber();
            if (number == null || number < 0) {
                return null;
            }

            BigDecimal price = product.getPrice();
            if (price == null) {
                return null;
            }

            String[] productSpecifications = product.getSpecifications();
            if (productSpecifications.length == 0) {
                return null;
            }
        }

        return null;
    }

    /**
     * 编辑商品
     * <p>
     * TODO
     * 目前商品修改的逻辑是
     * 1. 更新_goods表
     * 2. 逻辑删除_goods_specification、_goods_attribute、_goods_product
     * 3. 添加_goods_specification、_goods_attribute、_goods_product
     * <p>
     * 这里商品三个表的数据采用删除再添加的策略是因为
     * 商品编辑页面，支持管理员添加删除商品规格、添加删除商品属性，因此这里仅仅更新是不可能的，
     * 只能删除三个表旧的数据，然后添加新的数据。
     * 但是这里又会引入新的问题，就是存在订单商品货品ID指向了失效的商品货品表。
     * 因此这里会拒绝管理员编辑商品，如果订单或购物车中存在商品。
     * 所以这里可能需要重新设计。
     */
    @Transactional
    public Object update(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        Goods goods = goodsAllinone.getGoods();
        GoodsAttribute[] attributes = goodsAllinone.getAttributes();
        GoodsProduct[] products = goodsAllinone.getProducts();

        Integer id = goods.getId();


        // 商品基本信息表_goods
        if (goodsService.updateById(goods) == 0) {
            throw new RuntimeException("更新数据失败");
        }

        Integer gid = goods.getId();
        attributeService.deleteByGid(gid);
        productService.deleteByGid(gid);


        // 商品参数表_goods_attribute
        for (GoodsAttribute attribute : attributes) {
            attribute.setGoodsId(goods.getId());
            attributeService.add(attribute);
        }

        // 商品货品表_product
        for (GoodsProduct product : products) {
            product.setGoodsId(goods.getId());
            productService.add(product);
        }

        return null;
    }

    @Transactional
    public Object delete(Goods goods) {
        Integer id = goods.getId();
        if (id == null) {
            return null;
        }

        Integer gid = goods.getId();
        goodsService.deleteById(gid);
        attributeService.deleteByGid(gid);
        productService.deleteByGid(gid);
        return null;
    }

    @Transactional
    public Object create(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        Goods goods = goodsAllinone.getGoods();
        GoodsAttribute[] attributes = goodsAllinone.getAttributes();
        GoodsProduct[] products = goodsAllinone.getProducts();

        String name = goods.getName();
        if (goodsService.checkExistByName(name)) {
            return null;
        }

        // 商品基本信息表_goods
        goodsService.add(goods);


        // 商品参数表_goods_attribute
        for (GoodsAttribute attribute : attributes) {
            attribute.setGoodsId(goods.getId());
            attributeService.add(attribute);
        }

        // 商品货品表_product
        for (GoodsProduct product : products) {
            product.setGoodsId(goods.getId());
            productService.add(product);
        }
        return null;
    }

    public Object list2() {
        // http://element-cn.eleme.io/#/zh-CN/component/cascader
        // 管理员设置“所属分类”
        List<Category> l1CatList = categoryService.queryL1();
        List<CatVo> categoryList = new ArrayList<>(l1CatList.size());

        for (Category l1 : l1CatList) {
            CatVo l1CatVo = new CatVo();
            l1CatVo.setValue(l1.getId());
            l1CatVo.setLabel(l1.getName());

            List<Category> l2CatList = categoryService.queryByPid(l1.getId());
            List<CatVo> children = new ArrayList<>(l2CatList.size());
            for (Category l2 : l2CatList) {
                CatVo l2CatVo = new CatVo();
                l2CatVo.setValue(l2.getId());
                l2CatVo.setLabel(l2.getName());
                children.add(l2CatVo);
            }
            l1CatVo.setChildren(children);

            categoryList.add(l1CatVo);
        }


        Map<String, Object> data = new HashMap<>();
        data.put("categoryList", categoryList);
        return null;
    }

    public Object detail(Integer id) {
        Goods goods = goodsService.findById(id);
        List<GoodsProduct> products = productService.queryByGid(id);
        List<GoodsAttribute> attributes = attributeService.queryByGid(id);

        Integer categoryId = goods.getCategoryId();
        Category category = categoryService.findById(categoryId);
        Integer[] categoryIds = new Integer[]{};
        if (category != null) {
            Integer parentCategoryId = category.getPid();
            categoryIds = new Integer[]{parentCategoryId, categoryId};
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goods", goods);
        data.put("products", products);
        data.put("attributes", attributes);
        data.put("categoryIds", categoryIds);

        return null;
    }

}
