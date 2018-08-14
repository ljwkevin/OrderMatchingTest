package com.citi.ordermatching.serviceImpl;

import com.citi.ordermatching.dao.HistoryMapper;
import com.citi.ordermatching.domain.History;
import com.citi.ordermatching.enums.OrderStatus;
import com.citi.ordermatching.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Dell on 2018/8/14.
 */

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    @Override
    public List<History> findAllHistory() {
        return historyMapper.findAllHistory();
    }

    @Override
    public void insertHistory(History history) {
        historyMapper.insertSelective(history);
    }

    @Override
    public History findById(int id) {
        return historyMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean cancelOrder(History history) {
        history.setStatus(OrderStatus.CANCELLED.toString());
        return historyMapper.updateByPrimaryKeySelective(history)>0 ? true : false;
    }
}
