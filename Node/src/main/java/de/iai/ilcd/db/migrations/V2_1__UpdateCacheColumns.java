package de.iai.ilcd.db.migrations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.googlecode.flyway.core.migration.java.JavaMigration;

import de.iai.ilcd.model.common.GlobalRefUriAnalyzer;
import de.iai.ilcd.webgui.controller.ui.ComplianceUtilHandler;
import de.iai.ilcd.webgui.controller.ui.ComplianceUtilHandler.ComplianceSystemCode;

/**
 * Initialize the cache columns
 */
public class V2_1__UpdateCacheColumns implements JavaMigration {

	/**
	 * Logger to use
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger( V2_1__UpdateCacheColumns.class );

	/**
	 * Count of rows per iteration. In order to save memory, not all rows are processed simultaneously.
	 */
	private final static int ROWS_PER_ITERATION = 500;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void migrate( JdbcTemplate jdbcTemplate ) throws Exception {
		this.initProcessCacheWithoutComplianceSystemCol( jdbcTemplate );
		this.initProcessComplianceSystemColCache( jdbcTemplate );

		this.initFlowNameClassificationCache( jdbcTemplate, false ); // only non-elementary flows
		this.initFlowNameClassificationCache( jdbcTemplate, true ); // only elementary flows
		this.initFlowRefPropertyCache( jdbcTemplate ); // all flows

		this.initLCIAMethodCache( jdbcTemplate );
		this.initSourceCache( jdbcTemplate );
		this.initContactCache( jdbcTemplate );
		this.initUnitgroupCache( jdbcTemplate );
		this.initFlowPropertyCache( jdbcTemplate );

	}

	private void initProcessComplianceSystemColCache( final JdbcTemplate jdbcTemplate ) {

		ComplianceSystemRowCallbackHandler rcbh = new ComplianceSystemRowCallbackHandler() {

			private long lastProcessId = -1L;

			private List<String> uuids = new ArrayList<String>();

			private final ComplianceUtilHandler cUtil = new ComplianceUtilHandler();

			private final Object[] argTypes = new Object[] { Types.VARCHAR, Types.BIGINT };

			@Override
			public synchronized void processRow( ResultSet rs ) throws SQLException {
				long procId = rs.getLong( "pid" );
				// check if new process id changed, if yes => DB update for previous
				if ( procId != this.lastProcessId && this.lastProcessId != -1L ) {
					this.doUpdate();
				}

				String uuid = rs.getString( "UUID" );
				if ( uuid == null || uuid.trim().isEmpty() ) {
					String uri = rs.getString( "URI" );
					if ( uri != null ) {
						GlobalRefUriAnalyzer uriAnalyzer = new GlobalRefUriAnalyzer( uri );
						uuid = uriAnalyzer.getUuidAsString();
					}
				}
				this.lastProcessId = procId;
				if ( uuid != null ) {
					this.uuids.add( uuid );
				}

			}

			@Override
			public void doUpdate() {
				ComplianceSystemCode code = this.cUtil.getComplianceCode( this.uuids );
				jdbcTemplate.update( "UPDATE `process` SET `complianceSystem_cache`=? WHERE `ID`=?", new Object[] {
						StringUtils.substring( code.toString(), 0, 1 ), this.lastProcessId }, this.argTypes );
				this.uuids.clear();
			}
		};

		V2_1__UpdateCacheColumns.LOGGER.info( "Initializing cache for Compliance Systems of Processes" );
		jdbcTemplate
				.query( "SELECT pc.`Process_ID` as `pid`, gr.`UUID`,gr.`URI` FROM `process_compliancesystem` pc LEFT JOIN `compliancesystem`cs ON(pc.`complianceSystems_ID`=cs.`ID`) LEFT JOIN `globalreference` gr ON(cs.`SOURCEREFERENCE_ID`=gr.`ID`) WHERE cs.`OVERALLCOMPLIANCE`=\"FULLY_COMPLIANT\" ORDER BY pc.`Process_ID`",
						rcbh );

		// update last entry - not performed by query call because last process ID in result set
		// will never have a following different ID which triggers the update in the processRow-Method
		rcbh.doUpdate();
	}

