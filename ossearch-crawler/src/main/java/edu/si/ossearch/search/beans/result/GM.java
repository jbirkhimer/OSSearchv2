package edu.si.ossearch.search.beans.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a single KeyMatch result.
 * (zero or more instances of this subtag)
 *
 * Parent: GSP
 * Subtags: GL, GD?
 * Attributes: none
 */
@Slf4j
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"GL", "GD"})
public class GM {

    /**
     * Contains the URL of a KeyMatch result.
     * Format: Text (URL)
     *
     * Parent: GM
     * Subtags: none
     * Attributes: none
     */
    @NonNull
    @JsonProperty("GL")
    String GL;

    /**
     * Contains the Image URL of a KeyMatch result.
     * Format: Text (URL)
     *
     * Parent: GM
     * Subtags: none
     * Attributes: none
     */
    @NonNull
    @JsonProperty("GI")
    String GI;

    /**
     * Contains the description of a KeyMatch result.
     * Format: Text (HTML)
     *
     * Parent: GM
     * Subtags: none
     * Attributes: none
     */
    @JsonProperty("GD")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<String> GD = new ArrayList<>();

    @JsonIgnore
    String type;
}