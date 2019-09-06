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

    /**
     * 用户购物车信息
     *
     * @param userId 用户ID
     * @return 用户购物车信息
     */
    @GetMapping("index")
    public Object index(Integer userId) {
        if (userId == null) {
            return null;
        }

        List<Cart> cartList = cartService.queryByUid(userId);
        Integer goodsCount = 0;
        BigDecimal goodsAmount = new BigDecimal(0.00);
        Integer checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0.00);
        for (Cart cart : cartList) {
            goodsCount += cart.getNumber();
            goodsAmount = goodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            if (cart.getChecked()) {
                checkedGoodsCount += cart.getNumber();
                checkedGoodsAmount = checkedGoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }
        Map<String, Object> cartTotal = new HashMap<>();
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount);
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("cartList", cartList);
        result.put("cartTotal", cartTotal);

        return null;
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
//    @PostMapping("add")
//    public Object add(Integer userId, @RequestBody Cart cart) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//        if (cart == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        Integer productId = cart.getProductId();
//        Integer number = cart.getNumber().intValue();
//        Integer goodsId = cart.getGoodsId();
//        if (!ObjectUtils.allNotNull(productId, number, goodsId)) {
//            return ResponseUtil.badArgument();
//        }
//        if(number <= 0){
//            return ResponseUtil.badArgument();
//        }
//
//        //判断商品是否可以购买
//        Goods goods = goodsService.findById(goodsId);
//        if (goods == null || !goods.getIsOnSale()) {
//            return ResponseUtil.fail(GOODS_UNSHELVE, "商品已下架");
//        }
//
//        GoodsProduct product = productService.findById(productId);
//        //判断购物车中是否存在此规格商品
//        Cart existCart = cartService.queryExist(goodsId, productId, userId);
//        if (existCart == null) {
//            //取得规格的信息,判断规格库存
//            if (product == null || number > product.getNumber()) {
//                return ResponseUtil.fail(GOODS_NO_STOCK, "库存不足");
//            }
//
//            cart.setId(null);
//            cart.setGoodsSn(goods.getGoodsSn());
//            cart.setGoodsName((goods.getName()));
//            cart.setPicUrl(goods.getPicUrl());
//            cart.setPrice(product.getPrice());
//            cart.setSpecifications(product.getSpecifications());
//            cart.setUserId(userId);
//            cart.setChecked(true);
//            cartService.add(cart);
//        } else {
//            //取得规格的信息,判断规格库存
//            int num = existCart.getNumber() + number;
//            if (num > product.getNumber()) {
//                return ResponseUtil.fail(GOODS_NO_STOCK, "库存不足");
//            }
//            existCart.setNumber((short) num);
//            if (cartService.updateById(existCart) == 0) {
//                return ResponseUtil.updatedDataFailed();
//            }
//        }
//
//        return goodscount(userId);
//    }

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

            cart.setId(null);
            cart.setGoodsSn(goods.getGoodsSn());
            cart.setGoodsName((goods.getName()));
            cart.setPicUrl(goods.getPicUrl());
            cart.setPrice(product.getPrice());
            cart.setSpecifications(product.getSpecifications());
            cart.setUserId(userId);
            cart.setChecked(true);
//            cartService.add(cart);
            model.addAttribute("cart",cart);
            return "product/reconfirm_order";
        }
        return "login_old";
    }

    /**
     * 修改购物车商品货品数量
     *
     * @param userId 用户ID
     * @param cart   购物车商品信息， { id: xxx, goodsId: xxx, productId: xxx, number: xxx }
     * @return 修改结果
     */
//    @PostMapping("update")
//    public Object update(@LoginUser Integer userId, @RequestBody Cart cart) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//        if (cart == null) {
//            return ResponseUtil.badArgument();
//        }
//        Integer productId = cart.getProductId();
//        Integer number = cart.getNumber().intValue();
//        Integer goodsId = cart.getGoodsId();
//        Integer id = cart.getId();
//        if (!ObjectUtils.allNotNull(id, productId, number, goodsId)) {
//            return ResponseUtil.badArgument();
//        }
//        if(number <= 0){
//            return ResponseUtil.badArgument();
//        }
//
//        //判断是否存在该订单
//        // 如果不存在，直接返回错误
//        Cart existCart = cartService.findById(id);
//        if (existCart == null) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        // 判断goodsId和productId是否与当前cart里的值一致
//        if (!existCart.getGoodsId().equals(goodsId)) {
//            return ResponseUtil.badArgumentValue();
//        }
//        if (!existCart.getProductId().equals(productId)) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        //判断商品是否可以购买
//        Goods goods = goodsService.findById(goodsId);
//        if (goods == null || !goods.getIsOnSale()) {
//            return ResponseUtil.fail(GOODS_UNSHELVE, "商品已下架");
//        }
//
//        //取得规格的信息,判断规格库存
//        GoodsProduct product = productService.findById(productId);
//        if (product == null || product.getNumber() < number) {
//            return ResponseUtil.fail(GOODS_UNSHELVE, "库存不足");
//        }
//
//        existCart.setNumber(number.shortValue());
//        if (cartService.updateById(existCart) == 0) {
//            return ResponseUtil.updatedDataFailed();
//        }
//        return ResponseUtil.ok();
//    }

    /**
     * 购物车商品货品勾选状态
     * <p>
     * 如果原来没有勾选，则设置勾选状态；如果商品已经勾选，则设置非勾选状态。
     *
     * @param userId 用户ID
     * @param body   购物车商品信息， { productIds: xxx, isChecked: 1/0 }
     * @return 购物车信息
     */
