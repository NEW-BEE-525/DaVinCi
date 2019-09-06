package com.project.davinci.persistence;

import com.project.davinci.domain.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressDAO {
    int insertAddress (Address address);

}
