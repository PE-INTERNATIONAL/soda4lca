package de.iai.ilcd.webgui.controller;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean( name = "dirty" )
@ApplicationScoped
public class DirtyFlagBean {

	/**
	 * Time stamp of last data stock modification
	 */
	private long dataStockDirtyStamp = DirtyFlagBean.getNow();

	/**
	 * Determine if provided time stamp indicates that a stock was modified since
	 * 
	 * @param ts
	 *            time stamp to check
	 * @return <code>true</code> if modification was performed, <code>false</code> otherwise
	 */
	public boolean isStockModificationDirty( long ts ) {
		return this.dataStockDirtyStamp > ts;
	}

	/**
	 * Set stock modification to now
	 */
	public void stockModified() {
		this.dataStockDirtyStamp = DirtyFlagBean.getNow();
	}

	/**
	 * Get the current time stamp / milliseconds
	 * 
	 * @return current time stamp / milliseconds
	 */
	public final static long getNow() {
		return System.currentTimeMillis();
	}

}
