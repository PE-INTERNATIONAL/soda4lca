package eu.europa.ec.jrc.lca.commons.dao;

import java.util.HashMap;
import java.util.Map;

public class SearchParameters {
	private int first;
	private int pageSize;
	private String sortField;
	private boolean ascending;
	private Map<String, Object[]> filters = new HashMap<String, Object[]>();
	
	public SearchParameters(){
		
	}

	public SearchParameters(int first, int pageSize, String sortField,
			boolean ascending, Map<String, Object[]> filters){
		this.first = first;
		this.pageSize = pageSize;
		this.sortField = sortField;
		this.ascending = ascending;
		this.filters = filters;
	}

	public void append(SearchParameters sp){
		this.filters.putAll(sp.getFilters());
		this.sortField = sp.getSortField();
		this.ascending = sp.isAscending();
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public Map<String, Object[]> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Object[]> filters) {
		this.filters = filters;
	}

	public void addFilter(String key, Object...values){
		filters.put(key, values);
	}
}
