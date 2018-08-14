package com.citi.ordermatching.controller;

import com.alibaba.fastjson.JSON;
import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.domain.History;
import com.citi.ordermatching.service.DealRecordService;
import com.citi.ordermatching.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dell on 2018/8/14.
 */
@RestController
@RequestMapping("/record")
public class RecordController {

    @Autowired
    private DealRecordService dealRecordService;

    @Autowired
    private HistoryService historyService;

    @RequestMapping("/findAllRecord")
    public String findAllRecord(){
        List<DealRecord> recordList=dealRecordService.findAllRecord();
        String jsonResult= JSON.toJSONString(recordList);
        return jsonResult;
    }

    @RequestMapping("/findHistoryAndRecord")
    @ResponseBody
    public String findHistoryAndRecord(){
        List<DealRecord> recordList=dealRecordService.findAllRecord();
        List<History> historyList=historyService.findAllHistory();
        Map map=new HashMap();
        map.put("recordList",recordList);
        map.put("historyList",historyList);

        String jsonResult=JSON.toJSONString(map);
        return jsonResult;

    }
}
