package edu.si.ossearch.collection.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jbirkhimer
 */
@Getter
@Setter
@Entity
@Table(name = "page_result")
public class PageResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String logoPath;

    private String name;

    private String nameNoSpace;

    private String stylesheet;

    private String linkColor;

    @Column(columnDefinition = "TEXT")
    private String header;

    @Column(columnDefinition = "TEXT")
    private String footer;

    private String buttonText;

    private Integer boxMaxChar;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> includeField  = new HashSet<>();

    private Boolean useSearchButton;

    @Column(columnDefinition = "TEXT")
    private String head;

    @Column(columnDefinition = "TEXT")
    private String fullHtml;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "collection_id", nullable = false)
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private Collection collection;

    //Createed and Updated Information
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreated;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateUpdated;

}
