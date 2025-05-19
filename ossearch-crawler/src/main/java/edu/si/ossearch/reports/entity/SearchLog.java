package edu.si.ossearch.reports.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.si.ossearch.reports.entity.listeners.SearchLogEntityListener;
import edu.si.ossearch.search.beans.request.Query;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author jbirkhimer
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@EntityListeners(SearchLogEntityListener.class)
@Table(name = "search_log")
public class SearchLog implements Serializable {

    private static final long serialVersionUID = -7272242479560896738L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "collectionId")
    @Column(columnDefinition = "SMALLINT")
    private Integer collectionId;

    @Column(columnDefinition = "VARCHAR(50)")
    private String site;

    private String query;

    @JsonProperty(value = "docsFound")
    @Column(columnDefinition = "INT")
    private Integer docsFound;

    @Column(columnDefinition = "SMALLINT")
    private Integer queryTime;

    @JsonProperty(value = "elapsedTime")
    @Column(columnDefinition = "MEDIUMINT")
    private Integer elapsedTime;

    @Column(columnDefinition = "TEXT")
    private String rawQuery;

    @Column(columnDefinition = "TEXT")
    private String errors;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "createdDate")
    private Date createdDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;

    public SearchLog(Query query) {
        this.site = query.getSite();
        this.query = query.getQ();
    }
}
