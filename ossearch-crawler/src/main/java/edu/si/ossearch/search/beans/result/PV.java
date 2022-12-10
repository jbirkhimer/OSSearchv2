package edu.si.ossearch.search.beans.result;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Data
@NoArgsConstructor
//@RequiredArgsConstructor
@AllArgsConstructor
public class PV {

    @JacksonXmlProperty(isAttribute = true, localName = "V")
    String V;

    @JacksonXmlProperty(isAttribute = true, localName = "L")
    String L;

    @JacksonXmlProperty(isAttribute = true, localName = "H")
    String H;

    @JacksonXmlProperty(isAttribute = true, localName = "C")
    Integer C;

}