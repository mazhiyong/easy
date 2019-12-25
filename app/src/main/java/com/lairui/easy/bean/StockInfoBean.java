package com.lairui.easy.bean;

public class StockInfoBean {
    // 股票码
    public String stockShortCode;
    // 股票名称
    public String stockName;
    // 股票代码
    public String stockCode;
    // 当前价
    public String stockCurrentPrice;
    // 昨收价
    public String stockYesterdayPrice;
    // 今开价
    public String stockTodayPrice;
    // 成交量
    public String stockVolume;
    // 外盘
    public String stockOuterDisk;
    // 内盘
    public String stockInnerDisk;
    // 买1价
    public String stockBuy1Price;
    // 买1量
    public String stockBuy1Amount;
    // 买2价
    public String stockBuy2Price;
    // 买2量
    public String stockBuy2Amount;
    // 买3价
    public String stockBuy3Price;
    // 买3量
    public String stockBuy3Amount;
    // 买4价
    public String stockBuy4Price;
    // 买4量
    public String stockBuy4Amount;
    // 买5价
    public String stockBuy5Price;
    // 买5量
    public String stockBuy5Amount;
    // 卖1价
    public String stockSell1Price;
    // 卖1量
    public String stockSell1Amount;
    // 卖2价
    public String stockSell2Price;
    // 卖2量
    public String stockSell2Amount;
    // 卖3价
    public String stockSell3Price;
    // 卖3量
    public String stockSell3Amount;
    // 卖4价
    public String stockSell4Price;
    // 卖4量
    public String stockSell4Amount;
    // 卖5价
    public String stockSell5Price;
    // 卖5量
    public String stockSell5Amount;
    // 涨跌额
    public String stockRiseAndFallAmount;
    // 涨跌幅
    public String stockRiseAndFallAmplitude;
    // 最高
    public String stockHighestPrice;
    // 最低
    public String stockLowestPrice;
    // 成交量
    public String stockTransactionVolume;
    // 成交额
    public String stockTransactionAccount;
    // 换手率
    public String stockTurnoverRate;
    // 市盈率
    public String stockPriceEarningsRatio;
    // 振幅
    public String stockAmplitude;
    // 流通市值
    public String stockCirculationMarketValue;
    // 总市值
    public String stockTotalMarketValue;
    // 市净率
    public String stockPBRatio;
    // 涨停
    public String stockRiseTop;
    // 跌停
    public String stockFallBottom;
    // 委差
    public String stockEntrustDifference;
    // 委比  委比=〖(委买手数－委卖手数) ÷（委买手数 + 委卖手数)〗×100%
    public String stockEntrustRatio;
    //平均价
    public String stockAveragePrice;
    //
    public String stockLiangbi;



    public static StockInfoBean clearData() {
        StockInfoBean stockInfoBean = new StockInfoBean();
        stockInfoBean.stockShortCode = "";
        stockInfoBean.stockName = "";
        stockInfoBean.stockCode = "";
        stockInfoBean.stockCurrentPrice = "0.00";
        stockInfoBean.stockYesterdayPrice = "0.00";
        stockInfoBean.stockTodayPrice = "0.00";
        stockInfoBean.stockVolume = "0";
        stockInfoBean.stockOuterDisk = "0";
        stockInfoBean.stockInnerDisk = "0";
        stockInfoBean.stockBuy1Price = "0.00";
        stockInfoBean.stockBuy1Amount = "0.00";
        stockInfoBean.stockBuy2Price = "0.00";
        stockInfoBean.stockBuy2Amount = "0.00";
        stockInfoBean.stockBuy3Price = "0.00";
        stockInfoBean.stockBuy3Amount = "0.00";
        stockInfoBean.stockBuy4Price = "0.00";
        stockInfoBean.stockBuy4Amount = "0.00";
        stockInfoBean.stockBuy5Price = "0.00";
        stockInfoBean.stockBuy5Amount = "0.00";
        stockInfoBean.stockSell1Price = "0.00";
        stockInfoBean.stockSell1Amount = "0.00";
        stockInfoBean.stockSell2Price = "0.00";
        stockInfoBean.stockSell2Amount = "0.00";
        stockInfoBean.stockSell3Price = "0.00";
        stockInfoBean.stockSell3Amount = "0.00";
        stockInfoBean.stockSell4Price = "0.00";
        stockInfoBean.stockSell4Amount = "0.00";
        stockInfoBean.stockSell5Price = "0.00";
        stockInfoBean.stockSell5Amount = "0.00";
        stockInfoBean.stockRiseAndFallAmount = "0.00";
        stockInfoBean.stockRiseAndFallAmplitude = "0.00";
        stockInfoBean.stockHighestPrice = "0.00";
        stockInfoBean.stockLowestPrice = "0.00";
        stockInfoBean.stockTransactionVolume = "0";
        stockInfoBean.stockTransactionAccount = "0";
        stockInfoBean.stockTurnoverRate = "0.00";
        stockInfoBean.stockPriceEarningsRatio = "0.00";
        stockInfoBean.stockAmplitude = "0.00";
        stockInfoBean.stockCirculationMarketValue = "0";
        stockInfoBean.stockTotalMarketValue = "0";
        stockInfoBean.stockPBRatio = "0.00";
        stockInfoBean.stockRiseTop = "0.00";
        stockInfoBean.stockFallBottom = "0.00";
        stockInfoBean.stockEntrustDifference = "0";
        stockInfoBean.stockEntrustRatio = "0.00";
        stockInfoBean.stockAveragePrice = "0.00";
        stockInfoBean.stockLiangbi = "0.00";
        return stockInfoBean;
    }
}
