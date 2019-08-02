package com.jpmc.supersimplestock.service;

import java.math.BigDecimal;
import com.jpmc.supersimplestock.domain.pojo.Trade;
import com.jpmc.supersimplestock.exception.StockRestServiceException;

public interface SuperSimpleStockService {

    /**
     * Calculate the dividend yield
     *
     * @param stockSymbol
     * @param stockPrice
     * @return the dividend yield
     * @throws Exception
     */
     BigDecimal calculateDividendYield(String stockSymbol, BigDecimal stockPrice) throws StockRestServiceException;

    /**
     * Calculate the Price-Earnings Ratio
     *
     * @param stockSymbol
     * @param stockPrice
     * @return the P/E Ratio
     * @throws Exception
     */
     BigDecimal calculatePriceEarningsRatio(String stockSymbol, BigDecimal stockPrice) throws StockRestServiceException;

    /**
     * Record a trade
     *
     * @param trade
     * @throws Exception
     */
     void recordATrade(Trade trade) throws StockRestServiceException;

    /**
     * Calculate the Volume Weighted Stock Price in past 5 minutes
     *
     * @param stockSymbol
     * @return the stock price in past 5 minutes
     * @throws Exception
     */
     BigDecimal calculateVolumeWeightedStockPriceInPast15Minutes(String stockSymbol) throws StockRestServiceException;

    /**
     * Calculate GBCE All Share Index
     *
     * @return the All Share Index
     * @throws Exception
     */
     BigDecimal calculateGBCEAllShareIndex() throws StockRestServiceException;


}
