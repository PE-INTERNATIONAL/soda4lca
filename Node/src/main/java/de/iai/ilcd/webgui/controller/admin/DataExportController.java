package de.iai.ilcd.webgui.controller.admin;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzk.iai.ilcd.api.Constants;
import de.fzk.iai.ilcd.zip.ILCDManifest;
import de.fzk.iai.ilcd.zip.ILCDZip;
import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.common.DigitalFile;
import de.iai.ilcd.model.dao.ContactDao;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.model.dao.FlowDao;
import de.iai.ilcd.model.dao.FlowPropertyDao;
import de.iai.ilcd.model.dao.LCIAMethodDao;
import de.iai.ilcd.model.dao.ProcessDao;
import de.iai.ilcd.model.dao.SourceDao;
import de.iai.ilcd.model.dao.UnitGroupDao;
import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.model.source.Source;
import de.iai.ilcd.persistence.PersistenceUtil;
import de.schlichtherle.io.ArchiveException;
import de.schlichtherle.io.File;
import de.schlichtherle.io.FileOutputStream;

@ManagedBean
@ViewScoped
public class DataExportController {

	private final Logger logger = LoggerFactory.getLogger( DataExportController.class );

	private StreamedContent file;

	private IDataStockMetaData stock;

	public DataExportController() {

	}

	public ILCDZip export( IDataStockMetaData stock ) throws IOException {

		// the new archive
		ILCDZip zip = new ILCDZip( ConfigurationService.INSTANCE.getZipFileDirectory() + File.separator + Long.toString( System.currentTimeMillis() ) + ".zip" );

		logger.trace( zip.getAbsolutePath() );

		ILCDManifest manifest = new ILCDManifest( Constants.FORMAT_VERSION, ConfigurationService.INSTANCE.getVersionTag() );
		zip.setManifest( manifest );

		long start = System.currentTimeMillis();

		logger.info( "exporting flow props" );
		exportDatasets( new FlowPropertyDao(), stock, zip );
		logger.info( "exporting flows" );
		exportDatasets( new FlowDao(), stock, zip );
		logger.info( "exporting processes" );
		exportDatasets( new ProcessDao(), stock, zip );
		logger.info( "exporting LCIA methods" );
		exportDatasets( new LCIAMethodDao(), stock, zip );
		logger.info( "exporting sources" );
		exportDatasets( new SourceDao(), stock, zip );
		logger.info( "exporting contacts" );
		exportDatasets( new ContactDao(), stock, zip );
		logger.info( "exporting unit groups" );
		exportDatasets( new UnitGroupDao(), stock, zip );

		try {
			ILCDZip.umount( zip );
		}
		catch ( ArchiveException e ) {
			logger.error( "error writing archive", e );
			return null;
		}
		long stop = System.currentTimeMillis();

		logger.info( "done. export took " + (stop - start) / 1000 + " seconds" );

		return zip;
	}

