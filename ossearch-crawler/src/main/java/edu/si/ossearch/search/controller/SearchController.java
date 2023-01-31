package edu.si.ossearch.search.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import edu.si.ossearch.OSSearchException;
import edu.si.ossearch.search.beans.request.Paging;
import edu.si.ossearch.search.beans.request.Query;
import edu.si.ossearch.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author jbirkhimer
 */
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
//@Validated
@Controller
@RequestMapping("/search")
@Tag(description = "Search", name = "search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @Bean
    public MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter(Jackson2ObjectMapperBuilder builder) {
        XmlMapper xmlMapper = builder.createXmlMapper(true).build();
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        return new MappingJackson2XmlHttpMessageConverter(xmlMapper);
    }

    /**
     * OSSearch Search Endpoint
     * see <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/request_format.html">Google Search Appliance Documentation</a>
     *
     * @param query
     * @return
     */
    @GetMapping
    @PostMapping
    @Operation(summary = "search", responses = {@ApiResponse( content = {@Content(mediaType = "application/json"), @Content(mediaType = "application/xml"), @Content(mediaType = "text/html")})})
    public Object search(@Valid Query query, @Valid Paging paging,
                         @RequestParam(value = "hl", defaultValue = "true") Boolean useHighlighting,
                         @RequestParam(value = "edan", defaultValue = "false") Boolean edan,
                         @RequestParam(value = "edanDebug", defaultValue = "false") Boolean edanDebug) {

        try {
            return searchService.search(query, paging, useHighlighting, edan, edanDebug);
        } catch (Exception | OSSearchException e) {
            log.error("Problem with search!", e);
            return ResponseEntity.status(200).contentType(MediaType.TEXT_HTML).body("No response available. Error: " + e.getMessage());
        }
    }

    /**
     * OSSearch Search Endpoint
     * see <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/request_format.html">Google Search Appliance Documentation</a>
     *
     * @param searchRequest
     * @return
     */
    /*@GetMapping(value = "/")
    @Operation(summary = "search", responses = {@ApiResponse( content = {@Content(mediaType = "application/json"), @Content(mediaType = "application/xml"), @Content(mediaType = "text/html")})})
    public ResponseEntity<Object> search(@Valid SearchRequestDTO searchRequest) {

        SearchResponseDTO answer = searchService.search(searchRequest);

        return ResponseEntity.ok(Optional.of(answer.toString()).get());
    }*/

    /**
     * OSSearch Search Endpoint
     * see <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/request_format.html">Google Search Appliance Documentation</a>
     *
     * @param q Search query as entered by the user.
     *          See <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/request_format.html#1076993">Query Terms</a> for additional query features.
     *
     *
     *          Default value: N/A
     *
     * @param site Required parameter. Limits search results to the contents of the specified collection.
     *
     *             If this parameter does not have a valid value, other parameters in the query string do not work as expected.
     *             Omitting this parameter from a search query causes the entire search index to be queried instead of limiting search results.
     *
     *             If this parameter contains characters that are not allowed, the search appliance does not return any results for the query.
     *             This parameter allows . _ - and | .
     *
     *             You can search multiple collections by separating collection names with the OR character,
     *             which is notated as the pipe symbol, or the AND character, which is notated as a period.
     *
     *             The following example uses the AND character: &site=col1.col2
     *             The following example uses the OR character: &site=col1|col2
     *
     *             Query terms info, link and cache ignore collection restrictions that are specified by the site query parameter.
     *
     *             The site parameter is required for Advanced Search Reporting.
     *
     * @param client Required parameter. If this parameter does not have a valid value, other parameters in the query string do not work as expected.
     *               A string that indicates a valid front end and the policies defined for it, including KeyMatches,
     *               related queries, filters, remove URLs, and OneBox Modules.
     *               Notice that the rendering of the front end is determined by the proxystylesheet parameter. Example: client=myfrontend
     *
     * @param getfields Indicates that the names and values of the specified meta tags should be returned with each search result, when available.
     *                  See <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/request_format.html#1089494">Meta Tags</a> for more information.
     *
     *                  Meta tag names or values must be double URL-encoded (see <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/appendices.html#1081896">Appendix B: URL Encoding</a>).
     *
     *                  Default value: Empty string
     *
     * @param output Required parameter. If this parameter does not have a valid value, other parameters in the query string do not work as expected.
     *               Selects the format of the search results. Example: output=xml
     *
     *               Allowable Values:
     *               xml_no_dtd = XML results or custom HTML (See proxystylesheet parameter for details.)
     *               xml = XML results with Google DTD reference. When you use this value, omit proxystylesheet.
     *
     * @param requiredFields Restricts the search results to documents that contain the exact meta tag names or name-value pairs.
     *                       See <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/request_format.html#1089494">Meta Tags</a> for more information.
     *                       Meta tag names or values must be double URL-encoded (see <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/appendices.html#1081896">Appendix B: URL Encoding</a>).
     *
     *                       Default value: Empty string
     *
     * @param partialFields Restricts the search results to documents with meta tags whose values contain the specified words or phrases.
     *                      See <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/request_format.html#1089494">Meta Tags</a> for more information.
     *                      Meta tag names or values must be double URL-encoded (see <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/appendices.html#1081896">Appendix B: URL Encoding</a>).
     *
     *                      Default value: Empty string
     *
     * @param proxystylesheet If the value of the output parameter is xml_no_dtd, the output format is modified by the proxystylesheet value as follows:
     *                        Omitted = Results are in XML format.
     *                        Front End Name = Results are in Custom HTML format. The XSL stylesheet associated with the specified Front End is used to transform the output.
     *
     *                        See <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/results_format.html#1078386">Custom HTML</a> for more details. Notice that a valid front end and the policies defined for it are determined by the client parameter. If the proxystylesheet value is an empty string (""), an error is returned.
     *                        Default value: N/A
     *
     * @param start Specifies the index number of the first entry in the result set that is to be returned.
     *              Use this parameter and the num parameter (see <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/request_format.html#1076882">num</a>)
     *              to implement page navigation for search results.
     *              The index number of the results is 0-based. For example:
     *              1) start=0, num=10, returns the first 10 results. These are returned by default if you do not specify values for start or num.
     *              2) start=10, num=10, returns the next 10 results.
     *
     *              The maximum number of results available for a query is 1,000, i.e., the value of the start parameter
     *              added to the value of the num parameter cannot exceed 1,000.
     *
     *              Default value: 0
     *
     * @param num Maximum number of results to include in the search results.
     *            The maximum value of this parameter is 1000. Taken together, the values of the start (see start) and
     *            num parameters determine the range of the results that are returned.
     *            The initial index point of the search results is the value of the start parameter (see start).
     *            The ending index point of the search results is the value of the start parameter (see start) plus the
     *            value of the num parameter minus 1. All index points are zero based, meaning the first result has the value 0.
     *            The actual number of results may be smaller than the requested value.
     *
     *            Default value: 10
     *
     * @param sort Specifies a sorting method. Results can be sorted by date.
     *             (See <a href="https://www.google.com/support/enterprise/static/gsa/docs/admin/current/gsa_doc_set/xml_reference/request_format.html#1077686">Sorting</a> for sort parameter format and details.)
     *
     *             <field>:<direction>:<mode>:<format>
     *             <direction>:
     *              - D = descending
     *              - A = ascending
     *             <mode>:
     *              - S = Return the 1000 most relevant results, sorted by date.
     *              - R = Get all results, sort by date, and return the first 1000 results.
     *              You can use this option when freshness is more important than relevancy. Do not use this filter if your collection contains more than 50,000 documents.
     *              - L = Return the date information for each result. No sorting is done.
     *             <format>:
     *              - d1 = The format of the value returned for each search result is set to YYYY-MM-DD.
     *              (There appear to be no other options for that last parameter)
     *
     *             Default value: Empty string
     *
     * @param btnG
     *
     * @param inMeta
     *
     * @param dirs
     *
     * @return
     *
     */
    /*@GetMapping(value = "/test")
    @Operation(summary = "search", responses = {@ApiResponse( content = {@Content(mediaType = "application/json"), @Content(mediaType = "application/xml"), @Content(mediaType = "text/html")})})
    public ResponseEntity<Object> searchTest(@RequestParam(value = "q", required = true, defaultValue = "*") String q,
                                                              @RequestParam(value = "site", required = true) String site,
                                                              @RequestParam(value = "client", required = true) String client,
                                                              @Parameter(name = "output", required = true, schema = @Schema(type = "string", allowableValues = {"html", "xml", "xml_no_dtd", "json"}, defaultValue = "xml")) String output,
                                                              @RequestParam(value = "getfields") String getfields,
                                                              @RequestParam(value = "requiredFields") List<String> requiredFields,
                                                              @RequestParam(value = "partialFields") String partialFields,
                                                              @RequestParam(value = "proxystylesheet") String proxystylesheet,
                                                              @RequestParam(value = "start", defaultValue = "0") Integer start,
                                                              @RequestParam(value = "num", defaultValue = "10") Integer num,
                                                              @RequestParam(value = "sort") String sort,
                                                              @RequestParam(value = "btnG") String btnG,
                                                              @RequestParam(value = "inMeta") String inMeta,
                                                              @RequestParam(value = "dirs") List<String> dirs
    ) {

        JSONObject answer = new JSONObject();
        //JSONObject answer = searchService.search(q, site, client, output, getfields, requiredFields, partialFields, proxystylesheet, start, num, sort, btnG, inMeta, dirs);





        return ResponseEntity.ok(Optional.of(answer.toString()).get());
    }*/
}
