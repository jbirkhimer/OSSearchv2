package edu.si.ossearch.reports.entity.projections;

import edu.si.ossearch.reports.entity.SearchLog;
import org.springframework.data.rest.core.config.Projection;

/**
 * A Projection for the {@link SearchLog} entity
 */
@Projection(name = "searchLogKeywordCountsView", types = SearchLog.class)
public interface SearchLogKeywordCountsView {
    String getQuery();
    Long getWordCount();
    String getDateRange();
}

/*@Data
@Getter
@Setter
//@NoArgsConstructor
@Projection(name = "searchLogKeywordCountsView", types = SearchLog.class)
public class SearchLogKeywordCountsView {
    private String query;
    private Long wordCount;
//    @JsonIgnore
//    private Date startDateRange;
//    @JsonIgnore
//    private Date endDateRange;
    private String dateRange;

    public SearchLogKeywordCountsView(String query, Long wordCount, String dateRange) {
        this.query = query;
        this.wordCount = wordCount;
        this.dateRange = dateRange;
//        this.dateRange = formatDate(startDateRange) + " - " + formatDate(endDateRange);
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
        //return LocalDate.parse(date).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}*/
