package de.iai.ilcd.webgui.controller.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.fzk.iai.ilcd.service.model.IDeclaresCompliance;
import de.fzk.iai.ilcd.service.model.enums.ComplianceValue;
import de.fzk.iai.ilcd.service.model.process.IComplianceSystem;
import de.iai.ilcd.model.common.GlobalRefUriAnalyzer;
import de.iai.ilcd.model.process.ComplianceSystem;

/**
 * Handler for compliance system utilities, requires EL2!
 */
@ManagedBean( name = "complianceUtil" )
@ApplicationScoped
public class ComplianceUtilHandler {

	/**
	 * Enumeration with possible codes
	 */
	public enum ComplianceSystemCode {
		ILCD {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return "I";
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int getPriority() {
				return 30;
			}
		},
		ILCD_ENTRY {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return "E";
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int getPriority() {
				return 20;
			}
		},
		OTHER {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return "O";
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int getPriority() {
				return 10;
			}
		},
		NONE {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return "";
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int getPriority() {
				return 0;
			}
		};

		/**
		 * Get the priority of the code
		 * 
		 * @return priority of the code
		 */
		public abstract int getPriority();
	}

	/**
	 * List with all UUID string representation of ILCD compliance instances
	 */
	private final List<String> lstILCDComplianceUUID = ComplianceUtilHandler.generateILCDComplianceUUIDsList();

	/**
	 * List with all UUID string representation of ILCD entry compliance instances
	 */
	private final List<String> lstILCDEntryComplianceUUID = ComplianceUtilHandler.generateILCDEntryComplianceUUIDsList();

	/**
	 * Get the highest priority compliance system code for the provided collection
	 * 
	 * @param collComplianceSystems
	 *            list of compliance systems
	 * @return highest priority compliance system code
	 */
	public ComplianceSystemCode getComplianceCode( Collection<IComplianceSystem> collComplianceSystems ) {
		// no list => no code
		if ( collComplianceSystems == null ) {
			return ComplianceSystemCode.NONE;
		}
		// let's start with none
		ComplianceSystemCode code = ComplianceSystemCode.NONE;

		// check all codes
		for ( IComplianceSystem cs : collComplianceSystems ) {

			// only consider compliance declarations where overallCompliance is "fully compliant"
			if ( !cs.getOverallCompliance().equals( ComplianceValue.FULLY_COMPLIANT ) )
				continue;

			String uuidStr;

			// From interface methods, the UUID has to be extracted by URI.
			// To prevent that, the currently known implementation (which
			// provides the UUID directly) is being used if possible.
			if ( cs instanceof ComplianceSystem ) {
				ComplianceSystem csImpl = (ComplianceSystem) cs;
				uuidStr = csImpl.getReference().getUuid().getUuid();
			}

			// another implementation, nothing left but to extract from URI
			else {
				GlobalRefUriAnalyzer uriAnalyzer = new GlobalRefUriAnalyzer( cs.getReference().getUri() );
				uuidStr = uriAnalyzer.getUuidAsString();
			}

			// we got the UUID string, not get the code for it
			code = this.getComplianceSystemCode( uuidStr, code );

			// if ILCD found: nothing more to do .. highest priority
			if ( ComplianceSystemCode.ILCD.equals( code ) ) {
				return code;
			}
		}
		// return found code
		return code;
	}

	/**
	 * Get the highest priority compliance system code for the provided collection
	 * 
	 * @param uuids
	 *            the list of uuids to check. they will be considered to be fully compliant for overall compliance
	 * @return highest priority compliance system code
	 */
	public ComplianceSystemCode getComplianceCode( List<String> uuids ) {
		// no list => no code
		if ( uuids == null ) {
			return ComplianceSystemCode.NONE;
		}
		// let's start with none
		ComplianceSystemCode code = ComplianceSystemCode.NONE;

		// check all codes
		for ( String uuid : uuids ) {

			// we got the UUID string, not get the code for it
			code = this.getComplianceSystemCode( uuid, code );

			// if ILCD found: nothing more to do .. highest priority
			if ( ComplianceSystemCode.ILCD.equals( code ) ) {
				return code;
			}
		}
		// return found code
		return code;
	}

