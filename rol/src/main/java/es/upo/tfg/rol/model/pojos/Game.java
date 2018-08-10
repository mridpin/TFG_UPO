package es.upo.tfg.rol.model.pojos;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name = "game")
public class Game implements Serializable {

	/**
	 * Serial ID: Required for Tomcat, for classes to implement Serializable
	 * whenever instances of those classes are been stored as an attribute of
	 * the HttpSession. Source:
	 * https://stackoverflow.com/questions/2294551/java-io-writeabortedexception-writing-aborted-java-io-notserializableexception
	 */
	private static final long serialVersionUID = -6121532670877592375L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotNull
	@Size(min = 2, max = 64)
	@Column(name = "name")
	private String name;
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "scenario")
	private Scenario scenario;
	@NotNull
	@Column(name = "start_date")
	private Date startDate;
	@Column(name = "end_date")
	private Date endDate;
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "master")
	private User master;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "active_turn")
	private Turn activeTurn;

	public Game() {
	}

	public Game(Long id, @NotNull @Size(min = 2, max = 64) String name,
			@NotNull Scenario scenario, @NotNull Date startDate,
			Date endDate, @NotNull User master, @NotNull Turn activeTurn) {
		super();
		this.id = id;
		this.name = name;
		this.scenario = scenario;
		this.startDate = startDate;
		this.endDate = endDate;
		this.master = master;
		this.activeTurn = activeTurn;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public User getMaster() {
		return master;
	}

	public void setMaster(User master) {
		this.master = master;
	}	

	public Turn getActiveTurn() {
		return activeTurn;
	}

	public void setActiveTurn(Turn activeTurn) {
		this.activeTurn = activeTurn;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((master == null) ? 0 : master.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
		Game other = (Game) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (master == null) {
			if (other.master != null)
				return false;
		} else if (!master.equals(other.master))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Game [id=" + id + ", name=" + name + ", scenario=" + scenario
				+ ", startDate=" + startDate + ", endDate=" + endDate
				+ ", master=" + master + "]";
	}

}
