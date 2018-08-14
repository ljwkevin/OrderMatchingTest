package com.citi.ordermatching.dao;

import com.citi.ordermatching.domain.DealRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DealRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DealRecord record);

    int insertSelective(DealRecord record);

    DealRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DealRecord record);

    int updateByPrimaryKey(DealRecord record);
}