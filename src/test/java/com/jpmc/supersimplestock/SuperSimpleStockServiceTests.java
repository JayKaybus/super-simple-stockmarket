package com.jpmc.supersimplestock;

import com.jpmc.supersimplestock.domain.enums.TradeType;
import com.jpmc.supersimplestock.service.SuperSimpleStockService;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import com.jpmc.supersimplestock.dao.TradeDao;
import com.jpmc.supersimplestock.domain.pojo.Trade;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SuperSimpleStockServiceTests {

    @Autowired
    private SuperSimpleStockService stockMarketService;

    @Autowired
    private TradeDao tradeDao;

    private Logger logger = Logger.getLogger(SuperSimpleStockServiceTests.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCalculatedDividendYield_Success() throws Exception {
        // calculate dividend for a common stock
        BigDecimal dividend1 = stockMarketService.calculateDividendYield("TEA", BigDecimal.valueOf(200));
        assertThat("Dividend is calculated wrong", dividend1, equalTo(new BigDecimal("0.00")));
        logger.info("calculated dividend for a common stock TEA is:" +dividend1);

        // calculate dividend for a preferred stock
        BigDecimal dividend2 = stockMarketService.calculateDividendYield("ALE", BigDecimal.valueOf(100));
        assertThat("Dividend is calculated wrong", dividend2, equalTo(new BigDecimal("0.23")));
        logger.info("calculated dividend for a common stock ALE is:" +dividend2);

        BigDecimal dividend3 = stockMarketService.calculateDividendYield("GIN", BigDecimal.valueOf(100));
        assertThat("Dividend is calculated wrong", dividend3, equalTo(new BigDecimal("2.00")));
        logger.info("calculated dividend for a preferred stock GIN  is:" +dividend3);

    }

    @Test
    public void testCalculatedDividendYield_SymbolNull() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("Stock symbol cannot be null"));
        stockMarketService.calculateDividendYield(null, BigDecimal.valueOf(4));

    }

    @Test
    public void testCalculatedDividendYield_PriceNull() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("Stock price cannot be null"));
        stockMarketService.calculateDividendYield("POP", null);
    }

    @Test
    public void testCalculatedDividendYield_InvalidStock() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(containsString("is not present in the market"));
        stockMarketService.calculateDividendYield("GIIN", BigDecimal.valueOf(4.6));
    }

    @Test
    public void testCalculatePERatio_Success() throws Exception {
        // calculate P/E ratio for common stock
        BigDecimal priceEarningsRatio = stockMarketService.calculatePriceEarningsRatio("ALE", BigDecimal.valueOf(100));
        assertThat("Dividend is calculated wrong", priceEarningsRatio, equalTo(new BigDecimal("434.78")));
        logger.info("calculated P/E for the stock ALE  is:" +priceEarningsRatio);

        // calculate P/E ratio for preferred stock
        BigDecimal priceEarningsRatio1 = stockMarketService.calculatePriceEarningsRatio("GIN", BigDecimal.valueOf(0.55));
        assertThat("Dividend is calculated wrong", priceEarningsRatio1, equalTo(new BigDecimal("0.00")));
        logger.info("calculated P/E for the stock GIN  is:" +priceEarningsRatio1);
    }

    @Test
    public void testCalculatePERatio_SymbolNull() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("Stock symbol cannot be null"));
        stockMarketService.calculatePriceEarningsRatio(null, BigDecimal.valueOf(1.22));
    }


    @Test
    public void testRecordTrade_Success() throws Exception {
        addTrades();
        assertThat("Number of trades is incorrect", tradeDao.getAll().size() == 4);
        // add a new trade
        Trade trade1 = new Trade("POP", 24, TradeType.BUY, new BigDecimal("2.20"));
        stockMarketService.recordATrade(trade1);
        assertThat("Number of trades is incorrect", tradeDao.getAll().size() == 5);
        Trade trade2 = new Trade("POP", 12, TradeType.BUY, new BigDecimal("1.20"));
        stockMarketService.recordATrade(trade2);
    }

    @Test
    public void testRecordTrade_Null() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("Trade record cannot be null"));
        stockMarketService.recordATrade(null);
    }

    @Test
    public void testRecordTrade_SymbolNull() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("Stock symbol in a trade must not be null"));
        Trade trade = new Trade(null, 24, TradeType.SELL, new BigDecimal("2.20"));
        stockMarketService.recordATrade(trade);
    }

    @Test
    public void testRecordTrade_NegativeQty() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("Quantity of shares in a trade must be greater than zero"));
        Trade trade = new Trade("POP", -23, TradeType.SELL, new BigDecimal("2.20"));
        stockMarketService.recordATrade(trade);
    }

    @Test
    public void testRecordTrade_TradeIndicatorNull() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("Trade indicator cannot be nul"));
        Trade trade = new Trade("POP", 20, null, new BigDecimal("2.20"));
        stockMarketService.recordATrade(trade);
    }

    @Test
    public void testRecordTrade_PriceNegative() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("Price of a share in a trade must be greater than zero"));
        Trade trade = new Trade("POP", 20, TradeType.SELL, new BigDecimal("-2.20"));
        stockMarketService.recordATrade(trade);
    }

    @Test
    public void testRecordTrade_InvalidStock() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("A trade must be associated with a stock"));
        Trade trade = new Trade("RRR", 20, TradeType.SELL, new BigDecimal("2.20"));
        stockMarketService.recordATrade(trade);
    }

    @Test
    public void testCalculateStockPrice_Success() throws Exception {
        addTrades();

        // calculate stock price for GIN which was only once traded
        BigDecimal ginStockPrice = stockMarketService.calculateVolumeWeightedStockPriceInPast15Minutes("GIN");
        assertThat("Stock price for GIN is incorrect", ginStockPrice, equalTo(new BigDecimal("1.00")));
        logger.info("VolumeWeightedStockPriceInPast15Minutes for GIN is  :" +ginStockPrice);

        // calculate stock price for JOE which was not traded
        BigDecimal joeStockPrice = stockMarketService.calculateVolumeWeightedStockPriceInPast15Minutes("JOE");
        assertThat("Stock price for JOE is incorrect", joeStockPrice, equalTo(BigDecimal.ZERO));
        logger.info("VolumeWeightedStockPriceInPast15Minutes for JOE is  :" +joeStockPrice);

        BigDecimal teaStockPrice = stockMarketService.calculateVolumeWeightedStockPriceInPast15Minutes("TEA");
        assertThat("Stock price for JOE is incorrect", teaStockPrice, equalTo(new BigDecimal("1.00")));
        logger.info("VolumeWeightedStockPriceInPast15Minutes for TEA is  :" +teaStockPrice);
    }

    @Test
    public void testCalculateStockPrice_Negative() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("Stock symbol cannot be null"));
        stockMarketService.calculateVolumeWeightedStockPriceInPast15Minutes(null);
    }

    @Test
    public void testCalculateAllShareIndex_Success() throws Exception {
        addTrades();
        // calculate all share index for trades recorded by this time
        BigDecimal allShareIndex = stockMarketService.calculateGBCEAllShareIndex();
        assertThat("AllShareIndex is incorrect", allShareIndex, equalTo(new BigDecimal("1.31")));
        logger.info(" calculated Global Beverage Corporation Exchange for all stocks is :" +allShareIndex);
    }

    private void addTrades(){
        Trade trade1 = new Trade("GIN", 200, TradeType.SELL, new BigDecimal("1.00"));
        Trade trade2 = new Trade("POP", 10, TradeType.BUY, new BigDecimal("2.50"));
        Trade trade3 = new Trade("TEA", 10, TradeType.SELL, new BigDecimal("1.00"));
        Trade trade4 = new Trade("TEA", 3, TradeType.SELL, new BigDecimal("1.20"));
        trade4.setRecordCreationTimeStamp(LocalDateTime.now().minus(Duration.ofMinutes(25)));
        tradeDao.add(trade1);
        tradeDao.add(trade2);
        tradeDao.add(trade3);
        tradeDao.add(trade4);

    }


}