	/**
	 * Required for #initProcessComplianceSystemColCache
	 */
	private interface ComplianceSystemRowCallbackHandler extends RowCallbackHandler {

		/**
		 * Do actual update of data in DB
		 */
		public void doUpdate();
	}

	/**
	 * Initialize the cache for source (name + classification only)
	 * 
	 * @param jdbcTemplate
	 *            JDBC template to use
	 */
	private void initProcessCacheWithoutComplianceSystemCol( JdbcTemplate jdbcTemplate ) {
		ResultSetExtractor<NameClassLCIMethodCache> rse = new ResultSetExtractor<NameClassLCIMethodCache>() {

			@Override
			public NameClassLCIMethodCache createCacheObject( ResultSet rs ) throws SQLException, DataAccessException {
				String cl0 = rs.getString( "cl0" );
				String cl1 = rs.getString( "cl1" );
				String cl = "";
				if ( cl0 != null ) {
					cl += cl0;
					if ( cl1 != null ) {
						cl += " / " + cl1;
					}
				}

				StringBuilder name = new StringBuilder();
				String tmp = rs.getString( "baseName" );
				if ( !StringUtils.isBlank( tmp ) ) {
					name.append( tmp );
				}
				tmp = rs.getString( "routeName" );
				if ( !StringUtils.isBlank( tmp ) ) {
					name.append( ";" ).append( tmp );
				}
				tmp = rs.getString( "locationName" );
				if ( !StringUtils.isBlank( tmp ) ) {
					name.append( ";" ).append( tmp );
				}
				tmp = rs.getString( "unitName" );
				if ( !StringUtils.isBlank( tmp ) ) {
					name.append( ";" ).append( tmp );
				}

				return new NameClassLCIMethodCache( rs.getLong( "ID" ), name.toString(), cl, rs.getString( "lci" ) );
			}
		};

		QueryProvider<NameClassLCIMethodCache> qp = new QueryProvider<NameClassLCIMethodCache>() {

			@Override
			public String getSelectQueryText( long startIdx ) {
				String[] cl = this.getClassificationJoinWhere( "CLASSIFICATION_ID", "p", "" );
				return "SELECT p.`ID`,p.`baseName`,p.`routeName`,p.`locationName`,p.`unitName`,lc.`METHODPRINCIPLE` as `lci`," + this.getClClass( "", 0, "cl0" )
						+ "," + this.getClClass( "", 1, "cl1" )
						+ " FROM `process` p LEFT JOIN `lcimethodinformation` lc ON (p.`LCIMETHODINFORMATION_ID`=lc.`ID`) " + cl[0] + " WHERE " + cl[1]
						+ " GROUP BY p.`ID` " + this.getLimit( startIdx );
			}

			@Override
			public String getUpdateQueryText() {
				return "UPDATE `process` SET `name_cache`=?, `classification_cache`=?, `lciMethodInformation_cache`=? WHERE `ID`=? LIMIT 1";
			}

			@Override
			public void setPreparedStatementParams( PreparedStatement ps, NameClassLCIMethodCache data ) throws SQLException {
				ps.setString( 1, data.name );
				ps.setString( 2, data.classification );
				ps.setString( 3, data.lciMethod );
				ps.setLong( 4, data.id );
			}
		};

		V2_1__UpdateCacheColumns.LOGGER.info( "Initializing cache for Processes (without compliance system column)" );
		this.initCache( jdbcTemplate, "process", null, qp, rse );
	}

