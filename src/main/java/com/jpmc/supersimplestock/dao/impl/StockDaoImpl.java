package com.jpmc.supersimplestock.dao.impl;

import com.jpmc.supersimplestock.dao.StockDao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jpmc.supersimplestock.domain.enums.StockType;
import com.jpmc.supersimplestock.domain.pojo.Stock;


@Service("superSimpleStockDaoService")
public class StockDaoImpl implements StockDao {
	
	List<Stock> stocks;
	
	private StockDaoImpl() {
		stocks = new ArrayList<Stock>();
		stocks.add(new Stock("TEA", StockType.COMMON, new BigDecimal("0.0"), null, new BigDecimal("100.00")));
		stocks.add(new Stock("POP", StockType.COMMON, new BigDecimal("8.0"), null, new BigDecimal("100.00")));
		stocks.add(new Stock("ALE", StockType.COMMON, new BigDecimal("23.0"), null, new BigDecimal("60.00")));
		stocks.add(new Stock("GIN", StockType.PREFERRED, new BigDecimal("8.0"), new BigDecimal("2.0"), new BigDecimal("100.00")));
		stocks.add(new Stock("JOE", StockType.COMMON, new BigDecimal("13.0"), null, new BigDecimal("250.00")));
	}
	@Override
	public List<Stock> getAll() {
		return stocks;
	}


	@Override
	public void add(Stock stock) {
		stocks.add(stock);		
	}


	@Override
	public Optional<Stock> findMatchingSymbol(String symbol) {
		return stocks.stream()
				.filter(s -> s.getSymbol().equalsIgnoreCase(symbol)) //assuming small case
				.findFirst();
	}


}
