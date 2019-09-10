package com.project.davinci.controller;

import com.project.davinci.domain.Goods;
import com.project.davinci.domain.GoodsAllinone;
import com.project.davinci.service.AdminGoodsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
@RequestMapping("/admin/goods")
@Validated
public class AdminGoodsController {
    private final Log logger = LogFactory.getLog(AdminGoodsController.class);

    @Autowired
    private AdminGoodsService adminGoodsService;

    @GetMapping("")
    public String showGoods() {
        return "manage/search_product_manage";
    }

    @PostMapping("/list/")
    public String list(@RequestParam("search") Integer catId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit, Model model) {
        List<Goods> goodsList = adminGoodsService.list(catId, page, limit);
        model.addAttribute("goodsList", goodsList);
        return "manage/products_manage";
    }

    @GetMapping("/catAndBrand")
    public Object list2() {
        return adminGoodsService.list2();
    }

    /**
     * 编辑商品
     *
     * @param goodsAllinone
     * @return
     */
//    @RequiresPermissions("admin:goods:update")
//    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody GoodsAllinone goodsAllinone) {
        return adminGoodsService.update(goodsAllinone);
    }

    /**
     * 删除商品
     *
     * @param goods
     * @return
     */
//    @RequiresPermissions("admin:goods:delete")
//    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody Goods goods) {
        return adminGoodsService.delete(goods);
    }

    /**
     * 添加商品
     *
     * @param goodsAllinone
     * @return
     */
//    @RequiresPermissions("admin:goods:create")
//    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "上架")
    @PostMapping("/create")
    public Object create(@RequestBody GoodsAllinone goodsAllinone) {
        return adminGoodsService.create(goodsAllinone);
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
//    @RequiresPermissions("admin:goods:read")
//    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        return adminGoodsService.detail(id);

    }

}