	/**
	 * Initialize the cache for flow (reference property and reference property unit)
	 * 
	 * @param jdbcTemplate
	 *            JDBC template to use
	 */
	private void initFlowRefPropertyCache( JdbcTemplate jdbcTemplate ) {
		ResultSetExtractor<RefPropAndUnitCache> rse = new ResultSetExtractor<RefPropAndUnitCache>() {

			@Override
			public RefPropAndUnitCache createCacheObject( ResultSet rs ) throws SQLException, DataAccessException {
				String refProp, refPropUnit, fpName = rs.getString( "fpName" ), fpgrName = rs.getString( "fpgrName" ), uName = rs.getString( "uName" ), ugrName = rs
						.getString( "ugrName" );

				refProp = fpName != null ? fpName : fpgrName;
				refPropUnit = uName != null ? uName : ugrName;
				return new RefPropAndUnitCache( rs.getLong( "ID" ), refProp, refPropUnit );
			}
		};

		QueryProvider<RefPropAndUnitCache> qp = new QueryProvider<RefPropAndUnitCache>() {

			@Override
			public String getSelectQueryText( long startIdx ) {
				return "SELECT f.`ID`, fp.`name` as `fpName`, fpgr.`DEFAULTVALUE` as `fpgrName`, u.`unitname` as `uName`, uggr.`DEFAULTVALUE` as `ugrName` FROM `flow` f "
						+ "LEFT JOIN `flowpropertydescription` fpd ON(f.`REFERENCEPROPERTY_ID`=fpd.`ID`) "
						+ "LEFT JOIN `flowproperty` fp ON(fpd.`FLOWPROPERTY_ID`=fp.`ID`) "
						+ "LEFT JOIN `globalreference` fpgr ON(fpd.`FLOWPROPERTYREF_ID`=fpgr.`ID`) "
						+ "LEFT JOIN `unitgroup` ug ON(fp.`UNITGROUP_ID`=ug.`ID`) "
						+ "LEFT JOIN `unit` u ON(ug.`REFERENCEUNIT_ID`=u.`ID`) "
						+ "LEFT JOIN `globalreference` uggr ON(fp.`REFERENCETOUNITGROUP_ID`=uggr.`ID`) " + this.getLimit( startIdx );
			}

			@Override
			public String getUpdateQueryText() {
				return "UPDATE `flow` SET `referenceProperty_cache`=?, `referencePropertyUnit_cache`=? WHERE `ID`=? LIMIT 1";
			}

			@Override
			public void setPreparedStatementParams( PreparedStatement ps, RefPropAndUnitCache data ) throws SQLException {
				ps.setString( 1, data.refProp );
				ps.setString( 2, data.refPropUnit );
				ps.setLong( 3, data.id );
			}
		};

		V2_1__UpdateCacheColumns.LOGGER.info( "Initializing cache for Reference Property and Reference Property Unit Cache for Flow" );
		this.initCache( jdbcTemplate, "flow", null, qp, rse );
	}

	/**
	 * Initialize the cache for flow (name + classification only)
	 * 
	 * @param jdbcTemplate
	 *            JDBC template to use
	 * @param elementary
	 *            elementary flows or other flows
	 */
	private void initFlowNameClassificationCache( JdbcTemplate jdbcTemplate, boolean elementary ) {
		final String classField = elementary ? "CATEGORIZATION_ID" : "CLASSIFICATION_ID";
		final String operator = elementary ? "=" : "!=";

		ResultSetExtractor<NameClassCache> rse = new ResultSetExtractor<NameClassCache>() {

			@Override
			public NameClassCache createCacheObject( ResultSet rs ) throws SQLException, DataAccessException {
				String cl0 = rs.getString( "cl0" );
				String cl1 = rs.getString( "cl1" );
				String cl = "";
				if ( cl0 != null ) {
					cl += cl0;
					if ( cl1 != null ) {
						cl += " / " + cl1;
					}
				}
				return new NameClassCache( rs.getLong( "ID" ), rs.getString( "name" ), cl );
			}
		};

		QueryProvider<NameClassCache> qp = new QueryProvider<NameClassCache>() {

			@Override
			public String getSelectQueryText( long startIdx ) {
				String[] cl = this.getClassificationJoinWhere( classField, "f", "" );
				return "SELECT f.`ID`,f.`name`," + this.getClClass( "", 0, "cl0" ) + "," + this.getClClass( "", 1, "cl1" ) + " FROM `flow` f " + cl[0]
						+ " WHERE " + cl[1] + " GROUP BY f.`ID` " + this.getLimit( startIdx );
			}

			@Override
			public String getUpdateQueryText() {
				return "UPDATE `flow` SET `name_cache`=?, `classification_cache`=? WHERE `ID`=? LIMIT 1";
			}

			@Override
			public void setPreparedStatementParams( PreparedStatement ps, NameClassCache data ) throws SQLException {
				ps.setString( 1, data.name );
				ps.setString( 2, data.classification );
				ps.setLong( 3, data.id );
			}
		};

		V2_1__UpdateCacheColumns.LOGGER.info( "Initializing name + " + (elementary ? "categorization" : "classification") + " cache for"
				+ (elementary ? "" : " non-") + "elementary flows" );
		this.initCache( jdbcTemplate, "flow", "WHERE `TYPE`" + operator + "'ELEMENTARY_FLOW'", qp, rse );
	}

