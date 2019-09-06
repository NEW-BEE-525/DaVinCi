package com.project.davinci.service;

import com.project.davinci.domain.Address;
import com.project.davinci.persistence.AddressDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AddressService {
    @Resource
    private AddressDAO addressDAO;

    int addAddress(Address address){
        return addressDAO.insertAddress(address);
    }

}
