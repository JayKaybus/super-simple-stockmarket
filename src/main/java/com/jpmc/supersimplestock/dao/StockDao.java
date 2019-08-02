package com.jpmc.supersimplestock.dao;

import java.util.Optional;

import com.jpmc.supersimplestock.domain.pojo.Stock;

public interface StockDao extends GenericDao<Stock> {
	
	/**
	 * Find by stock symbol.
	 * @param symbol
	 * @return the {@link Optional<Stock>}
	 */
	Optional<Stock> findMatchingSymbol(String symbol);

}