	/**
	 * Initialize the cache for source (name + classification only)
	 * 
	 * @param jdbcTemplate
	 *            JDBC template to use
	 */
	private void initFlowPropertyCache( JdbcTemplate jdbcTemplate ) {
		ResultSetExtractor<NameClassDefUnitDefUnitGroupCache> rse = new ResultSetExtractor<NameClassDefUnitDefUnitGroupCache>() {

			@Override
			public NameClassDefUnitDefUnitGroupCache createCacheObject( ResultSet rs ) throws SQLException, DataAccessException {
				String cl0 = rs.getString( "cl0" );
				String cl1 = rs.getString( "cl1" );
				String cl = "";
				if ( cl0 != null ) {
					cl += cl0;
					if ( cl1 != null ) {
						cl += " / " + cl1;
					}
				}
				return new NameClassDefUnitDefUnitGroupCache( rs.getLong( "ID" ), rs.getString( "name" ), cl, rs.getString( "defUnit" ), rs
						.getString( "defUnitGroup" ) );
			}
		};

		QueryProvider<NameClassDefUnitDefUnitGroupCache> qp = new QueryProvider<NameClassDefUnitDefUnitGroupCache>() {

			@Override
			public String getSelectQueryText( long startIdx ) {
				String[] cl = this.getClassificationJoinWhere( "CLASSIFICATION_ID", "fp", "" );
				return "SELECT fp.`ID`,fp.`name`,ug.`name` as `defUnitGroup`,u.`unitname` as `defUnit`,"
						+ this.getClClass( "", 0, "cl0" )
						+ ","
						+ this.getClClass( "", 1, "cl1" )
						+ " FROM `flowproperty` fp LEFT JOIN `unitgroup` ug ON (fp.`REFERENCETOUNITGROUP_ID`=ug.`ID`) LEFT JOIN `unit` u ON (ug.`REFERENCEUNIT_ID`=u.`ID`) "
						+ cl[0] + " WHERE " + cl[1] + " GROUP BY fp.`ID` " + this.getLimit( startIdx );
			}

			@Override
			public String getUpdateQueryText() {
				return "UPDATE `flowproperty` SET `name_cache`=?, `classification_cache`=?, `defaultUnitGroup_cache`=?, `defaultUnit_cache`=? WHERE `ID`=? LIMIT 1";
			}

			@Override
			public void setPreparedStatementParams( PreparedStatement ps, NameClassDefUnitDefUnitGroupCache data ) throws SQLException {
				ps.setString( 1, data.name );
				ps.setString( 2, data.classification );
				ps.setString( 3, data.defaultUnitGroup );
				ps.setString( 4, data.defaultUnit );
				ps.setLong( 5, data.id );
			}
		};

		V2_1__UpdateCacheColumns.LOGGER.info( "Initializing cache for Flow Properties" );
		this.initCache( jdbcTemplate, "flowproperty", null, qp, rse );
	}

