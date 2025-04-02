package edu.si.ossearch.collection.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.collection.entity.converters.NavigationTypeConverter;
import edu.si.ossearch.collection.entity.converters.SortByConverter;
import edu.si.ossearch.collection.entity.converters.SortTypeConverter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.util.Date;

/**
 * @author jbirkhimer
 */
@Getter
@Setter
@Entity
@Table(name= "dynamic_navigation")
public class DynamicNavigation {

    public enum SortType {
        asc,
        desc
    }

    public enum SortBy {
        value,
        count
    }

    public enum NavigationType {
        requiredField,
        partialField,
        inMeta
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String displayName;

    private String attributeName;

    @JsonSerialize(converter = SortTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private SortType sort;

    @JsonSerialize(converter = SortByConverter.class)
    @Enumerated(EnumType.STRING)
    private SortBy sortBy;

    private Boolean multivalue = false;

    private String delimiter;

    @JsonSerialize(converter = NavigationTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private NavigationType navigationType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "collection_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @JsonBackReference
    private Collection collection;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;
}
