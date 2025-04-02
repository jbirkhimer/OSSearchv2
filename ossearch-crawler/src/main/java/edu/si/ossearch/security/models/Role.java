package edu.si.ossearch.security.models;

import lombok.Data;

import jakarta.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "roles")
//@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

//	@Enumerated(EnumType.STRING)
//	@Column(length = 20)
//	private RoleType name;

	@Column(length = 20)
	private String name;

	@ManyToMany
	@JoinTable(name = "roles_privileges", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
	private List<Privilege> privileges;

//	@Transient
//	@JsonIgnore
	@ManyToMany(mappedBy = "roles")
//	@JsonBackReference
	private List<User> users;

	public Role() {

	}

	public Role(String name) {
		this.name = name;
	}
}
