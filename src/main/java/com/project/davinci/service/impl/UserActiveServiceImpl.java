package com.project.davinci.service.impl;

import com.project.davinci.domain.UserActive;
import com.project.davinci.persistence.UserActiveDAO;
import com.project.davinci.service.UserActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 类描述：用户的浏览行为服务接口的具体实现类
 * 类名称：com.project.davinci.service.impl.UserActiveServiceImpl
 * @author 刘振宇
 * 2019年8月29日  10:25
 * @version V1.0
 */

@Service
public class UserActiveServiceImpl implements UserActiveService {

    @Autowired
    private UserActiveDAO userActiveDAO;

    @Override
    public List<UserActive> listAllUserActive() {
        return userActiveDAO.listAllUserActive();
    }

    //当数据库操作出现错误，会自动回滚
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    @Override
    public boolean saveUserActive(UserActive userActive) {
        boolean flag = false;
        // 1.先判断数据库中是否存在当前用户的浏览记录
        int rows = this.userActiveDAO.countUserActive(userActive);
        int saveRows = 0;
        int updateRows = 0;
        // 2.不存在就添加
        if (rows < 1) { // 不存在
            userActive.setHits(1L); // 不存在记录的话那肯定是第一次访问，那点击量肯定是1
            saveRows = this.userActiveDAO.saveUserActive(userActive);
        } else { // 已经存在
            // 3.存在则先把当前用户对当前二级类目的点击量取出来+1
            long hits = this.userActiveDAO.getHitsByUserActiveInfo(userActive);
            // 4.然后在更新用户的浏览记录
            hits++;
            userActive.setHits(hits);
            updateRows = this.userActiveDAO.updateUserActive(userActive);
        }
        if (saveRows > 0 || updateRows > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public int isExistUserActive(UserActive userActive) {
        return userActiveDAO.countUserActive(userActive);
    }

}
