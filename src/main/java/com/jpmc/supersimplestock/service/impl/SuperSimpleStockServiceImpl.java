package com.jpmc.supersimplestock.service.impl;

import com.jpmc.supersimplestock.dao.StockDao;
import com.jpmc.supersimplestock.dao.TradeDao;
import com.jpmc.supersimplestock.domain.pojo.Stock;
import com.jpmc.supersimplestock.domain.pojo.Trade;
import com.jpmc.supersimplestock.service.SuperSimpleStockService;
import com.jpmc.supersimplestock.exception.StockRestServiceException;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("superSimpleStocksService")
public class SuperSimpleStockServiceImpl implements SuperSimpleStockService
    {
        final static Logger LOG = Logger.getLogger(SuperSimpleStockServiceImpl.class);

        @Autowired
        private StockDao stockDao;

        @Autowired
        private TradeDao tradeDao;

        private int digitsAfterPoint = 2;

        private int lastMinutes = 15;


        @Override
        public BigDecimal calculateDividendYield(String stockSymbol, BigDecimal stockPrice) throws StockRestServiceException{
            BigDecimal dividendYield;
            if (stockSymbol == null) {
                throw new StockRestServiceException("Stock symbol cannot be null.",true);
            }
            if (stockPrice == null) {
                throw new StockRestServiceException("Stock price cannot be null.",true);
            }
            if (stockPrice.compareTo(BigDecimal.ZERO) != 1) {
                throw new StockRestServiceException("Stock price must be greater than zero.",true);
            }
            Optional<Stock> stock = stockDao.findMatchingSymbol(stockSymbol);
            if (!stock.isPresent()) {
                throw new StockRestServiceException("Stock with symbol [" + stockSymbol + "] is not present in the market.",true);
            }
            // calculate the dividend depending on stock type and price
            switch (stock.get().getType()) {
                case COMMON:
                    dividendYield = stock.get().getLastDividend().divide(stockPrice,digitsAfterPoint, RoundingMode.HALF_UP);
                    break;
                case PREFERRED:
                    dividendYield = stock.get().getFixedDividend().multiply(stock.get().getParValue())
                            .divide(stockPrice,digitsAfterPoint, RoundingMode.HALF_UP);
                    break;
                default:
                    throw new RuntimeException("Unknown stock type");
            }
            return dividendYield;
        }

        @Override
        public BigDecimal calculatePriceEarningsRatio(String stockSymbol, BigDecimal price) throws StockRestServiceException{
            LOG.info("Calculating the Price-Earnings Ratio...");
            BigDecimal priceEarningsRatio;
            try {
                BigDecimal dividend = calculateDividendYield(stockSymbol, price);
                if (dividend.equals(BigDecimal.ZERO.setScale(digitsAfterPoint))) {
                    throw new StockRestServiceException("Dividend calculated must not be equal to zero.",true);
                }
                priceEarningsRatio = price.divide(dividend,digitsAfterPoint,RoundingMode.HALF_UP);
            } catch(StockRestServiceException e) {
                LOG.error("While calculating the P/E Ratio an error occured: " + e.getMessage());
                throw e;
            }
            LOG.info("Price-Earnings Ratio was calculated for stock with symbol [" + stockSymbol +"].");
            return priceEarningsRatio;
        }

        @Override
        public void recordATrade(Trade trade) throws StockRestServiceException  {
            LOG.info("Recording a trade.");
            try {
                if (trade == null) {
                    throw new StockRestServiceException("Trade record cannot be null.",true);
                }
                // check all the values of the trade
                if (trade.getStockSymbol() == null) {
                    throw new StockRestServiceException("Stock symbol in a trade must not be null.",true);
                }
                if (trade.getQuantity() != null && trade.getQuantity() <= 0) {
                    throw new StockRestServiceException("Quantity of shares in a trade must be greater than zero.",true);
                }
                if (trade.getIndicator() == null) {
                    throw new StockRestServiceException("Trade indicator cannot be null.",true);
                }
                if (trade.getPrice() != null && trade.getPrice().compareTo(BigDecimal.ZERO) != 1) {
                    throw new StockRestServiceException("Price of a share in a trade must be greater than zero.",true);
                }

                Optional<Stock> stock = stockDao.findMatchingSymbol(trade.getStockSymbol());
                if (!stock.isPresent()) {
                    throw new StockRestServiceException("A trade must be associated with a stock.",true);
                }
                // add a new trade entry to the tradelist
                tradeDao.add(trade);
            } catch (StockRestServiceException e) {
                LOG.error("While recording a new trade an error occured: " + e.getMessage());
                throw e;
            }
            LOG.info("A new trade for stock with symbol [" + trade.getStockSymbol() + "] was recorded.");
        }

        @Override
        public BigDecimal calculateVolumeWeightedStockPriceInPast15Minutes(String stockSymbol) throws StockRestServiceException {
            LOG.info("Calculating Volume Weighted Stock Price based on trades in last 15 minutes.");
            BigDecimal stockPrice;
            try {
                if (stockSymbol == null) {
                    throw new StockRestServiceException("Stock symbol cannot be null.",true);
                }
                stockPrice = calculateVolumeWeightedStockPrice(stockSymbol, Duration.ofMinutes(lastMinutes));
            } catch(StockRestServiceException e) {
                LOG.error("While calculating Volume Weighted Stock Price an error occured: " + e.getMessage());
                throw new StockRestServiceException("While calculating Volume Weighted Stock Price an error occured.",true);
            }
            LOG.info("Volume Weighted Stock Price based on trades in last " + lastMinutes
                    + " minutes for stock with symbol [" + stockSymbol + "was calculated.");
            return stockPrice;
        }

        private BigDecimal calculateVolumeWeightedStockPrice(String stockSymbol, Duration pastTime) throws StockRestServiceException{
            LOG.info("calculateVolumeWeightedStockPrice");
            // For a given stock get the list of trades
            List<Trade> trades = tradeDao.findByStockSymbol(stockSymbol);
            List<Trade> lastFewMinTradesList;
            if (pastTime == null) {
                // if pasTime is null then filtering should not be done
                lastFewMinTradesList = trades;
            }
            else {
                // get the trades entry in last 15 minutes
                lastFewMinTradesList = getTradesByCreationTime(trades, pastTime);
            }
            BigDecimal totalTradedPrice = BigDecimal.ZERO;
            int sumOfTradeQuantity = 0;

            for (Trade trade : lastFewMinTradesList) {
                // calculate total traded price for a specific stock
                totalTradedPrice = totalTradedPrice.add(trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity())));
                // calculate total quantity for a specific stock
                sumOfTradeQuantity += trade.getQuantity();
            }
            BigDecimal totalStockPrice = BigDecimal.ZERO;
            if (sumOfTradeQuantity != 0) {
                totalStockPrice = totalTradedPrice.divide(BigDecimal.valueOf(sumOfTradeQuantity), digitsAfterPoint, RoundingMode.HALF_UP);
            }
            return totalStockPrice;
        }

        private List<Trade> getTradesByCreationTime(List<Trade> trades, Duration pastTime) throws StockRestServiceException {
            LOG.info("getTradesByCreationTime");
            List<Trade> lastFewMinTradesList;
            LocalDateTime startTimeStamp = LocalDateTime.now().minus(pastTime);
            // filter trades that were created in the specified duration of time
            lastFewMinTradesList = trades.stream()
                    .filter(x -> x.getRecordCreationTimeStamp().isAfter(startTimeStamp))
                    .collect(Collectors.toList());
            return lastFewMinTradesList;
        }

        @Override
        public BigDecimal calculateGBCEAllShareIndex() throws StockRestServiceException {
            LOG.info("Calculating Global Beverage Corporation Exchange All Share Index");
            BigDecimal geometricMean = BigDecimal.ZERO;
            List<Stock> stockList = stockDao.getAll();
            // get a list of prices for all stocks
            List<BigDecimal> stockPrices = new ArrayList<BigDecimal>();
            for (Stock stock : stockList) {
                BigDecimal stockPrice = calculateVolumeWeightedStockPrice(stock.getSymbol(), null);
                if (!stockPrice.equals(BigDecimal.ZERO)) {
                    stockPrices.add(stockPrice);
                }
            }
            if(stockPrices.size() > 0) {
                // create and populate a stock prices array of doubles
                double[] stockPricesArray = new double[stockPrices.size()];
                for (int i = 0; i < stockPricesArray.length; i++) {
                    stockPricesArray[i] = stockPrices.get(i).doubleValue();
                }
                // return geometric mean of all prices for all stocks
                geometricMean = BigDecimal.valueOf(StatUtils.geometricMean(stockPricesArray));
                return geometricMean.setScale(digitsAfterPoint, RoundingMode.HALF_UP);
            }
            return geometricMean;
        }

    }

