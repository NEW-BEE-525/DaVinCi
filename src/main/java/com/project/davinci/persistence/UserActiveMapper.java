package com.project.davinci.persistence;


import com.project.davinci.domain.UserActive;

import java.util.List;

/**
 * 类描述：操作数据库中用户的行为数据
 * 类名称：com.project.davinci.persistence.UserActiveMapper
 * @author 刘振宇
 * 2019年8月29日 10:20
 */

public interface UserActiveMapper {

    /**
     * 查询出所有的用户行为
     * @return 返回用户的行为数据
     */
    List<UserActive> listAllUserActive();

    /**
     * 根据用户已有的行为信息获取它对某个二级类目的点击量
     * @param userActive 用户的行为数据
     * @return 某个用户对某个二级类目的点击量
     */
    int getHitsByUserActiveInfo(UserActive userActive);

    /**
     * 统计某个用户的行为记录的条数
     * @param userActive 要查询的用户的行为记录的条件
     * @return 1就说明存在这个用户的行为，0说明不存在
     */
    int countUserActive(UserActive userActive);

    /**
     * 向用户行为表中添加一条用户的行为记录
     * @param userActive 用户的行为数据
     * @return 受影响的行数，1表示插入成功，0表示插入失败
     */
    int saveUserActive(UserActive userActive);

    /**
     * 更新用户对某个二级类目的点击量
     * @param userActive 用户的浏览行为数据
     * @return 1表示更新成功，0表示更新失败
     */
    int updateUserActive(UserActive userActive);
}
