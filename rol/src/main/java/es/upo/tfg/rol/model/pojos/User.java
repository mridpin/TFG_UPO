package es.upo.tfg.rol.model.pojos;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

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
	@Column(name = "avatar")	
	private String avatar;
	@NotNull(message = "{name.notnull}")
	@Size(min=2, max=64)
	@Column(name = "name")
	private String name;
	@NotNull(message = "{nickname.notnull}")
	@Size(min=2, max=32, message = "{nickname.size}")
	@Column(unique = true, name = "nickname")
	private String nickname;
	@NotNull(message = "{password.notnull}")
	@Size(min=5, max=64, message = "{password.size}")
	@Column(name = "password")
	private String password;

	public User() {
	}

	public User(String avatar,
			@NotNull(message = "{name.notnull}") @Size(min = 2, max = 64) String name,
			@NotNull(message = "{nickname.notnull}") @Size(min = 2, max = 32, message = "{nickname.size}") String nickname,
			@NotNull(message = "{password.notnull}") @Size(min = 5, max = 64, message = "{password.size}") String password) {
		this.avatar = avatar;
		this.name = name;
		this.nickname = nickname;
		this.password = password;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAvatar() {
		return this.avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((avatar == null) ? 0 : avatar.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
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
		User other = (User) obj;
		if (avatar == null) {
			if (other.avatar != null)
				return false;
		} else if (!avatar.equals(other.avatar))
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
		if (nickname == null) {
			if (other.nickname != null)
				return false;
		} else if (!nickname.equals(other.nickname))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", avatar=" + avatar + ", name=" + name + ", nickname=" + nickname + ", password="
				+ password + "]";
	}

}