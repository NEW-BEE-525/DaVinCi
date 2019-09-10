package com.project.davinci.persistence;

import com.project.davinci.domain.Bill;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BillDAO {

    List<Bill> getBillsByUserId(int id);

    int insertBill(Bill bill);
}