//    @PostMapping("checked")
//    public Object checked(@LoginUser Integer userId, @RequestBody String body) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//        if (body == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        List<Integer> productIds = JacksonUtil.parseIntegerList(body, "productIds");
//        if (productIds == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        Integer checkValue = JacksonUtil.parseInteger(body, "isChecked");
//        if (checkValue == null) {
//            return ResponseUtil.badArgument();
//        }
//        Boolean isChecked = (checkValue == 1);
//
//        cartService.updateCheck(userId, productIds, isChecked);
//        return index(userId);
//    }

    /**
     * 购物车商品删除
     *
     * @param userId 用户ID
     * @param body   购物车商品信息， { productIds: xxx }
     * @return 购物车信息
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data: xxx
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
//    @PostMapping("delete")
//    public Object delete(Integer userId, @RequestBody String body) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//        if (body == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        List<Integer> productIds = JacksonUtil.parseIntegerList(body, "productIds");
//
//        if (productIds == null || productIds.size() == 0) {
//            return ResponseUtil.badArgument();
//        }
//
//        cartService.delete(productIds, userId);
//        return index(userId);
//    }

    /**
     * 购物车商品货品数量
     * <p>
     * 如果用户没有登录，则返回空数据。
     *
     * @param userId 用户ID
     * @return 购物车商品货品数量
     */
//    @GetMapping("goodscount")
//    public Object goodscount(Integer userId) {
//        if (userId == null) {
//            return ResponseUtil.ok(0);
//        }
//
//        int goodsCount = 0;
//        List<Cart> cartList = cartService.queryByUid(userId);
//        for (Cart cart : cartList) {
//            goodsCount += cart.getNumber();
//        }
//
//        return ResponseUtil.ok(goodsCount);
//    }

    /**
     * 购物车下单
     *
     * @param userId    用户ID
     * @param cartId    购物车商品ID：
     *                  如果购物车商品ID是空，则下单当前用户所有购物车商品；
     *                  如果购物车商品ID非空，则只下单当前购物车商品。
     * @param addressId 收货地址ID：
     *                  如果收货地址ID是空，则查询当前用户的默认地址。
     * @param couponId  优惠券ID：
     *                  如果优惠券ID是空，则自动选择合适的优惠券。
     * @return 购物车操作结果
     */
//    @GetMapping("checkout")
//    public Object checkout(Integer userId, Integer cartId, Integer addressId, Integer couponId, Integer grouponRulesId) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//
////        // 收货地址
////        Address checkedAddress = null;
////        if (addressId == null || addressId.equals(0)) {
////            checkedAddress = addressService.findDefault(userId);
////            // 如果仍然没有地址，则是没有收货地址
////            // 返回一个空的地址id=0，这样前端则会提醒添加地址
////            if (checkedAddress == null) {
////                checkedAddress = new Address();
////                checkedAddress.setId(0);
////                addressId = 0;
////            } else {
////                addressId = checkedAddress.getId();
////            }
////
////        } else {
////            checkedAddress = addressService.query(userId, addressId);
////            // 如果null, 则报错
////            if (checkedAddress == null) {
////                return ResponseUtil.badArgumentValue();
////            }
////        }
//
//        // 商品价格
//        List<Cart> checkedGoodsList = null;
//        if (cartId == null || cartId.equals(0)) {
//            checkedGoodsList = cartService.queryByUidAndChecked(userId);
//        } else {
//            Cart cart = cartService.findById(cartId);
//            if (cart == null) {
//                return ResponseUtil.badArgumentValue();
//            }
//            checkedGoodsList = new ArrayList<>(1);
//            checkedGoodsList.add(cart);
//        }
//        BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
//        for (Cart cart : checkedGoodsList) {
//            //  只有当团购规格商品ID符合才进行团购优惠
//            if (grouponRules != null && grouponRules.getGoodsId().equals(cart.getGoodsId())) {
//                checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().subtract(grouponPrice).multiply(new BigDecimal(cart.getNumber())));
//            } else {
//                checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
//            }
//        }
//
//        // 订单费用
//        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice).max(new BigDecimal(0.00));
//
//        BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("addressId", addressId);
//        data.put("couponId", couponId);
//        data.put("cartId", cartId);
//        data.put("grouponRulesId", grouponRulesId);
//        data.put("grouponPrice", grouponPrice);
//        data.put("checkedAddress", checkedAddress);
//        data.put("availableCouponLength", availableCouponLength);
//        data.put("goodsTotalPrice", checkedGoodsPrice);
//        data.put("freightPrice", freightPrice);
//        data.put("couponPrice", couponPrice);
//        data.put("orderTotalPrice", orderTotalPrice);
//        data.put("actualPrice", actualPrice);
//        data.put("checkedGoodsList", checkedGoodsList);
//        return ResponseUtil.ok(data);
//    }
}