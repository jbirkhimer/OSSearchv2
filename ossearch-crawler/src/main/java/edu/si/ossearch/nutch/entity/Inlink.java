package edu.si.ossearch.nutch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString//(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "inlink")
public class Inlink implements Serializable {
    private static final long serialVersionUID = 1591113294723596783L;

    @Id
    private String id;

    @Column(columnDefinition = "TEXT", length = 4096)
    private String url;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private Set<String> anchors = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private Set<String> fromUrl = new HashSet<>();



}