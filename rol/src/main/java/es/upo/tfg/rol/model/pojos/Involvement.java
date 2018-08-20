package es.upo.tfg.rol.model.pojos;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "involvement")
public class Involvement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "country")
	private Country country;
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "coalition")
	private Coalition coalition;
	@NotNull
	@Range(min = 0, max = 1)
	@Column(name="involvement_percent")
	private Double involvementPercent;
	@Column(name="won_the_roll")
	private Boolean wonTheRoll;

	public Involvement() {
		// TODO Auto-generated constructor stub
	}
	
	public Involvement(@NotNull Country country, @NotNull Coalition coalition,
			@NotNull @Size(min = 0, max = 1) Double involvementPercent) {
		super();
		this.country = country;
		this.coalition = coalition;
		this.involvementPercent = involvementPercent;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Coalition getCoalition() {
		return coalition;
	}

	public void setCoalition(Coalition coalition) {
		this.coalition = coalition;
	}

	public Double getInvolvementPercent() {
		return involvementPercent;
	}

	public void setInvolvementPercent(Double involvementPercent) {
		this.involvementPercent = involvementPercent;
	}		

	public boolean isWonTheRoll() {
		return wonTheRoll;
	}

	public void setWonTheRoll(boolean wonTheRoll) {
		this.wonTheRoll = wonTheRoll;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coalition == null) ? 0 : coalition.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((involvementPercent == null) ? 0 : involvementPercent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Involvement other = (Involvement) obj;
		if (coalition == null) {
			if (other.coalition != null)
				return false;
		} else if (!coalition.equals(other.coalition))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (involvementPercent == null) {
			if (other.involvementPercent != null)
				return false;
		} else if (!involvementPercent.equals(other.involvementPercent))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Involvement [id=" + id + ", country=" + country + ", coalition="
				+ coalition + ", involvementPercent=" + involvementPercent + "]";
	}

}
