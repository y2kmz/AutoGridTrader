package com.company.model;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;

public class TaUtil {
    public static List<List<String>> historyCandleList;
    public static List<BigDecimal> historyClosePriceList;
    public static int resetCounter = 0;
    private static BigDecimal ma_100;

    public TaUtil(List<List<String>> historyCandle) throws DataFormatException {
        //把历史K线列表灌进来
        if(historyCandle.size()<90){
            throw new DataFormatException("取得的历史K线数据不对");
        }
        Collections.reverse(historyCandle);
        historyCandleList = historyCandle;
        historyClosePriceList = getCloseList(historyCandle);
        ma_100 = calculateMa(Math.min(historyClosePriceList.size(), 100));
    }

    public void pushNewOne(ArrayList<String> thisCandleData){
        //写入一个新的并且删除旧的，注意的是原有list里面的0是最新的，但是list.add好像是加到最后
        historyCandleList.add(thisCandleData);
        if(historyCandleList.size()>100){
            historyCandleList.remove(0);
        }
        historyClosePriceList.add(new BigDecimal(thisCandleData.get(4)));
        if(historyClosePriceList.size()>100){
            historyClosePriceList.remove(0);
        }
        ma_100 = calculateMa(Math.min(historyClosePriceList.size(), 100));
        resetCounter++;
    }

    public BigDecimal getMA100(){
        //取得close的MA100均线值,historyCandleList为空的话报错返回0
        return ma_100;
    }

    public BigDecimal getMA(int days){
        //取得close的MA均线值，最大100，设再多也就到100,historyCandleList为空的话报错返回0
        return calculateMa(days);
    }

    public BigDecimal getPreviousClose(int offset){
        //取得最后一个close，默认offset是0，如果有希望往前取的情况，就设定往前数的天数
        //比如刚存入最新的close，就要取前一天的，这时候offset=1
        return historyClosePriceList.get(historyClosePriceList.size()-1-offset);
    }




    private List<BigDecimal> getCloseList(List<List<String>> historyCandle){
        List<BigDecimal> list = new ArrayList<BigDecimal>();
        for (List<String> ele: historyCandle) {
            if ("1".equals(ele.get(8))){
                list.add(new BigDecimal(ele.get(4)));
            }
        }
        return list;
    }

    private BigDecimal calculateMa(int days){
        BigDecimal countUp = BigDecimal.ZERO;
//        if (null == historyClosePriceList)
//            throw new Exception("");
        int size = historyClosePriceList.size();
        if(size > days){
            for(int i = size - days; i < size; i++){
                countUp = countUp.add(historyClosePriceList.get(i));
            }
        } else {
            for (BigDecimal thisOne : historyClosePriceList) {
                countUp = countUp.add(thisOne);
            }
        }
        return countUp.divide(BigDecimal.valueOf(days));
    }

}