	/**
	 * Initialize the cache for unit group (name + classification + default unit only)
	 * 
	 * @param jdbcTemplate
	 *            JDBC template to use
	 */
	private void initUnitgroupCache( JdbcTemplate jdbcTemplate ) {
		ResultSetExtractor<NameClassDefUnitCache> rse = new ResultSetExtractor<NameClassDefUnitCache>() {

			@Override
			public NameClassDefUnitCache createCacheObject( ResultSet rs ) throws SQLException, DataAccessException {
				String cl0 = rs.getString( "cl0" );
				String cl1 = rs.getString( "cl1" );
				String cl = "";
				if ( cl0 != null ) {
					cl += cl0;
					if ( cl1 != null ) {
						cl += " / " + cl1;
					}
				}
				return new NameClassDefUnitCache( rs.getLong( "ID" ), rs.getString( "name" ), cl, rs.getString( "defUnit" ) );
			}
		};

		QueryProvider<NameClassDefUnitCache> qp = new QueryProvider<NameClassDefUnitCache>() {

			@Override
			public String getSelectQueryText( long startIdx ) {
				String[] cl = this.getClassificationJoinWhere( "CLASSIFICATION_ID", "ug", "" );
				return "SELECT ug.`ID`,ug.`name`,u.`unitname` as `defUnit`," + this.getClClass( "", 0, "cl0" ) + "," + this.getClClass( "", 1, "cl1" )
						+ " FROM `unitgroup` ug LEFT JOIN `unit` u ON( ug.`REFERENCEUNIT_ID`=u.`ID`) " + cl[0] + " WHERE " + cl[1] + " GROUP BY ug.`ID` "
						+ this.getLimit( startIdx );
			}

			@Override
			public String getUpdateQueryText() {
				return "UPDATE `unitgroup` SET `name_cache`=?, `classification_cache`=?, `referenceUnit_cache`=? WHERE `ID`=? LIMIT 1";
			}

			@Override
			public void setPreparedStatementParams( PreparedStatement ps, NameClassDefUnitCache data ) throws SQLException {
				ps.setString( 1, data.name );
				ps.setString( 2, data.classification );
				ps.setString( 3, data.defaultUnit );
				ps.setLong( 4, data.id );

			}
		};

		V2_1__UpdateCacheColumns.LOGGER.info( "Initializing cache for Unit Groups" );
		this.initCache( jdbcTemplate, "unitgroup", null, qp, rse );
	}

	/**
	 * Initialize the cache for contact (short name + classification only)
	 * 
	 * @param jdbcTemplate
	 *            JDBC template to use
	 */
	private void initContactCache( JdbcTemplate jdbcTemplate ) {
		ResultSetExtractor<NameClassCache> rse = new ResultSetExtractor<NameClassCache>() {

			@Override
			public NameClassCache createCacheObject( ResultSet rs ) throws SQLException, DataAccessException {
				String cl0 = rs.getString( "cl0" );
				String cl1 = rs.getString( "cl1" );
				String cl = "";
				if ( cl0 != null ) {
					cl += cl0;
					if ( cl1 != null ) {
						cl += " / " + cl1;
					}
				}

				String name = rs.getString( "name" );
				String shortName = rs.getString( "shortName" );

				return new NameClassCache( rs.getLong( "ID" ), (shortName != null && !shortName.trim().isEmpty() ? shortName : name), cl );
			}
		};

		QueryProvider<NameClassCache> qp = new QueryProvider<NameClassCache>() {

			@Override
			public String getSelectQueryText( long startIdx ) {
				String[] cl = this.getClassificationJoinWhere( "CLASSIFICATION_ID", "c", "_" );
				return "SELECT c.`ID`,c.`name`,c.`shortName`," + this.getClClass( "_", 0, "cl0" ) + "," + this.getClClass( "_", 1, "cl1" )
						+ " FROM `contact` c " + cl[0] + " WHERE " + cl[1] + " GROUP BY c.`ID` " + this.getLimit( startIdx );
			}

			@Override
			public String getUpdateQueryText() {
				return "UPDATE `contact` SET `name_cache`=?, `classification_cache`=? WHERE `ID`=? LIMIT 1";
			}

			@Override
			public void setPreparedStatementParams( PreparedStatement ps, NameClassCache data ) throws SQLException {
				ps.setString( 1, data.name );
				ps.setString( 2, data.classification );
				ps.setLong( 3, data.id );
			}
		};

		V2_1__UpdateCacheColumns.LOGGER.info( "Initializing cache for Contacts" );
		this.initCache( jdbcTemplate, "contact", null, qp, rse );
	}

