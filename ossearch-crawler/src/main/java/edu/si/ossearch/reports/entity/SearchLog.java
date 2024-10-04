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
    @Column(columnDefinition = "SMALLINT")
    private Integer collectionId;

    @Column(columnDefinition = "VARCHAR(50)")
    private String site;

//    @Column(columnDefinition = "VARCHAR(50)")
//    private String client;

    private String query;

//    @Column(columnDefinition = "VARCHAR(50)")
//    private String fields;

//    @Column(columnDefinition = "TINYINT")
//    @Column(columnDefinition = "MEDIUMINT")
//    private Integer numRows;

//    @Column(columnDefinition = "MEDIUMINT")
//    private Integer start;

//    @JsonProperty(value = "responseType")
//    @Column(columnDefinition = "VARCHAR(50)")
//    private String responseType;

//    @Column(columnDefinition = "MEDIUMINT")
//    private Integer pageNumber;

    @JsonProperty(value = "docsFound")
    @Column(columnDefinition = "INT")
    private Integer docsFound;

    @Column(columnDefinition = "SMALLINT")
    private Integer queryTime;

    @JsonProperty(value = "elapsedTime")
    @Column(columnDefinition = "MEDIUMINT")
    private Integer elapsedTime;

    private String rawQuery;

//    @Column(columnDefinition = "TEXT")
//    private String headers;

//    @JsonProperty(value = "requestIp")
//    @Column(columnDefinition = "VARCHAR(50)")
//    private String requestIp;

//    @Column(columnDefinition = "TEXT")
//    private String solrQuery;
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
//        this.client = query.getClient();
        this.query = query.getQ();
//        this.fields = query.getGetfields();
//        this.numRows = query.getNum();
//        this.start = query.getStart();
//        this.responseType = query.getOutput();
    }
}
