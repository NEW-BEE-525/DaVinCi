package com.project.davinci.service;

import com.github.pagehelper.PageHelper;
import com.project.davinci.domain.Footprint;
import com.project.davinci.domain.FootprintExample;
import com.project.davinci.persistence.FootprintDAO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FootprintService {
    @Resource
    private FootprintDAO footprintMapper;

    public List<Footprint> queryByAddTime(Integer userId, Integer page, Integer size) {
        FootprintExample example = new FootprintExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        example.setOrderByClause(Footprint.Column.addTime.desc());
        PageHelper.startPage(page, size);
        return footprintMapper.selectByExample(example);
    }

    public Footprint findById(Integer id) {
        return footprintMapper.selectByPrimaryKey(id);
    }

    public void deleteById(Integer id) {
        footprintMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(Footprint footprint) {
        footprint.setAddTime(LocalDateTime.now());
        footprint.setUpdateTime(LocalDateTime.now());
        footprintMapper.insertSelective(footprint);
    }

    public List<Footprint> querySelective(String userId, String goodsId, Integer page, Integer size, String sort, String order) {
        FootprintExample example = new FootprintExample();
        FootprintExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return footprintMapper.selectByExample(example);
    }
}
