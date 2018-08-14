package com.citi.ordermatching.service;

import com.citi.ordermatching.domain.History;

import java.util.List;

/**
 * Created by Dell on 2018/8/14.
 */
public interface HistoryService {

    public List<History> findAllHistory();

    public void insertHistory(History history);

    public History findById(int id);

    public boolean cancelOrder(History history);
}
