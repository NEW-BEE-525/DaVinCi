package com.project.davinci.persistence;

import java.util.List;

import com.project.davinci.domain.Order;
import com.project.davinci.domain.OrderExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DaOrderDAO {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    long countByExample(OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    int deleteByExample(OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    int insert(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    int insertSelective(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    Order selectOneByExample(OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    Order selectOneByExampleSelective(@Param("example") OrderExample example, @Param("selective") Order.Column... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    List<Order> selectByExampleSelective(@Param("example") OrderExample example, @Param("selective") Order.Column... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    List<Order> selectByExample(OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    Order selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") Order.Column... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    Order selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    Order selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") Order record, @Param("example") OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") Order record, @Param("example") OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    int logicalDeleteByExample(@Param("example") OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table _order
     *
     * @mbg.generated
     */
    int logicalDeleteByPrimaryKey(Integer id);
}