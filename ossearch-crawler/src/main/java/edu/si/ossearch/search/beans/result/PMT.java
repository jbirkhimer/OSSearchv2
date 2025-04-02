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

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

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
@Slf4j
@Data
@NoArgsConstructor
//@RequiredArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"NM", "DN", "IR", "T"})
public class PMT {

    @JacksonXmlProperty(isAttribute = true, localName = "DN")
    String DN;

    @JacksonXmlProperty(isAttribute = true, localName = "IR")
    Integer IR;

    @JacksonXmlProperty(isAttribute = true, localName = "NM")
    String NM;

    @JacksonXmlProperty(isAttribute = true, localName = "T")
    Integer T;

    /**
     * Encapsulates one value count information.
     * (one or more instances of the subtag)
     *
     * Parent: PARM
     * Subtags: PV+
     * Attributes:
     *  V: Text - Value (empty for range attributes)
     *  L: Text - Contains low range value (empty for non-range attribute)
     *  H: Text - Contains high range value (empty for non-range attribute)
     *  C: Text (Integer) - Doc count matching this value or under this range
     */
    @JsonProperty("PV")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<PV> PVList = new ArrayList<>();
}