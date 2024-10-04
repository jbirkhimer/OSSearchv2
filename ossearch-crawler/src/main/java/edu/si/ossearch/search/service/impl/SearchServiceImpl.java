package edu.si.ossearch.search.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import edu.si.ossearch.OSSearchException;
import edu.si.ossearch.collection.entity.Collection;
import edu.si.ossearch.collection.entity.DynamicNavigation;
import edu.si.ossearch.collection.entity.Keymatch;
import edu.si.ossearch.collection.entity.PageResult;
import edu.si.ossearch.collection.repository.CollectionRepository;
import edu.si.ossearch.edan.EdanResponse;
import edu.si.ossearch.edan.api.client.EdanClient;
import edu.si.ossearch.edan.util.EdanUtils;
import edu.si.ossearch.search.beans.result.*;
import edu.si.ossearch.search.beans.request.Paging;
import edu.si.ossearch.search.beans.request.Query;
import edu.si.ossearch.reports.entity.SearchLog;
import edu.si.ossearch.reports.repository.SearchLogRepository;
import edu.si.ossearch.search.service.*;
import edu.si.ossearch.search.util.http.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.si.ossearch.collection.entity.DynamicNavigation.NavigationType;
import static edu.si.ossearch.collection.entity.DynamicNavigation.NavigationType.inMeta;
import static edu.si.ossearch.collection.entity.DynamicNavigation.SortBy.count;
import static edu.si.ossearch.collection.entity.DynamicNavigation.SortType.asc;
import static edu.si.ossearch.collection.entity.Keymatch.KeymatchType.*;
import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;

