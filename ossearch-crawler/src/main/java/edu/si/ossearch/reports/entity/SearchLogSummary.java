package edu.si.ossearch.reports.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing daily summary of search counts by collection
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "search_log_summary")
@IdClass(SearchLogSummaryId.class)
public class SearchLogSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "collection_id")
    private Integer collectionId;

    @Id
    @Column(name = "log_date")
    private LocalDate logDate;

    @Column(name = "site", length = 50)
    private String site;

    @Column(name = "search_count", nullable = false)
    private Long searchCount = 0L;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        lastUpdated = LocalDateTime.now();
    }

    /**
     * Increment the search count by 1
     */
    public void incrementCount() {
        searchCount = searchCount + 1;
    }
}
