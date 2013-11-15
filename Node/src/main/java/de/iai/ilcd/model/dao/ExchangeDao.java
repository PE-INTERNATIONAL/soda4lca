package de.iai.ilcd.model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.fzk.iai.ilcd.service.model.enums.TypeOfFlowValue;
import de.iai.ilcd.model.common.DataSetVersion;
import de.iai.ilcd.model.process.Exchange;
import de.iai.ilcd.model.process.ExchangeDirection;
import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * Data access object for Exchanges
 */
public class ExchangeDao {

	/**
	 * Get the list of exchanges of a process with given UUID and version
	 * 
	 * @param processUuid
	 * @param v
	 *            specific version of process data set. may be <code>null</code> (means: mostRecentVersion)
	 * @param exDir
	 *            exchange direction (may be <code>null</code>, means: no constraint, both directions in result)
	 * @param flowType
	 *            type of the flow (may be <code>null</code>, means: all types will be returned)
	 * @return list of exchanges matching the criteria
	 */
	@SuppressWarnings( "unchecked" )
	public List<Exchange> getExchangesForProcess( String processUuid, DataSetVersion v, ExchangeDirection exDir, TypeOfFlowValue flowType ) {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		sb.append( "SELECT DISTINCT(e) FROM Process p LEFT JOIN p.exchanges e WHERE p.uuid.uuid=:uuid" );
		paramMap.put( "uuid", processUuid );

		// version handling: version provided: use it, else use most recent one
		if ( v != null ) {
			sb.append( " AND p.version.majorVersion=:majVer AND p.version.minorVersion=:minVer AND p.version.subMinorVersion=:subVer" );
			paramMap.put( "majVer", v.getMajorVersion() );
			paramMap.put( "minVer", v.getMinorVersion() );
			paramMap.put( "subVer", v.getSubMinorVersion() );

		}
		else {
			sb.append( " AND p.mostRecentVersion=1" );
		}

		if ( exDir != null ) {
			sb.append( " AND e.exchangeDirection=:dir" );
			paramMap.put( "dir", exDir );
		}

		if ( exDir != null ) {
			sb.append( " AND e.exchangeDirection=:dir" );
			paramMap.put( "dir", exDir );
		}

		if ( flowType != null ) {
			sb.append( " AND e.flow IS NOT NULL AND e.flow.type=:type" );
			paramMap.put( "type", flowType );
		}

		EntityManager em = PersistenceUtil.getEntityManager();
		Query q = em.createQuery( sb.toString() );

		for ( Entry<String, Object> param : paramMap.entrySet() ) {
			q.setParameter( param.getKey(), param.getValue() );
		}

		return (List<Exchange>) q.getResultList();
	}

}
