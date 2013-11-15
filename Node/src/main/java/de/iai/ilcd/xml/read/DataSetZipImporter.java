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

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.model.common.DataSet;
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
import de.schlichtherle.io.File;

/**
 * 
 * @author clemens.duepmeier
 */
public class DataSetZipImporter {

	private static final Logger logger = LoggerFactory.getLogger( DataSetZipImporter.class );

	private static final String PROCESSDIR = "processes";

	private static final String METHODSDIR = "lciamethods";

	private static final String FLOWDIR = "flows";

	private static final String PROPERTYDIR = "flowproperties";

	private static final String UNITGROUPDIR = "unitgroups";

	private static final String CONTACTDIR = "contacts";

	private static final String SOURCEDIR = "sources";

	public void importZipFile( String zipFileName, PrintWriter out, RootDataStock rds ) {

		logger.debug( "importing ZIP archive " + zipFileName );

		File zipFile = new File( zipFileName );

		if ( !zipFile.isArchive() ) {
			out.println( "Error: file " + zipFileName + " is not a zip archive!" );
			out.println( "Please upload only ILCD conformant zip archives." );
			out.flush();
			return;
		}

		String subDir = "";
		// we try to parse several different zip file formats
		// we allow data sets to be directly in subdirectories, like processes, etc. in the root directory of the zip
		// file
		// or data sets directories are under a subdirectory. Official ILCD zip files have them in "ILCD" or "ilcd"
		// subdirectories.
		// So this case is also handled
		subDir = this.findDataSubDirectory( zipFile, zipFileName );

		ContactReader contactReader = new ContactReader();
		ContactDao contactDao = new ContactDao();
		File contactsDirectory = new File( zipFileName + subDir + "/contacts" );
		if ( !contactsDirectory.exists() || !contactsDirectory.isDirectory() ) {
			if ( out != null ) {
				out.println( "Notice: the zip file does not contain any contact data sets" );
				out.flush();
			}
		}
		else {
			if ( out != null ) {
				out.println( "processing contact data sets" );
				out.flush();
			}

			this.importFiles( contactsDirectory, contactReader, contactDao, out, rds );
		}

		SourceReader sourceReader = new SourceReader();
		SourceDao sourceDao = new SourceDao();
		File sourcesDirectory = new File( zipFileName + subDir + "/sources" );
		if ( !sourcesDirectory.exists() || !sourcesDirectory.isDirectory() ) {
			if ( out != null ) {
				out.println( "Notice: the zip file does not contain any source data sets" );
				out.flush();
			}
		}
		else {
			if ( out != null ) {
				out.println( "processing source data sets" );
				out.flush();
			}

			this.importFiles( sourcesDirectory, sourceReader, sourceDao, out, rds );
		}

		UnitGroupReader unitGroupReader = new UnitGroupReader();
		UnitGroupDao unitGroupDao = new UnitGroupDao();
		File unitGroupsDirectory = new File( zipFileName + subDir + "/unitgroups" );
		if ( !unitGroupsDirectory.exists() || !unitGroupsDirectory.isDirectory() ) {
			if ( out != null ) {
				out.println( "Notice: the zip file does not contain any unit group data sets" );
				out.flush();
			}
		}
		else {
			if ( out != null ) {
				out.println( "processing unit group data sets" );
				out.flush();
			}

			this.importFiles( unitGroupsDirectory, unitGroupReader, unitGroupDao, out, rds );
		}

		FlowPropertyReader flowPropertyReader = new FlowPropertyReader();
		FlowPropertyDao flowPropertyDao = new FlowPropertyDao();
		File flowPropertiesDirectory = new File( zipFileName + subDir + "/flowproperties" );
		if ( !flowPropertiesDirectory.exists() || !flowPropertiesDirectory.isDirectory() ) {
			if ( out != null ) {
				out.println( "Notice: the zip file does not contain any flow property data sets" );
				out.flush();
			}
		}
		else {
			if ( out != null ) {
				out.println( "processing flow property data sets" );
				out.flush();
			}

			this.importFiles( flowPropertiesDirectory, flowPropertyReader, flowPropertyDao, out, rds );
		}

		FlowReader flowReader = new FlowReader();
		FlowDao flowDao = new FlowDao();
		File flowsDirectory = new File( zipFileName + subDir + "/flows" );
		if ( !flowsDirectory.exists() || !flowsDirectory.isDirectory() ) {
			if ( out != null ) {
				out.println( "Notice: the zip file does not contain any flow data sets" );
				out.flush();
			}
		}
		else {
			if ( out != null ) {
				out.println( "processing flow data sets" );
				out.flush();
			}

			this.importFiles( flowsDirectory, flowReader, flowDao, out, rds );
		}

		ProcessReader processReader = new ProcessReader();
		ProcessDao processDao = new ProcessDao();
		File processesDirectory = new File( zipFileName + subDir + "/processes" );
		if ( !processesDirectory.exists() || !processesDirectory.isDirectory() ) {
			if ( out != null ) {
				out.println( "Notice: the zip file does not contain any process data sets" );
				out.flush();
			}
		}
		else {
			if ( out != null ) {
				out.println( "processing process data sets" );
				out.flush();
			}
			this.importFiles( processesDirectory, processReader, processDao, out, rds );
		}

		LCIAMethodReader lciamethodReader = new LCIAMethodReader();
		LCIAMethodDao lciamethodDao = new LCIAMethodDao();
		File lciamethodsDirectory = new File( zipFileName + subDir + "/lciamethods" );
		if ( !lciamethodsDirectory.exists() || !lciamethodsDirectory.isDirectory() ) {
			if ( out != null ) {
				out.println( "Notice: the zip file does not contain any lciamethod data sets" );
				out.flush();
			}
		}
		else {
			if ( out != null ) {
				out.println( "processing lciamethod data sets" );
				out.flush();
			}
			this.importFiles( lciamethodsDirectory, lciamethodReader, lciamethodDao, out, rds );
		}

		// flush output at end;
		if ( out != null ) {
			out.flush();
		}

		// don't forget to unmount zipFile and release resources of it
		try {
			File.umount( true );
		}
		catch ( Exception e ) {
			// do nothing, we just close all zip archive file resources
			logger.error( "error unmounting zipfile", e );
		}

	}

