package de.iai.ilcd.db.migrations;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.xml.JDOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.googlecode.flyway.core.migration.java.JavaMigration;

import de.iai.ilcd.xml.read.DataSetParsingHelper;

/**
 * Create Product Model flag column and fill for current processes
 */
public class V2_8__ProductModelFlag implements JavaMigration {

	/**
	 * Logger to use
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger( V2_8__ProductModelFlag.class );

	/**
	 * Rows per iteration
	 */
	private final static int ROWS_PER_ITERATION = 5;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void migrate( JdbcTemplate jdbcTemplate ) throws Exception {
		V2_8__ProductModelFlag.LOGGER.info( "Create Product Model flag column and fill for current processes." );

		final String sqlAddCol = "ALTER TABLE `process` ADD `containsProductModel` TINYINT( 1 ) NOT NULL , ADD INDEX ( `containsProductModel` )";
		final String sqlUpdate = "UPDATE `process` SET `containsProductModel`=? WHERE `ID`=?";

		// 1st: Add tinyint column
		jdbcTemplate.execute( sqlAddCol );

		final long iterations = this.getIterations( jdbcTemplate );

		final ResultSetExtractor<List<ProductModelFlag>> rse = new ResultSetExtractor<List<ProductModelFlag>>() {

			@Override
			public List<ProductModelFlag> extractData( ResultSet rs ) throws SQLException, DataAccessException {
				List<ProductModelFlag> lst = new ArrayList<ProductModelFlag>();
				while ( rs.next() ) {
					final long id = rs.getLong( "ID" );
					final byte[] data = rs.getBytes( "COMPRESSEDCONTENT" );
					lst.add( new ProductModelFlag( id, data ) );
				}
				return lst;
			}
		};

		for ( long i = 0; i < iterations; i++ ) {
			final String sql = "SELECT p.`ID`,x.`COMPRESSEDCONTENT` FROM `process` p LEFT JOIN `xmlfile` x ON(p.`XMLFILE_ID`=x.`ID`) " + this.getLimit( i );

			final List<ProductModelFlag> cList = jdbcTemplate.query( sql, rse );

			BatchPreparedStatementSetter pset = new BatchPreparedStatementSetter() {

				@Override
				public void setValues( PreparedStatement ps, int i ) throws SQLException {
					final ProductModelFlag f = cList.get( i );
					ps.setShort( 1, f.flag ? (short) 1 : (short) 0 );
					ps.setLong( 2, f.id );
				}

				@Override
				public int getBatchSize() {
					return cList.size();
				}
			};

			jdbcTemplate.batchUpdate( sqlUpdate, pset );
		}

	}

	/**
	 * Get SQL limit text for iteration step
	 * 
	 * @param iteration
	 *            index of iteration
	 * @return SQL limit text
	 */
	private String getLimit( long iteration ) {
		final long startIdx = iteration * V2_8__ProductModelFlag.ROWS_PER_ITERATION;
		return "LIMIT " + Long.toString( startIdx ) + "," + Long.toString( V2_8__ProductModelFlag.ROWS_PER_ITERATION );
	}

	/**
	 * Get count of iterations
	 * 
	 * @param jdbcTemplate
	 * @param tableName
	 * @return count of iterations
	 */
	private final long getIterations( JdbcTemplate jdbcTemplate ) {
		final long count = jdbcTemplate.queryForLong( "SELECT count(*) FROM `process`" );
		return (long) Math.ceil( count / (float) V2_8__ProductModelFlag.ROWS_PER_ITERATION );
	}

	/**
	 * Utility class for ID and name
	 */
	private static class ProductModelFlag {

		/**
		 * Database ID
		 */
		public final long id;

		/**
		 * Value for product model flag column
		 */
		public final boolean flag;

		/**
		 * Create the cache object for the name
		 * 
		 * @param id
		 *            Database ID
		 * @param data
		 *            GZIP compressed data of XML
		 */
		public ProductModelFlag( long id, byte[] data ) {
			super();
			this.id = id;
			this.flag = this.determineFlag( data );
		}

		/**
		 * Determine the product model flag value (decompress GZIP data and check for productModel entries)
		 * 
		 * @param data
		 *            GZIP compressed data of XML
		 * @return <code>true</code> if product models are available, <code>false</code> otherwise
		 */
		private boolean determineFlag( byte[] data ) {
			try {
				JDOMParser parser = new JDOMParser();
				parser.setValidating( false );
				Object doc = parser.parseXML( new GZIPInputStream( new ByteArrayInputStream( data ) ) );
				JXPathContext context = JXPathContext.newContext( doc );
				context.setLenient( true );
				context.registerNamespace( "common", "http://lca.jrc.it/ILCD/Common" );
				context.registerNamespace( "ilcd", "http://lca.jrc.it/ILCD/Process" );
				context.registerNamespace( "pm", "http://iai.kit.edu/ILCD/ProductModel" );

				DataSetParsingHelper helper = new DataSetParsingHelper( context );
				return helper.getElements( "/ilcd:processDataSet/ilcd:processInformation/common:other/pm:productModel" ).size() > 0;
			}
			catch ( Exception e ) {
				return false;
			}

		}

	}

}
