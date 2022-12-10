package edu.si.ossearch.collection.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.collection.entity.converters.UrlExclusionPatternTypeConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author jbirkhimer
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
//@Entity
//@Table(name= "url_exclusion_pattern")
@Embeddable
public class UrlExclusionPattern {

    public enum Type {
        contains,
        regex
    }

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;

    private int orderId;
    @Column(columnDefinition = "TEXT")
    private String expression;

    @JsonSerialize(converter = UrlExclusionPatternTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private UrlExclusionPattern.Type type;

    private Boolean ignoreCase = false;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "collection_id", nullable = false)
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private Collection collection;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "crawlConfig_id", nullable = false)
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private CrawlConfig crawlConfig;

//    @CreationTimestamp
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date createdDate;
//
//    @UpdateTimestamp
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date updatedDate;
}
