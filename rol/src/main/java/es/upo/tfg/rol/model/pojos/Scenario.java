package es.upo.tfg.rol.model.pojos;
import java.io.Serializable;
import java.util.Map;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "scenario")
public class Scenario implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotNull
	@Size(min = 2, max = 256)
	@Column(name = "name")
	private String name;
	@Size(min = 2, max = 512)
	@Column(name = "description")
	private String description;
	@NotNull
	@Column(name = "data_file")
	private String data;
	@Transient
	private Map<String, Map<String, Map<String, Double>>> attributes;
	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "author")
	private User author;

	public Scenario() {
		// TODO Auto-generated constructor stub
	}

	public Scenario(Long id, @NotNull @Size(min = 2, max = 256) String name,
			@Size(min = 2, max = 512) String description, @NotNull String data,
			Map<String, Map<String, Map<String, Double>>> attributes,
			@NotNull User author) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.data = data;
		this.attributes = attributes;
		this.author = author;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Map<String, Map<String, Map<String, Double>>> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Map<String, Map<String, Double>>> attributes) {
		this.attributes = attributes;
	}	

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		Scenario other = (Scenario) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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
		return "Scenario [id=" + id + ", name=" + name + ", description=" + description
				+ ", data=" + data + "]";
	}

}
