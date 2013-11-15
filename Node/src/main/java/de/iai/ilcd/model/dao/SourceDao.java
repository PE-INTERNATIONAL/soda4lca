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
package de.iai.ilcd.model.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.tools.generic.ValueParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzk.iai.ilcd.service.model.ISourceListVO;
import de.fzk.iai.ilcd.service.model.ISourceVO;
import de.fzk.iai.ilcd.service.model.enums.PublicationTypeValue;
import de.iai.ilcd.model.common.DigitalFile;
import de.iai.ilcd.model.source.Source;
import de.iai.ilcd.persistence.PersistenceUtil;
import de.schlichtherle.io.File;

/**
 * Data access object for {@link Source sources}
 */
public class SourceDao extends DataSetDao<Source, ISourceListVO, ISourceVO> {

	private static final Logger logger = LoggerFactory.getLogger( SourceDao.class );

	public SourceDao() {
		super( "Source", Source.class, ISourceListVO.class, ISourceVO.class );
	}

	protected String getDataStockField() {
		return "sources";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preCheckAndPersist( Source source ) {
		// nothing to do
	}

	/**
	 * Concrete implementation required for saving of digital files
	 */
	@Override
	public boolean checkAndPersist( Source source, PersistType pType, PrintWriter out ) {
		EntityManager em = PersistenceUtil.getEntityManager();

		Source existingSource = this.getByUuid( source.getUuid().getUuid() );
		if ( existingSource != null ) {
			if ( pType == PersistType.ONLYNEW ) {
				out.println( "Warning: source data set with this uuid already exists in database; will ignore this data set" );
				return false;
			}
		}

		EntityTransaction t = em.getTransaction();
		try {
			t.begin();
			if ( existingSource != null && (pType == PersistType.MERGE) ) {
				// delete first the existing one, we will use the new one
				if ( out != null ) {
					out.println( "Notice: source data set with this uuid already exists in database; will merge this data set" );
				}
				em.remove( existingSource );
				this.deleteDigitalFiles( source );
			}

			em.persist( source );

			t.commit();

			if ( !super.setMostRecentVersionFlags( source.getUuidAsString() ) ) {
				return false;
			}

			if ( source != null && source.getId() != null ) {
				if ( !this.saveDigitalFiles( source, out ) ) {
					if ( out != null ) {
						out.println( "Warning: couldn't save all files of this source data set into database file directory: see messages above" );
					}
				}
			}
			return true;

		}
		catch ( Exception e ) {
			if ( out != null ) {
				out.println( "Can't save source data file to database because of: " + e.getMessage() );
			}
			t.rollback();
			return false;
		}
	}

	private boolean saveDigitalFiles( Source source, PrintWriter out ) {
		EntityManager em = PersistenceUtil.getEntityManager();

		File directory = null;
		EntityTransaction t = em.getTransaction();
		try {

			// OK, now let's handle the files if any
			if ( (source.getFiles().size() > 0) && (source.getId() > 0) ) { // we have files and the source has a valid
																			// id
				// first let's check if the source has already a file directory to save binary files
				String directoryPath = source.getFilesDirectory();
				directory = new File( directoryPath );

				if ( !directory.exists() ) {
					directory.mkdirs(); // OK, create the directory and all parents
				} // OK, now that we verified that we have a directory, let's copy the files to the directory
				for ( DigitalFile digitalFile : source.getFiles() ) {
					String sourcePath = digitalFile.getFileName();
					logger.info( "have to save digital file {}", sourcePath );
					File file = new File( sourcePath );
					if ( file.canRead() ) {
						// copy file only if we have a real file and not only a URL
						File dest = new File( directory, file.getName() );
						if ( !file.copyTo( dest ) ) {
							if ( out != null ) {
								out.println( "cannot copy file " + file.getName() + " of source data set " + source.getName().getDefaultValue()
										+ " to database file firectory" );
							}
							logger.error( "cannot copy digital file {} to source directory {}", file.getName(), directoryPath );
						}
						// now, replace name in digitalFile with just the name of the file
						digitalFile.setFileName( FilenameUtils.getName( sourcePath ) );
					}
					else {
						if ( !file.getName().startsWith( "http:" ) || !file.getName().startsWith( "https:" ) ) {
							// there are sometimes URL refs in source which don't have http:// prepended
							if ( !file.getName().matches( ".+\\...." ) && file.getName().contains( "." ) ) {
								// looks like a URL with no http:// in front; try to fix that
								digitalFile.setFileName( "http://" + file.getName() );
							}
							else {
								// we have a file which we cannot find
								digitalFile.setFileName( FilenameUtils.getName( sourcePath ) );
								out.println( "warning: digital file " + FilenameUtils.getName( sourcePath ) + " of source data set "
										+ source.getName().getDefaultValue() + " cannot be found in external_docs directory" );
								logger.warn( "warning: digital file {} of source data set {} cannot be found in external_docs directory; will be ignored", file
										.getName(), source.getName().getDefaultValue() );
							}
						}
					}
					t.begin();
					em.persist( digitalFile );
					t.commit();
				}
			}
		}
		catch ( Exception e ) {
			// OK, let's delete the digital files and rollback the whole transaction to remove database items
			logger.error( "cannot save digital file", e );
			if ( t.isActive() ) {
				t.rollback();
			}
			this.deleteDigitalFiles( source );
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Source remove( Source source ) throws DeleteDataSetException {
		if ( source == null || source.getId() == null ) {
			return null;
		}
		Source tmp = super.remove( source );
		this.deleteDigitalFiles( tmp );
		return tmp;
	}

	private void deleteDigitalFiles( Source source ) {
		File directory = null;
		if ( source == null ) {
			return;
		}
		if ( (source.getFiles().size() > 0) && (source.getId() > 0) ) { // we have files and the source has a valid id
			// first let's check if the source has already a file directory to save binary files
			String directoryPath = source.getFilesDirectory();
			directory = new File( directoryPath );

			if ( directory != null && directory.exists() ) {
				try {
					FileUtils.deleteDirectory( directory );
				}
				catch ( IOException ex ) {
					// we will ignore this because we tried the best to delete everything
				}
			}
		}
		return;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addWhereClausesAndNamedParamesForQueryStringJpql( String typeAlias, ValueParser params, List<String> whereClauses,
			Map<String, Object> whereParamValues ) {
		String type = params.getString( "type" );
		if ( type != null && (type.length() > 3) && (!type.equals( "select option" )) ) {
			PublicationTypeValue typeValue = null;
			try {
				typeValue = PublicationTypeValue.valueOf( type );
			}
			catch ( Exception e ) {
				// ignore it as we do not have a parsable value
			}
			if ( typeValue != null ) {
				whereClauses.add( typeAlias + ".type=:typeOfSrc" );
				whereParamValues.put( "typeOfSrc", typeValue );
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryStringOrderJpql( String typeAlias, String sortString ) {
		if ( "classification.classHierarchyAsString".equals( sortString ) ) {
			return typeAlias + ".classificationCache";
		}
		else if ( "publicationType.value".equals( sortString ) ) {
			return typeAlias + ".publicationType";
		}
		else {
			return typeAlias + ".nameCache";
		}
	}

	/*
	 * ================================================== == DEPRECATED METHODS / BACKWARDS COMPATIBILITY ==
	 * ==================================================
	 */
	/**
	 * Get list of all sources
	 * 
	 * @return list of all sources
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getAll()}.
	 * @see #getAll()
	 */
	@Deprecated
	public List<Source> getSources() {
		return this.getAll();
	}

	/**
	 * Get a source by UUID
	 * 
	 * @param uuid
	 *            UUID of flow to get
	 * @return source with provided UUID
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByUuid(String)}.
	 * @see #getByUuid(String)
	 */
	@Deprecated
	public Source getSource( String uuid ) {
		return this.getByUuid( uuid );
	}

	/**
	 * Get the source with the provided JPA id
	 * 
	 * @param datasetId
	 *            JPA id
	 * @return source with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public Source getSource( long datasetId ) {
		return this.getByDataSetId( datasetId );
	}

	/**
	 * Get the source with the provided JPA id
	 * 
	 * @param id
	 *            JPA id as string
	 * @return source with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(String)}.
	 * @see #getByDataSetId(String)
	 */
	@Deprecated
	public Source getSourceById( String id ) {
		return this.getByDataSetId( id );
	}

	/**
	 * Get the source with the provided JPA id
	 * 
	 * @param id
	 *            JPA id
	 * @return source with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public Source getSourceById( Long id ) {
		return this.getByDataSetId( id );
	}

	/**
	 * Get sources by main class
	 * 
	 * @param mainClass
	 *            main class to get source by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getByClass(String)
	 */
	@Deprecated
	public List<Source> getSourcesByClass( String mainClass ) {
		return this.getByClass( mainClass );
	}

	/**
	 * Get the number of sources by main class
	 * 
	 * @param mainClass
	 *            main class to get source by
	 * @return the number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getNumberByClass(String)
	 */
	@Deprecated
	public long getNumberOfSources( String mainClass ) {
		return this.getNumberByClass( mainClass );
	}

	/**
	 * Get sources by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get source by
	 * @param subClass
	 *            sub class to get source by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getByClass(String,String)
	 */
	@Deprecated
	public List<Source> getSourcesByClass( String mainClass, String subClass ) {
		return this.getByClass( mainClass, subClass );
	}

	/**
	 * Get number of sources by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get source by
	 * @param subClass
	 *            sub class to get source by
	 * @return number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getNumberByClass(String,String)
	 */
	@Deprecated
	public long getNumberOfSources( String mainClass, String subClass ) {
		return this.getNumberByClass( mainClass, subClass );
	}
}
