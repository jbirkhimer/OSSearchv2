package edu.si.ossearch.search.beans.result;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MT {

    @JacksonXmlProperty(isAttribute = true, localName = "N")
    String N;

    @JacksonXmlProperty(isAttribute = true, localName = "V")
    String V;
}