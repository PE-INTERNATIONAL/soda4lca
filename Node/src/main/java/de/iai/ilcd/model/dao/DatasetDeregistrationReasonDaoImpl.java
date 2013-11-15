package de.iai.ilcd.model.dao;

import org.springframework.stereotype.Repository;

import de.iai.ilcd.model.registry.DatasetDeregistrationReason;

@Repository( value = "datasetDeregistrationReasonDao" )
public class DatasetDeregistrationReasonDaoImpl extends GenericDAOImpl<DatasetDeregistrationReason, Long> implements DatasetDeregistrationReasonDao {

}
