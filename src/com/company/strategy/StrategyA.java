package com.company.strategy;

import com.company.TradeLoader;
import com.company.model.Constant;
import com.company.order.OrderTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class StrategyA {
    private static Logger logger = LoggerFactory.getLogger(StrategyA.class);

    //持仓仓位
    private BigDecimal positionA = BigDecimal.ZERO;
    //加载已有的仓位
    public void loadPosition(BigDecimal qty){
        positionA = qty;
    }

    //买卖开关
    boolean haveBuyOrder1 = false;
    boolean haveBuyOrder2 = false;
    boolean haveBuyOrder3 = false;
    boolean haveBuyOrder4 = false;
    boolean haveBuyOrder5 = false;

    boolean haveSellOrder1 = false;
    boolean haveSellOrder2 = false;
    boolean haveSellOrder3 = false;
    boolean haveSellOrder4 = false;
    boolean haveSellOrder5 = false;

    //本次卖出一次应有的数量（一格20%仓位）
    BigDecimal oneSellQty = BigDecimal.ZERO;

    public void runStrategy(BigDecimal thisClose, BigDecimal previousClose) {
//        //均线基准单位（没用到）
//        int baseLineDay = 90;
//        //网格宽度（%）
//        double stepRate = 0.002;
//        //买入一份的基数
//        BigDecimal orderBaseQty = new BigDecimal("0.001");
//
//        //卖出一格20%仓位
//        BigDecimal oneSellPercent= new BigDecimal("0.2");

        //每个网格加仓数量
        BigDecimal orderQty1 = Constant.ORDER_BASE_QTY.multiply(BigDecimal.ONE);
        BigDecimal orderQty2 = Constant.ORDER_BASE_QTY.multiply(new BigDecimal("2"));
        BigDecimal orderQty3 = Constant.ORDER_BASE_QTY.multiply(new BigDecimal("3"));
        BigDecimal orderQty4 = Constant.ORDER_BASE_QTY.multiply(new BigDecimal("4"));
        BigDecimal orderQty5 = Constant.ORDER_BASE_QTY.multiply(new BigDecimal("5"));

        //baseLine：均线位置
        double baseLine = TradeLoader.candleStore.getMA100().doubleValue();

        double up5 = baseLine * (1.00 + Constant.STEP_RATE * 5);
        double up4 = baseLine * (1.00 + Constant.STEP_RATE * 4);
        double up3 = baseLine * (1.00 + Constant.STEP_RATE * 3);
        double up2 = baseLine * (1.00 + Constant.STEP_RATE * 2);
        double up1 = baseLine * (1.00 + Constant.STEP_RATE * 1);
        double down1 = baseLine * (1.00 - 1 * Constant.STEP_RATE);
        double down2 = baseLine * (1.00 - 2 * Constant.STEP_RATE);
        double down3 = baseLine * (1.00 - 3 * Constant.STEP_RATE);
        double down4 = baseLine * (1.00 - 4 * Constant.STEP_RATE);
        double down5 = baseLine * (1.00 - 5 * Constant.STEP_RATE);

        double nowPrice = thisClose.doubleValue();
        double prePrice = previousClose.doubleValue();


        DecimalFormat df = new DecimalFormat("#.00");

//        String logText = "close: \t" + thisClose + "\tpreviousClose:\t" + previousClose +
//                "\tup5:\t" + up5 + "\tup4:\t" + up4 + "\tup3:\t" + up3 + "\tup2:\t" + up2 + "\tup1:\t" + up1 +
//                "\tbase:\t" + baseLine +
//                "\tdown1:\t" + down1 + "\tdown2:\t" + down2 + "\tdown3:\t" + down3 + "\tdown4:\t" + down4 + "\tdown5:\t" + down5;
//        System.out.println(logText);

        String logText = "close:\t{}\tpreviousClose:\t{}\tup5:\t{}\tup4:\t{}\tup3:\t{}\tup2:\t{}\tup1:\t{}\tbase:\t{}\tdown1:\t{}\tdown2:\t{}\tdown3:\t{}\tdown4:\t{}\tdown5:\t{}";
        logger.info(logText, thisClose, previousClose,
                df.format(up5), df.format(up4), df.format(up3), df.format(up2), df.format(up1),
                baseLine,
                df.format(down1), df.format(down2), df.format(down3), df.format(down4), df.format(down5));
        //写入CSV日志文件以供分析
//++++++++++++++++++++++++++Buy Start++++++++++++++++++++++++++++++++
        //本次进场买单总量合计
        BigDecimal thisOrderQty = BigDecimal.ZERO;

        if (crossUnder(nowPrice, prePrice, down1) && !haveBuyOrder1){
            thisOrderQty = thisOrderQty.add(orderQty1);
            haveBuyOrder1 = true;
        }
        if (crossUnder(nowPrice, prePrice, down2) && !haveBuyOrder2){
            thisOrderQty = thisOrderQty.add(orderQty2);
            haveBuyOrder2 = true;
        }
        if (crossUnder(nowPrice, prePrice, down3) && !haveBuyOrder3){
            thisOrderQty = thisOrderQty.add(orderQty3);
            haveBuyOrder3 = true;
        }
        if (crossUnder(nowPrice, prePrice, down4) && !haveBuyOrder4){
            thisOrderQty = thisOrderQty.add(orderQty4);
            haveBuyOrder4 = true;
        }
        if (crossUnder(nowPrice, prePrice, down5) && !haveBuyOrder5){
            thisOrderQty = thisOrderQty.add(orderQty5);
            haveBuyOrder5 = true;
        }

        //小数整形，以免下不了单（最小单位违反）
        thisOrderQty = thisOrderQty.setScale(5, RoundingMode.DOWN).stripTrailingZeros();

        if (thisOrderQty.compareTo(BigDecimal.ZERO) > 0){
            //下买单，买单数量thisOrderQty
            OrderTool orderTool = new OrderTool();
            String orderResultJson = orderTool.placeBuyOrder(Constant.TRADE_SYMBOL, thisOrderQty);
            //在全局静态变量里记录仓位
            positionA = positionA.add(thisOrderQty);
            //写入CSV日志文件以供分析
            logger.info("下买单，买单数量:{}, 现在{}仓位: {}", thisOrderQty, Constant.TRADE_COIN, positionA);

            thisOrderQty = BigDecimal.ZERO;

            haveSellOrder1 = false;
            haveSellOrder2 = false;
            haveSellOrder3 = false;
            haveSellOrder4 = false;
            haveSellOrder5 = false;

            oneSellQty = BigDecimal.ZERO;
        }
//++++++++++++++++++++++++++Buy End++++++++++++++++++++++++++++++++

//++++++++++++++++++++++++++sell start++++++++++++++++++++++++++++++++
        //本次减仓卖单总量合计
        BigDecimal thisSellQty = BigDecimal.ZERO;
        /**
         * 这里会有误差：如果程序在up3~~up4之间重启了，
         * 那么剩下的过4线和过5线，两次，每次卖出应该是剩下仓位的一半
         * 这里简单的处理成仓位的2成，其实是有问题的，会导致数字对不上
         *
         * 如果需要处理断点重启的情况，需要记录现在的位置做适当的处理，还要考虑程序停转的期间，有没有漏过卖点，挺麻烦的。
         * 所以简单的无视，固定就是卖两成仓位，索然有误差，但是向下波动买入一次就复位了，误差不大可以接受
         */
        if(oneSellQty.compareTo(BigDecimal.ZERO) == 0){
            oneSellQty = positionA.multiply(Constant.ONE_SELL_PERCENT);
        }

        if (crossOver(nowPrice, prePrice, up1) && !haveSellOrder1){
            thisSellQty = thisSellQty.add(oneSellQty);
            haveSellOrder1 = true;
        }
        if (crossOver(nowPrice, prePrice, up2) && !haveSellOrder2){
            thisSellQty = thisSellQty.add(oneSellQty);
            haveSellOrder2 = true;
        }
        if (crossOver(nowPrice, prePrice, up3) && !haveSellOrder3){
            thisSellQty = thisSellQty.add(oneSellQty);
            haveSellOrder3 = true;
        }
        if (crossOver(nowPrice, prePrice, up4) && !haveSellOrder4){
            thisSellQty = thisSellQty.add(oneSellQty);
            haveSellOrder4 = true;
        }
        if (crossOver(nowPrice, prePrice, up5) && !haveSellOrder5){
            //过五清仓，但是注意不要把仓位记录改了，所以加个0做个新的对象。
            thisSellQty = positionA.add(BigDecimal.ZERO);
            haveSellOrder5 = true;
        }

        //小数整形，以免下不了单（最小单位违反）
        thisSellQty = thisSellQty.setScale(5, RoundingMode.DOWN).stripTrailingZeros();

        if (thisSellQty.compareTo(BigDecimal.ZERO) > 0) {
            //下卖单，一格卖单的数量是总仓位的1/5，如果穿过多格，这里下的是合计值
            OrderTool orderTool = new OrderTool();
            String orderResultJson = orderTool.placeSellOrder(Constant.TRADE_SYMBOL, thisSellQty);
            //校准仓位
            positionA = positionA.subtract(thisSellQty);
            //写入CSV日志文件以供分析
            logger.info("下卖单，卖出数量:{}, 现在{}仓位: {}", thisSellQty, Constant.TRADE_COIN, positionA);

            thisSellQty = BigDecimal.ZERO;

            haveBuyOrder1 = false;
            haveBuyOrder2 = false;
            haveBuyOrder3 = false;
            haveBuyOrder4 = false;
            haveBuyOrder5 = false;
        }
//++++++++++++++++++++++++++sell end++++++++++++++++++++++++++++++++

    }

    private boolean crossUnder(double nowOne, double previousOne, double guideline){
        return previousOne > guideline && nowOne <= guideline;
    }

    private boolean crossOver(double nowOne, double previousOne, double guideline){
        return previousOne < guideline && nowOne >= guideline;
    }

}
