package edu.si.ossearch.collection.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = CrawlConfig.TABLE_NAME1)
public class CrawlConfig {

    public static final String TABLE_NAME = "crawl_config";
    public static final String TABLE_NAME1 = "crawl_config";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Crawler related information
    private String crawlDbPath;

    private String crawlSeedPath;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition = "TEXT")
    private Set<String> seedUrls;

    // Crawler Site information
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition = "TEXT")
    private Set<String> includeSiteUrls;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition = "TEXT")
    private Set<String> excludeSiteUrls;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @OrderBy("orderId")
//    @Fetch(FetchMode.SELECT)
//    private Set<RegexUrlFilters> regexUrlFilters;

    //    @JsonIgnore
//    @OneToMany(mappedBy = "crawlConfig", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    @Setter(AccessLevel.NONE)
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderBy("orderId")
    @Fetch(FetchMode.SELECT)
    private Set<UrlExclusionPattern> urlExclusionPatterns;

//    public void setUrlExclusionPatterns(Set<UrlExclusionPattern> urlExclusionPatterns) {
//        this.urlExclusionPatterns = urlExclusionPatterns;
//        for (UrlExclusionPattern urlExclusionPattern : urlExclusionPatterns) {
//            urlExclusionPattern.setCrawlConfig(this);
//        }
//    }

    //Crawler Sitemap information
    private Boolean useSitemap = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition = "TEXT")
    private Set<String> sitemapUrls;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition = "TEXT")
    private Set<String> includeSitemapUrls;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition = "TEXT")
    private Set<String> excludeSitemapUrls;

    private String crawlCronSchedule;
    private String cronEditorData;

    @OneToOne(mappedBy = "crawlConfig", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Collection collection;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;
}