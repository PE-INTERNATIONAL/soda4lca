package de.iai.ilcd.model.registry;

import java.io.UnsupportedEncodingException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "dataset_deregistration_reason" )
public class DatasetDeregistrationReason {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	private byte[] reason;

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public byte[] getReason() {
		return reason;
	}

	public void setReason( byte[] reason ) {
		this.reason = reason;
	}

	public String getContent() throws UnsupportedEncodingException {
		if ( reason != null ) {
			return new String( reason, "UTF-8" );
		}
		return null;
	}

	public void setContent( String content ) throws UnsupportedEncodingException {
		if ( content != null ) {
			reason = content.getBytes( "UTF-8" );
		}
		else {
			reason = null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		DatasetDeregistrationReason other = (DatasetDeregistrationReason) obj;
		if ( id == null ) {
			if ( other.id != null )
				return false;
		}
		else if ( !id.equals( other.id ) )
			return false;
		return true;
	}

}
