package com.citi.ordermatching.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.citi.ordermatching.dao.DealRecordMapper;
import com.citi.ordermatching.dao.HistoryMapper;
import com.citi.ordermatching.dao.OrderbookMapper;
import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.domain.History;
import com.citi.ordermatching.domain.Orderbook;
import com.citi.ordermatching.domain.RecordOrder;
import com.citi.ordermatching.enums.OrderStatus;
import com.citi.ordermatching.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dell on 2018/8/14.
 */

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private DealRecordMapper dealRecordMapper;

    @Autowired
    private OrderbookMapper orderbookMapper;

    @Override
    public String findAllHistory() {



        List<History> historyList=historyMapper.findAllHistory();
        List<DealRecord> executionList=new ArrayList<>();
        List<Orderbook>  ordersList=new ArrayList<>();

        List<RecordOrder> recordOrderList=new ArrayList<>();


        for(History history:historyList){
            RecordOrder recordOrder=new RecordOrder();

            recordOrder.setHistory(history);
            String orderid=history.getOrderid();
            String type=history.getType();
            Orderbook orderbook=orderbookMapper.selectByOrderid(orderid);
            int id=orderbook.getId();
            if(type.equals("BID")){
                List<DealRecord> dealRecords=dealRecordMapper.findAllDealRecordByBidId(orderid);
                recordOrder.setDealRecordList(dealRecords);
                executionList.addAll(dealRecords);

            }else if(type.equals("ASK")){
                List<DealRecord> dealRecords=dealRecordMapper.findAllDealRecordByAskId(orderid);
                recordOrder.setDealRecordList(dealRecords);
                executionList.addAll(dealRecords);
            }
            if(orderbook.getStatus().equals(OrderStatus.CANCELLED.toString())||
                    orderbook.getStatus().equals(OrderStatus.WAITING.toString())){
                recordOrder.setOrderbook(orderbook);
                ordersList.add(orderbook);
            }else{
                recordOrder.setOrderbook(null);
            }

            recordOrderList.add(recordOrder);

        }

        Map map=new HashMap();
        map.put("executionList",executionList);
        map.put("ordersList",ordersList);
        map.put("historyList",recordOrderList);

        String jsonResult= JSONObject.toJSON(map).toString();

        return jsonResult;

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