	private <T extends DataSet> void exportDatasets( DataSetDao<T, ?, ?> dao, IDataStockMetaData stock, ILCDZip zip ) {

		long dataSetCount;

		if ( stock != null )
			dataSetCount = dao.getCount( stock );
		else
			dataSetCount = dao.getAllCount();

		final int pageSize = 100;

		long pages = dataSetCount / pageSize;
		long remainder = dataSetCount % pageSize;
		if ( remainder > 0 )
			pages++;

		if ( logger.isTraceEnabled() )
			logger.trace( dataSetCount + " datasets, pagesize: " + pageSize + ", " + pages + " pages" );

		File dir = null;

		if ( dao instanceof ProcessDao ) {
			dir = new File( zip.getAbsolutePath() + File.separator + "processes" );
		}
		else if ( dao instanceof LCIAMethodDao ) {
			dir = new File( zip.getAbsolutePath() + File.separator + "lciamethods" );
		}
		else if ( dao instanceof FlowDao ) {
			dir = new File( zip.getAbsolutePath() + File.separator + "flows" );
		}
		else if ( dao instanceof FlowPropertyDao ) {
			dir = new File( zip.getAbsolutePath() + File.separator + "flowproperties" );
		}
		else if ( dao instanceof UnitGroupDao ) {
			dir = new File( zip.getAbsolutePath() + File.separator + "unitgroups" );
		}
		else if ( dao instanceof SourceDao ) {
			dir = new File( zip.getAbsolutePath() + File.separator + "sources" );
			File externalDocsDir = new File( zip.getAbsolutePath() + File.separator + "external_docs" );
			externalDocsDir.mkdir();
		}
		else if ( dao instanceof ContactDao ) {
			dir = new File( zip.getAbsolutePath() + File.separator + "contacts" );
		}

		if ( dir != null )
			dir.mkdir();
		else
			throw new IllegalArgumentException( "Invalid DAO object" );

		BufferedWriter writer = null;

		for ( int currentPage = 0; currentPage < pages; currentPage++ ) {
			if ( logger.isTraceEnabled() )
				logger.trace( "exporting page " + (currentPage + 1) + " of " + pages );
			writeDatasets( dao, stock, currentPage, pageSize, zip, dir, writer );
		}

	}

	private <T extends DataSet> void writeDatasets( DataSetDao<T, ?, ?> dao, IDataStockMetaData stock, int page, int pageSize, ILCDZip zip, File dir,
			Writer writer ) {

		PersistenceUtil.closeEntityManager();
		List<T> dataSetList;

		if ( stock != null )
			dataSetList = dao.get( stock, (page * pageSize), pageSize );
		else
			dataSetList = dao.get( (page * pageSize), pageSize );

		if ( logger.isTraceEnabled() )
			logger.trace( "  exporting datasets " + (page * pageSize) + " through " + ((page * pageSize) + pageSize - 1) );

		if ( dataSetList.equals( Collections.EMPTY_LIST ) ) {
			logger.trace( "(no datasets found)" );
			return;
		}

		for ( T dataset : dataSetList ) {
			try {
				FileOutputStream fos = new FileOutputStream( dir.getAbsolutePath() + File.separator + dataset.getUuidAsString() + ".xml" );
				OutputStreamWriter osw = new OutputStreamWriter( fos, "UTF-8" );
				writer = new BufferedWriter( osw );
				writer.write( dataset.getXmlFile().getContent() );
				writer.close();
				osw.close();
				fos.close();

				if ( dataset instanceof Source ) {
					for ( DigitalFile file : ((Source) dataset).getFiles() ) {
						File digitalFile = new File( file.getAbsoluteFileName() );
						if ( digitalFile.exists() )
							ILCDZip.cp( digitalFile, new File( zip.getAbsolutePath() + File.separator + "external_docs" + File.separator + file.getFileName() ) );
					}
				}

				dataset = null;
			}
			catch ( Exception e ) {
				logger.error( "error writing to archive", e );
			}
			finally {
				try {
					if ( writer != null )
						writer.close();
				}
				catch ( Exception e ) {
					logger.error( "error writing to archive", e );
				}
			}
		}
	}

	public String getZipFileName() {
		if ( this.stock == null ) {
			logger.debug( "no datastock selected, exporting entire database" );
			return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + ".zip";
		}
		else {
			logger.info( "starting export of datastock " + this.stock.getName() );
			return this.stock.getName() + ".zip";
		}
	}


	public StreamedContent getFile() {

		ILCDZip zip;
		try {
			zip = export( this.stock );

			if ( zip != null ) {
				FileInputStream stream = new FileInputStream( zip );
				file = new DefaultStreamedContent( stream, "application/zip", getZipFileName() );
			}
		}
		catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return file;
	}

	public IDataStockMetaData getStock() {
		return stock;
	}

	public void setStock( IDataStockMetaData stock ) {
		this.stock = stock;
	}
}