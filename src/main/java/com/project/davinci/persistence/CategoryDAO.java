package com.project.davinci.persistence;

import java.util.List;

import com.project.davinci.domain.Category;
import com.project.davinci.domain.CategoryExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CategoryDAO {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    long countByExample(CategoryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    int deleteByExample(CategoryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    int insert(Category record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    int insertSelective(Category record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    Category selectOneByExample(CategoryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    Category selectOneByExampleSelective(@Param("example") CategoryExample example, @Param("selective") Category.Column... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    List<Category> selectByExampleSelective(@Param("example") CategoryExample example, @Param("selective") Category.Column... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    List<Category> selectByExample(CategoryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    Category selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") Category.Column... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    Category selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    Category selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") Category record, @Param("example") CategoryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") Category record, @Param("example") CategoryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Category record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Category record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    int logicalDeleteByExample(@Param("example") CategoryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_category
     *
     * @mbg.generated
     */
    int logicalDeleteByPrimaryKey(Integer id);
}