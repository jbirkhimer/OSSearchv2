package edu.si.ossearch.search.beans.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates all dynamic navigation results.
 * (zero or one instance of the subtag)
 *
 * Parent: RES
 * Subtags: PC, PMT*
 * Attributes: none
 */
@Slf4j
@Data
@NoArgsConstructor
//@RequiredArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"U", "UE", "T", "MT", "S", "CRAWLDATE", "FS", "HAS", "HN", "LANG", "RK", "UD"})
public class PARM {

    /**
     * Indicates whether the counts are exact or partial. 0-exact, 1-partial.
     *
     * Format: Text (Integer 0 or 1)
     *
     * Parent: PARM
     * Subtags: none
     * Attributes: none
     */
    @JsonProperty("PC")
    Integer PC;

    /**
     * Encapsulates results for one attribute. A maximum of 5k values (PV) are returned after sorting all by count or value as configured and discarding the rest.
     * (zero or more instances of the subtag)
     *
     * Parent: PARM
     * Subtags: PV+
     * Attributes:
     *  NM: Text - Metatag name
     *  DN: Text - Display name
     *  IR: Text - (Integer) Attribute is range type (1) or not (0)
     *  T: Text (Integer) - Attribute type: 0-String, 1-Integer, 2-Float, 3-Currency, 4-Date
     */
    @JsonProperty("PMT")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<PMT> PMTList = new ArrayList<>();
}