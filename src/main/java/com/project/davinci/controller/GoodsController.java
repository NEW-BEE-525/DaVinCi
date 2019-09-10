package com.project.davinci.controller;

import com.github.pagehelper.PageInfo;
import com.project.davinci.domain.*;
import com.project.davinci.service.*;
import com.project.davinci.utils.RecommendUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.*;
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
	private UserActiveService userActiveService;

    @Resource
    private CartService cartService;
//
	private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

	private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

	private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);


    @GetMapping("/inProduct")
    public String viewProduct(){
        return "product/in_product.html";
    }

    @GetMapping("/outProduct")
	public String viewOutProduct(){return "product/out_product.html";}

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

        FutureTask<List> goodsAttributeListTask = new FutureTask<>(goodsAttributeListCallable);
        FutureTask<List> productListCallableTask = new FutureTask<>(productListCallable);

        executorService.submit(goodsAttributeListTask);
        executorService.submit(productListCallableTask);

		model.addAttribute("info", info);
        model.addAttribute("attribute", goodsAttributeListTask.get());
        model.addAttribute("productList", productListCallableTask.get());
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


	@PostMapping("searchGoods")
	@ResponseBody
	public List<Goods> list(@RequestBody Map<String, String> map) {

        String keyword = map.get("keyword");
		//查询列表数据
		List<Goods> goodsList = goodsService.querySelective(null, null, keyword, null, null, 1, 10, "add_time", "desc");

//		PageInfo<Goods> pagedList = PageInfo.of(goodsList);

		return goodsList;
	}
	@RequestMapping(value = "recommend_goods",method = RequestMethod.GET)
	@ResponseBody
	public List<Goods>  recommend_goods(HttpSession session){
		//当前登录的用户
		Account account=(Account)session.getAttribute("account");
        List<Goods> goodsList1 = new ArrayList<>(5);
		if (account==null){
            List<Goods> goodsList = goodsService.queryByCategory(1008009,1,10);
            for (int i=0;i<5;i++){
                goodsList1.add(goodsList.get(i));
            }
		    return goodsList1;
        }
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
		Map<Long,Integer> map = new HashMap<>(6);
		for(long recommeddateProduct:recommeddateProductList){
			if(map.containsKey(recommeddateProduct)){
				Integer count = map.get(recommeddateProduct);
				count++;
				map.put(recommeddateProduct,count);
			}
			else {
				map.put(recommeddateProduct,1);
			}
		}
		Set set=map.entrySet();

		Iterator iterator=set.iterator();

		while (iterator.hasNext()) {

			Map.Entry mapentry = (Map.Entry) iterator.next();
			List<Goods> goodsList = goodsService.queryByCategory(Integer.valueOf(mapentry.getKey().toString()),1,10);
			for(int i = 0;i < Integer.valueOf(mapentry.getValue().toString());i++){
				goodsList1.add(goodsList.get(i));
			}

		}
			return goodsList1;
	}


}