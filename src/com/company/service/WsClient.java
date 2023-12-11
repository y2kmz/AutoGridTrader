package com.company.service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import com.company.TradeLoader;
import com.company.model.Constant;
import com.company.model.HistoryCandleResponse;
import com.company.model.TaUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsClient extends WebSocketClient {
    private static Logger logger = LoggerFactory.getLogger(WsClient.class);
    private ObjectMapper mapper = new ObjectMapper();

    public WsClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info(">>>>>>>>>>>websocket open");
    }


    @Override
    public void onClose(int i, String s, boolean b) {
        logger.info(">>>>>>>>>>>websocket close: code:{}, reason:{}, b:{}", i, s, b);
    }

    @Override
    public void onError(Exception e) {
        logger.error(">>>>>>>>>websocket error ", e);
    }

    @Override
    public void onMessage(String s) {
//        logger.info(">>>>>>>>>>>websocket message: {}", s);
//        System.out.println(DateUtils.getUnixTime() + ">>>>>>>>>> websocket message: " + s);
        if("pong".equals(s)){
            return;
        }
        try {
            HashMap msg = mapper.readValue(s, HashMap.class);
            if (msg.containsKey("data")) {
                ArrayList<ArrayList<String>> dataList = (ArrayList<ArrayList<String>>) msg.get("data");
                ArrayList<String> thisCandleData = dataList.get(0);
                if ("1".equals(thisCandleData.get(8))) {
//                    logger.info(">>>>>>>>>>>websocket message: {}", s);
//                    logger.info("close: {}, endFlag: {}", thisCandleData.get(4), thisCandleData.get(8));
                    //取得了最新的K线，开始操作
                    //保存最新的K线到历史K线表（保存最新，删除超出100的最旧的，计算MA）
                    TradeLoader.candleStore.pushNewOne(thisCandleData);

                    //调用策略
//                    logger.info("调用策略A");
                    TradeLoader.strategy.runStrategy(
                            new BigDecimal(thisCandleData.get(4)), TradeLoader.candleStore.getPreviousClose(1));

                    //每500次K线重置一次历史K线，以免偏差
                    if (TradeLoader.candleStore.resetCounter % 500 == 0) {
                        //启动websocket，获取历史一百根K线数据
                        String historyCandleJson = HttpClient.requestGet(Constant.GET_HISTORY_CANDLE_URL, null);
                        try {
                            HistoryCandleResponse respon = mapper.readValue(historyCandleJson, HistoryCandleResponse.class);
                            TradeLoader.candleStore = new TaUtil(respon.getData());
                            logger.info("每500次K线重置一次历史K线，以免偏差");
                        } catch (Exception e) {
                            logger.error("请求K线过程异常", e);
                            return;
                        }
                        TradeLoader.candleStore.resetCounter = 0;
                    }

                }
            }
        } catch (JsonProcessingException e) {
            logger.error("异常数据：{}", s);
            logger.error("解析异常", e);
        }

    }
}
