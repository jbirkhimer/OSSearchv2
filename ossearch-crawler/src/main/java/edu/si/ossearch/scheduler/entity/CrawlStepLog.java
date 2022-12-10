package edu.si.ossearch.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.scheduler.entity.converter.StateConverter;
import edu.si.ossearch.scheduler.entity.converter.StepTypeConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "crawl_step_log")
public class CrawlStepLog implements Serializable {
    private static final long serialVersionUID = -1888135355095768691L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobKey;
    private String jobId;

    @JsonSerialize(converter = StepTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private CrawlLog.StepType stepType;

    @JsonSerialize(converter = StateConverter.class)
    @Enumerated(EnumType.STRING)
    private CrawlLog.State state;

    private int round;

    @Column(columnDefinition = "TEXT")
    private String args;

    @Column(columnDefinition = "TEXT")
    private String dbStats;

    private long solrCount;

    @Column(columnDefinition = "TEXT")
    private String errors;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "crawl_log_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CrawlLog crawlLog;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;
}