package com.company.model;

public class OrderMarket {
//    {
//        "instId":"BTC-USDT",
//        "tdMode":"cash",
//        "clOrdId":"b15",
//        "side":"buy",
//        "ordType":"limit",
//        "px":"2.15",
//        "sz":"2"
//    }

    private String instId;
    private String tdMode;
    private String clOrdId;
    private String side;
    private String ordType;
    private String tgtCcy;
    private String sz;

    public OrderMarket() {
        tdMode = "cash";
        ordType = "market";
        tgtCcy = "base_ccy";
    }

    public OrderMarket(String symbol, String orderId, String side, String qty) {
        this.instId = symbol;
        this.tdMode = "cash";
        this.clOrdId = orderId;
        this.side = side;
        this.ordType = "market";
        tgtCcy = "base_ccy";
        this.sz = qty;
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public String getTdMode() {
        return tdMode;
    }

    public void setTdMode(String tdMode) {
        this.tdMode = tdMode;
    }

    public String getClOrdId() {
        return clOrdId;
    }

    public void setClOrdId(String clOrdId) {
        this.clOrdId = clOrdId;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getOrdType() {
        return ordType;
    }

    public void setOrdType(String ordType) {
        this.ordType = ordType;
    }

    public String getTgtCcy() {
        return tgtCcy;
    }

    public void setTgtCcy(String tgtCcy) {
        this.tgtCcy = tgtCcy;
    }

    public String getSz() {
        return sz;
    }

    public void setSz(String sz) {
        this.sz = sz;
    }
}
