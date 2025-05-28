package edu.si.ossearch.reports.entity.listeners;

import com.opencsv.CSVWriter;
import edu.si.ossearch.reports.config.SearchLogCsvExportConfig;
import edu.si.ossearch.reports.entity.*;
import edu.si.ossearch.reports.repository.SearchLogAllTimeSummaryRepository;
import edu.si.ossearch.reports.repository.SearchLogSummaryRepository;
import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

/**
 * Entity listener for SearchLog to update both summary tables
 * and export to CSV in real-time
 */
@Slf4j
@Component
public class SearchLogEntityListener {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String[] CSV_HEADERS = {
            "id", "collectionId", "site", "query", "docsFound", "queryTime",
            "elapsedTime", "rawQuery", "errors", "createdDate", "updatedDate"
    };

    private static SearchLogSummaryRepository summaryRepository;
    private static SearchLogAllTimeSummaryRepository allTimeSummaryRepository;
    private static SearchLogCsvExportConfig csvExportConfig;

    @Autowired
    public void setSummaryRepository(SearchLogSummaryRepository repository) {
        SearchLogEntityListener.summaryRepository = repository;
    }

    @Autowired
    public void setAllTimeSummaryRepository(SearchLogAllTimeSummaryRepository repository) {
        SearchLogEntityListener.allTimeSummaryRepository = repository;
    }

    @Autowired
    public void setCsvExportConfig(SearchLogCsvExportConfig config) {
        SearchLogEntityListener.csvExportConfig = config;
    }

    /**
     * After a SearchLog is persisted, update both summary tables
     * and export to CSV
     */
    @PostPersist
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPostPersist(SearchLog searchLog) {
        try {
            if (searchLog.getCollectionId() == null) {
                log.warn("SearchLog has null collectionId, skipping summary update");
                return;
            }

            Integer collectionId = searchLog.getCollectionId();
            String site = searchLog.getSite();

            // Update 30-day rolling summary
            updateRollingSummary(searchLog, collectionId, site);

            // Update all-time summary
            updateAllTimeSummary(collectionId, site);

            // Export to CSV if enabled
            if (csvExportConfig != null && csvExportConfig.isEnabled()) {
                try {
                    exportToCSV(searchLog);
                } catch (Exception e) {
                    log.error("Error exporting search log to CSV", e);
                }
            }

        } catch (Exception e) {
            // Log but don't fail the transaction
            log.error("Error updating search log summaries", e);
        }
    }

    private void updateRollingSummary(SearchLog searchLog, Integer collectionId, String site) {
        try {
            // Get the date of the search log, defaulting to today if null
            LocalDate logDate = searchLog.getCreatedDate() != null
                ? convertToLocalDate(searchLog.getCreatedDate())
                : LocalDate.now();

            // Create composite key
            SearchLogSummaryId summaryId = new SearchLogSummaryId(collectionId, logDate);

            // Try to find existing summary for this collection and date
            Optional<SearchLogSummary> summaryOpt = summaryRepository.findById(summaryId);

            SearchLogSummary summary;
            if (summaryOpt.isPresent()) {
                summary = summaryOpt.get();
                summary.incrementCount();
            } else {
                // Create new summary if it doesn't exist
                summary = new SearchLogSummary();
                summary.setCollectionId(collectionId);
                summary.setLogDate(logDate);
                summary.setSite(site);
                summary.setSearchCount(1L);
            }

            summaryRepository.save(summary);
            log.debug("Updated 30-day search count for collection {} on {}: {}",
                      collectionId, logDate, summary.getSearchCount());
        } catch (Exception e) {
            log.error("Error updating 30-day rolling summary", e);
        }
    }

    private void updateAllTimeSummary(Integer collectionId, String site) {
        try {
            // Try to find existing all-time summary
            Optional<SearchLogAllTimeSummary> summaryOpt = allTimeSummaryRepository.findById(collectionId);

            SearchLogAllTimeSummary summary;
            if (summaryOpt.isPresent()) {
                summary = summaryOpt.get();
                summary.incrementCount();
            } else {
                // Create new summary if it doesn't exist
                summary = new SearchLogAllTimeSummary();
                summary.setCollectionId(collectionId);
                summary.setSite(site);
                summary.setSearchCount(1L);
            }

            allTimeSummaryRepository.save(summary);
            log.debug("Updated all-time search count for collection {}: {}",
                     collectionId, summary.getSearchCount());
        } catch (Exception e) {
            log.error("Error updating all-time summary", e);
        }
    }

    /**
     * Export a single search log to CSV in real-time
     * This method has minimal performance impact as it:
     * - Runs asynchronously (in a new transaction)
     * - Performs lightweight file I/O operations
     * - Appends a single row at a time
     * - Uses buffered I/O through FileWriter
     */
    private void exportToCSV(SearchLog searchLog) throws IOException {
        if (searchLog.getCreatedDate() == null) {
            log.warn("SearchLog has null createdDate, skipping CSV export");
            return;
        }

        LocalDate logDate = convertToLocalDate(searchLog.getCreatedDate());

        // Get site name (use a safe default if null or empty)
        String site = (searchLog.getSite() != null && !searchLog.getSite().isEmpty())
            ? searchLog.getSite().replaceAll("[^a-zA-Z0-9-_]", "_") // Sanitize for safe folder name
            : "unknown";

        // Create formatted strings for path components
        String year = String.valueOf(logDate.getYear());
        String month = logDate.getMonth().toString();
        String formattedDate = logDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Create the file path with site/year/month folders
        String baseDir = csvExportConfig.getBaseDirectory();
        Path yearMonthDir = Paths.get(baseDir, site, year, month + "-" + year);

        // Create directories if they don't exist
        Files.createDirectories(yearMonthDir);

        // Create the file with the requested naming pattern
        String filename = "daily-search_log-" + formattedDate + ".csv";
        Path csvFile = yearMonthDir.resolve(filename);

        boolean fileExists = Files.exists(csvFile);

        // Use try-with-resources with append mode (true)
        try (FileWriter fileWriter = new FileWriter(csvFile.toFile(), true);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            // Write headers if new file
            if (!fileExists) {
                csvWriter.writeNext(CSV_HEADERS);
            }

            // Write data row
            String[] row = new String[CSV_HEADERS.length];
            row[0] = searchLog.getId() != null ? searchLog.getId().toString() : "";
            row[1] = searchLog.getCollectionId() != null ? searchLog.getCollectionId().toString() : "";
            row[2] = searchLog.getSite() != null ? searchLog.getSite() : "";
            row[3] = searchLog.getQuery() != null ? searchLog.getQuery() : "";
            row[4] = searchLog.getDocsFound() != null ? searchLog.getDocsFound().toString() : "";
            row[5] = searchLog.getQueryTime() != null ? searchLog.getQueryTime().toString() : "";
            row[6] = searchLog.getElapsedTime() != null ? searchLog.getElapsedTime().toString() : "";
            row[7] = searchLog.getRawQuery() != null ? searchLog.getRawQuery() : "";
            row[8] = searchLog.getErrors() != null ? searchLog.getErrors() : "";
            row[9] = searchLog.getCreatedDate() != null ? DATE_FORMAT.format(searchLog.getCreatedDate()) : "";
            row[10] = searchLog.getUpdatedDate() != null ? DATE_FORMAT.format(searchLog.getUpdatedDate()) : "";

            csvWriter.writeNext(row);
        } catch (IOException e) {
            log.error("Error writing search log to CSV: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Convert java.util.Date to LocalDate
     */
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                  .atZone(ZoneId.systemDefault())
                  .toLocalDate();
    }
}
