package eu.europa.ec.jrc.lca.commons.view.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.service.LazyLoader;

public class ILCDLazyDataModel<T> extends LazyDataModel<T> {

	private static final long serialVersionUID = 1688174744205570903L;

	public static final int DEFAULT_PAGE_SIZE = 10;

	private LazyLoader<T> loader;

	private SearchParameters initialRestriction;

	public ILCDLazyDataModel(LazyLoader<T> loader) {
		this(loader, null);
	}

	public ILCDLazyDataModel(LazyLoader<T> loader, SearchParameters initialRestriction) {
		this.setLoader(loader);
		this.setInitialRestriction(initialRestriction);
		this.setPageSize(DEFAULT_PAGE_SIZE);
		this.setRowCount(loader.count(initialRestriction).intValue());
	}

	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {

		SearchParameters sp = new SearchParameters(first, pageSize, sortField, (sortOrder == SortOrder.ASCENDING),
				getParametersMap(filters));

		if (getInitialRestriction() != null) {
			sp.append(getInitialRestriction());
		}

		this.setRowCount(getLoader().count(sp).intValue());
		return getLoader().loadLazy(sp);
	}

	private Map<String, Object[]> getParametersMap(Map<String, String> filters) {
		Map<String, Object[]> paramsMap = new HashMap<String, Object[]>();
		for (Entry<String, String> entry : filters.entrySet()) {
			paramsMap.put(entry.getKey(), new Object[] { entry.getValue() });
		}
		return paramsMap;
	}

	public LazyLoader<T> getLoader() {
		return loader;
	}

	public void setLoader(LazyLoader<T> loader) {
		this.loader = loader;
	}

	public SearchParameters getInitialRestriction() {
		return initialRestriction;
	}

	public void setInitialRestriction(SearchParameters initialRestriction) {
		this.initialRestriction = initialRestriction;
	}
}
