package edu.si.ossearch.search.beans.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the navigation information for the result set.
 * The NB tag is present only if either the previous or additional results are available.
 * (zero or one instance of the subtag)
 *
 * Parent: RES
 * Subtags: PU?, NU?
 * Attributes: none
 */
@Slf4j
@Data
@NoArgsConstructor
//@RequiredArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"PU", "NU"})
public class NB {

    /**
     * Contains a relative URL pointing to the next results page.
     * The NU tag is present only when more results are available.
     *
     * (zero or one instance of the subtag)
     * Format: Text (Relative URL)
     *
     * Parent: NB
     * Subtags: none
     * Attributes: none
     */
    @JsonProperty("NU")
    String NU;

    /**
     * Contains relative URL to the previous results page.
     * The PU tag is present only if previous results are available.
     *
     * (zero or one instance of the subtag)
     * Format: Text (Relative URL)
     *
     * Parent: NB
     * Subtags: none
     * Attributes: none
     */
    @JsonProperty("PU")
    String PU;
}