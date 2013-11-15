/*******************************************************************************
 * Copyright (c) 2011 Karlsruhe Institute of Technology (KIT) - Institute for
 * Applied Computer Science (IAI).
 * 
 * This file is part of soda4LCA - the Service-Oriented Life Cycle Data Store.
 * 
 * soda4LCA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * soda4LCA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with soda4LCA. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package de.iai.ilcd.xml.read;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.dao.ContactDao;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.model.dao.FlowDao;
import de.iai.ilcd.model.dao.FlowPropertyDao;
import de.iai.ilcd.model.dao.LCIAMethodDao;
import de.iai.ilcd.model.dao.PersistType;
import de.iai.ilcd.model.dao.ProcessDao;
import de.iai.ilcd.model.dao.SourceDao;
import de.iai.ilcd.model.dao.UnitGroupDao;
import de.iai.ilcd.model.datastock.RootDataStock;
import de.iai.ilcd.persistence.PersistenceUtil;

public class DataSetImporter {

	private static final Logger logger = LoggerFactory.getLogger( DataSetImporter.class );

	public void importZipFile( String zipFileName, PrintWriter out, RootDataStock rds ) {
		DataSetZipImporter zipImporter = new DataSetZipImporter();
		zipImporter.importZipFile( zipFileName, out, rds );
	}

	public void importFiles( String baseDirectory, PrintWriter out, RootDataStock rds ) {

		if ( !baseDirectory.endsWith( "/" ) ) {
			baseDirectory = baseDirectory + "/";
		}
		DataSetImporter importer = new DataSetImporter();

		ContactReader contactReader = new ContactReader();
		ContactDao contactDao = new ContactDao();
		importer.importFiles( baseDirectory + "contacts", contactReader, contactDao, out, rds );

		SourceReader sourceReader = new SourceReader();
		SourceDao sourceDao = new SourceDao();
		importer.importFiles( baseDirectory + "sources", sourceReader, sourceDao, out, rds );

		DataSetReader unitGroupReader = new UnitGroupReader();
		UnitGroupDao unitGroupDao = new UnitGroupDao();
		importer.importFiles( baseDirectory + "unitgroups", unitGroupReader, unitGroupDao, out, rds );

		FlowPropertyReader flowPropertyReader = new FlowPropertyReader();
		FlowPropertyDao flowPropertyDao = new FlowPropertyDao();
		importer.importFiles( baseDirectory + "flowproperties", flowPropertyReader, flowPropertyDao, out, rds );

		FlowReader flowReader = new FlowReader();
		FlowDao flowDao = new FlowDao();
		importer.importFiles( baseDirectory + "flows", flowReader, flowDao, out, rds );

		ProcessReader processReader = new ProcessReader();
		ProcessDao processDao = new ProcessDao();
		importer.importFiles( baseDirectory + "processes", processReader, processDao, out, rds );

		LCIAMethodReader lciaMethodReader = new LCIAMethodReader();
		LCIAMethodDao lciaMethodDao = new LCIAMethodDao();
		importer.importFiles( baseDirectory + "lciamethods", lciaMethodReader, lciaMethodDao, out, rds );
	}

	public boolean importFile( DataSetType type, String fileName, PrintWriter out, RootDataStock rds ) {
		switch ( type ) {
			case PROCESS:
				return this.importProcessFile( fileName, out, rds );
			case FLOW:
				return this.importFlowFile( fileName, out, rds );
			case FLOWPROPERTY:
				return this.importFlowPropertyFile( fileName, out, rds );
			case UNITGROUP:
				return this.importUnitGroupFile( fileName, out, rds );
			case SOURCE:
				return this.importSourceFile( fileName, out, rds );
			case CONTACT:
				return this.importContactFile( fileName, out, rds );
			case LCIAMETHOD:
				return this.importLCIAMethodFile( fileName, out, rds );
			default:
				break;
		}
		return false;
	}

	public boolean importLCIAMethodFile( String fileName, PrintWriter out, RootDataStock rds ) {
		LCIAMethodReader lciaMethodReader = new LCIAMethodReader();
		LCIAMethodDao lciaMethodDao = new LCIAMethodDao();
		return this.importFile( fileName, lciaMethodReader, lciaMethodDao, out, rds );
	}

	public boolean importProcessFile( String fileName, PrintWriter out, RootDataStock rds ) {
		ProcessReader processReader = new ProcessReader();
		ProcessDao processDao = new ProcessDao();
		return this.importFile( fileName, processReader, processDao, out, rds );
	}

	public boolean importFlowFile( String fileName, PrintWriter out, RootDataStock rds ) {
		FlowReader flowReader = new FlowReader();
		FlowDao flowDao = new FlowDao();
		return this.importFile( fileName, flowReader, flowDao, out, rds );
	}

	public boolean importFlowPropertyFile( String fileName, PrintWriter out, RootDataStock rds ) {
		FlowPropertyReader flowpropReader = new FlowPropertyReader();
		FlowPropertyDao flowpropDao = new FlowPropertyDao();
		return this.importFile( fileName, flowpropReader, flowpropDao, out, rds );
	}

	public boolean importUnitGroupFile( String fileName, PrintWriter out, RootDataStock rds ) {
		UnitGroupReader unitGroupReader = new UnitGroupReader();
		UnitGroupDao unitGroupDao = new UnitGroupDao();
		return this.importFile( fileName, unitGroupReader, unitGroupDao, out, rds );
	}

	public boolean importContactFile( String fileName, PrintWriter out, RootDataStock rds ) {
		ContactReader contactReader = new ContactReader();
		ContactDao contactDao = new ContactDao();
		return this.importFile( fileName, contactReader, contactDao, out, rds );
	}

	public boolean importSourceFile( String fileName, PrintWriter out, RootDataStock rds ) {
		SourceReader sourceReader = new SourceReader();
		SourceDao sourceDao = new SourceDao();
		return this.importFile( fileName, sourceReader, sourceDao, out, rds );
	}

	private void importFiles( String directoryPath, DataSetReader reader, DataSetDao dao, PrintWriter out, RootDataStock rds ) {
		Collection<File> files = null;

		try {
			File dir = new File( directoryPath );
			if ( !dir.isDirectory() ) {
				return; // no files available of this type
			}
			files = FileUtils.listFiles( dir, null, false );
			if ( !(files.size() > 0) ) {
				return; // no files available of this type
			}
			logger.info( "count of files for import: ", files.size() );
			if ( out != null ) {
				out.println( "Number of files to import: " + files.size() );
				out.flush();
			}
			logger.debug( "importing from " + directoryPath );

			Iterator<File> fileIter = files.iterator();
			while ( fileIter.hasNext() ) {
				File file = fileIter.next();
				String filename = file.getAbsolutePath();
				logger.info( "Import file: {}", filename );
				if ( out != null ) {
					out.println( "Import file: " + file.getName() );
					out.flush();
				}
				boolean persisted = this.importFile( filename, reader, dao, out, rds );
				String message = "";
				if ( persisted ) {
					message = " successfully imported.";
				}
				else {
					message = " FILE WAS NOT IMPORTED, see messages above";
				}
				if ( out != null ) {
					out.println( message );
					out.flush();
				}
			}
		}
		catch ( Exception e ) {
			logger.error( "Exception while importing files from directory {}", directoryPath );
			logger.error( "Exception is: ", e );
			if ( out != null ) {
				out.println( "Cannot import all files" );
				out.flush();
			}
		}
	}

	private boolean importFile( String fileName, DataSetReader reader, DataSetDao dao, PrintWriter out, RootDataStock rds ) {
		if ( rds == null ) {
			out.println( "Cannot create data set without a root data stock" );
			return false;
		}
		// EntityManager em = PersistenceUtil.getEntityManager();
		boolean persisted = false;

		DataSet dataset;
		try {
			dataset = reader.readFromFile( fileName, out );
			dataset.setRootDataStock( rds );
		}
		catch ( Exception ex ) {
			if ( out != null ) {
				out.println( "Warning: Cannot import data set " + fileName.subSequence( fileName.lastIndexOf( File.separator ), fileName.length() - 1 ) );
				out.flush();
			}
			logger.error( "Cannot read data set {}", fileName );
			logger.error( "Exception is: ", ex );
			return false;
		}

		// dao.persist(dataset);
		persisted = dao.checkAndPersist( dataset, PersistType.ONLYNEW, out );
		if ( out != null ) {
			out.flush();
		}

		PersistenceUtil.closeEntityManager();

		return persisted;
	}
}
