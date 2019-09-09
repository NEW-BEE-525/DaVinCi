package com.project.davinci.controller;

import com.github.pagehelper.PageInfo;
//import com.mysql.jdbc.StringUtils;
import com.project.davinci.domain.*;
import com.project.davinci.service.*;
import com.project.davinci.utils.RecommendUtils;
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
import javax.servlet.http.HttpSession;
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
	private CommentService commentService;

    @Autowired
    private UserActiveService userActiveService;
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
		// 商品信息
		Goods info = goodsService.findById(id);

		// 商品属性
		Callable<List> goodsAttributeListCallable = () -> goodsAttributeService.queryByGid(id);

        // 商品规格对应的数量和价格
        Callable<List> productListCallable = () -> productService.queryByGid(id);

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
    public List<Goods> getGoofList(@RequestBody Map<String, String> map, HttpSession session){
        Integer id = Integer.valueOf(map.get("id"));
        List<Goods> goodsList = goodsService.queryByCategory(id, 0, 6);
        UserActive userActive=new UserActive();
        Account account=(Account)session.getAttribute("account");
        userActive.setUserId(Long.valueOf(account.getId()));
        userActive.setCategory2Id(Long.valueOf(id));
        userActiveService.saveUserActive(userActive);
        return goodsList;
    }

    @RequestMapping(value = "recommend_goods",method = RequestMethod.GET)
    @ResponseBody
    public void recommend_goods(HttpSession session){
        //当前登录的用户
        Account account=(Account)session.getAttribute("account");
//        UserActive userActive=new UserActive();
//        userActive.setUserId(Long.valueOf(account.getId()));
//        userActive.setCategory2Id(Long.valueOf(0));
//        int i=userActiveService.isExistUserActive(userActive);
//        if (i==0){
//            //新用户，返回指定的商品
//        }
//        else {
            //根据与其他用户的相似度生成商品清单
            /*
            1、查找此用户和数据库中用户查询记录
            2、组装好的用户的查询记录的map集合
            3、计算用户查询记录的相似度
            4、选择topN的用户查询记录
            5、返回这N个用的查询记录
             */
            List<UserActive> userActiveList=userActiveService.listAllUserActive();
            ConcurrentHashMap<Long, ConcurrentHashMap<Long, Long>> activeMap= RecommendUtils.assembleUserBehavior(userActiveList);
            List<UserSimilarity> similarityList=RecommendUtils.calcSimilarityBetweenUsers(activeMap);
            List<Long> UserSimilarityList=RecommendUtils.getSimilarityBetweenUsers(Long.valueOf(account.getId()),similarityList,5);
            List<Long> recommeddateProductList=RecommendUtils.getRecommendateCategory2(Long.valueOf(account.getId()),UserSimilarityList,userActiveList);
            System.out.println(recommeddateProductList);
       // }
    }

//
//		// 记录用户的足迹 异步处理
//		if (userId != null) {
//			executorService.execute(()->{
//				Footprint footprint = new Footprint();
//				footprint.setUserId(userId);
//				footprint.setGoodsId(id);
//				footprintService.add(footprint);
//			});
//		}
//
//		FutureTask<Object> objectCallableTask = new FutureTask<>(objectCallable);
//
//		FutureTask<List> issueCallableTask = new FutureTask<>(issueCallable);
//
//		FutureTask<Brand> brandCallableTask = new FutureTask<>(brandCallable);
//        FutureTask<List> grouponRulesCallableTask = new FutureTask<>(grouponRulesCallable);
//
//
//		executorService.submit(objectCallableTask);
//
//		executorService.submit(issueCallableTask);
//
//		executorService.submit(brandCallableTask);
//		executorService.submit(grouponRulesCallableTask);
//
//		Map<String, Object> data = new HashMap<>();
//
//		try {
//			data.put("info", info);
//			data.put("userHasCollect", userHasCollect);
//			data.put("issue", issueCallableTask.get());
//
//			data.put("specificationList", objectCallableTask.get());
//
//
//			data.put("brand", brandCallableTask.get());
//			data.put("groupon", grouponRulesCallableTask.get());
//			//SystemConfig.isAutoCreateShareImage()
//			data.put("share", SystemConfig.isAutoCreateShareImage());
//
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		//商品分享图片地址
//		data.put("shareImage", info.getShareUrl());
//		return ResponseUtil.ok(data);
//	}

//	/**
//	 * 商品分类类目
//	 *
//	 * @param id 分类类目ID
//	 * @return 商品分类类目
//	 */
//	@GetMapping("category")
//	public Object category(@NotNull Integer id) {
//		Category cur = categoryService.findById(id);
//		Category parent = null;
//		List<Category> children = null;
//
//		if (cur.getPid() == 0) {
//			parent = cur;
//			children = categoryService.queryByPid(cur.getId());
//			cur = children.size() > 0 ? children.get(0) : cur;
//		} else {
//			parent = categoryService.findById(cur.getPid());
//			children = categoryService.queryByPid(cur.getPid());
//		}
//		Map<String, Object> data = new HashMap<>();
//		data.put("currentCategory", cur);
//		data.put("parentCategory", parent);
//		data.put("brotherCategory", children);
//		return ResponseUtil.ok(data);
//	}

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
//	@GetMapping("list")
//	public Object list(
//		Integer categoryId,
//		Integer brandId,
//		String keyword,
//		Boolean isNew,
//		Boolean isHot,
//		@LoginUser Integer userId,
//		@RequestParam(defaultValue = "1") Integer page,
//		@RequestParam(defaultValue = "10") Integer limit,
//		@Sort(accepts = {"add_time", "retail_price", "name"}) @RequestParam(defaultValue = "add_time") String sort,
//		@Order @RequestParam(defaultValue = "desc") String order) {
//
//		//添加到搜索历史
//		if (userId != null && !StringUtils.isNullOrEmpty(keyword)) {
//			LitemallSearchHistory searchHistoryVo = new LitemallSearchHistory();
//			searchHistoryVo.setKeyword(keyword);
//			searchHistoryVo.setUserId(userId);
//			searchHistoryVo.setFrom("wx");
//			searchHistoryService.save(searchHistoryVo);
//		}
//
//		//查询列表数据
//		List<Goods> goodsList = goodsService.querySelective(categoryId, brandId, keyword, isHot, isNew, page, limit, sort, order);
//
//		// 查询商品所属类目列表。
//		List<Integer> goodsCatIds = goodsService.getCatIds(brandId, keyword, isHot, isNew);
//		List<Category> categoryList = null;
//		if (goodsCatIds.size() != 0) {
//			categoryList = categoryService.queryL2ByIds(goodsCatIds);
//		} else {
//			categoryList = new ArrayList<>(0);
//		}
//
//		PageInfo<Goods> pagedList = PageInfo.of(goodsList);
//
//		Map<String, Object> entity = new HashMap<>();
//		entity.put("list", goodsList);
//		entity.put("total", pagedList.getTotal());
//		entity.put("page", pagedList.getPageNum());
//		entity.put("limit", pagedList.getPageSize());
//		entity.put("pages", pagedList.getPages());
//		entity.put("filterCategoryList", categoryList);
//
//		// 因为这里需要返回额外的filterCategoryList参数，因此不能方便使用ResponseUtil.okList
//		return ResponseUtil.ok(entity);
//	}

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