package com.citi.ordermatching.service;

import com.citi.ordermatching.dao.HistoryMapper;
import com.citi.ordermatching.dao.OrderbookMapper;
import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.domain.History;
import com.citi.ordermatching.domain.Orderbook;
import com.citi.ordermatching.enums.OrderStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.citi.ordermatching.util.GenerateOrderid.generateOrderid;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderbookServiceTest {
    @Autowired
    private OrderbookService orderbookService;
    private OrderbookMapper orderbookMapper;
    private HistoryService historyService;
    private HistoryMapper historyMapper;
    History history = new History();
    Orderbook orderbook = new Orderbook();
    DealRecord dr = new DealRecord();
    private DealRecordService dealRecordService;
    Date date = new Date();

    @Test
    public void findBidBySymbol() {

        String BidSymbol = "IBM";
        List<Orderbook> findBidBySymbol = orderbookService.findBidBySymbol(BidSymbol);
        //  System.out.println(findBidBySymbol);
        ArrayList<String> strFindBid = new ArrayList<>();
        ArrayList<Integer> IdFindBid = new ArrayList<>();
        for (int i = 0; i < findBidBySymbol.size(); i++) {
            strFindBid.add(findBidBySymbol.get(i).getSymbol());
            IdFindBid.add(Integer.valueOf(findBidBySymbol.get(i).getId()));
            assertTrue(String.valueOf(strFindBid.contains(BidSymbol)), true);
            if (strFindBid.contains(BidSymbol)) {
                System.out.println("The BidSymbol is exist! And the id is " + IdFindBid.get(i));
            } else {
                System.out.println("The BidSymbol is not exist! ");
            }

        }
    }

    @Test
    public void findAskBySymbol() {
        String AskSymbol = "C";
        List<Orderbook> findAskBySymbol = orderbookService.findAskBySymbol(AskSymbol);
        //   System.out.println(findAskBySymbol);
        ArrayList<String> strFindAsk = new ArrayList<>();
        ArrayList<Integer> IdFindAsk = new ArrayList<>();
        for (int i = 0; i < findAskBySymbol.size(); i++) {
            strFindAsk.add(findAskBySymbol.get(i).getSymbol());
            IdFindAsk.add(Integer.valueOf(findAskBySymbol.get(i).getId()));
            assertTrue(String.valueOf(strFindAsk.contains(AskSymbol)), true);
            if (strFindAsk.contains(AskSymbol)) {
                System.out.println("The AskBySymbol is exist! And the id is " + IdFindAsk.get(i));
            } else {
                System.out.println("The AskBySymbol is not exist! ");
            }
        }
    }

    @Test
    public void addOrderbookItem() {
        orderbook.setType("BID");
        orderbook.setSymbol("Test");
        orderbook.setTraderid(Integer.valueOf("6"));
        orderbook.setPrice(Double.valueOf("111.1111"));
        orderbook.setStatus("CANCELLED");
        orderbook.setSize(1);
        orderbook.setOperatetime(date);
        orderbookService.addOrderbookItem(orderbook);
        System.out.println("success add ! And the symbol is " + orderbook.getSymbol());
    }

    @Test
    public void deleteOrderbookItem() {

        orderbook.setId(30);
        Boolean delete = orderbookService.deleteOrderbookItem(Integer.valueOf(orderbook.getId()));
        // System.out.println(delete);
        assertEquals(true, delete);
        System.out.println("success delete the id  " + orderbook.getId());
    }

    @Test
    public void processOrder() {
//        /*test Matching*/
//        orderbook.setStrategy("Matching");
//        orderbookService.processOrder(orderbook);
//        System.out.println(orderbook.getStrategy());
//        /*
//        @test MKT-ASK
//        */
//        orderbook.setStrategy("MKT");
//        orderbook.setSymbol("IBM");
//        orderbook.setType("ASK");
//         orderbookService.processOrder(orderbook);
//        System.out.println(orderbook.getId()+"222222222222");
//        /*
//        @test MKT-BID
//        */
//        orderbook.setStrategy("MKT");
//        orderbook.setType("BID");
//        orderbookService.processOrder(orderbook);
//        System.out.println(orderbook.getType()+"3333333333333333333");
//       orderbookService.processOrder(orderbook);
//      /*
//        @test LMT
//        */
//        orderbook.setStrategy("LMT");
//        orderbookService.processOrder(orderbook);
    }

    @Test
    public void processMatching() {
        history.setSymbol("IBM");
        //history.setId(80);
        history.setSize(45);
        history.setStrategy("Matching");
        history.setType("ASK");
        history.setTraderid(3);
        history.setStatus("WAITING");
        history.setCommittime(new Date());
        history.setPrice(219.98);
        //orderid调用静态方法生成
        history.setOrderid(generateOrderid(new Date(), history.getTraderid()));
        history.setDuration(null);
/*        orderbook.setSymbol(history.getSymbol());
        orderbook.setSize(history.getSize());
        orderbook.setStrategy(history.getStrategy());
        orderbook.setType(history.getType());
        orderbook.setTraderid(history.getTraderid());
        orderbook.setStatus(history.getStatus());
        orderbook.setOperatetime(history.getCommittime());
        orderbook.setPrice(history.getPrice());
        orderbook.setOrderid(history.getOrderid());*/
        orderbookService.processMatching(history);
        List<Orderbook> askList = orderbookService.findAskBySymbol(orderbook.getSymbol());
        for (int i = 0; i < askList.size(); i++) {
            System.out.println(askList.get(i).getStatus() + "<<<<<<<<");
        }
    }
    @Test
    public void processMKT() {
        history.setSymbol("IBM");
        //history.setId(81);
        history.setSize(45);
        history.setStrategy("MKT");
        history.setType("ASK");
        history.setTraderid(3);
        history.setStatus("WAITING");
        history.setCommittime(new Date());
        //history.setPrice(219.98);
        history.setOrderid(generateOrderid(new Date(), history.getTraderid()));


//        orderbook.setType("ASK");
        orderbookService.processMKT(history);
//        List<Orderbook> bidList=orderbookService.findBidBySymbol(history.getSymbol());
//        Orderbook bestBid = bidList.get(0);
//       System.out.println("the type is changed successfully!"+bestBid.getType());


    }

    @Test
    public void processLMT() {
        history.setSymbol("IBM");
        //history.setId(82);
        history.setSize(45);
        history.setStrategy("LMT");
        history.setType("ASK");
        history.setTraderid(3);
        history.setStatus("WAITING");
        history.setCommittime(new Date());
        history.setPrice(232.45);
        history.setOrderid(generateOrderid(new Date(), history.getTraderid()));

/*        orderbook.setId(history.getId());
        orderbook.setSymbol(history.getSymbol());
        orderbook.setSize(history.getSize());
        orderbook.setStrategy(history.getStrategy());
        orderbook.setType(history.getType());
        orderbook.setTraderid(history.getTraderid());
        orderbook.setStatus(history.getStatus());
        orderbook.setSymbol(history.getSymbol());
        orderbook.setOperatetime(history.getCommittime());
        orderbook.setPrice(history.getPrice());
        orderbook.setOrderid(history.getOrderid());*/

        orderbookService.processLMT(history);

    }
@Test
public void processSTP(){

}
    @Test
    public void findById() {
        orderbook.setTraderid(Integer.valueOf("2"));
        Orderbook ob = orderbookService.findById(orderbook.getTraderid());
        System.out.println(ob.getType());
    }


    @Test
    public void cancelOrder() {

        List<Orderbook> findOrder = orderbookService.findAllOrders();
        for (int i = 0; i < findOrder.size(); i++) {
            System.out.println("it's status is " + findOrder.get(i).getStatus());
            if (findOrder.get(i).getStatus().contains("CANCELLED")) {
                orderbookService.cancelOrder(orderbook);
                System.out.println("Successed cancel!");
            } else {
                System.out.println("The order can't be canceled");
            }
        }
    }
}