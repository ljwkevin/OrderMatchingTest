package com.citi.ordermatching.serviceImpl;

import com.citi.ordermatching.dao.StockMapper;
import com.citi.ordermatching.domain.Stock;
import com.citi.ordermatching.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Dell on 2018/8/12.
 */

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    @Override
    public List<Stock> findAllStocks() {
        return stockMapper.findAllStocks();
    }

    @Override
    public Stock findStockByParam(String param) {
        return stockMapper.findStockByParam(param);
    }


}
