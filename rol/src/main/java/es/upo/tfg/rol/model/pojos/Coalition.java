package es.upo.tfg.rol.model.pojos;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "coalition")
public class Coalition implements Serializable {

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
	@OneToMany(mappedBy="coalition", fetch = FetchType.EAGER)
	private List<Involvement> involvements;

	public Coalition() {
		// TODO Auto-generated constructor stub
	}	

	public Coalition(Long id, @NotNull @Size(min = 2, max = 256) String name,
			List<Involvement> involvements) {
		super();
		this.id = id;
		this.name = name;
		this.involvements = involvements;
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

	public List<Involvement> getInvolvements() {
		return involvements;
	}

	public void setInvolvements(List<Involvement> involvements) {
		this.involvements = involvements;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((involvements == null) ? 0 : involvements.hashCode());
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
		Coalition other = (Coalition) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (involvements == null) {
			if (other.involvements != null)
				return false;
		} else if (!involvements.equals(other.involvements))
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
		return "Coalition [id=" + id + ", name=" + name + "]";
	}
		
}
