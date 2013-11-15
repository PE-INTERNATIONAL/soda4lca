package de.iai.ilcd.db.migrations;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.persistence.Enumerated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.migration.java.JavaMigration;

import de.fzk.iai.ilcd.service.model.enums.LCIMethodPrincipleValue;
import de.iai.ilcd.model.process.LCIMethodInformation;

/**
 * Fixes problem with {@link LCIMethodInformation#methodPrinciple} field. The {@link Enumerated}-Annotation
 * was missing and therefore the change of the order of the {@link LCIMethodPrincipleValue} enum values could
 * lead to bad data. After the patch, string based enum storage is used.
 */
public class V1_1__FixLCIMethodEnumCol implements JavaMigration {

	/**
	 * Logger to use
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger( V1_1__FixLCIMethodEnumCol.class );

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void migrate( JdbcTemplate jdbcTemplate ) throws Exception {

		V1_1__FixLCIMethodEnumCol.LOGGER.info( "Fixing enum storage for LCIMethodInformation field 'methodPrinciple' from enum index to dstring value." );

		final String sqlColStrAdd = "ALTER TABLE `lcimethodinformation` ADD `METHODPRINCIPLE_STR` VARCHAR( 255 ) NULL";
		final String sqlUpd = "UPDATE `lcimethodinformation` SET `METHODPRINCIPLE_STR`=? WHERE `METHODPRINCIPLE`=?";
		final String sqlColDrop = "ALTER TABLE `lcimethodinformation` DROP `METHODPRINCIPLE`";
		final String sqlColStrRename = "ALTER TABLE `lcimethodinformation` CHANGE `METHODPRINCIPLE_STR` `METHODPRINCIPLE` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL";

		// 1st: Add varchar column
		jdbcTemplate.execute( sqlColStrAdd );

		final LCIMethodPrincipleValue[] enumVals = LCIMethodPrincipleValue.values();

		// 2nd: Set all values
		BatchPreparedStatementSetter pstSet = new BatchPreparedStatementSetter() {

			@Override
			public void setValues( PreparedStatement ps, int i ) throws SQLException {
				ps.setString( 1, enumVals[i].name() );
				ps.setInt( 2, i );
			}

			@Override
			public int getBatchSize() {
				return enumVals.length;
			}
		};
		jdbcTemplate.batchUpdate( sqlUpd, pstSet );

		// 3rd: drop original enum index column
		jdbcTemplate.execute( sqlColDrop );

		// 4th: rename new column
		jdbcTemplate.execute( sqlColStrRename );

	}

}
