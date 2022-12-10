package edu.si.ossearch.search.beans.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GSP = “Google Search Protocol”
 * Encapsulates all data that is returned in the Google XML search results.
 * Format: This is the root element.
 *
 * Subtags: (CT?, CUSTOM?, ENTOBRESULTS, GM*, PARAM+, Q, RES?, Spelling?, Synonyms?, TM) | CACHE
 * Attributes:
 *  VER: Text - Indicates version of the search results output. The current output version is “3.2”.
 *
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"Q", "PARAM", "GM", "RES"/*, "CT", "CUSTOM", "ENTOBRESULTS", "Spelling", "Synonyms", "TM"*/})
public class GSP {

    @JacksonXmlProperty(isAttribute = true, localName = "VER")
    String VER = "3.2";

    /**
     * The search query terms submitted to the Google search appliance to generate these results.
     * Format: HTML
     *
     * Parent: GSP
     * Subtags: none
     * Attributes: none
     */
    @JsonProperty("Q")
    String Q;

    /**
     * The search request parameters that were submitted to the Google Search Appliance to generate these results.
     * (one or more instances of this subtag)
     *
     * Parent: GSP
     * Subtags: none
     * Attributes:
     *  name: Text - Name of the input parameter
     *  value: HTML - formatted version of the input parameter value
     *  original_value: Text - Original URL-encoded version of the input parameter value
     */
    @JsonProperty("PARAM")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<PARAM> PARAM = new ArrayList<>();

    /**
     * Encapsulates a single KeyMatch result.
     * (zero or more instances of this subtag)
     *
     * Parent: GSP
     * Subtags: GL, GD?
     * Attributes: none
     */
    @JsonProperty("GM")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<GM> GM = new ArrayList<>();

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
    @JsonProperty("RES")
    RES RES;

//    /**
//     * Search Comments Example comment: Sorry, no content found for this URL
//     * (zero or one instance of this subtag)
//     * Format: HTML
//     *
//     * Parent: GSP
//     * Subtags: (Custom XML specified in the search request)
//     * Attributes: none
//     */
////    @JsonProperty("CT")
//    String CT;
//
//    /**
//     * Encapsulates custom XML tags that are specified in the proxycustom input parameter.
//     * (zero or one instance of this subtag)
//     *
//     * Parent: GSP
//     * Subtags: none
//     * Attributes: none
//     */
//    //@JsonProperty("CUSTOM")
//    String CUSTOM;
//
//    /**
//     * Encapsulates the results returned by OneBox modules.
//     *
//     * Parent: GSP
//     * Subtags: OBRES
//     * Attributes: none
//     */
//    //@JsonProperty("ENTOBRESULTS")
//    String ENTOBRESULTS;
//
//    /**
//     * Encapsulates alternate spelling suggestions for the submitted query. Only one spelling suggestion is returned at this time.
//     * (zero or one instance of this subtag)
//     *
//     * Parent: GSP
//     * Subtags: Suggestion+
//     * Attributes: none
//     */
//    //@JsonProperty("Spelling")
//    String Spelling;
//
//    /**
//     * Encapsulates the related queries for the submitted query. Up to 20 related queries may be returned, depending on the related queries list that is associated with the front end.
//     * (zero or one instance of this subtag)
//     *
//     * Parent: GSP
//     * Subtags: OneSynonym+
//     * Attributes: none
//     */
//    //@JsonProperty("Synonyms")
//    String Synonyms;
//
//    /**
//     * Total server time to return search results, measured in seconds.
//     * Format: Text (Floating-point number)
//     *
//     * Parent: GSP
//     * Subtags: none
//     * Attributes: none
//     */
//    //@JsonProperty("TM")
//    Float TM;
}