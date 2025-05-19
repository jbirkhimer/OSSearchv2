package edu.si.ossearch.reports.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for search log CSV export and compression
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ossearch.searchlog.csv-export")
public class SearchLogCsvExportConfig {
    
    /**
     * Enable/disable CSV export functionality
     */
    private boolean enabled = false;
    
    /**
     * Base directory for CSV export files
     */
    private String baseDirectory = "./export";
    
    /**
     * Cron expression for daily compression (default: 1:00 AM every day)
     */
    private String dailyCompressionCron = "0 0 1 * * ?";
    
    /**
     * Cron expression for monthly compression (default: 2:00 AM on the 2nd day of each month)
     */
    private String monthlyCompressionCron = "0 0 2 2 * ?";
}
