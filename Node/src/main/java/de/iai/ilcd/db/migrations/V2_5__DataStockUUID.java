package de.iai.ilcd.db.migrations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.googlecode.flyway.core.migration.java.JavaMigration;

/**
 * Add UUID to data stocks and set random UUIDs for availabe data stocks.
 */
public class V2_5__DataStockUUID implements JavaMigration {

	/**
	 * Logger to use
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger( V2_5__DataStockUUID.class );

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void migrate( JdbcTemplate jdbcTemplate ) throws Exception {

		V2_5__DataStockUUID.LOGGER.info( "Adding UUIDs to DataStocks." );

		final String sqlSelectIds = "SELECT `ID` FROM `datastock`";
		final String sqlAddCol = "ALTER TABLE `datastock` ADD `UUID` VARCHAR( 36 ) NOT NULL ";
		final String sqlSetUUID = "UPDATE `datastock` SET `UUID`=? WHERE `ID`=?";
		final String sqlUniqueKey = "ALTER TABLE `datastock` ADD UNIQUE (`UUID`)";

		// 1st: Add varchar column
		jdbcTemplate.execute( sqlAddCol );

		// 2nd: get all DataStock IDs
		ResultSetExtractor<List<Long>> rse = new ResultSetExtractor<List<Long>>() {

			@Override
			public List<Long> extractData( ResultSet rs ) throws SQLException, DataAccessException {
				List<Long> lst = new ArrayList<Long>();
				while ( rs.next() ) {
					lst.add( rs.getLong( "ID" ) );
				}
				return lst;
			}
		};
		final List<Long> idList = jdbcTemplate.query( sqlSelectIds, rse );

		// 3rd: set random UUIDs for all DataStocks
		BatchPreparedStatementSetter pstSet = new BatchPreparedStatementSetter() {

			@Override
			public void setValues( PreparedStatement ps, int i ) throws SQLException {
				ps.setString( 1, UUID.randomUUID().toString() );
				ps.setLong( 2, idList.get( i ) );
			}

			@Override
			public int getBatchSize() {
				return idList.size();
			}
		};
		jdbcTemplate.batchUpdate( sqlSetUUID, pstSet );

		// 4th: add unique key
		jdbcTemplate.execute( sqlUniqueKey );

	}

}
