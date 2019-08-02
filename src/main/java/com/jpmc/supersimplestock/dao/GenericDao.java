package com.jpmc.supersimplestock.dao;

import java.util.List;

public interface GenericDao<T> {
	
	/**
	 * Get all elements
	 * @return T
	 */
	 List<T> getAll();
	
	/**
	 * Add new element
	 * @param element
	 */
	void add(T element);

}
