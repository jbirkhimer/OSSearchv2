package edu.si.ossearch.collection.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.collection.entity.converters.UrlNormalizerPatternScopeConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author jbirkhimer
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
//@Entity
//@Table(name= "url_normalizer_pattern")
@Embeddable
public class URLNormalizerPattern {
    public enum Scope {
        _default,
        partition, //Scope used by {@link org.apache.nutch.crawl.URLPartitioner}.
        generate_host_count, //Scope used by {@link org.apache.nutch.crawl.Generator}.
        fetcher, //Scope used by {@link org.apache.nutch.fetcher.Fetcher} when processing redirect URLs.
        crawldb, //Scope used when updating the CrawlDb with new URLs.
        linkdb, //Scope used when updating the LinkDb with new URLs.
        inject, //Scope used by {@link org.apache.nutch.crawl.Injector}.
        outlink, //Scope used when constructing new {@link org.apache.nutch.parse.Outlink} instances.
        indexer, //Scope used when indexing URLs.
    }

    public class Rule {
        public Pattern pattern;

        public String substitution;
    }

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;

    @Column(columnDefinition = "TEXT")
    private String pattern;
    @Column(columnDefinition = "TEXT")
    private String substitution = "";

    @JsonSerialize(converter = UrlNormalizerPatternScopeConverter.class)
    @Enumerated(EnumType.STRING)
    private URLNormalizerPattern.Scope scope;

    public Map<String, String> getRule() {
        Map<String, String> rule = new HashMap<>();
        rule.put("pattern", this.pattern);
        rule.put("substitution", this.substitution);
        return rule;
    }
}
