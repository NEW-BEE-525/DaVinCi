package com.project.davinci.controller;

import com.github.pagehelper.PageInfo;
//import com.mysql.jdbc.StringUtils;
import com.project.davinci.domain.*;
import com.project.davinci.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.linlinjava.litemall.core.system.SystemConfig;
//import org.linlinjava.litemall.core.validator.Order;
//import org.linlinjava.litemall.core.validator.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 商品服务
 */
@Controller
public class GoodsController {
	private final Log logger = LogFactory.getLog(GoodsController.class);

	@Resource
	private GoodsService goodsService;

	@Resource
	private GoodsProductService productService;

	@Resource
	private GoodsAttributeService goodsAttributeService;

	@Resource
    private AccountService accountService;

    @Resource
    private CartService cartService;
//
//
//	@Autowired
//	private FootprintService footprintService;
//
//	@Autowired
//	private CategoryService categoryService;
//
//	@Autowired
//	private SearchHistoryService searchHistoryService;
//
//	@Autowired
//	private GoodsSpecificationService goodsSpecificationService;
//
	private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

	private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

	private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);


    @GetMapping("/inProduct")
    public String viewProduct(){
        return "product/in_product.html";
    }

	@RequestMapping("/productDetail/{id}")
	public String detail(@PathVariable (value = "id") String id_str, Model model) throws Exception {
		Integer id = Integer.valueOf(id_str);
        Integer goodsId;
        if (id<1000000){
		    Cart cart = cartService.findById(id);
		    goodsId = cart.getGoodsId();
        }else {
            goodsId = id;
        }
		// 商品信息
		Goods info = goodsService.findById(goodsId);
		// 商品属性
		Callable<List> goodsAttributeListCallable = () -> goodsAttributeService.queryByGid(goodsId);

        // 商品规格对应的数量和价格
        Callable<List> productListCallable = () -> productService.queryByGid(goodsId);

//         //评论
//        Callable<Map> commentsCallable = () -> {
//            List<Comment> comments = commentService.queryGoodsByGid(id, 0, 2);
//            List<Map<String, Object>> commentsVo = new ArrayList<>(comments.size());
//            long commentCount = PageInfo.of(comments).getTotal();
//            for (Comment comment : comments) {
//                Map<String, Object> c = new HashMap<>();
//                c.put("id", comment.getId());
//                c.put("addTime", comment.getAddTime());
//                c.put("content", comment.getContent());
//                Account account = accountService.getAccount(comment.getUserId());
//                c.put("nickname", account == null ? "" : account.getUsername());
//                c.put("avatar", account == null ? "" : account.getAvatar());
//                c.put("picList", comment.getPicUrls());
//                commentsVo.add(c);
//            }
//            Map<String, Object> commentList = new HashMap<>();
//            commentList.put("count", commentCount);
//            commentList.put("data", commentsVo);
//            return commentList;
//        };
        FutureTask<List> goodsAttributeListTask = new FutureTask<>(goodsAttributeListCallable);
        FutureTask<List> productListCallableTask = new FutureTask<>(productListCallable);
//        FutureTask<Map> commentsCallableTsk = new FutureTask<>(commentsCallable);

        executorService.submit(goodsAttributeListTask);
//        executorService.submit(commentsCallableTsk);
        executorService.submit(productListCallableTask);

		model.addAttribute("info", info);
        model.addAttribute("attribute", goodsAttributeListTask.get());
        model.addAttribute("productList", productListCallableTask.get());
//        model.addAttribute("comment", commentsCallableTsk.get());
		return "product/product_info_detail";
	}



    @RequestMapping(value = "getGoodList", method = RequestMethod.POST)
    @ResponseBody
    public List<Goods> getGoofList(@RequestBody Map<String, String> map){
        Integer id = Integer.valueOf(map.get("id"));
        //商品列表
        List<Goods> goodsList = goodsService.queryByCategory(id, 0, 6);
        return goodsList;
    }


//	/**
//	 * 根据条件搜素商品
//	 * <p>
//	 * 1. 这里的前五个参数都是可选的，甚至都是空
//	 * 2. 用户是可选登录，如果登录，则记录用户的搜索关键字
//	 *
//	 * @param categoryId 分类类目ID，可选
//	 * @param brandId    品牌商ID，可选
//	 * @param keyword    关键字，可选
//	 * @param isNew      是否新品，可选
//	 * @param isHot      是否热买，可选
//	 * @param userId     用户ID
//	 * @param page       分页页数
//	 * @param limit       分页大小
//	 * @param sort       排序方式，支持"add_time", "retail_price"或"name"
//	 * @param order      排序类型，顺序或者降序
//	 * @return 根据条件搜素的商品详情
//	 */
	@GetMapping("list")
	public Object list(@RequestBody Map<String, String> map) {

        String keyword = map.get("keyword");
		//查询列表数据
		List<Goods> goodsList = goodsService.querySelective(null, null, keyword, null, null, 1, 10, "add_time", "desc");

		PageInfo<Goods> pagedList = PageInfo.of(goodsList);

		Map<String, Object> entity = new HashMap<>();
		entity.put("goodsList", goodsList);
		return null;
	}

//	/**
//	 * 商品详情页面“大家都在看”推荐商品
//	 *
//	 * @param id, 商品ID
//	 * @return 商品详情页面推荐商品
//	 */
//	@GetMapping("related")
//	public Object related(@NotNull Integer id) {
//		Goods goods = goodsService.findById(id);
//		if (goods == null) {
//			return ResponseUtil.badArgumentValue();
//		}
//
//		// 目前的商品推荐算法仅仅是推荐同类目的其他商品
//		int cid = goods.getCategoryId();
//
//		// 查找六个相关商品
//		int related = 6;
//		List<Goods> goodsList = goodsService.queryByCategory(cid, 0, related);
//		return ResponseUtil.okList(goodsList);
//	}

//	/**
//	 * 在售的商品总数
//	 *
//	 * @return 在售的商品总数
//	 */
//	@GetMapping("count")
//	public Object count() {
//		Integer goodsCount = goodsService.queryOnSale();
//		return ResponseUtil.ok(goodsCount);
//	}

}