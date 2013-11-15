package eu.europa.ec.jrc.lca.commons.domain;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="t_nonce")
@NamedQueries({
	@NamedQuery(name = "removeOldNonces", query = "DELETE FROM Nonce n WHERE n.useDate < :clearDate")
})
public class Nonce {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date useDate;
	
	private byte[] value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getUseDate() {
		return useDate;
	}

	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value =  Arrays.copyOf(value, value.length);
	}
}
