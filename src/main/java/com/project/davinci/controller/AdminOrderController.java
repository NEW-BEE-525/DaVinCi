package com.project.davinci.controller;

import com.project.davinci.domain.Order;
import com.project.davinci.service.AdminOrderService;
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
@RequestMapping("/admin/order")
@Validated
public class AdminOrderController {
    private final Log logger = LogFactory.getLog(AdminOrderController.class);

    @Autowired
    private AdminOrderService adminOrderService;

//    @RequiresPermissions("admin:order:list")
//    @RequiresPermissionsDesc(menu = {"商场管理", "订单管理"}, button = "查询")
    @GetMapping("")
    public String list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort,
                       @RequestParam(defaultValue = "desc") String order, Model model) {
        List<Order> orderList = adminOrderService.list(null, null, null, page, limit, sort, order);
        model.addAttribute("orderList",orderList);
        return "manage/orders_manage.html";
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
//    @RequiresPermissions("admin:order:read")
//    @RequiresPermissionsDesc(menu = {"商场管理", "订单管理"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        return adminOrderService.detail(id);
    }
//
//    /**
//     * 订单退款
//     *
//     * @param body 订单信息，{ orderId：xxx }
//     * @return 订单退款操作结果
//     */
////    @RequiresPermissions("admin:order:refund")
////    @RequiresPermissionsDesc(menu = {"商场管理", "订单管理"}, button = "订单退款")
//    @PostMapping("/refund")
//    public Object refund(@RequestBody String body) {
//        return adminOrderService.refund(body);
//    }
//


    @GetMapping("/ship/{id}")
    public String ship(@PathVariable (value = "id") String id_str,Model model) {
        Integer orderId = Integer.valueOf(id_str);
        adminOrderService.ship(orderId);
        Integer page = 1;
        Integer limit = 10;
        String sort = "add_time";
        String order = "desc";
        List<Order> orderList = adminOrderService.list(null, null, null, page, limit, sort, order);
        model.addAttribute("orderList",orderList);
        return "manage/orders_manage.html";
    }
//
//
//    /**
//     * 回复订单商品
//     *
//     * @param body 订单信息，{ orderId：xxx }
//     * @return 订单操作结果
//     */
////    @RequiresPermissions("admin:order:reply")
////    @RequiresPermissionsDesc(menu = {"商场管理", "订单管理"}, button = "订单商品回复")
//    @PostMapping("/reply")
//    public Object reply(@RequestBody String body) {
//        return adminOrderService.reply(body);
//    }

}
