package edu.si.ossearch.reports.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.si.ossearch.search.beans.request.Query;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
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
@Table(name = "search_log")
public class SearchLog implements Serializable {

    private static final long serialVersionUID = -7272242479560896738L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "collectionId")
    private Long collectionId;

    private String site;

    private String client;

    private String query;

    private String fields;

    private Integer numRows;

    private Long start;

    @JsonProperty(value = "responseType")
    private String responseType;

    private Integer pageNumber;

    @JsonProperty(value = "docsFound")
    private Long docsFound;

    private Integer queryTime;

    @JsonProperty(value = "elapsedTime")
    private Long elapsedTime;

    @Column(columnDefinition = "TEXT")
    private String rawQuery;

    @Column(columnDefinition = "TEXT")
    private String headers;

    @JsonProperty(value = "requestIp")
    private String requestIp;

    @Column(columnDefinition = "TEXT")
    private String solrQuery;

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
        this.client = query.getClient();
        this.query = query.getQ();
        this.fields = query.getGetfields();
        this.numRows = query.getNum();
        this.start = query.getStart();
        this.responseType = query.getOutput();
    }
}
