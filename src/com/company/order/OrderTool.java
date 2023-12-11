package com.company.order;

import com.company.Util.DateUtils;
import com.company.model.Constant;
import com.company.model.OrderLimit;
import com.company.model.OrderMarket;
import com.company.service.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class OrderTool {

    //检查深度

    //检查盘口是否偏离预设目标价

    //检查自己的资金(指定币种就可以同时查自己的资金和持仓)
//    @Test
//    public void checkAsset() throws JsonProcessingException {
    public String checkAsset(String symbol){
        ObjectMapper mapper = new ObjectMapper();
//        获取账户中BTC、ETH两种资产余额
//        GET /api/v5/account/balance?ccy=BTC,USDT
        String path = "/api/v5/account/balance";
        String queryString = "ccy="+symbol;
        String signText = null;
        String timestamp = DateUtils.getUnixTime();
        try {
            signText = HmacSHA256Base64Utils.sign(timestamp,
                    "get",
                    path,
                    queryString,
                    null,
                    Constant.OK_SECRET_KEY);
//            System.out.println(signText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", Constant.CONTENT_TYPE);
        headerMap.put("OK-ACCESS-KEY", Constant.OK_ACCESS_KEY);
        headerMap.put("OK-ACCESS-SIGN", signText);
        headerMap.put("OK-ACCESS-PASSPHRASE", Constant.OK_ACCESS_PASSPHRASE);
        headerMap.put("OK-ACCESS-TIMESTAMP", timestamp);
//        headerMap.put("x-simulated-trading", Constant.X_SIMULATED_TRADING);

        String result = HttpClient.requestGet(Constant.REST_HOST + path + "?" + queryString, headerMap);

//        System.out.println(result);
//        JsonNode resultObj = mapper.readTree(result);
//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultObj));

        return result;
    }

    //挂市价买入单，qty最小支持BTC的5位小数，多了会切掉
    public String placeBuyOrder(String symbol, BigDecimal qty){
        String result = null;
        if(qty.compareTo(BigDecimal.ZERO)>0) {
            try {
                result = placeOrder(symbol, Constant.TRADE_BUY, qty.setScale(5, RoundingMode.DOWN).stripTrailingZeros().toPlainString());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }


    //挂市价卖出单，qty最小支持BTC的5位小数，多了会切掉
    public String placeSellOrder(String symbol, BigDecimal qty){
        String result = null;
        if(qty.compareTo(BigDecimal.ZERO)>0) {
            try {
                result = placeOrder(symbol, Constant.TRADE_SELL, qty.setScale(5, RoundingMode.DOWN).stripTrailingZeros().toPlainString());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }


    //挂单（市价）（买卖单都可以，0.001BTC大约30美元）
//    @Test
//    public void placeBuyOrder() throws JsonProcessingException {
    private String placeOrder(String symbol, String side, String qty) throws JsonProcessingException {
        String orderPath = "/api/v5/trade/order";
        String orderId = OrderIdUtils.createTsId();
        OrderMarket order = new OrderMarket(symbol, orderId, side, qty);
//        OrderMarket order = new OrderMarket(Constant.TRADE_SYMBOL, orderId, "buy", "0.001");
//        OrderLimit order = new OrderLimit(Constant.TRADE_SYMBOL, orderId, "buy", "0.001", "28050.15");

        ObjectMapper mapper = new ObjectMapper();
        String bodyJson = mapper.writeValueAsString(order);

        String signText = null;
        String timestamp = DateUtils.getUnixTime();
        try {
            signText = HmacSHA256Base64Utils.sign(timestamp,
                    "POST",
                    orderPath,
                    null,
                    bodyJson,
                    Constant.OK_SECRET_KEY);
//            System.out.println(signText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", Constant.CONTENT_TYPE);
        headerMap.put("OK-ACCESS-KEY", Constant.OK_ACCESS_KEY);
        headerMap.put("OK-ACCESS-SIGN", signText);
        headerMap.put("OK-ACCESS-PASSPHRASE", Constant.OK_ACCESS_PASSPHRASE);
        headerMap.put("OK-ACCESS-TIMESTAMP", timestamp);
//        headerMap.put("x-simulated-trading", Constant.X_SIMULATED_TRADING);

        String result = HttpClient.requestPost(Constant.REST_HOST + orderPath, headerMap, bodyJson);

//        String result = "{}";
//        System.out.println(result);
        return result;
    }


    //挂买单（限价）


    //挂卖单（市价）
    //挂卖单（限价）


    //检查订单是否成交


    //取消订单


    /**
     * 是不是应该用一个线程来做，以免干扰主监听程序
     */

}
