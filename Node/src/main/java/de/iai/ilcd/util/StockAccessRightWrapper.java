package de.iai.ilcd.util;

import de.iai.ilcd.model.security.ISecurityEntity;
import de.iai.ilcd.security.ProtectionType;
import de.iai.ilcd.security.StockAccessRight;

public class StockAccessRightWrapper {

	/**
	 * The stock access right instance
	 */
	private final StockAccessRight accessRight;

	/**
	 * Wrapper for read
	 */
	private final StockAccessRightBooleanPropertyWrapper read;

	/**
	 * Wrapper for read
	 */
	private final StockAccessRightBooleanPropertyWrapper write;

	/**
	 * Wrapper for import (naming due to keyword issues)
	 */
	private final StockAccessRightBooleanPropertyWrapper importRight;

	/**
	 * Wrapper for export
	 */
	private final StockAccessRightBooleanPropertyWrapper export;

	/**
	 * Wrapper for checkin
	 */
	private final StockAccessRightBooleanPropertyWrapper checkin;

	/**
	 * Wrapper for checkout
	 */
	private final StockAccessRightBooleanPropertyWrapper checkout;

	/**
	 * Wrapper for release
	 */
	private final StockAccessRightBooleanPropertyWrapper release;

	/**
	 * Wrapper for create
	 */
	private final StockAccessRightBooleanPropertyWrapper create;

	/**
	 * Wrapper for delete
	 */
	private final StockAccessRightBooleanPropertyWrapper delete;

	/**
	 * Display name of subject
	 */
	private final String subjectName;

	/**
	 * Create wrapper
	 * 
	 * @param accessRight
	 *            stock access right instance to wrap
	 */
	public StockAccessRightWrapper( StockAccessRight accessRight, ISecurityEntity subject ) {
		super();
		this.accessRight = accessRight;
		this.read = new StockAccessRightBooleanPropertyWrapper( ProtectionType.READ );
		this.write = new StockAccessRightBooleanPropertyWrapper( ProtectionType.WRITE );
		this.importRight = new StockAccessRightBooleanPropertyWrapper( ProtectionType.IMPORT );
		this.export = new StockAccessRightBooleanPropertyWrapper( ProtectionType.EXPORT );
		this.checkin = new StockAccessRightBooleanPropertyWrapper( ProtectionType.CHECKIN );
		this.checkout = new StockAccessRightBooleanPropertyWrapper( ProtectionType.CHECKOUT );
		this.release = new StockAccessRightBooleanPropertyWrapper( ProtectionType.RELEASE );
		this.create = new StockAccessRightBooleanPropertyWrapper( ProtectionType.CREATE );
		this.delete = new StockAccessRightBooleanPropertyWrapper( ProtectionType.DELETE );
		this.subjectName = subject != null ? subject.getDisplayName() : "";
	}

	public boolean isGuest() {
		return this.accessRight.getUgId() == 0;
	}

	public String getSubjectName() {
		return this.subjectName;
	}

	public StockAccessRight getAccessRight() {
		return this.accessRight;
	}

	public StockAccessRightBooleanPropertyWrapper getRead() {
		return this.read;
	}

	public StockAccessRightBooleanPropertyWrapper getWrite() {
		return this.write;
	}

	public StockAccessRightBooleanPropertyWrapper getImport() {
		return this.importRight;
	}

	public StockAccessRightBooleanPropertyWrapper getExport() {
		return this.export;
	}

	public StockAccessRightBooleanPropertyWrapper getCheckin() {
		return this.checkin;
	}

	public StockAccessRightBooleanPropertyWrapper getCheckout() {
		return this.checkout;
	}

	public StockAccessRightBooleanPropertyWrapper getRelease() {
		return this.release;
	}

	public StockAccessRightBooleanPropertyWrapper getCreate() {
		return this.create;
	}

	public StockAccessRightBooleanPropertyWrapper getDelete() {
		return this.delete;
	}

	/**
	 * Wrapper for {@link StockAccessRight} instances and {@link ProtectionType}s as boolean property
	 */
	public class StockAccessRightBooleanPropertyWrapper {

		/**
		 * The protection type of this tab
		 */
		private final ProtectionType protectionType;

		/**
		 * Wrapper for {@link StockAccessRight} instances and {@link ProtectionType}s as boolean property
		 * 
		 * @param protectionType
		 *            the protection type
		 * @param accessRight
		 *            the access right
		 */
		private StockAccessRightBooleanPropertyWrapper( ProtectionType protectionType ) {
			super();
			this.protectionType = protectionType;
		}

		/**
		 * Get the wrapped value
		 * 
		 * @return wrapped value
		 */
		public boolean getValue() {
			return this.protectionType.isContained( StockAccessRightWrapper.this.accessRight.getValue() );
		}

		/**
		 * Set the wrapped value
		 * 
		 * @param val
		 *            value to set
		 */
		public void setValue( boolean val ) {
			StockAccessRightWrapper.this.accessRight.setValue( this.protectionType.applyToValue( val, StockAccessRightWrapper.this.accessRight.getValue() ) );
		}

	}
}