	/**
	 * Initialize the cache for source (name + classification only)
	 * 
	 * @param jdbcTemplate
	 *            JDBC template to use
	 */
	private void initSourceCache( JdbcTemplate jdbcTemplate ) {
		ResultSetExtractor<NameClassCache> rse = new ResultSetExtractor<NameClassCache>() {

			@Override
			public NameClassCache createCacheObject( ResultSet rs ) throws SQLException, DataAccessException {
				String cl0 = rs.getString( "cl0" );
				String cl1 = rs.getString( "cl1" );
				String cl = "";
				if ( cl0 != null ) {
					cl += cl0;
					if ( cl1 != null ) {
						cl += " / " + cl1;
					}
				}
				return new NameClassCache( rs.getLong( "ID" ), rs.getString( "name" ), cl );
			}
		};

		QueryProvider<NameClassCache> qp = new QueryProvider<NameClassCache>() {

			@Override
			public String getSelectQueryText( long startIdx ) {
				String[] cl = this.getClassificationJoinWhere( "CLASSIFICATION_ID", "s", "" );
				return "SELECT s.`ID`,s.`name`," + this.getClClass( "", 0, "cl0" ) + "," + this.getClClass( "", 1, "cl1" ) + " FROM `source` s " + cl[0]
						+ " WHERE " + cl[1] + " GROUP BY s.`ID` " + this.getLimit( startIdx );
			}

			@Override
			public String getUpdateQueryText() {
				return "UPDATE `source` SET `name_cache`=?, `classification_cache`=? WHERE `ID`=? LIMIT 1";
			}

			@Override
			public void setPreparedStatementParams( PreparedStatement ps, NameClassCache data ) throws SQLException {
				ps.setString( 1, data.name );
				ps.setString( 2, data.classification );
				ps.setLong( 3, data.id );
			}
		};

		V2_1__UpdateCacheColumns.LOGGER.info( "Initializing cache for Sources" );
		this.initCache( jdbcTemplate, "source", null, qp, rse );
	}

	/**
	 * Initialize the cache for LCIA method (name only)
	 * 
	 * @param jdbcTemplate
	 *            JDBC template to use
	 */
	private void initLCIAMethodCache( JdbcTemplate jdbcTemplate ) {
		ResultSetExtractor<NameCache> rse = new ResultSetExtractor<NameCache>() {

			@Override
			public NameCache createCacheObject( ResultSet rs ) throws SQLException, DataAccessException {
				return new NameCache( rs.getLong( "ID" ), rs.getString( "name" ) );
			}
		};

		QueryProvider<NameCache> qp = new QueryProvider<NameCache>() {

			@Override
			public String getSelectQueryText( long startIdx ) {
				return "SELECT `ID`,`name` FROM `lciamethod` " + this.getLimit( startIdx );
			}

			@Override
			public String getUpdateQueryText() {
				return "UPDATE `lciamethod` SET `name_cache`=? WHERE `ID`=? LIMIT 1";
			}

			@Override
			public void setPreparedStatementParams( PreparedStatement ps, NameCache data ) throws SQLException {
				ps.setString( 1, data.name );
				ps.setLong( 2, data.id );
			}
		};

		V2_1__UpdateCacheColumns.LOGGER.info( "Initializing cache for LCIAMethods" );
		this.initCache( jdbcTemplate, "lciamethod", null, qp, rse );
	}

	/**
	 * Get count of iterations
	 * 
	 * @param jdbcTemplate
	 * @param tableName
	 * @return
	 */
	private final long getIterations( JdbcTemplate jdbcTemplate, String tableName, String where ) {
		final long count = jdbcTemplate.queryForLong( "SELECT count(*) FROM `" + tableName + "`" + (where != null ? " " + where : "") );
		return (long) Math.ceil( count / (float) V2_1__UpdateCacheColumns.ROWS_PER_ITERATION );
	}

	/**
	 * Initialize cache with given table and query provider
	 * 
	 * @param jdbcTemplate
	 *            JDBC template to use
	 * @param baseTable
	 *            base table to use
	 * @param qp
	 *            query provider to use
	 * @param rse
	 *            the result set extractor to use
	 */
	private <T extends NameCache> void initCache( JdbcTemplate jdbcTemplate, String baseTable, String where, final QueryProvider<T> qp,
			ResultSetExtractor<T> rse ) {
		final long iterations = this.getIterations( jdbcTemplate, baseTable, where );
		for ( long i = 0; i < iterations; i++ ) {
			final String sql = qp.getSelectQueryText( i * V2_1__UpdateCacheColumns.ROWS_PER_ITERATION );

			final List<T> cList = jdbcTemplate.query( sql, rse );

			BatchPreparedStatementSetter pset = new BatchPreparedStatementSetter() {

				@Override
				public void setValues( PreparedStatement ps, int i ) throws SQLException {
					qp.setPreparedStatementParams( ps, cList.get( i ) );
				}

				@Override
				public int getBatchSize() {
					return cList.size();
				}
			};

			jdbcTemplate.batchUpdate( qp.getUpdateQueryText(), pset );
		}
	}

