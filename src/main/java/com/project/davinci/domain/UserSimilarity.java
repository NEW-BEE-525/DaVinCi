package com.project.davinci.domain;
import java.io.Serializable;

/**
 * 类描述：存储用户与用户之间的相似度
 * 类名称：com.project.davinci.domain.UsersSimilarity
 * @author 刘振宇
 * 2019年8月29日 9:58
 * @version V1.0
 */

public class UserSimilarity implements Serializable, Comparable<UserSimilarity>{
    private static final long serialVersionUID = 3940726816248544380L;

    // 用户id
    private Long userId;

    // 进行比较的用户id
    private Long userRefId;

    // userId与userRefId之间的相似度
    private Double similarity;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserRefId() {
        return userRefId;
    }

    public void setUserRefId(Long userRefId) {
        this.userRefId = userRefId;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    @Override
    public int compareTo(UserSimilarity o) {
        return o.getSimilarity().compareTo(this.getSimilarity());
    }
}
