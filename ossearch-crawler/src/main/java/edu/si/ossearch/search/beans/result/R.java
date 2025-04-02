package edu.si.ossearch.search.beans.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

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
@Slf4j
@Data
@NoArgsConstructor
//@RequiredArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"U", "UE", "T", "MT", "S", "CRAWLDATE", "FS", "HAS", "HN", "LANG", "RK", "UD"})
public class R {

    @JacksonXmlProperty(isAttribute = true, localName = "N")
    Integer N;

    @JacksonXmlProperty(isAttribute = true, localName = "L")
    Integer L;

    @JacksonXmlProperty(isAttribute = true, localName = "MIME")
    String MIME;

    /**
     * The URL of the search result.
     *
     * Format: Text (Absolute URL)
     *
     * Parent: R
     * Subtags: none
     * Attributes: none
     */
    @JsonProperty("U")
    String U;

    /**
     * The URL-encoded version of the URL that is in the U parameter.
     *
     * Format: Text (URL-encoded version of the URL)
     *
     * Parent: R
     * Subtags: none
     * Attributes: none
     */
    @JsonProperty("UE")
    String UE;

    /**
     * The title of the search result.
     *
     * Format: Text (HTML)
     *
     * Parent: R
     * Subtags: none
     * Attributes: none
     */
    @JsonProperty("T")
    String T;

    /**
     * Meta tag name and value pairs obtained from the search result.
     * Only meta tags (see Meta Tags) that are requested in the search request are returned.
     *
     * Parent: R
     * Subtags: none
     * Attributes:
     *  N: Text - Name of the meta tag
     *  V: Text - Value of the meta tag
     */
    @JsonProperty("MT")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<MT> MTList = new ArrayList<>();

    /**
     * The snippet for the search result.
     * Query terms appear in bold in the results. Line breaks are included for proper text wrapping.
     * In documents larger than 300KB, snippets may not contain query terms that occur beyond the first 300KB of the document. For non-HTML documents, the 300KB limit applies to the converted version, not the original document.
     *
     * Format: Text (HTML)
     *
     * Parent: R
     * Subtags: none
     * Attributes: none
     */
    @JsonProperty("S")
    String S;

    /**
     * Additional details about the search result.
     * (zero or one instance of the subtag)
     *
     * Parent: R
     * Subtags: none
     * Attributes:
     *  NAME: Text - Name of the result descriptor
     *  VALUE: Text - Value of the result descriptor
     */
    @JsonProperty("FS")
    String FS;

}