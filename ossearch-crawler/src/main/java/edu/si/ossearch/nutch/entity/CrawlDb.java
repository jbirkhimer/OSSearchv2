package edu.si.ossearch.nutch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString//(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "crawl_db")
public class CrawlDb implements Serializable {

    private static final long serialVersionUID = -6698654913702498445L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer collectionId;

    @OneToMany(mappedBy = "crawlDb", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Webpage> webpages = new ArrayList<>();

}