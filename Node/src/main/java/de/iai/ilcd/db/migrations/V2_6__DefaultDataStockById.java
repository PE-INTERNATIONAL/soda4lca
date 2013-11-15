package de.iai.ilcd.db.migrations;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.googlecode.flyway.core.migration.java.JavaMigration;

/**
 * Replace name as identifier for default data stock
 */
public class V2_6__DefaultDataStockById implements JavaMigration {

	/**
	 * Logger to use
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger( V2_6__DefaultDataStockById.class );

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void migrate( JdbcTemplate jdbcTemplate ) throws Exception {

		V2_6__DefaultDataStockById.LOGGER.info( "Replace name as identifier for default data stock." );

		final String sqlAddCol = "ALTER TABLE `configuration` ADD `default_datastock_id` BIGINT( 20 ) NOT NULL;";
		final String sqlSelectId = "SELECT d.`ID` FROM `configuration` c LEFT JOIN `datastock` d ON(c.`default_datastock_name`=d.`name`) LIMIT 1";
		final String sqlSetIdValue = "UPDATE `configuration`SET `default_datastock_id` = ?";
		final String sqlDelCol = "ALTER TABLE `configuration` DROP `default_datastock_name`";

		// 1st: Add bigint column
		jdbcTemplate.execute( sqlAddCol );

		// 2nd: get the ID
		ResultSetExtractor<Long> rse = new ResultSetExtractor<Long>() {

			@Override
			public Long extractData( ResultSet rs ) throws SQLException, DataAccessException {
				if ( rs.next() ) {
					return rs.getLong( "ID" );
				}
				else {
					return 1L;
				}
			}
		};
		final Long id = jdbcTemplate.query( sqlSelectId, rse );

		// 3rd: set the ID
		jdbcTemplate.update( sqlSetIdValue, id );

		// 4th: remove varchar col
		jdbcTemplate.execute( sqlDelCol );

	}

}
