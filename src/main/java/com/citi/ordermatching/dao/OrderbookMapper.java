package com.citi.ordermatching.dao;

import com.citi.ordermatching.domain.Orderbook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderbookMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Orderbook record);

    int insertSelective(Orderbook record);

    Orderbook selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Orderbook record);

    int updateByPrimaryKey(Orderbook record);

    List<Orderbook> findAskBySymbol(String symbol);
    List<Orderbook> findBidBySymbol(String symbol);
}