	public void importFiles( File zipDirectory, DataSetReader reader, DataSetDao dao, PrintWriter out, RootDataStock rds ) {
		File[] files = null;

		int filesImported = 0;
		int filesNotImported = 0;

		try {
			files = (File[]) zipDirectory.listFiles();

			if ( out != null ) {
				logger.debug( "importing from " + zipDirectory.getAbsolutePath() );
				out.println( "Number of files to import: " + files.length );
				out.flush();
			}

			int i = 1;
			for ( File file : files ) {
				boolean persisted = this.importFile( file, reader, dao, out, rds );
				if ( out != null ) {
					if ( persisted ) {
						out.println( "Imported file (" + i + "/" + files.length + "): " + file.getName() );
						filesImported++;
					}
					else {
						out.println( "FILE WAS NOT IMPORTED: " + file.getName() );
						filesNotImported++;
					}
					out.flush();
				}
				i++;
			}
		}
		catch ( Exception e ) {
			logger.error( "Cannot import all files from directory: {}", zipDirectory.getAbsolutePath() );
			logger.error( "Exception is: ", e );
		}
		if ( filesImported > 0 ) {
			out.println( filesImported + " files were imported" );
			out.flush();
		}
		if ( filesNotImported > 0 ) {
			out.println( filesNotImported + " files were NOT imported" );
			out.flush();
		}
	}

	public boolean importFile( File dataSetFile, DataSetReader reader, DataSetDao<DataSet, ?, ?> dao, PrintWriter out, RootDataStock rds ) {

		logger.debug( "importing " + dataSetFile.getName() );

		DataSet dataset;
		try {
			dataset = reader.readDataSetFromFile( dataSetFile, out );
			dataset.setRootDataStock( rds );
		}
		catch ( Exception ex ) {
			logger.error( "The file {} was not imported", dataSetFile.getAbsolutePath() );
			logger.error( "The exception is: ", ex );
			return false;
		}

		boolean persisted = dao.checkAndPersist( dataset, PersistType.ONLYNEW, out );

		PersistenceUtil.closeEntityManager();

		return persisted;
	}

	private boolean isDataSetDirectory( String directory ) {
		if ( directory.equals( PROCESSDIR ) || directory.equals( METHODSDIR ) || directory.equals( FLOWDIR ) || directory.equals( PROPERTYDIR )
				|| directory.equals( UNITGROUPDIR ) || directory.equals( CONTACTDIR ) || directory.equals( SOURCEDIR ) ) {
			return true;
		}
		else {
			return false;
		}
	}

	private String findDataSubDirectory( File zipFile, String zipFileName ) {
		String subDir = "";

		String[] topDirectories = zipFile.list();
		for ( String directory : topDirectories ) {
			if ( this.isDataSetDirectory( directory ) ) {
				// OK, the data sets seem to be in subdirectories directly under the zip root directory
				return subDir;
			}
			// haven't found data directories, let's scan one level deeper
			File zipSubDirectory = new File( zipFileName + "/" + directory );
			if ( !zipSubDirectory.isDirectory() ) {
				// no directory
				continue;
			}
			String[] secondLevelDirectories = zipSubDirectory.list();
			if ( secondLevelDirectories == null ) {
				// we have no subentries in this subdirectory
				continue;
			}
			for ( String secondDirectory : secondLevelDirectories ) {
				if ( this.isDataSetDirectory( secondDirectory ) ) {
					// OK, we found a possible data directory, let's use that
					subDir = "/" + directory;
					return subDir;
				}
			}

		}

		return subDir;

	}
}
