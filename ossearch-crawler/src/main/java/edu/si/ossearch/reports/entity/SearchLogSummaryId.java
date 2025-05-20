package edu.si.ossearch.reports.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Composite primary key for SearchLogSummary
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchLogSummaryId implements Serializable {
    private Integer collectionId;
    private LocalDate logDate;
}