	/**
	 * Get the highest priority compliance system code for the provided collection
	 * 
	 * @param collComplianceSystems
	 *            list of compliance systems
	 * @return highest priority compliance system code
	 */
	public String getComplianceCodeAsString( Collection<IComplianceSystem> collComplianceSystems ) {
		return this.getComplianceCode( collComplianceSystems ).toString();
	}

	/**
	 * JSF components usually expect {@link List lists} and not a {@link Set set}. This utility method returns the
	 * {@link IDeclaresCompliance#getComplianceSystems() compliance systems} wrapped as a {@link List list} (concrete:
	 * an {@link ArrayList})
	 * 
	 * @param p
	 *            the object that declares compliance
	 * @return the compliance systems as list
	 */
	public List<IComplianceSystem> getComplianceSetAsList( IDeclaresCompliance p ) {
		if ( p != null && p.getComplianceSystems() != null ) {
			return new ArrayList<IComplianceSystem>( p.getComplianceSystems() );
		}
		else {
			return Collections.emptyList();
		}
	}

	/**
	 * Get higher priority compliance system code for UUID and current code
	 * 
	 * @param uuid
	 *            UUID (string representation) to check
	 * @param current
	 *            current code
	 * @return compliance code for higher priority (either current of the one for the UUID)
	 */
	private ComplianceSystemCode getComplianceSystemCode( String uuid, ComplianceSystemCode current ) {
		ComplianceSystemCode c = ComplianceSystemCode.OTHER;
		if ( this.lstILCDComplianceUUID.contains( uuid ) ) {
			c = ComplianceSystemCode.ILCD;
		}
		else if ( this.lstILCDEntryComplianceUUID.contains( uuid ) ) {
			c = ComplianceSystemCode.ILCD_ENTRY;
		}
		return c.getPriority() > current.getPriority() ? c : current;
	}

	/**
	 * Generate list of all UUIDs (String representation) of ILCD data compliance
	 * 
	 * @return list with UUIDs for ILCD data compliance
	 */
	private static List<String> generateILCDComplianceUUIDsList() {
		List<String> lst = new ArrayList<String>();
		lst.add( "d975693e-d4e0-4c43-a943-539d9f84cac8" );
		lst.add( "d5693c8f-9308-4911-a334-fdbcce4b3ef7" );
		lst.add( "0cb541c2-116d-44d8-ad42-cbb23b551f2d" );
		lst.add( "424b32b5-f279-4fd6-8d33-f106dbe64a95" );
		lst.add( "27389dd4-30dd-4f89-8ceb-6e878ec22cda" );
		lst.add( "7bc53f07-4fe0-4619-b08a-061d7eceb585" );
		lst.add( "85c70ebb-6909-462a-9efa-8d97cee275ee" );
		lst.add( "55a9c38d-6190-4cd4-b589-45268e4c9475" );
		lst.add( "9d42c820-1a10-49f3-a387-5a1d355d37ed" );
		lst.add( "43160353-af6f-40e7-bd9a-6930b960885a" );
		lst.add( "fec6171f-e2ef-4bb6-934a-37fa323b254b" );
		lst.add( "50d961dc-0b6a-4796-a2b5-1a12d4f53343" );
		return lst;
	}

	/**
	 * Generate list of all UUIDs (String representation) of ILCD entry level data compliance
	 * 
	 * @return list with UUIDs for ILCD entry level data compliance
	 */
	private static List<String> generateILCDEntryComplianceUUIDsList() {
		List<String> lst = new ArrayList<String>();
		lst.add( "d92a1a12-2545-49e2-a585-55c259997756" );
		return lst;
	}

}
