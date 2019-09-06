package com.project.davinci.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GoodsProductDAO {
    int addStock(@Param("id") Integer id, @Param("num") Short num);
    int reduceStock(@Param("id") Integer id, @Param("num") Short num);
}