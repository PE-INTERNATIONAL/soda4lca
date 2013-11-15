package eu.europa.ec.jrc.lca.commons.dao;

import java.util.Date;

public interface NonceDao {
	boolean exists(String nonceValue);

	void save(String nonceValue);

	void clearNonces(Date date);
}
