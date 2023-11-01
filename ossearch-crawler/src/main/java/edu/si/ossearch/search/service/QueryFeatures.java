package edu.si.ossearch.search.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


/**
 * Calculate the various features for a given search query
 * @author jbirkhimer
 */
@Slf4j
@Service
public class QueryFeatures {

    //@Autowired
    //SearchLogRepository searchLogRepository;

    @Value(value ="${ossearch.spam-keywords}")
    private Set<String> spamKeywords; // = new HashSet<>(Arrays.asList(".net", ".com", ".org", "telegram", "whatsapp"));

    public Boolean isSpam(String query, Integer id) {
        QueryFeaturesBean queryFeaturesBean = calculateFeatures(query, id);

        if (!queryFeaturesBean.keywords.isEmpty() || queryFeaturesBean.chineseRatio > 0.3) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Takes a search query and a list of previous search queries as input, and returns a Map object containing the
     * calculated features.
     *
     * @param query
     * @param id
     * @return
     */
    public QueryFeaturesBean calculateFeatures(String query, Integer id) {
        QueryFeaturesBean row = new QueryFeaturesBean();

        row.query = query;

        int length = query.length();
        row.length = length;

        int chineseChars = countChineseCharacters(query);
        row.chineseChars = chineseChars;

        double chineseRatio = (double) chineseChars / length;
        row.chineseRatio = chineseRatio;

        Set<String> keywords = findKeywords(query);
        row.keywords = Strings.join(keywords, ',');

        //TODO: Frequency of the query in the logs
        //int frequency = Collections.frequency(searchLog, query);
        //row.frequency = searchLogRepository.keywordFrequency(query, id);

        return row;
    }

    /**
     * Iterates over the characters in the query and checks if each character belongs to the CJK Unified Ideographs
     * Unicode block, which contains most of the Chinese characters.
     * @param query
     * @return
     */
    private static int countChineseCharacters(String query) {
        int count = 0;
        for (int i = 0; i < query.length(); i++) {
            if (Character.UnicodeBlock.of(query.charAt(i)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if the query contains any of the predefined spam keywords and returns a set of the matching keywords.
     * @param query
     * @return
     */
    private Set<String> findKeywords(String query) {
        Set<String> keywords = new HashSet<>();
        for (String keyword : spamKeywords) {
            if (query.toLowerCase().contains(keyword)) {
                keywords.add(keyword);
            }
        }
        return keywords;
    }

}