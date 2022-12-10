package edu.si.ossearch.search.beans.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the set of all search results.
 * (zero or one instance of the subtag)
 *
 * Parent: GSP
 * Subtags: FI?, M, NB?, PARM?, R*, XT?
 * Attributes:
 *  SN: Text (Integer) - The index (1-based) of the first search result returned in this result set.
 *  EN: Text (Integer) - Indicates the index (1-based) of the last search result returned in this result set.
 */
@Slf4j
@Data
@NoArgsConstructor
//@RequiredArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"M", "NB", "R", "PARM"})
public class RES {

    /**
     * The index (1-based) of the first search result returned in this result set.
     *
     * Format: Text (Integer)
     *
     * Parent: RES
     * Subtags: none
     * Attributes: none
     */
    @JacksonXmlProperty(isAttribute = true, localName = "SN")
    Integer SN;

    /**
     * Indicates the index (1-based) of the last search result returned in this result set.
     *
     * Format: Text (Integer)
     *
     * Parent: RES
     * Subtags: none
     * Attributes: none
     */
    @JacksonXmlProperty(isAttribute = true, localName = "EN")
    Integer EN;


    /**
     * The estimated total number of results for the search.
     * The estimate of the total number of results for a search can be too high or too low.
     * See Appendix A: Estimated vs. Actual Number of Results.
     *
     * Format: Text (Integer)
     *
     * Parent: RES
     * Subtags: none
     * Attributes: none
     */
    @JsonProperty("M")
    @JacksonXmlElementWrapper(useWrapping = false)
    Integer M;

    /**
     * Encapsulates the navigation information for the result set.
     * The NB tag is present only if either the previous or additional results are available.
     * (zero or one instance of the subtag)
     *
     * Parent: RES
     * Subtags: PU?, NU?
     * Attributes: none
     */
    @JsonProperty("NB")
    NB NB;

    /**
     * Encapsulates the details of an individual search result.
     * (zero or more instance of the subtag)
     *
     * Parent: RES
     * Subtags: CRAWLDATE, FS?, HAS, HN?, LANG, MT*, RK, S?, T?, U, UD, UE
     * Attributes:
     *  N: Text (Integer) - The index number (1-based) of this search result.
     *  L: Text (Integer) - The recommended indentation level of the results.
     *      This value is 1 unless Duplicate Directory Filtering occurs (see Automatic Filtering).
     *      In this case, the second directory result has a value of 2.
     *  MIME: Text - The MIME type of the search result.
     */
    @JsonProperty("R")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<R> RList = new ArrayList<>();

    /**
     * Encapsulates all dynamic navigation results.
     * (zero or one instance of the subtag)
     *
     * Parent: RES
     * Subtags: PC, PMT*
     * Attributes: none
     */
    @JsonProperty("PARM")
    PARM PARM;
}