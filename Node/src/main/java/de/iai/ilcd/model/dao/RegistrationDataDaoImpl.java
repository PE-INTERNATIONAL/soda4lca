package de.iai.ilcd.model.dao;

import org.springframework.stereotype.Repository;

import de.iai.ilcd.model.registry.RegistrationData;

@Repository( value = "registrationDataDao" )
public class RegistrationDataDaoImpl extends GenericDAOImpl<RegistrationData, Long> implements RegistrationDataDao {

}
