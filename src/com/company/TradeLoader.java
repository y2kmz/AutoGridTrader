package com.company;

import com.company.order.OrderTool;
import com.company.service.HttpClient;
import com.company.model.Constant;
import com.company.model.HistoryCandleResponse;
import com.company.model.TaUtil;
import com.company.service.WsClient;
import com.company.strategy.StrategyA;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.enums.ReadyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URI;

public class TradeLoader {
    private static Logger logger = LoggerFactory.getLogger(TradeLoader.class);

    public static TaUtil candleStore;
    public static StrategyA strategy = new StrategyA();


    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        logger.info("启动程序");

        //REST获取历史一百根K线数据
        logger.info("获取历史一百根K线数据");
        String historyCandleJson = HttpClient.requestGet(Constant.GET_HISTORY_CANDLE_URL, null);
        try {
            HistoryCandleResponse respon = mapper.readValue(historyCandleJson, HistoryCandleResponse.class);
            candleStore = new TaUtil(respon.getData());
        } catch (Exception e) {
            logger.error("解析异常", e);
            return;
        }

        //REST获取持币仓位
        logger.info("获取BTC仓位");
        OrderTool orderTool = new OrderTool();
        String assetJson = orderTool.checkAsset(Constant.TRADE_COIN);
        try {
            JsonNode jsonNode = mapper.readTree(assetJson);
            // path:  @data/@details/ccy,cashBal
            JsonNode datas = jsonNode.get("data");
            for (JsonNode data : datas){
                JsonNode details = data.get("details");
                for (JsonNode detail : details){
                    if(Constant.TRADE_COIN.equals(detail.get("ccy").asText())){
                        String qty = detail.get("cashBal").asText();
                        logger.info("加载现有的{}仓位: {}", Constant.TRADE_COIN, qty);
                        strategy.loadPosition(new BigDecimal(qty));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("解析异常", e);
            return;
        }

        //启动websocket，获取K线数据
        try {
            URI url = new URI(Constant.WEBSOCKET_URL);
            WsClient wsClient = new WsClient(url);

            while (!wsClient.isOpen()) {
                logger.info("连接服务器...");

                if (wsClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                    wsClient.connectBlocking();
                } else if (wsClient.getReadyState().equals(ReadyState.CLOSING) || wsClient.getReadyState().equals(ReadyState.CLOSED)) {
                    wsClient.reconnectBlocking();
                }
                while (!wsClient.getReadyState().equals(ReadyState.OPEN)) {
                    logger.info("连接中...");
                    Thread.sleep(1000);
                }
                wsClient.send(Constant.SUBSCRIBE_TEXT);
                logger.info("监听开始：K线 {}，交易对 {}，网格宽度{}, 加仓基数{}", Constant.TRADE_CHANNEL, Constant.TRADE_SYMBOL, ""+Constant.STEP_RATE*100+"%", Constant.ORDER_BASE_QTY);

                while (wsClient.isOpen()){
                    wsClient.send("ping");
                    //15秒发送一次心跳数据
                    Thread.sleep(15000);
                }
            }
        } catch (Exception e) {
            logger.error("websocket连接异常", e);
        }
    }
}
