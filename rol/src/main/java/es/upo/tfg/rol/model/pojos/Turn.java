package es.upo.tfg.rol.model.pojos;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "turn")
public class Turn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotNull
	@Size(min = 2, max = 64)
	@Column(name = "subscenario")
	private String subscenario;
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game")
	private Game game;
	// TODO: SECURE THE ORDER BY ADDING A ORDER ATTRIBUTE

	public Turn() {
	}

	public Turn(@NotNull @Size(min = 2, max = 64) String subscenario,
			@NotNull Game game) {
		super();
		this.subscenario = subscenario;
		this.game = game;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubscenario() {
		return subscenario;
	}

	public void setSubscenario(String subscenario) {
		this.subscenario = subscenario;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((subscenario == null) ? 0 : subscenario.hashCode());
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
		Turn other = (Turn) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (subscenario == null) {
			if (other.subscenario != null)
				return false;
		} else if (!subscenario.equals(other.subscenario))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Turn [id=" + id + ", subscenario=" + subscenario + ", game=" + game + "]";
	}

}
