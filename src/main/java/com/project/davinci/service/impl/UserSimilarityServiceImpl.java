package com.project.davinci.service.impl;


import com.project.davinci.domain.UserSimilarity;
import com.project.davinci.persistence.UserSimilarityMapper;
import com.project.davinci.service.UserSimilarityServive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类描述：对用户之间的相似度进行增删改查的服务实现类
 * 类名称：com.project.davinci.service.impl.UserSimilarityServiceImpl
 * @author 刘振宇
 * 2019年8月29日  11:05
 * @version V1.0
 */

@Service
public class UserSimilarityServiceImpl implements UserSimilarityServive {
    @Autowired
    private UserSimilarityMapper userSimilarityMapper;


    @Override
    public boolean saveUserSimilarity(UserSimilarity userSimilarity) {
        boolean flag = false;

        int rows = this.userSimilarityMapper.saveUserSimilarity(userSimilarity);
        if (rows > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean updateUserSimilarity(UserSimilarity userSimilarity) {
        boolean flag = false;

        int rows = this.userSimilarityMapper.updateUserSimilarity(userSimilarity);
        if (rows > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean isExistsUserSimilarity(UserSimilarity userSimilarity) {
        int count = this.userSimilarityMapper.countUserSimilarity(userSimilarity);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<UserSimilarity> listUserSimilarityByUId(Long userId) {
        if (userId == null) {
            return null;
        }
        List<UserSimilarity> userSimilarityList = this.userSimilarityMapper.listUserSimilarityByUId(userId);

        return userSimilarityList;
    }

}
