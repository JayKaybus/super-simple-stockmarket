package com.jpmc.supersimplestock.dao;

import java.util.List;

import com.jpmc.supersimplestock.domain.pojo.Trade;

public interface TradeDao extends GenericDao<Trade> {
	
	/**
	 * Find trades by stock symbol.
	 * @param stockSymbol
	 * @return the {@link List<Trade>}
	 */
	 List<Trade> findByStockSymbol(String stockSymbol);

}