/**
 * @author jbirkhimer
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Value(value = "${spring.data.solr.collection}")
    @NonNull
    String solrCollection;

    @Value(value = "${ossearch.disable-searchlog-db-updates}")
    @NonNull
    boolean disableSearchLogDbUpdates;

    @Autowired
    @Qualifier("slave")
    private SolrClient solrClient;

    @Autowired
    private EdanClient edanClient;

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    SearchLogRepository searchLogRepository;

    @Autowired
    SearchMetaTagService searchMetaTagService;

    @Autowired
    SearchPageResultHelperService pageResultHelperService;

    @Autowired
    QueryFeatures queryFeatures;

    static String[] specialTerms = {"allinanchor", "allintext", "allintitle", "allinurl", "cache", "daterange", "ext", "-ext", "filetype", "-filetype", "inmeta", "intext", "intitle", "inurl", "info", "link", "site", "wildcard"};

    static Map<String, String> specialTermFieldMapping;
    static {
        specialTermFieldMapping = new HashMap<>();
        specialTermFieldMapping.put("allinanchor", "anchor");
        specialTermFieldMapping.put("allintext", "title,content");
        specialTermFieldMapping.put("allintitle", "title");
        specialTermFieldMapping.put("allinurl", "url");

        specialTermFieldMapping.put("intext", "title,content");
        specialTermFieldMapping.put("intitle", "title");
        specialTermFieldMapping.put("inurl", "url");

        specialTermFieldMapping.put("link", "url");
        specialTermFieldMapping.put("site", "url");

        specialTermFieldMapping.put("daterange", "date");

        specialTermFieldMapping.put("filetype", "type");
    }

    private enum RangeType {
        META_TAG, DATE, NUMBER
    }

    /*@Override
    public SearchResponseDTO search(SearchRequestDTO searchRequest) throws Exception {
        SearchResponseDTO searchResponse = new SearchResponseDTO();
        return searchResponse;
    }*/


    @Override
    @Transactional
    public Object search(Query query, @Valid Paging paging, Boolean useHighlighting, Boolean edan, Boolean edanDebug) throws Exception, OSSearchException {

        //log and save request to db
        SearchLog searchLog = new SearchLog(query);
//        searchLog.setRequestIp(RequestUtils.getRemoteIP());
        searchLog.setRawQuery(RequestUtils.getRawQueryString());
        //searchLog.setHeaders(new JSONObject(RequestUtils.getHeaders()).toString());

        Optional<Collection> optionalCollection = collectionRepository.getByName(query.getSite());

        if (!optionalCollection.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        Collection collection = optionalCollection.get();

        if (queryFeatures.isSpam(query.getQ(), Math.toIntExact(collection.getId()))) {
            String rawQuery = RequestUtils.getRawQueryString();
            log.warn("Filter potential spam request! rawQuery: {}", rawQuery);
            return ResponseEntity.noContent().build();
        }

        searchLog.setCollectionId(Math.toIntExact(collection.getId()));
        if (!disableSearchLogDbUpdates) {
            searchLogRepository.save(searchLog);
        }

        List<String> outputTypes = Arrays.asList("html", "xml", "xml_no_dtd", "json");
        if (!outputTypes.contains(query.getOutput())) {
            searchLog.setErrors("No Output Type of " + query.getOutput());
            if (!disableSearchLogDbUpdates) {
                searchLogRepository.saveAndFlush(searchLog);
            }
            return ResponseEntity.status(200).contentType(MediaType.TEXT_HTML).body("No response available.");
        }

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setFacet(true);
        solrQuery.setFacetMinCount(1);
        //solrQuery.setFacetLimit(-1);

        List<String> collectionIdFq = new ArrayList();
        collectionIdFq.add(String.valueOf(collection.getId()));
        if (!collection.getIncludedCollections().isEmpty()) {
            collection.getIncludedCollections().forEach(includeedCollection -> collectionIdFq.add(String.valueOf(includeedCollection.getId())));
        }
        Collector<CharSequence, ?, String> collector = (collectionIdFq.size() == 1) ? Collectors.joining(" ") : Collectors.joining(" ", "(", ")");
        solrQuery.addFilterQuery("collectionID:" + collectionIdFq.stream().collect(collector));

        processPagingStartAndRows(solrQuery, query, paging, collection);

        processQueryTerms(solrQuery, query);

        processInmeta(solrQuery, query.getInmeta());

        processGetFields(solrQuery, query.getGetfields(), collection.getIncludeFields());

        // Decipher the incoming sort request
        processSort(solrQuery, query.getSort());

        // Decipher the incoming dir request
        processSubPathSearching(solrQuery, query.getDirs());

        // return any dynamicNavigation.attributeName's matches for requiredFields.metaFieldMap.key's
        processFilterQueries(solrQuery, query.getRequiredfields(), false);

        // return any dynamicNavigation.attributeName's matches for partialFields.metaFieldMap.key's
        processFilterQueries(solrQuery, query.getPartialfields(), true);

        if (collection.getUseFacets()) {
            processFacets(solrQuery, collection.getDynamicNavigations());
        }

        //TODO: ?????
        // proxycustom -> {<HOME/>,<ADVANCED/>,<TEST/>}
        // proxyreload -> force reload of serverside stylesheet (else default reloaded after 15 mins)
        // proxystylesheet -> if output==xml_no_dtd then omitted
        // ie -> input encoding
        // oe -> output encoding
        // client -> string specifying valid frontend (default -needs to be speficied- is default_frontend)

        if (useHighlighting) {
            solrQuery.setHighlight(true);
            solrQuery.addHighlightField("title");
            solrQuery.addHighlightField("content");
//            solrQuery.setHighlightSimplePre("<b>");
//            solrQuery.setHighlightSimplePost("</b>");
//            solrQuery.setParam("hl.tag.pre", "<b>");
//            solrQuery.setParam("hl.tag.post", "</b>");
            solrQuery.add("hl.fragmentsBuilder", "colored");
            solrQuery.add("hl.method", "fastVector");
            solrQuery.setHighlightFragsize(120);
            solrQuery.add("hl.usePhraseHighlighter","true");
        }

        QueryResponse rsp;
        if (!edan) {
            try {
                log.info("search request solr query: {}", solrQuery.jsonStr());
                //searchLog.setSolrQuery(solrQuery.jsonStr());
                rsp = solrClient.query(solrCollection, solrQuery);
                searchLog.setDocsFound(Math.toIntExact(rsp.getResults().getNumFound()));
                searchLog.setQueryTime(rsp.getQTime());
                searchLog.setElapsedTime(Math.toIntExact(rsp.getElapsedTime()));
                if (!disableSearchLogDbUpdates) {
                    searchLogRepository.save(searchLog);
                }
            } catch (SolrException | IOException | SolrServerException e) {
                searchLog.setErrors(e.getMessage());
                if (!disableSearchLogDbUpdates) {
                    searchLogRepository.save(searchLog);
                }
                String message = e.getMessage();
                if (message.contains("org.apache.solr.search.SyntaxError")) {
                    message = StringUtils.substringAfter(message, "SyntaxError: "); // message.substring(message.indexOf("Cannot parse"), message.length());
                    message = StringEscapeUtils.escapeHtml4(message);
                }
                throw new OSSearchException(message);
            }
        } else {
            try {
                MultiValueMap<String, String> edanRequest = EdanUtils.getMultiValueMap(solrQuery, useHighlighting);
                URI uri = edanClient.getURI(edanRequest);
                log.info("search request edan query: {}", uri.getQuery());
                //searchLog.setSolrQuery(uri.getQuery());
                EdanResponse edanResponse = edanClient.sendRequest(uri);
                List<String> fieldsFilter = Arrays.asList(solrQuery.getFields().split(","));
                //Collections.addAll(fieldsFilter, solrQuery.getFacetFields());
                rsp = EdanUtils.convertQueryResponse(edanRequest, edanResponse, fieldsFilter);
                searchLog.setDocsFound(Math.toIntExact(rsp.getResults().getNumFound()));
                searchLog.setQueryTime(rsp.getQTime());
                searchLog.setElapsedTime(Math.toIntExact(rsp.getElapsedTime()));
                if (!disableSearchLogDbUpdates) {
                    searchLogRepository.save(searchLog);
                }
            } catch (Exception e) {
                searchLog.setErrors(e.getMessage());
                if (!disableSearchLogDbUpdates) {
                    searchLogRepository.save(searchLog);
                }
                throw new OSSearchException(e.getMessage());
            }
        }

        log.info("search request finished: {}", searchLog);

        if (!disableSearchLogDbUpdates) {
            searchLogRepository.saveAndFlush(searchLog);
        }

        if (query.getOutput() == null || query.getOutput().isEmpty() || query.getOutput().equals("html")) {
            return createHtmlResponse(rsp, query, paging, collection, edan, searchLog);
        } else if (query.getOutput().equals("xml") || query.getOutput().equals("xml_no_dtd")) {
            return createXmlResponse(rsp, query, paging, collection, searchLog, edan, RequestUtils.getRemoteIP());
        } else if (query.getOutput().equals("json")) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(rsp.getResults());
        }

        return ResponseEntity.status(200).contentType(MediaType.TEXT_HTML).body("No response available.");
    }

    private void processPagingStartAndRows(SolrQuery solrQuery, Query query, Paging paging, Collection collection) throws Exception {

        int startIndex = query.getStart() < 0 ? 0 : Math.toIntExact(query.getStart());
        int pageSize = (query.getNum() <= 0 || query.getNum() > 200)
                        ? ((collection.getResultLimit() < 200)
                            ? ((collection.getResultLimit() <= 0)
                                ? 200
                                : collection.getResultLimit())
                            : 200)
                        : query.getNum();

        /*if (paging.getSubmit() != null && !paging.getSubmit().isEmpty()) {
            String submit = paging.getSubmit();

            if (submit.equals("< Prev")) {
                startIndex = startIndex - pageSize;
            } else if (submit.equals("Next >")) {
                startIndex = startIndex + pageSize;
            } else if (StringUtils.isNumeric(submit)) {
                startIndex = Integer.parseInt(submit) * pageSize;
            }
        }*/

        solrQuery.setStart(startIndex);
        solrQuery.setRows(pageSize);
    }

    private ResponseEntity<Object> createXmlResponse(QueryResponse rsp, Query query, Paging paging, Collection collection, SearchLog searchLog, Boolean edan, String remoteIP) throws Exception {

        GSP gsp = new GSP();
        gsp.setQ(query.getQ());

        List<PARAM> params = gsp.getPARAM();
        //String q = StringEscapeUtils.escapeHtml4(query.getQ());
        String q = query.getQ();
        params.add(new PARAM("q", q, q));
        if (query.getOutput().equals("xml_no_dtd")) {
            params.add(new PARAM("num", query.getNum(), query.getNum(), query.getNum(), query.getNum()));
            params.add(new PARAM("start", query.getStart(), query.getStart(), query.getStart(), query.getStart()));
        }
        if (query.getRequiredfields() != null && !query.getRequiredfields().isEmpty()) {
            params.add(new PARAM("requiredfields", query.getRequiredfields(), query.getRequiredfields()));
        }
        params.add(new PARAM("site", query.getSite(), query.getSite()));
        params.add(new PARAM("output", query.getOutput(), query.getOutput(), query.getOutput(), query.getOutput()));
        params.add(new PARAM("proxyreload", query.isProxyreload() ? 1 : 0, query.isProxyreload() ? 1 : 0));
        params.add(new PARAM("oe", query.getOe(), query.getOe(), query.getOe(), query.getOe()));
        params.add(new PARAM("ie", query.getIe(), query.getIe(), query.getIe(), query.getIe()));
        params.add(new PARAM("ip", remoteIP, remoteIP));

        //Check q string for any keymatch.SearchTerm's that wee need to add to the results
        processKeymatch(gsp.getGM(), query.getQ(), collection.getKeymatches());

        //TODO: for testing keymatches
        /*if (gsp.getGM().isEmpty()) {
            gsp.getGM().add(new GM("url_1", Arrays.asList("desc_1")));
            gsp.getGM().add(new GM("url_2", Arrays.asList("desc_2")));
        }*/

        RES res = new RES();

        SolrDocumentList solrResults = rsp.getResults();
        int sn = Math.toIntExact(solrResults.getStart())+1;
        int en = sn + solrResults.size();

        res.setSN(sn);
        res.setEN(en-1);

        res.setM(Math.toIntExact(solrResults.getNumFound()));

        //TODO update start param for NU/PU
        NB nb = new NB();
        nb.setNU("/search?"+searchLog.getRawQuery());
        //nb.setPU("/search?"+searchLog.getRawQuery()); //TODO: do we want PU

        res.setNB(nb);

        Map<String, Map<String, List<String>>> highlighting = rsp.getHighlighting() != null ? rsp.getHighlighting() : new HashMap<>();

        for (SolrDocument doc : solrResults) {

            Map<String, List<String>> docHighlighting = highlighting.getOrDefault((String) doc.getFieldValue("id"), null);

            R r = new R();
            r.setN(solrResults.indexOf(doc) + sn);

            String url = (String) doc.getFieldValue("url");
            url = url != null && !url.isEmpty() ? url : "No url found";
            r.setU(url);
            r.setUE(url); //TODO: handle url encoding for UE

            //TODO: handle title length, special char's and anything else we need to do
            String title = docHighlighting != null && docHighlighting.containsKey("title") && !docHighlighting.get("title").isEmpty()
                    ? docHighlighting.get("title").get(0)
                    : (String) doc.getFieldValue("title");
            title = title != null && !title.isEmpty()
                    //? StringEscapeUtils.escapeHtml4(title)
                    ? title
                    : "No title found...";
            r.setT(title);

            String content = docHighlighting != null && docHighlighting.containsKey("content") && !docHighlighting.get("content").isEmpty()
                    ? "..." + docHighlighting.get("content").get(0) + "..."
                    : StringUtils.abbreviate((String) doc.getFieldValue("content"), 123);
            //content = content != null && !content.isEmpty() ? StringEscapeUtils.escapeHtml4(content) : "No content found";
            content = content != null && !content.isEmpty() ? content : "No content found...";
            r.setS(content);


            //TODO: add FS element with sort field if applicable

            List<String> allMetatags = doc.getFieldNames().contains("meta__all_metatags")
                    ? doc.getFieldValues("meta__all_metatags").stream()
                        .map(object -> Objects.toString(object, null))
                        .map(field -> field.replace(" ", "__").replace(":", "_"))
                        .collect(Collectors.toList())
                    : new ArrayList<>();

            doc.getFieldNames().stream()
                    .sorted(String::compareToIgnoreCase)
                    .filter(field -> allMetatags.contains(field) || field.equals("topicids"))
                    .forEach(field -> {
                            //.replace("image_", "image:").replace("fb_", "fb:").replace("og_", "og:").replace("twitter_", "twitter:").replace("__", " ")
                            String n = field.replaceAll("meta_", "").replace("__", " ").replace("_", ":");
                            String v = doc.getFieldValues(field).stream().map(p -> String.valueOf(p).trim()).collect(Collectors.joining(","));
                            //v = StringEscapeUtils.escapeHtml4(v);
                            MT mt = new MT(n, v);
                            r.getMTList().add(mt);
                    });

            res.getRList().add(r);
        }

        gsp.setRES(res);

        PARM parm = new PARM();
        parm.setPC(0);

        collection.getDynamicNavigations().stream()
                .filter(dynamicNavigation -> dynamicNavigation.getNavigationType() != inMeta)
                .sorted(Comparator.comparing(DynamicNavigation::getDisplayName, String::compareToIgnoreCase))
                .forEach(dynamicNavigation -> {

                    PMT pmt = new PMT();
                    pmt.setNM(dynamicNavigation.getAttributeName());
                    pmt.setDN(dynamicNavigation.getDisplayName());
                    pmt.setIR(0);
                    pmt.setT(0);

                    String solrFieldName = searchMetaTagService.getMetaTagMapping()
                            .getOrDefault(dynamicNavigation.getAttributeName(), dynamicNavigation.getAttributeName());

                    Comparator<Entry<String, Long>> sortType = dynamicNavigation.getSortBy() == count
                            ? Entry.<String, Long>comparingByValue()
                            .thenComparing(
                                    dynamicNavigation.getSort() == asc ? comparingByKey(String::compareToIgnoreCase) : reverseOrder(comparingByKey(String::compareToIgnoreCase))
                            )
                            : Entry.<String, Long>comparingByKey(String::compareToIgnoreCase).thenComparing(comparingByValue());

                    sortType = dynamicNavigation.getSort() == asc ? sortType : reverseOrder(sortType);

                    /*LinkedHashMap<String, Long> resultSet =
                            rsp.getFacetField(solrFieldName)
                                    .getValues().stream()
                                    .collect(Collectors.toMap(FacetField.Count::getName, FacetField.Count::getCount)).entrySet().stream()
                                    .sorted(Map.Entry.<String, Long>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));*/

                    if (edan) {
                        solrFieldName = solrFieldName.toLowerCase();
                    }

                    if (rsp.getFacetField(solrFieldName) != null) {
                        rsp.getFacetField(solrFieldName)
                                .getValues().stream()
                                .collect(Collectors.toMap(FacetField.Count::getName, FacetField.Count::getCount))
                                .entrySet().stream()
                                .sorted(sortType)
                                .forEach(entry -> {
                                    PV pv = new PV();
                                    pv.setV((String) ((Entry<?, ?>) entry).getKey());
                                    pv.setC(((Long) ((Entry<?, ?>) entry).getValue()).intValue());
                                    pmt.getPVList().add(pv);
                                });
                    }

                    if (pmt.getPVList().size() > 0) {
                        parm.getPMTList().add(pmt);
                    }
                });

        res.setPARM(parm);

        return ResponseEntity.ok().contentType(MediaType.TEXT_XML).body(gsp);
    }

    private Object createHtmlResponse(QueryResponse rsp, Query query, Paging paging, Collection collection, Boolean edan, SearchLog searchLog) throws Exception {

        PageResult pageResult = collection.getPageResults().stream()
                .filter(pageresult -> pageresult.getName().equals(query.getClient()))
                .findFirst()
                .orElse(null);

        List<GM> keymatches = new ArrayList<>();
        processKeymatch(keymatches, query.getQ(), collection.getKeymatches());

        String htmlContent = pageResultHelperService.generatePageResult(query, pageResult, paging, rsp, keymatches, edan);

        return ResponseEntity.status(200).contentType(MediaType.TEXT_HTML).body(htmlContent);
    }

    //TODO: only meta_ fields, default fields, and includeFields
    private void processGetFields(SolrQuery solrQuery, String getFields, Set<String> includeFields) throws Exception {
        if (getFields != null && getFields.equals("*")) {
            solrQuery.addField("*");
        } else {
            Set<String> fields = new HashSet<>();
            fields.addAll(Arrays.asList("id", "host", "meta__all_metatags"));
            fields.addAll(includeFields);

            if (getFields != null && !getFields.isEmpty()) {
                for (String field : getFields.split("\\.")) {
                    field = URLDecoder.decode(field, "utf-8");
                    field = field.equals("topicids") ? field : searchMetaTagService.getMetaTagMapping().get(field);
                    if (field != null && !field.isEmpty()) {
                        fields.add(field);
                    }
                }
            }

            fields.forEach(solrQuery::addField);
        }

        String solrFields = String.join(",", Arrays.stream(solrQuery.getFields().split(",")).sorted().collect(Collectors.toList()));

        /*Set<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("id", "host", "meta__all_metatags"));
        fields.addAll(includeFields);

        if (getFields != null && !getFields.isEmpty()) {
            if (getFields.equals("*")) {
                solrQuery.setFields("*");
            } else {
                for (String field : getFields.split("\\.")) {
                    field = URLDecoder.decode(field, "utf-8");
                    field = searchMetaTagService.getMetaTagMapping().get(field);
                    if (field != null && !field.isEmpty()) {
                        fields.add(field);
                    }
                }
            }
        }

        fields.stream()
                .map(field -> searchMetaTagService.getMetaTagMapping().getOrDefault(field, field))
                .forEach(solrQuery::addField);*/
    }

    /**
     * Handle inmeta and other special query terms also handle any solr fields, boolean operators, phrase searches, and grouping
     * special query terms:
     *  allinanchor, allintext, allintitle, allinurl, cache, daterange, ext, filetype, inmeta, intext, intitle, inurl, info, link, site, wildcard
     *
     * ex. q=(search terms AND "phrase search") OR other search terms AND type:jpg inmeta:gEra:1400..1930
     *
     */
    private void processQueryTerms(SolrQuery solrQuery, Query q) throws Exception, OSSearchException {
        //String regex = "[^a-zA-Z0-9%-:\"/+.&*?~=]";
        //regex = "((?=" + regex + ")|(?<=" + regex + "))";

        if (q.getQ() != null && !q.getQ().isEmpty()) {

            String[] queryParts = splitOnWordList(q.getQ(), specialTerms, "(?<=^|\\s|$)(?=", ")");

            log.debug("q split: {}", Arrays.toString(queryParts));

            for (String queryPart : queryParts) {
                if (stringContainsItemFromList(queryPart, specialTerms)) {
                    parseSpecialTerms(solrQuery, queryPart);
                } else {
                    queryPart = queryPart.trim().replaceAll("\\s(AND|OR)$", "");
                    //Escaping Common Problem Special Characters for solr
                    queryPart = queryPart.replace("\\", "\\\\");
                    queryPart = queryPart.replace("/", "\\/");
                    queryPart = queryPart.replace(":", "\\:");
                    /*queryPart = queryPart.replace("\\", "\\\\");
                    for (String chr : Arrays.asList("+", "-", "&", "|", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "?", ":", "/")) {
                        queryPart = queryPart.replace(chr, '\\' + chr);
                    }*/

                    /*queryPart = removeDanglingChars(queryPart, "'", "\"");

                    if (!isBalanced(queryPart)) {
                        throw new OSSearchException("q parentheses are not balanced");
                    }*/

                    solrQuery.setQuery(queryPart);
                }
            }
        } else {
            q.setQ("*:*");
            solrQuery.setQuery("*:*");
        }
    }

    public void parseSpecialTerms(SolrQuery solrQuery, String queryPart) throws Exception {

        String[] specialTermParts = splitOnWordList(queryPart.trim(), specialTerms, "(?:(?<=", ")\\b:)");
        log.debug("specialTerm: {}", Arrays.toString(specialTermParts));

        String specialTerm = specialTermParts[0];
        String specialTermValue = specialTermParts[1];

        String fieldName = specialTermFieldMapping.get(specialTerm);
        String[] fields = fieldName != null ? fieldName.split(",") : null;

        switch (specialTerm) {
            case "allinanchor":
            case "allinurl": // restricts the results to documents containing that word in the result URL.
            case "allintitle": // restricts the results to documents containing that word in the title.
            case "allintext": // restricts the search to documents whose titles or body text contains the search terms.
                String[] specialTermValues = splitOnWordOrPhrase(specialTermValue);
                String value = specialTermValues.length > 1 ? Arrays.stream(specialTermValues).collect(Collectors.joining(" AND ", "(", ")")) : specialTermValue;
                Arrays.stream(fields).forEach(field -> {
                    solrQuery.addFilterQuery(field + ":" + value.trim());
                    //solrQuery.addField(field);
                });
                break;

            case "intext": //restricts the search to documents that contain the search word in the titles or body text of the documents.
            case "intitle": // restricts the results to documents containing that word in the title.
            case "inurl": // restricts the results to documents containing that word in the result URL.
            case "link": // lists web pages that have links to the specified web page.
            case "filetype": // filters the results to include only documents with the specified MIME content type.
                Arrays.stream(fields).forEach(field -> {
                    solrQuery.addFilterQuery(field + ":" + specialTermValue.trim());
                    //solrQuery.addField(field);
                });
                break;

            case "daterange": // Restrict search to documents with modification dates that fall within a time frame.
                for (String field : fields) {
                    String daterangeValue = specialTermValue.contains("..") ? processRangeValues(specialTermValue) : specialTermValue;
                    solrQuery.addFilterQuery(field + ":" + daterangeValue.trim());
                    //solrQuery.addField(field);
                }
                break;

            case "cache":
                // returns the cached HTML version of the specified web document that was crawled.
                // Note there can be no space between cache: and the web page URL.
                // Words that appear in the query are highlighted in the cached document.
                // ex. cache:www.google.com web
                break;

            case "ext":
                // filters the results to include only documents with the specified file extension.
                // oss has no field for ext but we can prob use type field and map the mimetype to an extension
                break;

            case "inmeta":
                // restricts results to required or partial meta tag values in the same way as the requiredfields and partialfields search parameters.
                processInmeta(solrQuery, specialTermValue);
                break;

            case "info":
                // returns a single result for the specified URL if the URL exists in the index.
                // No other query terms can be specified when using this special query term.
                break;

            case "site":
                // ossearch uses this for collection name filtering and replaces the gsa functionality of this special term with param 'dir'
                // gsa uses this to restrict search to documents within a domain or directory. ex: site:www.google.com/enterprise/
                break;

            case "wildcard":
                break;

            default:
                log.warn("unknown term found: {}", specialTerm);
        }
    }

    private void processInmeta(SolrQuery solrQuery, String specialTermValue) throws Exception {

        if (specialTermValue != null && !specialTermValue.isEmpty()) {
            String[] inMetaParts = splitOnWordList(specialTermValue, searchMetaTagService.getMetaTagMapping().keySet().toArray(new String[0]), "(?<=(?<=", ")\\b[:=~])");

            String inmetaField = searchMetaTagService.getMetaTagMapping().get(inMetaParts[0].replaceAll("[~=:]$", ""));
            String inmetaValue = inMetaParts[1];
            boolean isPartial = inMetaParts[0].endsWith("~") ? true : false;

            StringBuilder inmetaSb = new StringBuilder();
            StringTokenizer tokenizer = new StringTokenizer(inmetaValue);
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken();
                if (token.contains("..")) {
                    inmetaSb.append(processRangeValues(token));
                } else if (token.equals("AND") || token.equals("OR")) {
                    inmetaSb.append(" " + token + " ");
                } else {
                    inmetaSb.append(token);
                }
            }

            solrQuery.addFilterQuery(isPartial ? (inmetaField + ":*" + inmetaSb.toString().trim() + "*") : (inmetaField + ":" + inmetaSb.toString().trim()));
            //solrQuery.addField(inmetaField);
        }
    }

    private void processFacets(SolrQuery solrQuery, Set<DynamicNavigation> dynamicNavigations) throws Exception {

        //TODO: apply facet sort ?????
        // You can use facet sort per field to order the different facets respectively.
        // e.g. f.<field_name>.facet.sort=count|index
        // You can only sort by predefined criteria.
        // facet.sort is what you use and it can only be:
        // - count - sort the constraints by count (highest count first)
        // - index - to return the constraints sorted in their index order (lexicographic by indexed term). For terms in the ascii range, this will be alphabetically sorted.
        // Have a look at this: https://solr.apache.org/guide/6_6/faceting.html#Faceting-Thefacet.sortParameter

        dynamicNavigations.forEach(dynamicNavigation -> {
            NavigationType navigationType = dynamicNavigation.getNavigationType();
            String attributeName = dynamicNavigation.getAttributeName();

            if (navigationType == NavigationType.requiredField) {
                solrQuery.addFacetField(attributeName.startsWith("meta_") || attributeName.equalsIgnoreCase("topicids") ? attributeName : "meta_" + attributeName);
            }

            if (navigationType == NavigationType.partialField) {
                solrQuery.addFacetField(attributeName.startsWith("meta_") || attributeName.equalsIgnoreCase("topicids") ? attributeName : "meta_" + attributeName);
            }

            if (navigationType == inMeta) {
                solrQuery.addFacetField(attributeName.startsWith("meta_") || attributeName.equalsIgnoreCase("topicids") ? attributeName : "meta_" + attributeName);
            }

        });

    }

    private void processFilterQueries(SolrQuery solrQuery, String input, boolean isPartialfields) throws Exception, OSSearchException {

        if (input !=null && !input.isEmpty()) {

            for (String field : searchMetaTagService.getMetaTagMapping().keySet()) {
                input = input.replace(field, field.replace(".", "%2E"));
            }

            if (!isBalanced(input)) {
                throw new OSSearchException("requiredfields or partialfields parentheses are not balanced");
            }

            List<String> fq = new ArrayList<>();

            input = input.replace("..", "%2E%2E");

            String regex = "[()|*.-]";

            regex = "((?=" + regex + ")|(?<=" + regex + "))";
            log.debug("regex: {}", regex);

            String[] terms = input.split(regex);
            log.debug("terms: {}", Arrays.asList(input.split(regex)));

            for (int i = 0; i < terms.length; i++) {

                String token = terms[i].trim();   // strips off any whitespace;

                switch (token) {
                    case "(":
                    case ")":
                        fq.add(token);
                        break;
                    case "-":
                        fq.add(token);
                        break;
                    case "*":
                        fq.add(token);
                        break;
                    case ".":
                        if (!fq.isEmpty() && !fq.get(fq.size()-1).equals("AND") && !fq.get(fq.size()-1).equals("OR")) {
                            fq.add("AND");
                        }
                        break;
                    case "|":
                        if (!fq.isEmpty() && !fq.get(fq.size()-1).equals("AND") && !fq.get(fq.size()-1).equals("OR")) {
                            fq.add("OR");
                        }
                        break;
                    default:

                        try {
                            String tokenDecoded = URLDecoder.decode(token, "utf-8");
                            String[] metaParts = splitOnWordList(tokenDecoded, searchMetaTagService.getMetaTagMapping().keySet().toArray(new String[0]), "(?<=(?<=", ")\\b[:=~])");

                            if (metaParts.length>1) {
                                isPartialfields = metaParts[0].endsWith("~") ? true : isPartialfields;

                                /*String lookup_key = searchMetaTagService.getMetaTagMapping().keySet().stream()
                                        .filter(k -> metaParts[0].replaceAll("[~=:]$", "").contains(k))
                                        .findAny()
                                        .orElse(metaParts[0].replaceAll("[~=:]$", ""));

                                String key = searchMetaTagService.getMetaTagMapping().getOrDefault(lookup_key, lookup_key);*/

                                String key = searchMetaTagService.getMetaTagMapping().getOrDefault(metaParts[0].replaceAll("[~=:]$", ""), metaParts[0].replaceAll("[~=:]$", ""));

                                String[] values = Arrays.copyOfRange(metaParts, 1, metaParts.length);
                                String value = Arrays.stream(values).collect(Collectors.joining("")).trim();
                                value = removeDanglingChars(value, "'", "\"");

                                if (key != null && !value.isEmpty()) {
                                    //Handle requiredfields metatag values that have ranges
                                    if (value.contains("..") && !isPartialfields) {
                                        value = processRangeValues(value);
                                        fq.add(key + ":" + value);
                                    } else {
                                        fq.add(isPartialfields ? (key + ":*" + value + "*") : (key + ":\"" + value + "\""));
                                    }
                                    solrQuery.addField(key);
                                }
                            } else {
                                /**
                                 * Handle situations where <metaFieldName>:<value1>|<value2>|<value2> as opposed to
                                 * <metaFieldName>:<value1>|<metaFieldName>:<value2>|<metaFieldName>:<value2>
                                 */

                                // lookbehind to see if we have AND/OR
                                if (fq.get(fq.size()-1).equals("AND") || fq.get(fq.size()-1).equals("OR")) {

                                    // lookbehind to see if we have a metaTag or field
                                    if (stringContainsItemFromList(fq.get(fq.size()-2), searchMetaTagService.getMetaTagMapping().values().toArray(new String[0]))) {

                                        isPartialfields = fq.get(fq.size() - 2).endsWith("*") ? true : isPartialfields;

                                        String key = StringUtils.substringBefore(fq.get(fq.size() - 2), ":");
                                        String value = metaParts[0].trim();
                                        value = removeDanglingChars(value, "'", "\"");

                                        fq.add(isPartialfields ? (key + ":*" + value + "*") : (key + ":\"" + value + "\""));
                                        solrQuery.addField(key);
                                    }
                                }
                            }


                        } catch (UnsupportedEncodingException e) {
                            throw new OSSearchException("requiredfields or partialfields unsupported encoding for: " + token + ", exception: " + e.getMessage());
                        }
                }
            }

            //TODO validate the final fq is a valid expression and fields have values associated

            if (!fq.isEmpty()) {
                String value = fq.stream().collect(Collectors.joining(" ")).trim();
                //value = replaceNotPairs(value, "\"", "");
                log.debug("solr fq: {}", value);
                solrQuery.addFilterQuery(value);
            }
        }
    }

    private static String processRangeValues(String value) throws Exception {
        final String regex = "(?<start>[^\\s]+)?\\.\\.(?<end>[^\\s]+)?";

        if (value.matches(regex)) {
            StringBuilder sb = new StringBuilder();

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);

            while (matcher.find()) {
                String start = matcher.group("start");
                String end = matcher.group("end");

                sb.append("[");
                sb.append(start != null && !start.isEmpty() ? start : "*");
                sb.append(" TO ");
                sb.append(end != null && !end.isEmpty() ? end : "*");
                sb.append("]");
            }

            value = sb.toString();
        }

        return value;
    }

    private void processKeymatch(List<GM> gmList, @NotBlank String q, Set<Keymatch> keymatches) throws Exception {

        keymatches.forEach(keymatch -> {
            String searchTerm = keymatch.getSearchTerm();
            Keymatch.KeymatchType keymatchType = keymatch.getKeymatchType();
            String urlForMatch = keymatch.getUrlForMatch();

            if (keymatchType == exact && q.equalsIgnoreCase(searchTerm)) {
                GM gm = new GM(keymatch.getUrlForMatch(), Arrays.asList(keymatch.getTitleForMatch()), keymatchType.name());
                gmList.add(gm);
            } else if (keymatchType == phrase && StringUtils.containsIgnoreCase(q, searchTerm)) {
                GM gm = new GM(keymatch.getUrlForMatch(), Arrays.asList(keymatch.getTitleForMatch()), keymatchType.name());
                gmList.add(gm);
            } else if (keymatchType == keyword && Arrays.asList(q.toLowerCase().split(" ")).contains(searchTerm.toLowerCase())) {
                GM gm = new GM(keymatch.getUrlForMatch(), Arrays.asList(keymatch.getTitleForMatch()), keymatchType.name());
                gmList.add(gm);

                //TODO: php code checks keymatch urlForMatch do we need too???
                /*if (!answer.isEmpty()) {
                    answer.forEach(key -> {
                        if (key.getUrlForMatch().equals(urlForMatch)) {
                            answer.add((Keymatch) keymatch);
                        }
                    });
                } else {
                    answer.add((Keymatch) keymatch);
                }*/
            }
        });
    }

    /**
     * Converts dirs param to solr query
     *
     * input dirs=nk360\/resources,nk360\/about,nk360\/professional-development,nk360\/lessons-resources,nk360\/student-programs,nk360\/student-programs,nk360\/visit,nk360\/faq
     *
     * return dirs= (id:*https/nk360\/resources* OR id:*https/nk360\/about* OR id:*https/nk360\/professional-development* OR id:*https/nk360\/lessons-resources* OR id:*https/nk360\/student-programs* OR id:*https/nk360\/student-programs* OR id:*https/nk360\/visit* OR id:*https/nk360\/faq*)
     *
     * @param dirs
     * @return
     */
    public void processSubPathSearching(SolrQuery solrQuery, String dirs) throws Exception {
        Set<String> dirsList = new HashSet<>();

        if (dirs != null && !dirs.isEmpty()) {

            for (String dir : Arrays.asList(dirs.split(","))) {

                String dir_name = null;

                if (!dir.startsWith("/")) {
                    dir_name = "/" + dir;
                }

                dir_name = "*https" + dir_name + "*";

                dirsList.add(dir_name);

                //dirsAnswer = dirsAnswer != null ? dirsAnswer + " OR " + dir_name : dir_name;
            }

            /*if (dirsAnswer != null) {
                dirsAnswer = " (" + dirsAnswer + ") ";
            }*/

            Collector<CharSequence, ?, String> collector = (dirsList.size() == 1) ? Collectors.joining(" ") : Collectors.joining(" OR ", "(", ")");
            solrQuery.addFilterQuery("id:" + dirsList.stream().collect(collector));
        }
    }

    /**
     * Search provides three sorting options for search results:
     *
     * <name>:<direction>
     *      <name>: The name of the field that should be used to sort by. This string must be double-URL-encoded.
     *      <direction>:
     *          - D = descending
     *          - A = ascending
     *
     * date:<direction>:<mode>:<format>
     *      <direction>:
     *          - D = descending
     *          - A = ascending
     *      <mode>:
     *          - S = Return the 1000 most relevant results, sorted by date.
     *          - R = Get all results, sort by date, and return the first 1000 results.
     *              You can use this option when freshness is more important than relevancy.
     *              Do not use this filter if your collection contains more than 50,000 documents.
     *          - L = Return the date information for each result. No sorting is done.
     *      <format>:
     *          - d1 = The format of the value returned for each search result is set to YYYY-MM-DD.
     *          (There appear to be no other options for that last parameter)
     *
     * meta:<name>:<direction>:<mode>:<language>:<case>:<numeric>
     *      <name>: The name of the meta tag that should be used to sort by. This string must be double-URL-encoded.
     *      <direction>:
     *          - D = descending
     *          - A = ascending (default)
     *      <mode>:
     *          - E = Return the 1000 most relevant results, then sort by metadata. Default.
     *          - ED =  Same as mode E, but also sort dates chronologically. Supported in GSA version 7.2.0.G.230 and later.
     *          - S = Return the 1000 most relevant results, then sort by metadata, then apply Advanced Score Reporting, Unification biasing, and filtering.
     *          - SD =  Same as mode S, but also sort dates chronologically. Supported in GSA version 7.2.0.G.230 and later.
     *      <language>: any ISO 639-1 code. A 2-character language code indicating the language rules to use to sort. en is the default.
     *      <case>:
     *          - D = Do not consider case when sorting. Default.
     *          - U = Sort uppercase version of a letter before the lowercase version of that letter.
     *              Note that this does not sort all uppercase characters before all lowercase characters.
     *          - L = Sort lowercase version of a letter before the uppercase version of that letter.
     *      <numeric>:
     *          - D = Numeric sorting is disabled, so 123 comes before 2 because 1 is less than 2. Default. The order is ascending (the default).
     *          - Y = Numeric sorting is enabled, so 2 comes before 123 because 2 is less than 123.
     *              This only sorts positive integers, but does classify numbers inside longer alphanumeric strings. So ABC2XYZ will come before ABC123XYZ.
     *          - F = This is similar to Y, but also identifies and sorts negative and floating-point numbers.
     *              It will identify proper punctuation based on the language specified, so a decimal point is a . for English, but a , for German.
     *          - N = Can be used to sort pure English numbers (only containing digits and +-. punctuation) faster than using Y or F. But values like ABC2XYZ will not be sorted.
     *
     * @param solrQuery
     * @param sort sort string one of date:<direction>:<mode>:<format>, meta:<name>:<direction>:<mode>:<language>:<case>:<numeric>, or <name> <direction>
     */
    public void processSort(SolrQuery solrQuery, String sort) throws Exception {

        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = StringUtils.splitPreserveAllTokens(sort, ":");

            if (sortParts.length > 2) {
                if (sortParts[0].equals("date")) {
                    //TODO: implement other sort options for date
                    log.info("sort date: {}", Arrays.asList(sortParts));
                    solrQuery.setSort("meta_sort-lastmodified", sortParts[1].equals("D") ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);
                } else if (sortParts[0].equals("meta")) {
                    //TODO: implement other sort options for meta
                    log.info("sort meta: {}", Arrays.asList(sortParts));
                    String sortField = searchMetaTagService.getMetaTagMapping().get(URLDecoder.decode(sortParts[1], "utf-8"));
                    solrQuery.setSort(sortField, sortParts[2].equals("D") ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);
                }
            } else {
                //TODO: what if we have multiple fields to sort on
                sortParts = StringUtils.splitPreserveAllTokens(URLDecoder.decode(sort, "utf-8"), " ");
                String sortField = searchMetaTagService.getMetaTagMapping().getOrDefault(sortParts[0], sortParts[0]);
                solrQuery.setSort(sortField, sortParts[1].equals("desc") ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);
                log.info("sort field: {}", sortField, sortParts[1]);
            }
        }
    }

    private boolean isBalanced(String str) throws Exception {
        Deque<Character> deque = new LinkedList<>();
        if (StringUtils.containsAny(str, '{' , '}', '[', ']', '(', ')')) {
            for (char ch: str.toCharArray()) {
                if (Arrays.asList('{' , '}', '[', ']', '(', ')').contains(ch)) {
                    if (ch == '{' || ch == '[' || ch == '(') {
                        deque.addFirst(ch);
                    } else {
                        if (!deque.isEmpty()
                                && ((deque.peekFirst() == '{' && ch == '}')
                                || (deque.peekFirst() == '[' && ch == ']')
                                || (deque.peekFirst() == '(' && ch == ')'))) {
                            deque.removeFirst();
                        } else {
                            return false;
                        }
                    }
                }
            }

        }
        return deque.isEmpty();
    }

    /**
     * Replace dangling character like quotes
     *
     * @param input the string to process
     * @param removeChars characters to be removed
     * @return
     */
    private static String removeDanglingChars(String input, String ... removeChars) throws Exception {
        for (String removeChar : removeChars) {
            input = replaceNotPairs(input, removeChar, "");
        }
        return input;
    }

    /**
     * Replace dangling character
     *
     * @param input the string to process
     * @param replace the string to replace
     * @param replaceWith string to replace with
     * @return
     */
    private static String replaceNotPairs(String input, String replace, String replaceWith) throws Exception {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = Pattern.compile("\\B"+replace+"\\w*( \\w*)*"+replace+"\\B").matcher(input);
        int start = 0;
        int last = 0;
        while (matcher.find()) {
            start = matcher.start();
            sb.append(input.substring(last, start).replace(replace, replaceWith));
            last = matcher.end();
            sb.append(matcher.group());
        }
        sb.append(input.substring(last).replace(replace, replaceWith));
        return sb.toString();
    }

    public static String[] splitOnWordOrPhrase(String inputStr) throws Exception {
        List<String> result = new ArrayList<>();
        String regex = "\"[^\"]+\"|[^\"\\s]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputStr);

        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result.toArray(new String[0]);
    }

    public static String[] splitOnWordList(String inputStr, String[] wordList, String prefixRegex, String suffixRegex) throws Exception {
        String regex = Stream.of(wordList).map(Pattern::quote).collect(Collectors.joining("|", prefixRegex, suffixRegex));
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return pattern.split(inputStr);
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] specialTerms) throws Exception {
        return Arrays.stream(specialTerms).map(specialTerm -> specialTerm+":")
                .filter(inputStr::contains)
                .anyMatch(specialTerm ->
                        Arrays.stream(inputStr.split("\\s+")).anyMatch(part -> part.startsWith(specialTerm) || part.startsWith("-"+specialTerm))
                );
    }

    private String getJsonString(Object entity) throws Exception {
        ObjectMapper mapper = new JsonMapper();
        return mapper.writeValueAsString(entity);
    }

    private JSONObject json_merge(JSONObject input, JSONObject mergeObj) throws Exception {
        JSONObject merged = new JSONObject(input, JSONObject.getNames(input));
        for (String key : JSONObject.getNames(mergeObj)) {
            merged.put(key, mergeObj.get(key));
        }
        return merged;
    }
}
