package de.iai.ilcd.model.adapter;

import java.util.List;

import de.fzk.iai.ilcd.service.client.impl.vo.DataStockList;
import de.fzk.iai.ilcd.service.client.impl.vo.DataStockVO;
import de.iai.ilcd.model.datastock.AbstractDataStock;

/**
 * Adapter for data stock lists
 */
public class DataStockListAdapter extends DataStockList {

	/**
	 * Create the adapter
	 * 
	 * @param datastocks
	 *            the data stocks to adapt
	 */
	public DataStockListAdapter( List<AbstractDataStock> datastocks ) {
		final List<DataStockVO> lstDsVo = this.getDataStocks();
		if ( datastocks != null ) {
			for ( AbstractDataStock ads : datastocks ) {
				lstDsVo.add( DataStockListAdapter.getServiceApiVo( ads ) );
			}
		}
	}

	/**
	 * Copy all values from an {@link AbstractDataStock} object to a {@link DataStockVO} instance.
	 * 
	 * @param ads
	 *            data stock to adapt
	 * @return created {@link DataStockVO}
	 */
	public static DataStockVO getServiceApiVo( AbstractDataStock ads ) {
		DataStockVO vo = new DataStockVO();
		vo.setRoot( ads.isRoot() );
		vo.setShortName( ads.getName() );
		vo.setUuid( ads.getUuid().getUuid() );
		LStringAdapter.copyLStrings( ads.getLongTitle(), vo.getName() );
		LStringAdapter.copyLStrings( ads.getDescription(), vo.getDescription() );
		return vo;
	}

}
