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

	public Roll() {
		// TODO Auto-generated constructor stub
	}

}
