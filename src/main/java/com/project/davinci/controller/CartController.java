package com.project.davinci.controller;

import com.project.davinci.domain.Account;
import com.project.davinci.domain.Cart;
import com.project.davinci.domain.Goods;
import com.project.davinci.domain.GoodsProduct;
import com.project.davinci.service.CartService;
import com.project.davinci.service.GoodsProductService;
import com.project.davinci.service.GoodsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用户购物车服务
 */
@Controller

public class CartController {
    private final Log logger = LogFactory.getLog(CartController.class);

    @Resource
    private CartService cartService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsProductService productService;
//    @Resource
//    private AddressService addressService;
//    @Resource
//    private CouponService couponService;
//    @Resource
//    private CouponUserService couponUserService;
//    @Resource
//    private CouponVerifyService couponVerifyService;

    //    /**
//     * 用户购物车信息
//     *
//     * @param userId 用户ID
//     * @return 用户购物车信息
//     */
    @RequestMapping(value = "/getCart", method = RequestMethod.GET)
    public String getCart(HttpSession session,Model model) {
        Account account = (Account) session.getAttribute("account");
        if (account != null) {
            List<Cart> cartList = cartService.queryByUid(account.getId());
            Integer goodsCount = 0;
            BigDecimal goodsAmount = new BigDecimal(0.00);
            for (Cart cart : cartList) {
                goodsCount += cart.getNumber();
                goodsAmount = goodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
            Map<String, Object> cartTotal = new HashMap<>();
            cartTotal.put("goodsCount", goodsCount);
            cartTotal.put("goodsAmount", goodsAmount);

            model.addAttribute("cartList", cartList);
            model.addAttribute("cartTotal", cartTotal);
            return "product/cart";
        } else {
            return "redirect:/login";
        }

    }

    //    /**
//     * 加入商品到购物车
//     * <p>
//     * 如果已经存在购物车货品，则增加数量；
//     * 否则添加新的购物车货品项。
//     *
//     * @param userId 用户ID
//     * @param cart   购物车商品信息， { goodsId: xxx, productId: xxx, number: xxx }
//     * @return 加入购物车操作结果
//     */
    @RequestMapping(value = "productDetail/addCart", method = RequestMethod.POST)
    @ResponseBody
    public String addCart(@RequestBody Map<String, String> map, HttpSession session) {
        Account account = (Account) session.getAttribute("account");
        if (account != null) {
            Integer productId = Integer.valueOf(map.get("productId"));
            Short number = Short.valueOf(map.get("number"));
            int userId = account.getId();
            GoodsProduct product = productService.findById(productId);
            Integer goodsId = product.getGoodsId();
            Goods goods = goodsService.findById(goodsId);
            Cart cart = new Cart();
            cart.setProductId(productId);
            cart.setNumber(number);
            cart.setGoodsId(goodsId);
            //判断购物车中是否存在此规格商品
            Cart existCart = cartService.queryExist(goodsId, productId, userId);
            if (existCart == null) {
                cart.setId(null);
                cart.setGoodsSn(goods.getGoodsSn());
                cart.setGoodsName((goods.getName()));
                cart.setPicUrl(goods.getPicUrl());
                cart.setPrice(product.getPrice());
                cart.setSpecifications(product.getSpecifications());
                cart.setUserId(userId);
                cart.setChecked(true);
                cartService.add(cart);
            } else {
                int num = existCart.getNumber() + number;
                existCart.setNumber((short) num);
                if (cartService.updateById(existCart) == 0) {
                    return "0";
                }
            }
            return "1";
        }
        return "-1";
    }


    //    /**
//     * 立即购买
//     * <p>
//     * 和add方法的区别在于：
//     * 1. 如果购物车内已经存在购物车货品，前者的逻辑是数量添加，这里的逻辑是数量覆盖
//     * 2. 添加成功以后，前者的逻辑是返回当前购物车商品数量，这里的逻辑是返回对应购物车项的ID
//     *
//     * @param userId 用户ID
//     * @param cart   购物车商品信息， { goodsId: xxx, productId: xxx, number: xxx }
//     * @return 立即购买操作结果
//     */
    @GetMapping("/fastadd/{productId}")
    public String fastadd(@PathVariable(value = "productId") String id_str, Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("account");
        if (account != null) {
            Integer productId = Integer.valueOf(id_str);
            GoodsProduct goodsProduct = productService.findById(productId);
            Integer goodsId = goodsProduct.getGoodsId();
            Integer userId = account.getId();
            Cart cart = new Cart();
            cart.setProductId(productId);
            cart.setGoodsId(goodsId);

            //判断商品是否可以购买
            Goods goods = goodsService.findById(goodsId);

            GoodsProduct product = productService.findById(productId);

            cart.setId(0);
            cart.setGoodsSn(goods.getGoodsSn());
            cart.setGoodsName((goods.getName()));
            Short a = 1;
            cart.setNumber(a);
            cart.setPicUrl(goods.getPicUrl());
            cart.setPrice(product.getPrice());
            cart.setSpecifications(product.getSpecifications());
            cart.setUserId(userId);
            cart.setChecked(true);
//            cartService.add(cart);
            model.addAttribute("cart", cart);
            return "product/reconfirm_order";
        }
        return "redirect:/login";
    }


    @GetMapping("/cartBuy/{cartId}")
    public String cartBuy(@PathVariable(value = "cartId") String id_str, Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("account");
        if (account != null) {
            Integer cartId = Integer.valueOf(id_str);
            Cart cart = cartService.findById(cartId);
            model.addAttribute("cart", cart);
            return "product/reconfirm_order";
        }
        return "redirect:/login";
    }
    //    /**
//     * 修改购物车商品货品数量
//     *
//     * @param userId 用户ID
//     * @param cart   购物车商品信息， { id: xxx, goodsId: xxx, productId: xxx, number: xxx }
//     * @return 修改结果
//     */
    @RequestMapping(value = "updateCart", method = RequestMethod.POST)
    @ResponseBody
    public String updateCart(@RequestBody Map<String, String> map, HttpSession session) {

        Account account = (Account) session.getAttribute("account");
        if (account != null) {
            Short number = Short.valueOf(map.get("number"));
            Integer cartId = Integer.valueOf(map.get("cartId"));
            if (number <= 0) {
                return "0";
            }
            Cart existCart = cartService.findById(cartId);
            if (existCart == null) {
                return "0";
            }
            existCart.setNumber(number.shortValue());
            if (cartService.updateById(existCart) == 0) {
                return "0";
            }
            return "1";
        } else {
            return "0";
        }
    }

    //    /**
//     * 购物车商品删除
//     *
//     * @param userId 用户ID
//     * @param body   购物车商品信息， { productIds: xxx }
//     * @return 购物车信息
//     * 成功则
//     * {
//     * errno: 0,
//     * errmsg: '成功',
//     * data: xxx
//     * }
//     * 失败则 { errno: XXX, errmsg: XXX }
//     */
    @RequestMapping(value = "deleteCart", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteCart(@RequestBody Map<String, String> map, HttpSession session) {
        Account account = (Account) session.getAttribute("account");
        if (account != null) {
            Integer cartId = Integer.valueOf(map.get("cartId"));
            List<Integer> productIds = new ArrayList<>();
            Cart Cart = cartService.findById(cartId);
            productIds.add(Cart.getProductId());
            if (productIds == null || productIds.size() == 0) {
                return "0";
            }
            cartService.delete(productIds, account.getId());
            return "1";

        } else {
            return "0";
        }
    }

//    @RequestMapping(value = "showGoods", method = RequestMethod.POST)
////    @ResponseBody
//    public String showGoods(@RequestBody Map<String, String> map) {
//        Integer cartId = Integer.valueOf(map.get("cartId"));
//        Cart cart = cartService.findById(cartId);
//        System.out.println(cart.getId());
//        if (cart!=null){
//            Integer goodsId = cart.getGoodsId();
//            return "redirect:/productDetail/"+goodsId;
//        }
//        else
//            return "0";
//    }


}