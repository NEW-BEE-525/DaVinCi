package com.project.davinci.service;

import java.util.List;
import com.project.davinci.domain.UserSimilarity;

/**
 * 类描述：对用户之间的相似度进行增删改查的服务接口
 * 类名称：com.project.davinci.service.UserSimilarityService
 * @author 刘振宇
 * 2019年8月29日  10:50
 * @version V1.0
 */
public interface UserSimilarityServive {
    /**
     * 新增用户相似度数据
     * @param userSimilarity 用户相似度数据
     * @return true则新增用户相似度成功，false则失败
     */
    boolean saveUserSimilarity(UserSimilarity userSimilarity);

    /**
     * 更新用户相似度数据
     * @param userSimilarity 用户相似度数据
     * @return 受影响到的行数，0表示影响0行，1表示影响1行
     */
    boolean updateUserSimilarity(UserSimilarity userSimilarity);

    /**
     * 判断某两个用户之间的相似度是否已经存在
     * @param userSimilarity 存储两个用户的id
     * @return true表示已经存在，false表示不存在
     */
    boolean isExistsUserSimilarity(UserSimilarity userSimilarity);

    /**
     * 查询某个用户与其他用户之间的相似度列表
     * @param userId 带查询的用户id
     * @return 该用户与其他用户的相似度列表
     */
    List<UserSimilarity> listUserSimilarityByUId(Long userId);
}
