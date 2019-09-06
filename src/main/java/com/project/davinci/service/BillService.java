package com.project.davinci.service;

import com.project.davinci.domain.Bill;
import com.project.davinci.persistence.BillDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BillService {
    @Resource
    private BillDAO billDAO;

    public List<Bill> getBills(int user_id){
        return billDAO.getBillsByUserId(user_id);
    }

    public int addBill(Bill bill){
        return billDAO.insertBill(bill);
    }
}
