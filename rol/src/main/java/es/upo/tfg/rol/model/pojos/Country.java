package es.upo.tfg.rol.model.pojos;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name = "country")
public class Country implements Serializable {

	/**
	 * Serial ID: Required for Tomcat, for classes to implement Serializable
	 * whenever instances of those classes are been stored as an attribute of the
	 * HttpSession. Source:
	 * https://stackoverflow.com/questions/2294551/java-io-writeabortedexception-writing-aborted-java-io-notserializableexception
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotNull
	@Size(min = 2, max = 64)
	@Column(name = "name")
	private String name;
	@NotNull
	@Column(name = "data_file")
	private String data;
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "player")
	private User player;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game")
	private Game game;

	/*
	 * Three levels of nesting: Outer map is <Year, Map> Mid map is <Type, Map>
	 * Inner map is <Attribute, Value> It's basically a tree, but implemented as an
	 * assortment of maps to use later in javascript. Might want to change it to
	 * TreeMap later for undetstandability
	 * 
	 * @Transient so its not persistent, as all is stored in the csv file
	 */
	@Transient
	private Map<String, Map<String, Map<String, Double>>> attributes;

	public Country() {
	}

	public Country(Long id, @NotNull @Size(min = 2, max = 64) String name,
			@NotNull String data, @NotNull User player, @NotNull Game game) {
		super();
		this.id = id;
		this.name = name;
		this.data = data;
		this.player = player;
		this.game = game;
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public User getPlayer() {
		return player;
	}

	public void setPlayer(User player) {
		this.player = player;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Map<String, Map<String, Map<String, Double>>> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Map<String, Map<String, Double>>> attributes) {
		this.attributes = attributes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((game == null) ? 0 : game.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Country other = (Country) obj;
		if (game == null) {
			if (other.game != null)
				return false;
		} else if (!game.equals(other.game))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Country [id=" + id + ", name=" + name + ", data=" + data + ", player="
				+ player + ", game=" + game + "]";
	}

}
