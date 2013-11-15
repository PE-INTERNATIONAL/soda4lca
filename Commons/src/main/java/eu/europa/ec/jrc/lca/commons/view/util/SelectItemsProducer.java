package eu.europa.ec.jrc.lca.commons.view.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public abstract class SelectItemsProducer<T> {
	
	private List<SelectItem> items = new ArrayList<SelectItem>();
	
	public List<SelectItem> getItems(){
		return items;
	}
	
	public SelectItemsProducer(){
		
	}
	
	public SelectItemsProducer(List<T> entities){
		setEntities(entities);
	}
	
	public final void setEntities(List<T> entities){
		if(entities!=null){
			for(T entity: entities){
				SelectItem item = new SelectItem();
				item.setValue(getValue(entity));
				item.setLabel(getLabel(entity));
				items.add(item);
			}
		}
	}
	
	public void addSelectItem(int position, SelectItem item){
		items.add(position, item);
	}
	
	public void addNotSelectedItem(){
		SelectItem notSelected = new SelectItem("", Messages.getString(null, "notSelected", null));
		notSelected.setNoSelectionOption(true);
		this.addSelectItem(0, notSelected);
	}
	
	public abstract Object getValue(T entity);
	
	public abstract String getLabel(T entity);
}
