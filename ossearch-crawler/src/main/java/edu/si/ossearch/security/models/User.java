package edu.si.ossearch.security.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import edu.si.ossearch.config.JsonDeserializers;
import edu.si.ossearch.collection.entity.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

//@Data
@Getter
@Setter
@Entity
@Table(	name = "users",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "username"),
				@UniqueConstraint(columnNames = "email")
		})
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

//	@NotBlank
	@Size(max = 120)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@JsonDeserialize(using = JsonDeserializers.PasswordDeserializer.class)
	private String password;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles",
				joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
//	@JsonManagedReference
	private Set<Role> roles = new HashSet<>();

	@Transient
	@Getter(AccessLevel.NONE)
	private Set<String> roleList;

	public Set<String> getRoleList() {
		Set<String> answer = new HashSet<>();
		getRoles().forEach(role -> {
			answer.add(role.getName());
		});
		return answer;
	}

	@Size(max = 20)
	private String firstName;

	@Size(max = 20)
	private String lastName;

	@Column(columnDefinition = "boolean default true")
	private boolean enabled = true;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastLogin;

//	@ManyToMany(fetch = FetchType.EAGER)
//	@JoinTable(name = "user_collections",
//			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
//			inverseJoinColumns = @JoinColumn(name = "collection_id", referencedColumnName = "id"))
	@ManyToMany(mappedBy = "users", cascade = CascadeType.PERSIST)
	private Set<Collection> collections = new HashSet<>();

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dateCreated;

	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dateUpdated;

	public User() {
	}

	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

}
