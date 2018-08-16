package com.citi.ordermatching.dao;

import com.citi.ordermatching.domain.History;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;


@Mapper
public interface HistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(History record);

    int insertSelective(History record);

    History selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(History record);

    int updateByPrimaryKey(History record);

    List<History> findAllHistory(int userid);

    History selectByOrderid(String orderid);

    ArrayList<History> selectBitSTPWaitingByPriceAscByTimeAsc(String symbol);

    ArrayList<History> selectAskSTPWaitingByPriceDescByTimeAsc(String symbol);

    int updateByOrderidSelective(History history);

    ArrayList<History> selectBitMITWaitingByPriceDescByTimeAsc(String symbol);

    ArrayList<History> selectAskMITWaitingByPriceAscByTimeAsc(String symbol);
}