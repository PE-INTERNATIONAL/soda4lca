package de.iai.ilcd.model.process;

import java.util.Comparator;

import de.fzk.iai.ilcd.service.model.enums.DataQualityIndicatorName;

public class DataQualityIndicatorComparator implements Comparator<DataQualityIndicator> {

	@Override
	public int compare( DataQualityIndicator arg0, DataQualityIndicator arg1 ) {

		if ( arg0.name.equals( DataQualityIndicatorName.OVERALL_QUALITY ) ) {
			return 100;
		}

		return arg0.getName().compareTo( arg1.getName() );
	}

}
