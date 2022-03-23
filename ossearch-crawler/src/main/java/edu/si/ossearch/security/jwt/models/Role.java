package edu.si.ossearch.security.jwt.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
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