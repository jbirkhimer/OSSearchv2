package edu.si.ossearch.search.beans.result;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Nonnull;

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
 *
 */
@Slf4j
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class PARAM {

    @NonNull
    @JacksonXmlProperty(isAttribute = true)
    String name;

    @NonNull
    @JacksonXmlProperty(isAttribute = true)
    Object value;

    @NonNull
    @JacksonXmlProperty(isAttribute = true)
    Object original_value;

    @JacksonXmlProperty(isAttribute = true)
    Object url_escaped_value;

    @JacksonXmlProperty(isAttribute = true)
    Object js_escaped_value;

}