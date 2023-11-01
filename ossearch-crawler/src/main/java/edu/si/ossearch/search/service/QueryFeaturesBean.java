package edu.si.ossearch.search.service;

import lombok.Data;

/**
 * @author jbirkhimer
 */
@Data
public class QueryFeaturesBean {
    String query;
    int label;
    int length;
    int chineseChars;
    double chineseRatio;
    String keywords;
    int frequency;
}
