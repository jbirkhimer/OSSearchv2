package edu.si.ossearch.reports.service;

import edu.si.ossearch.reports.config.SearchLogCsvExportConfig;
import edu.si.ossearch.reports.repository.SearchLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service for compressing search log CSV files and table maintenance
 */
@Slf4j
@Service
public class SearchLogCompressionService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private SearchLogCsvExportConfig csvExportConfig;

    @Autowired
    private SearchLogRepository searchLogRepository;
    /**
     * Daily task to gzip previous day's CSV files
     * Runs according to configured cron schedule
     */
    @Scheduled(cron = "${ossearch.searchlog.csv-export.daily-compression-cron}")
    public void compressPreviousDayFiles() {
        if (!csvExportConfig.isEnabled()) {
            return;
        }

        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayFormatted = yesterday.format(DATE_FORMATTER);
        log.info("Starting compression of previous day's CSV files: {}", yesterdayFormatted);

        try {
            Path baseDir = Paths.get(csvExportConfig.getBaseDirectory());
            if (!Files.exists(baseDir)) {
                log.warn("Base directory does not exist: {}", baseDir);
                return;
            }

            // Process all site directories
            try (DirectoryStream<Path> siteDirs = Files.newDirectoryStream(baseDir, Files::isDirectory)) {
                for (Path siteDir : siteDirs) {
                    compressSitePreviousDayFiles(siteDir, yesterday);
                }
            }

            log.info("Completed compression of previous day's CSV files");
        } catch (Exception e) {
            log.error("Error compressing previous day's CSV files", e);
        }
    }

    /**
     * Monthly task to zip and remove previous month's folders
     * Runs according to configured cron schedule
     */
    @Scheduled(cron = "${ossearch.searchlog.csv-export.monthly-compression-cron}")
    public void compressPreviousMonthFolders() {
        if (!csvExportConfig.isEnabled()) {
            return;
        }

        LocalDate firstDayOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayOfPreviousMonth = firstDayOfCurrentMonth.minusDays(1);

        int year = lastDayOfPreviousMonth.getYear();
        Month month = lastDayOfPreviousMonth.getMonth();

        String monthYearStr = month + "-" + year;

        log.info("Starting compression of previous month's folders: {}", monthYearStr);

        try {
            Path baseDir = Paths.get(csvExportConfig.getBaseDirectory());
            if (!Files.exists(baseDir)) {
                log.warn("Base directory does not exist: {}", baseDir);
                return;
            }

            // Process all site directories
            try (DirectoryStream<Path> siteDirs = Files.newDirectoryStream(baseDir, Files::isDirectory)) {
                for (Path siteDir : siteDirs) {
                    compressSitePreviousMonthFolder(siteDir, year, month, monthYearStr);
                }
            }

            log.info("Completed compression of previous month's folders");
            // Truncate the search_log table after successful compression
            truncateSearchLogTable();
        } catch (Exception e) {
            log.error("Error compressing previous month's folders", e);
        }
    }

    /**
     * Truncate the search_log table
     * Called monthly after successful archiving of search logs
     */
    @Transactional
    public void truncateSearchLogTable() {
        try {
            log.info("Truncating search_log table");
            searchLogRepository.truncateTable();
            log.info("Successfully truncated search_log table");
        } catch (Exception e) {
            log.error("Error truncating search_log table", e);
        }
    }
    /**
     * Compress previous day's CSV files for a specific site
     */
    private void compressSitePreviousDayFiles(Path siteDir, LocalDate date) {
        String year = String.valueOf(date.getYear());
        String month = date.getMonth().toString();
        String monthYear = month + "-" + year;
        String dayFormatted = date.format(DATE_FORMATTER);

        // Path to month directory
        Path yearDir = siteDir.resolve(year);
        if (!Files.exists(yearDir)) {
            return;
        }

        Path monthDir = yearDir.resolve(monthYear);
        if (!Files.exists(monthDir)) {
            return;
        }

        // Find the CSV file
        Path csvFile = monthDir.resolve("daily-search_log-" + dayFormatted + ".csv");
        if (!Files.exists(csvFile)) {
            log.info("No CSV file found for {} in site {}", dayFormatted, siteDir.getFileName());
            return;
        }

        // Path for gzipped file
        Path gzipFile = monthDir.resolve("daily-search_log-" + dayFormatted + ".csv.gz");

        // Compress the file
        try (InputStream in = Files.newInputStream(csvFile);
             GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(gzipFile))) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            // Delete the original file after successful compression
            Files.delete(csvFile);
            log.info("Successfully compressed {} to {}", csvFile, gzipFile);

        } catch (IOException e) {
            log.error("Error compressing file {}: {}", csvFile, e.getMessage());
        }
    }

    /**
     * Compress previous month's folder for a specific site
     */
    private void compressSitePreviousMonthFolder(Path siteDir, int year, Month month, String monthYearStr) {
        Path yearDir = siteDir.resolve(String.valueOf(year));
        if (!Files.exists(yearDir)) {
            return;
        }

        Path monthDir = yearDir.resolve(monthYearStr);
        if (!Files.exists(monthDir)) {
            return;
        }

        // Path for zip file
        Path zipFile = yearDir.resolve(monthYearStr + ".zip");

        try {
            // Create zip file
            try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipFile))) {
                Files.walkFileTree(monthDir, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        // Get relative path for zip entry
                        Path relativePath = monthDir.relativize(file);
                        String zipEntryName = relativePath.toString().replace('\\', '/');

                        // Add zip entry
                        zipOut.putNextEntry(new ZipEntry(zipEntryName));

                        // Write file content to zip
                        Files.copy(file, zipOut);
                        zipOut.closeEntry();

                        return FileVisitResult.CONTINUE;
                    }
                });
            }

            // Delete the month directory after successful compression
            deleteDirectory(monthDir);

            log.info("Successfully compressed and removed directory {}", monthDir);

        } catch (IOException e) {
            log.error("Error compressing directory {}: {}", monthDir, e.getMessage());
        }
    }

    /**
     * Recursively delete a directory
     */
    private void deleteDirectory(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
