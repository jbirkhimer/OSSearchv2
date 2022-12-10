package edu.si.ossearch.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import edu.si.ossearch.scheduler.entity.converter.JobTypeConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo.JobType.SCHEDULED_CRAWL;

/**
 * @author jbirkhimer
 */
//@ToString
@Getter
@Setter
@ToString//(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "scheduler_job_info", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueJobNameAndJobGroup", columnNames = {"jobName", "jobGroup"})
})
public class CrawlSchedulerJobInfo implements Serializable {

    private static final long serialVersionUID = -747244979496943067L;

    public enum JobType {
        REINDEX, RECRAWL, SCHEDULED_CRAWL, CUSTOM_CRAWL, ONE_TIME_CRAWL, ADD_URLS, CRAWL_NOW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    private String jobId;
    private String jobName;
    private String jobGroup = "scheduled_crawl";
    private String jobStatus;
    private String jobClass;
    private String description;

    private boolean cronJob;
    private String cronExpression;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextFireTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date previousFireTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finalFireTime;

    private String collectionName;
    private String collectionId;
    private int numberOfRounds = 50;

    private boolean reindex = false;
    private boolean recrawl = false;

//    @JsonSerialize(converter = JobTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private JobType jobType = SCHEDULED_CRAWL;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheduler_job_info_crawl_option_mapping",
            joinColumns = {@JoinColumn(name = "scheduler_job_info_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "crawl_option_name")
    private Map<String, String> crawlOptions = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition="TEXT")
    @CollectionTable(name = "scheduler_job_info_nutch_property_mapping",
            joinColumns = {@JoinColumn(name = "scheduler_job_info_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "nutch_property_name")
//    @Column(name = "nutch_property")
    private Map<String, String> nutchProperties = new HashMap<>();

    /*@OneToMany(mappedBy = "crawlSchedulerJobInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<NutchStepArg> nutchConfigs = new HashSet<>();

    public void setNutchConfigs(Set<NutchStepArg> nutchConfigs) {
        this.nutchConfigs = nutchConfigs;
        for (NutchStepArg nutchConfig : nutchConfigs) {
            nutchConfig.setCrawlSchedulerJobInfo(this);
        }
    }*/

    /*@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "nutch_step_arg_mapping",
            joinColumns = {@JoinColumn(name = "scheduler_job_info_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "arg_id", referencedColumnName = "id")})
    @MapKey(name = "stepName")
    private Map<String, NutchStepArg> nutchStepArgs = new HashMap<>();*/

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "nutch_step_args_id")
    private NutchStepArgs nutchStepArgs;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;

}
