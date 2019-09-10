package com.project.davinci.domain;
import java.io.Serializable;

/**
 * 类描述：用于存储用户的购买行为
 * 类名：com.project.davinci.domain.UserActive
 * @author 刘振宇
 * 2019年8月29日 9:40
 * version:V1.0
 */
public class UserActive implements Serializable {
    private static final long serialVersionUID = -103797500202536441L;

    // 用户id
    private Long user_id;

    // 二级类目的id
    private Long category2_id;

    // 该用户对该二级类目的点击量
    private Long hits;

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long userId) {
        this.user_id = userId;
    }

    public Long getCategory2Id() {
        return category2_id;
    }

    public void setCategory2Id(Long category2Id) {
        this.category2_id = category2Id;
    }

    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
    }
}
