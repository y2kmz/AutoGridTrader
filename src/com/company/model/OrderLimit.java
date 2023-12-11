package com.company.model;

public class OrderLimit {
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
    private String sz;
    private String px;

    public OrderLimit() {
        tdMode = "cash";
        ordType = "limit";
    }

    public OrderLimit(String symbol, String orderId, String side, String qty, String price) {
        this.instId = symbol;
        this.tdMode = "cash";
        this.clOrdId = orderId;
        this.side = side;
        this.ordType = "limit";
        this.sz = qty;
        this.px = price;
    }

    public String getPx() {
        return px;
    }

    public void setPx(String px) {
        this.px = px;
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

    public String getSz() {
        return sz;
    }

    public void setSz(String sz) {
        this.sz = sz;
    }
}
