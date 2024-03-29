package com.project.davinci.service;

import com.github.pagehelper.PageHelper;
import com.project.davinci.domain.Category;
import com.project.davinci.domain.CategoryExample;
import com.project.davinci.persistence.CategoryDAO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {
    @Resource
    private CategoryDAO categoryDAO;
    private Category.Column[] CHANNEL = {Category.Column.id, Category.Column.name, Category.Column.iconUrl};

    public List<Category> queryL1WithoutRecommend(int offset, int limit) {
        CategoryExample example = new CategoryExample();
        example.or().andLevelEqualTo("L1").andNameNotEqualTo("推荐").andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        return categoryDAO.selectByExample(example);
    }

    public List<Category> queryL1(int offset, int limit) {
        CategoryExample example = new CategoryExample();
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        return categoryDAO.selectByExample(example);
    }

    public List<Category> queryL1() {
        CategoryExample example = new CategoryExample();
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        System.out.println(categoryDAO);
        return categoryDAO.selectByExample(example);
    }

    public List<Category> queryByPid(Integer pid) {
        CategoryExample example = new CategoryExample();
        example.or().andPidEqualTo(pid).andDeletedEqualTo(false);
        return categoryDAO.selectByExample(example);
    }

    public List<Category> queryL2ByIds(List<Integer> ids) {
        CategoryExample example = new CategoryExample();
        example.or().andIdIn(ids).andLevelEqualTo("L2").andDeletedEqualTo(false);
        return categoryDAO.selectByExample(example);
    }

    public Category findById(Integer id) {
        return categoryDAO.selectByPrimaryKey(id);
    }

    public List<Category> querySelective(String id, String name, Integer page, Integer size, String sort, String order) {
        CategoryExample example = new CategoryExample();
        CategoryExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return categoryDAO.selectByExample(example);
    }

    public int updateById(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        return categoryDAO.updateByPrimaryKeySelective(category);
    }


    public void deleteById(Integer id) {
        categoryDAO.logicalDeleteByPrimaryKey(id);
    }

    public void add(Category category) {
        category.setAddTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryDAO.insertSelective(category);
    }

    public List<Category> queryChannel() {
        CategoryExample example = new CategoryExample();
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        return categoryDAO.selectByExampleSelective(example, CHANNEL);
    }
}
