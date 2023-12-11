package com.company.model;

import java.util.List;
import java.util.Map;

public class WsSubscribe {

     private String op;
     private List<Map<String, String>> args;

     public WsSubscribe(){
          op = "subscribe";
          args = List.of(Map.of("channel","candle1m","instId","BTC-USDT"));
     }

     public WsSubscribe(String op1, String channel1, String instId1){
          op = op1;
          args = List.of(Map.of("channel", channel1,"instId", instId1));
     }


     public String getOp() {
          return op;
     }

     public void setOp(String op) {
          this.op = op;
     }

     public List<Map<String, String>> getArgs() {
          return args;
     }

     public void setArgs(List<Map<String, String>> args) {
          this.args = args;
     }
}