	/**
	 * Query provider
	 * 
	 * @param <T>
	 *            Type of data
	 */
	private abstract class QueryProvider<T> {

		public abstract String getSelectQueryText( long startIdx );

		public abstract String getUpdateQueryText();

		public abstract void setPreparedStatementParams( PreparedStatement ps, T data ) throws SQLException;

		/**
		 * Get the join SQL string for classification + where part
		 * Do not use the aliases <code>aliasPrefix+[c|m1|m2|cla1|cla2]</code> in rest of query!
		 * 
		 * @param classCol
		 *            column for classification
		 * @param mtAlias
		 *            alias of the main table
		 * @return array with <code>[0] &rarr; join string, [1] &rarr; where clause part</code>
		 */
		protected String[] getClassificationJoinWhere( String classCol, String mtAlias, String aliasPrefix ) {
			final String c = aliasPrefix + "c";
			final String m1 = aliasPrefix + "m1";
			final String m2 = aliasPrefix + "m2";
			final String cla0 = aliasPrefix + "cla0";
			final String cla1 = aliasPrefix + "cla1";
			return new String[] {
					"LEFT JOIN `classification` " + c + " ON (" + mtAlias + ".`" + classCol + "`=" + c + ".`ID`) " + "LEFT JOIN `classification_clclass` " + m1
							+ " ON (" + c + ".`ID`=" + m1 + ".`Classification_ID`) " + "LEFT JOIN `clclass` " + cla0 + " ON (" + m1 + ".`classes_ID`=" + cla0
							+ ".`ID` AND " + cla0 + ".`clLevel`=0) " + "LEFT JOIN `classification_clclass` " + m2 + " ON (" + c + ".`ID`=" + m2
							+ ".`Classification_ID` AND " + m2 + ".`classes_ID` != " + m1 + ".`classes_ID`) " + "LEFT JOIN `clclass` " + cla1 + " ON (" + m2
							+ ".`classes_ID`=" + cla1 + ".`ID` AND " + cla1 + ".`clLevel`=1)",

					"(" + cla0 + ".`clLevel`=0 AND (" + cla0 + ".`ID` != " + cla1 + ".`ID` OR " + cla1 + ".`ID` is null))" };
		}

		protected String getClClass( String aliasPrefix, int lvl, String fieldname ) {
			if ( lvl == 1 ) {
				return "GROUP_CONCAT( DISTINCT " + aliasPrefix + "cla" + Integer.toString( lvl ) + ".`clName`) as `" + fieldname + "`";
			}
			return aliasPrefix + "cla" + Integer.toString( lvl ) + ".`clName` as `" + fieldname + "`";
		}

		protected String getLimit( long startIdx ) {
			return "LIMIT " + Long.toString( startIdx ) + "," + Long.toString( V2_1__UpdateCacheColumns.ROWS_PER_ITERATION );
		}
	}

	/**
	 * Utility class for ID and name
	 */
	private static class NameCache {

		/**
		 * Database ID
		 */
		public final long id;

		/**
		 * Value for name cache column
		 */
		public final String name;

		/**
		 * Create the cache object for the name
		 * 
		 * @param id
		 *            Database ID
		 * @param name
		 *            Value for name cache column
		 */
		public NameCache( long id, String name ) {
			super();
			this.id = id;
			this.name = StringUtils.substring( name, 0, 255 );
		}

	}

	/**
	 * Utility class for ID and name and classification
	 */
	private static class NameClassCache extends NameCache {

		/**
		 * Value for classification cache column
		 */
		public final String classification;

