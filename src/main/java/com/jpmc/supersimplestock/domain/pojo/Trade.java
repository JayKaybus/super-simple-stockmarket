package com.jpmc.supersimplestock.domain.pojo;

import com.jpmc.supersimplestock.domain.enums.TradeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trade {
    private String stockSymbol;

    private Integer quantity;

    private TradeType indicator;

    private BigDecimal price;

    private LocalDateTime recordCreationTimeStamp;

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setIndicator(TradeType indicator) {
        this.indicator = indicator;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setRecordCreationTimeStamp(LocalDateTime recordCreationTimeStamp) {
        this.recordCreationTimeStamp = recordCreationTimeStamp;
    }


    public Trade(String stockSymbol, Integer quantity, TradeType indicator, BigDecimal price) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.indicator = indicator;
        this.price = price;
        this.recordCreationTimeStamp = LocalDateTime.now();
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public TradeType getIndicator() {
        return indicator;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getRecordCreationTimeStamp() {
        return recordCreationTimeStamp;
    }


    }


