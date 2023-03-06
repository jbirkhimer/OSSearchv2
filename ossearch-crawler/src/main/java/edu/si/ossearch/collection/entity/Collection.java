package edu.si.ossearch.collection.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.collection.entity.converters.ResponseTypeConverter;
import edu.si.ossearch.security.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jbirkhimer
 */
@Getter
@Setter
@Entity
@Table(name = Collection.TABLE_NAME, uniqueConstraints = {
        @UniqueConstraint(name = "UniqueCollectionName", columnNames = {"name"})
})
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Collection {
    public static final String TABLE_NAME = "collection";

    public enum ResponseType {
        html,
        json,
        xml
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String siteUrl;

    //Search related configurations
    @JsonSerialize(converter = ResponseTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private ResponseType responseType = ResponseType.html;

    private Integer resultsPerPage = 10;

    private Integer resultLimit = 0;

    private Boolean excludeDuplicateResults = false;

    //Fields and Metadata Fields
    /**
     * <b>Include Response Fields</b>
     * By default search results will be returned with <b>Title</b>, <b>Snippet</b>, and <b>URL</b>.
     * Site owners can optionally select <b>Anchor</b>, <b>Meta Description</b> and <b>Meta Keywords</b> to be returned with the search results.
     *
     * <b>Meta Description</b> and <b>Meta Keywords</b> are metatags that are included on some pages; this is website-dependent.
     *
     * <b>Anchor</b> is the text of the link that linked to the page. OSS won’t always find Anchors for all URLs, but here's an example:
     * <p>
     * The URL https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition has 2 anchors:
     * <p>
     * 1. "Latin American Research", derived from the left-hand menu on this page: https://siarchives.si.edu/history/featured-topics
     * 2. "view in English", derived from the English language link on this page: https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0
     */
    //TODO: make table and entity for fields
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> includeFields = new HashSet<>(Arrays.asList("title", "content", "url"));

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> keywords = new HashSet<>();

    //    private Integer metadataBase = 0; //TODO: is this needed for anything ???

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private Set<Keymatch> keymatches = new HashSet<>();

    public void setKeymatches(Set<Keymatch> keymatches) {
        this.keymatches = keymatches;
        for (Keymatch keymatch : keymatches) {
            keymatch.setCollection(this);
        }
    }

//    private Boolean requiredField = false; //TODO: is this needed for anything ??? Yes means use dynamicNavigation
    private Boolean useFacets = false;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private Set<DynamicNavigation> dynamicNavigations = new HashSet<>();

    public void setDynamicNavigations(Set<DynamicNavigation> dynamicNavigations) {
        this.dynamicNavigations = dynamicNavigations;
//        this.dynamicNavigations.retainAll(dynamicNavigations);
        for (DynamicNavigation dynamicNavigation : dynamicNavigations) {
            dynamicNavigation.setCollection(this);
        }
    }

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private Set<PageResult> pageResults = new HashSet<>();

    //Collection Relationships
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name="included_collections",
            joinColumns=@JoinColumn(name="collectionId"),
            inverseJoinColumns=@JoinColumn(name="includedCollectionId")
    )
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//    @JsonManagedReference
    private Set<Collection> includedCollections = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "includedCollections", fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private Set<Collection> partOfCollections = new HashSet<>();

    //Crawler related information
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "crawl_config_id", nullable = false)
    private CrawlConfig crawlConfig;

    //User Access Information
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User owner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "collections_user",
			joinColumns = @JoinColumn(name = "collection_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> users = new HashSet<>();

    @PrePersist
    public void addUsers() {
        users.forEach(user -> user.getCollections().add(this));
    }

    @PreRemove
    public void removeUsers() {
        users.forEach(user -> user.getCollections().remove(this));
    }

    //Createed and Updated Information
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreated;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateUpdated;

    public boolean hasUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /*@CreatedBy
    private String createdBy;
    @CreatedBy
    private String updatedBy;


    @PrePersist
    public void prePersist() {
        dateCreated = new Date();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl user = (UserDetailsImpl) principal;
                createdBy = user.getUsername();
            }
        }
    }

    @PreUpdate
    public void preUpdate() {
        dateUpdated = new Date();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl user = (UserDetailsImpl) principal;
                createdBy = user.getUsername();
            }
        }
    }*/


}
