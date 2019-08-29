package com.project.davinci.service;
import com.project.davinci.domain.UserActive;

import java.util.List;

/**
 * 类描述：对用户购物行为记录进行增删改查的服务接口
 * 类名称：com.project.davinci.persistence.UserActiveService
 * @author 刘振宇
 * 2019年8月29日 10:20
 */

public interface UserActiveService {
    /**
     * 查询出所有的用户行为
     * @return 返回用户的行为数据
     */
    List<UserActive> listAllUserActive();

    /**
     * 保存用户的浏览记录，如果用户的浏览记录存在则更新，不存在则添加
     * @param userActive 用户的行为数据
     * @return true表示更新成功，false表示更新失败
     */
    boolean saveUserActive(UserActive userActive);
}
