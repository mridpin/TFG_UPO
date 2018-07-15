package es.upo.tfg.rol.model.pojos;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "roll")
public class Roll implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotNull
	@Column(name = "attacker_score")
	private Double attackerScore;
	@NotNull
	@Column(name = "defender_score")
	private Double defenderScore;
	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attacker")
	private Coalition attacker;
	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "defender")
	private Coalition defender;
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "war")
	private War war;

	public Roll() {
		// TODO Auto-generated constructor stub
	}

	public Roll(Long id, @NotNull Double attackerScore, @NotNull Double defenderScore,
			@NotNull Coalition attacker, @NotNull Coalition defender, @NotNull War war) {
		super();
		this.id = id;
		this.attackerScore = attackerScore;
		this.defenderScore = defenderScore;
		this.attacker = attacker;
		this.defender = defender;
		this.war = war;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getAttackerScore() {
		return attackerScore;
	}

	public void setAttackerScore(Double attackerScore) {
		this.attackerScore = attackerScore;
	}

	public Double getDefenderScore() {
		return defenderScore;
	}

	public void setDefenderScore(Double defenderScore) {
		this.defenderScore = defenderScore;
	}

	public Coalition getAttacker() {
		return attacker;
	}

	public void setAttacker(Coalition attacker) {
		this.attacker = attacker;
	}

	public Coalition getDefender() {
		return defender;
	}

	public void setDefender(Coalition defender) {
		this.defender = defender;
	}

	public War getWar() {
		return war;
	}

	public void setWar(War war) {
		this.war = war;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attacker == null) ? 0 : attacker.hashCode());
		result = prime * result
				+ ((attackerScore == null) ? 0 : attackerScore.hashCode());
		result = prime * result + ((defender == null) ? 0 : defender.hashCode());
		result = prime * result
				+ ((defenderScore == null) ? 0 : defenderScore.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((war == null) ? 0 : war.hashCode());
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
		Roll other = (Roll) obj;
		if (attacker == null) {
			if (other.attacker != null)
				return false;
		} else if (!attacker.equals(other.attacker))
			return false;
		if (attackerScore == null) {
			if (other.attackerScore != null)
				return false;
		} else if (!attackerScore.equals(other.attackerScore))
			return false;
		if (defender == null) {
			if (other.defender != null)
				return false;
		} else if (!defender.equals(other.defender))
			return false;
		if (defenderScore == null) {
			if (other.defenderScore != null)
				return false;
		} else if (!defenderScore.equals(other.defenderScore))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (war == null) {
			if (other.war != null)
				return false;
		} else if (!war.equals(other.war))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Roll [id=" + id + ", attackerScore=" + attackerScore + ", defenderScore="
				+ defenderScore + ", attacker=" + attacker + ", defender=" + defender
				+ ", war=" + war + "]";
	}

}
