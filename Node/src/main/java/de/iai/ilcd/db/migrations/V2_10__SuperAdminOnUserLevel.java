package de.iai.ilcd.db.migrations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.googlecode.flyway.core.migration.java.JavaMigration;

/**
 * Super admin permission on user level
 */
public class V2_10__SuperAdminOnUserLevel implements JavaMigration {

	/**
	 * Logger to use
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger( V2_10__SuperAdminOnUserLevel.class );

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void migrate( JdbcTemplate jdbcTemplate ) throws Exception {
		V2_10__SuperAdminOnUserLevel.LOGGER.info( "Manage super admin permission on user level" );

		final String sqlAddCol = "ALTER TABLE `user` ADD `super_admin_permission` TINYINT( 1 ) NOT NULL, ADD INDEX ( `super_admin_permission` )";
		final String sqlSelect = "SELECT u.`ID` FROM `user` u LEFT JOIN `usergroup_user` m ON (u.`ID`=m.`users_ID`) LEFT JOIN `usergroup` g ON (g.`ID`=m.`groups_ID`) WHERE g.`super_admin_permission`=1";
		final String sqlUpdate = "UPDATE `user` SET `super_admin_permission`=1 WHERE `ID`=?";
		final String sqlDropCol = "ALTER TABLE usergroup DROP INDEX `super_admin_permission`, DROP `super_admin_permission`";

		// 1st: Add tinyint column
		jdbcTemplate.execute( sqlAddCol );

		// 2nd: get the user IDs of all current users with super admin permissions
		final ResultSetExtractor<List<Long>> rse = new ResultSetExtractor<List<Long>>() {

			@Override
			public List<Long> extractData( ResultSet rs ) throws SQLException, DataAccessException {
				List<Long> lst = new ArrayList<Long>();
				while ( rs.next() ) {
					lst.add( rs.getLong( "ID" ) );
				}
				return lst;
			}
		};
		final List<Long> cList = jdbcTemplate.query( sqlSelect, rse );

		// 3rd: and set the new permissions on user level
		BatchPreparedStatementSetter pset = new BatchPreparedStatementSetter() {

			@Override
			public void setValues( PreparedStatement ps, int i ) throws SQLException {
				ps.setLong( 1, cList.get( i ) );
			}

			@Override
			public int getBatchSize() {
				return cList.size();
			}
		};
		jdbcTemplate.batchUpdate( sqlUpdate, pset );

		// 4th: drop old column + index
		jdbcTemplate.execute( sqlDropCol );

	}

}
