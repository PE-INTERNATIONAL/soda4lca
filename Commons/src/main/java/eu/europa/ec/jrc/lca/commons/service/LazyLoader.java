package eu.europa.ec.jrc.lca.commons.service;

import java.util.List;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;

public interface LazyLoader<T> {
	List<T> loadLazy(SearchParameters sp);

	Long count(SearchParameters sp);
}
