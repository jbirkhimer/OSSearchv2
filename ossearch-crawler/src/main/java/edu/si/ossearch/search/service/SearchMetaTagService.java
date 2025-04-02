package edu.si.ossearch.search.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jakarta.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This service class loads on startup an exhaustive list of all possible meta tag fields from solr and
 * from nutch-site.xml index.parse.md property and is used for extracting
 * key/value's from requiredfields and partialfields search api param.
 *
 * The requiredfields and partialfields search param should be double encoded when sent to /search api but
 * encoded meta tag fields are added here just in-case
 *
 * @author jbirkhimer
 */
@Slf4j
@Service
@NoArgsConstructor
public class SearchMetaTagService {

    @Autowired
    @Qualifier("master")
    SolrClient solrClient;

    @Value(value = "${spring.data.solr.host}")
    @NonNull
    String solrUrl;
    @Value(value = "${spring.data.solr.collection}")
    @NonNull
    String solrCollection;

    @Value(value = "${ossearch.nutch.confDir}")
    @NonNull
    File nutchConfDir;

    private Set<String> metaTags = new TreeSet<>();
    private Map<String, String> metaTagMapping = new HashMap<>();
    private Set<String> solrNonMetaFields = new TreeSet<>();
    private Set<String> allSolrFields = new TreeSet<>();
    private Set<String> nutchFields = new TreeSet<>();

    @PostConstruct
    public void init() {
        if (nutchConfDir == null) {
            log.warn("nutchConf is null using src/main/resources/conf");
            nutchConfDir = new File("src/main/resources/conf");
        }
        if (solrClient == null) {
            if (solrUrl == null && solrCollection == null) {

                solrUrl = "http://si-wsdsolr01.si.edu:8983/solr";
                solrCollection = "external";

                solrUrl = "http://localhost:8984/solr";
                solrCollection = "external-v1_FULL";
            }
            log.warn("solrClient is null creating it with baseUrl:{}", solrUrl);
            solrClient = new HttpSolrClient.Builder(solrUrl).build();
        }

        metaTags.clear();
        metaTagMapping.clear();
        solrNonMetaFields.clear();
        allSolrFields.clear();
        nutchFields.clear();

        loadNutchIndexedParseMetaTags();
        loadMetaTagsFromSolr();

        metaTags.removeIf(StringUtils::isEmpty);

        log.warn("Meta tags loaded into system: {}", metaTags.size());
    }

    public Set<String> getMetaTags() {
        return metaTags;
    }

    public Map<String, String> getMetaTagMapping() {
        return metaTagMapping;
    }

    public Set<String> getSolrNonMetaFields() {
        return solrNonMetaFields;
    }

    public Set<String> getAllSolrFields() {
        return allSolrFields;
    }

    public Set<String> getNutchFields() {
        return nutchFields;
    }

    private void loadNutchIndexedParseMetaTags() {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xml = builder.parse(new File(nutchConfDir + "/nutch-site.xml"));
            XPath xpath = XPathFactory.newInstance().newXPath();

            //Get first match
            String indexParseMdValue = (String) xpath.evaluate("/configuration/property[name = 'index.parse.md']/value/text()", xml, XPathConstants.STRING);

            String indexMvMdValue = (String) xpath.evaluate("/configuration/property[name = 'index.metadata.multivalued.fields']/value/text()", xml, XPathConstants.STRING);

            Set<String> nutchMetaTags = new TreeSet<>();
            nutchMetaTags.addAll(Arrays.asList(indexParseMdValue.split(",")));
            nutchMetaTags.addAll(Arrays.asList(indexMvMdValue.split(",")));

            metaTags.addAll(nutchMetaTags);

            nutchMetaTags.forEach(nutchMetaTag -> {
                metaTagMapping.put(nutchMetaTag.replace("meta_", ""), nutchMetaTag);
                metaTagMapping.put("-"+nutchMetaTag.replace("meta_", ""), "-"+nutchMetaTag);
            });

            nutchFields.addAll(nutchMetaTags);

        } catch (IOException | XPathExpressionException | ParserConfigurationException | SAXException e) {
            log.warn("Could not load nutch index.pars.md meta tags! {}", e.getMessage(), e);
        }
    }

    private void loadMetaTagsFromSolr() {
        LukeRequest lukeRequest = new LukeRequest();
        lukeRequest.setNumTerms(0);

        try {
            LukeResponse lukeResponse = lukeRequest.process(solrClient, solrCollection);
            Map<String, LukeResponse.FieldInfo> fieldInfoMap = lukeResponse.getFieldInfo();

            for (Map.Entry<String, LukeResponse.FieldInfo> entry : fieldInfoMap.entrySet()) {

                String fieldName = entry.getKey();
                LukeResponse.FieldInfo fieldInfo = entry.getValue();

                log.debug("loading from solr field: {}", fieldName);

                fieldName = fieldName.replace("image_", "image:").replace("fb_", "fb:").replace("og_", "og:").replace("twitter_", "twitter:").replace("__", " ");

                allSolrFields.add(fieldName);

                //String metaTag = StringUtils.substringAfter(fieldName, "meta_");

                if ((fieldName.startsWith("meta_") && !fieldName.startsWith("meta__")) || fieldName.equals("topicids")) {
                    log.debug("adding solr meta_ field: {}", fieldName);

                    metaTags.add(fieldName);
                    metaTagMapping.put(fieldName.replace("meta_", ""), entry.getKey());
                    metaTagMapping.put("-"+fieldName.replace("meta_", ""), "-"+entry.getKey());
                } else {
                    solrNonMetaFields.add(fieldName);
                    metaTagMapping.put(fieldName, fieldName);
                    metaTagMapping.put("-"+fieldName, "-"+fieldName);
                }
            }
        } catch (SolrServerException | IOException e) {
            log.error("Problem getting meta tag fields from solr! {}", e.getMessage(), e);
        }
    }
}
