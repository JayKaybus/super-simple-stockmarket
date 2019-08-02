package com.jpmc.supersimplestock.rest;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.jpmc.supersimplestock.service.SuperSimpleStockService;
import com.jpmc.supersimplestock.exception.StockRestServiceException;
import java.math.BigDecimal;
import com.jpmc.supersimplestock.domain.pojo.Trade;


@RestController
@RequestMapping("/stockmarket")
public class SuperSimpleStockController {

    final static Logger LOG = Logger.getLogger(SuperSimpleStockController.class);

    @Autowired
    private SuperSimpleStockService stockService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String testMyApp() {
        return "Welcome to Simple Super Stock Mark App";
    }

    @RequestMapping(value = "/calcDividendYield/{symbol}/{stockPrice}", method = RequestMethod.GET)
    public BigDecimal calcDividendYield(@PathVariable(name="stockPrice") BigDecimal stockPrice,
                                        @PathVariable(name="symbol") String symbol) throws StockRestServiceException  {
        BigDecimal dividendYield = BigDecimal.ZERO;
        if (stockPrice == null) {
            throw new StockRestServiceException("Stock price cannot be null",true);
        }
        if (stockPrice.compareTo(BigDecimal.ZERO) != 1) {
            throw new StockRestServiceException("Stock price must be greater than zero.",true);
        }
        try {
             dividendYield = stockService.calculateDividendYield(symbol, stockPrice);
        }
        catch (StockRestServiceException e)
        {
            LOG.error("Error occured in calculating price Earnings Ratio" + e.getMessage());
            throw new StockRestServiceException(e.getMessage(),true);
        }
        return dividendYield;
    }

    @RequestMapping(value = "/calcPERatio/{symbol}/{stockPrice}", method = RequestMethod.GET)
    public BigDecimal calcPERatio(@PathVariable("stockPrice") BigDecimal stockPrice,
                                  @PathVariable("symbol") String symbol) throws StockRestServiceException{
        BigDecimal peRatio = BigDecimal.ZERO;
        if (stockPrice == null) {
            throw new StockRestServiceException("Stock price cannot be null",true);
        }
        if (stockPrice.compareTo(BigDecimal.ZERO) != 1) {
            throw new StockRestServiceException("Stock price must be greater than zero.",true);
        }
        try {
            peRatio = stockService.calculatePriceEarningsRatio(symbol, stockPrice);
        }
        catch (StockRestServiceException e)
        {
            LOG.error("Error occured in calculating price Earnings Ratio" + e.getMessage());
            throw new StockRestServiceException(e.getMessage(),true);
        }
        return peRatio;
    }


    @RequestMapping(value = "/recordATrade", method = RequestMethod.POST)
    public void recordATrade(@RequestBody Trade tradeRequest) throws StockRestServiceException {
        try {
            stockService.recordATrade(tradeRequest);
        }
        catch (StockRestServiceException e)
        {
            LOG.error("Error occured in recording a trade" + e.getMessage());
            throw new StockRestServiceException(e.getMessage() ,true);
        }
    }

    @RequestMapping(value = "/calcVolumeWtStockPrice/{symbol}", method = RequestMethod.GET)
    public BigDecimal getVolumeWeightedPriceInPast15Minutes(@PathVariable("symbol") String symbol) throws StockRestServiceException {
        BigDecimal volWeightedPrice = BigDecimal.ZERO;
        try {
            volWeightedPrice = stockService.calculateVolumeWeightedStockPriceInPast15Minutes(symbol);
        }
        catch (StockRestServiceException e)
        {
            LOG.error("Error occured in calculating volume weighted Price in Past15Minutes" + e.getMessage());
            throw new StockRestServiceException(e.getMessage(),true);
        }
        return volWeightedPrice;
    }

    @RequestMapping(value = "/GBCEAllShareIndex", method = RequestMethod.GET)
    public BigDecimal calculateGBCEAllShareIndex() throws StockRestServiceException{
        BigDecimal gbce = BigDecimal.ZERO;
        try {
            gbce  = stockService.calculateGBCEAllShareIndex();
        }
        catch (StockRestServiceException e)
        {
            LOG.error("Error occured in calculating GBCEAllShareIndex " + e.getMessage());
            throw new StockRestServiceException(e.getMessage() ,true);
        }
        return gbce;
    }

}
