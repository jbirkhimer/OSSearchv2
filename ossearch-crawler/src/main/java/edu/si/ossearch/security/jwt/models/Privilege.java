package edu.si.ossearch.security.jwt.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * @author jbirkhimer
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "privilege")
//@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Enumerated(EnumType.STRING)
//    @NonNull
//    private PrivilegeType name;

    @NonNull
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private List<Role> roles;

}
