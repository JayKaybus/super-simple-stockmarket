package com.jpmc.supersimplestock.dao.impl;

import com.jpmc.supersimplestock.dao.TradeDao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jpmc.supersimplestock.domain.pojo.Trade;


@Service("superSimpleTradeDaoService")
public class TradeDaoImpl implements TradeDao {

	List<Trade> tradeItem;
	
	private TradeDaoImpl() {
		tradeItem = new ArrayList<>();
	}

	@Override
	public List<Trade> getAll() {
		return tradeItem;
	}

	@Override
	public void add(Trade trade) {
		tradeItem.add(trade);
	}

	@Override
	public List<Trade> findByStockSymbol(String stockSymbol) {
		return tradeItem.stream()
				.filter(t -> t.getStockSymbol().equals(stockSymbol))
				.collect(Collectors.toList());
	}


}