		/**
		 * Create the cache object for the name
		 * 
		 * @param id
		 *            Database ID
		 * @param name
		 *            Value for name cache column
		 * @param classification
		 *            Value for classification cache column
		 */
		public NameClassCache( long id, String name, String classification ) {
			super( id, name );
			this.classification = StringUtils.substring( classification, 0, 100 );
		}

	}

	/**
	 * Utility class for ID and name and classification
	 */
	private static class RefPropAndUnitCache extends NameCache {

		/**
		 * Value for reference property cache column
		 */
		public final String refProp;

		/**
		 * Value for reference property unit cache column
		 */
		public final String refPropUnit;

		/**
		 * Create the cache object for the name
		 * 
		 * @param id
		 *            Database ID
		 * @param name
		 *            Value for name cache column
		 * @param classification
		 *            Value for classification cache column
		 */
		public RefPropAndUnitCache( long id, String refProp, String refPropUnit ) {
			super( id, null );
			this.refProp = StringUtils.substring( refProp, 0, 20 );
			this.refPropUnit = StringUtils.substring( refPropUnit, 0, 10 );
		}

	}

	/**
	 * Utility class for ID and name and classification and default unit
	 */
	private static class NameClassDefUnitCache extends NameClassCache {

		/**
		 * Value for classification cache column
		 */
		public final String defaultUnit;

		/**
		 * Create the cache object for the name
		 * 
		 * @param id
		 *            Database ID
		 * @param name
		 *            Value for name cache column
		 * @param classification
		 *            Value for classification cache column
		 */
		public NameClassDefUnitCache( long id, String name, String classification, String defaultUnit ) {
			super( id, name, classification );
			this.defaultUnit = StringUtils.substring( defaultUnit, 0, 10 );
		}

	}

	/**
	 * Utility class for ID and name and classification and default unit
	 */
	private static class NameClassDefUnitDefUnitGroupCache extends NameClassDefUnitCache {

		/**
		 * Value for classification cache column
		 */
		public final String defaultUnitGroup;

		/**
		 * Create the cache object for the name
		 * 
		 * @param id
		 *            Database ID
		 * @param name
		 *            Value for name cache column
		 * @param classification
		 *            Value for classification cache column
		 */
		public NameClassDefUnitDefUnitGroupCache( long id, String name, String classification, String defaultUnit, String defaultUnitGroup ) {
			super( id, name, classification, defaultUnit );
			this.defaultUnitGroup = StringUtils.substring( defaultUnitGroup, 0, 20 );
		}

	}

	/**
	 * Utility class for caching ID and name and classification and LCI method
	 */
	private static class NameClassLCIMethodCache extends NameClassCache {

		/**
		 * Value for LCI method
		 */
		public final String lciMethod;

		/**
		 * Create the cache object for the name
		 * 
		 * @param id
		 *            Database ID
		 * @param name
		 *            Value for name cache column
		 * @param classification
		 *            Value for classification cache column
		 * @param lciMethod
		 *            Value for LCI method cache column
		 */
		public NameClassLCIMethodCache( long id, String name, String classification, String lciMethod ) {
			super( id, name, classification );
			this.lciMethod = StringUtils.substring( lciMethod, 0, 20 );
		}

	}

	/**
	 * Basic extractor for result sets. Manages list and result set row iterations.
	 * 
	 * @param <T>
	 *            type of data to extract
	 */
	private abstract class ResultSetExtractor<T> implements org.springframework.jdbc.core.ResultSetExtractor<List<T>> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final List<T> extractData( ResultSet rs ) throws SQLException, DataAccessException {
			List<T> tmp = new ArrayList<T>();
			while ( rs.next() ) {
				tmp.add( this.createCacheObject( rs ) );
			}
			return tmp;
		}

		/**
		 * Create the object with values that shall be written to cache
		 * 
		 * @param rs
		 *            result set for this row, just use the <code>get<i>DataType</i></code> methods! <b>Do not call
		 *            {@link ResultSet#next()}!</b>
		 * @return create object with values for cache
		 * @throws SQLException
		 *             on SQL error
		 * @throws DataAccessException
		 *             on data access error
		 */
		public abstract T createCacheObject( ResultSet rs ) throws SQLException, DataAccessException;
	}

}