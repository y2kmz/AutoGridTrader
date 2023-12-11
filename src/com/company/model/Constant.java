package com.company.model;

import java.math.BigDecimal;

public class Constant {

    //==================策略A参数============================
    //均线基准单位（没用到）
    public static final int BASELINEDAY = 90;
    //网格宽度（%）
    public static final double STEP_RATE = 0.002;
    //买入一份的基数
    public static final BigDecimal ORDER_BASE_QTY = new BigDecimal("0.001");

    //卖出一格20%仓位
    public static final BigDecimal ONE_SELL_PERCENT= new BigDecimal("0.2");
    //监听1分钟K线
    public static final String TRADE_CHANNEL = "candle1m";
    //==================固定字符串=============================
    public static final String TRADE_SYMBOL = "BTC-USDT";
    public static final String TRADE_COIN = "BTC";
    public static final String TRADE_BUY = "buy";
    public static final String TRADE_SELL = "sell";

    //==================OKEX API的各个服务的地址=============================
//    public static final String WEBSOCKET_URL = "wss://ws.okx.com:8443/ws/v5/public";
    public static final String WEBSOCKET_URL = "wss://ws.okx.com:8443/ws/v5/business";
    public static final String REST_HOST = "https://www.okx.com";
    //获取所有可交易现货币种信息
    public static final String GET_ALL_SYMBOL_URL = "https://aws.okx.com/api/v5/public/instruments?instType=SPOT";
    //获取历史K线
    public static final String GET_HISTORY_CANDLE_URL = "https://aws.okx.com/api/v5/market/history-candles?instId="+TRADE_SYMBOL;
    //WS订阅指令：交易对，K线的粒度
    public static final String SUBSCRIBE_TEXT = "{\"op\": \"subscribe\",\n" +
            "    \"args\": [{\n" +
            "        \"channel\": \""+TRADE_CHANNEL+"\",\n" +
            "        \"instId\": \""+TRADE_SYMBOL+"\"\n" +
            "    }]}";


    //==================REST API的认证信息=============================

    public static final String CONTENT_TYPE = "application/json";
    public static final String OK_ACCESS_KEY = "XXXXXXXXXXXXXXX";
    public static final String OK_ACCESS_PASSPHRASE = "Apikey123!";
    public static final String OK_SECRET_KEY = "XXXXXXXXXXXXXXXXXXXXXXXX";
    public static final String X_SIMULATED_TRADING = "1";

    //==================REST API的认证信息=============================


}
