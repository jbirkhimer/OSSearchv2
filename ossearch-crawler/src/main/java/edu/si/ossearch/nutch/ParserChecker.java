package edu.si.ossearch.nutch;

import crawlercommons.robots.BaseRobotRules;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.StringUtils;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.SignatureFactory;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.net.URLNormalizers;
import org.apache.nutch.parse.*;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.protocol.Protocol;
import org.apache.nutch.protocol.ProtocolFactory;
import org.apache.nutch.protocol.ProtocolOutput;
import org.apache.nutch.scoring.ScoringFilters;
import org.apache.nutch.util.StringUtil;

import java.util.*;

/**
 * Parser checker, useful for testing parser. It also accurately reports
 * possible fetching and parsing failures and presents protocol status signals
 * to aid debugging. The tool enables us to retrieve the following data from any
 * url:
 * <ol>
 * <li><tt>contentType</tt>: The URL {@link Content}
 * type.</li>
 * <li><tt>signature</tt>: Digest is used to identify pages (like unique ID) and
 * is used to remove duplicates during the dedup procedure. It is calculated
 * using {@link org.apache.nutch.crawl.MD5Signature} or
 * {@link org.apache.nutch.crawl.TextProfileSignature}.</li>
 * <li><tt>Version</tt>: From {@link ParseData}.</li>
 * <li><tt>Status</tt>: From {@link ParseData}.</li>
 * <li><tt>Title</tt>: of the URL</li>
 * <li><tt>Outlinks</tt>: associated with the URL</li>
 * <li><tt>Content Metadata</tt>: such as <i>X-AspNet-Version</i>, <i>Date</i>,
 * <i>Content-length</i>, <i>servedBy</i>, <i>Content-Type</i>,
 * <i>Cache-Control</i>, etc.</li>
 * <li><tt>Parse Metadata</tt>: such as <i>CharEncodingForConversion</i>,
 * <i>OriginalCharEncoding</i>, <i>language</i>, etc.</li>
 * <li><tt>ParseText</tt>: The page parse text which varies in length depdnecing
 * on <code>content.length</code> configuration.</li>
 * </ol>
 *
 * @author jbirkhimer
 *
 */

@Slf4j
public class ParserChecker {
  private Configuration conf;

  public ParserChecker(Configuration conf) {
    this.conf = conf;
  }

  public Configuration getConf() {
    return conf;
  }

  public HashMap<String, Object> process(String url, boolean normalize, boolean checkRobotsTxt, boolean dumpText, boolean followRedirects) throws Exception {
    StringBuilder output = new StringBuilder();
    HashMap<String, Object> answer = new HashMap<>();

    URLNormalizers normalizers = null;
    // used to simulate the metadata propagated from injection
    HashMap<String, String> metadata = new HashMap<>();
    String forceAsContentType = null;
    ScoringFilters scfilters = new ScoringFilters(conf);

    if (normalize) {
      normalizers = new URLNormalizers(conf, URLNormalizers.SCOPE_DEFAULT);
    }

    if (normalizers != null) {
      url = normalizers.normalize(url, URLNormalizers.SCOPE_DEFAULT);
    }

    output.append("fetching: " + url).append("\n");

    CrawlDatum datum = new CrawlDatum();

    Iterator<String> iter = metadata.keySet().iterator();
    while (iter.hasNext()) {
      String key = iter.next();
      String value = metadata.get(key);
      if (value == null)
        value = "";
      datum.getMetaData().put(new Text(key), new Text(value));
    }

    int maxRedirects = conf.getInt("http.redirect.max", 3);
    if (followRedirects) {
      if (maxRedirects == 0) {
        output.append("Following max. 3 redirects (ignored http.redirect.max == 0)").append("\n");
        maxRedirects = 3;
      } else {
        output.append("Following max. "+maxRedirects+" redirects").append("\n");
      }
    }

    ProtocolOutput protocolOutput = getProtocolOutput(url, datum, checkRobotsTxt, output);
    Text turl = new Text(url);

    // Following redirects and not reached maxRedirects?
    int numRedirects = 0;
    while (protocolOutput != null && !protocolOutput.getStatus().isSuccess() && followRedirects && protocolOutput.getStatus().isRedirect() && maxRedirects >= numRedirects) {
      String[] stuff = protocolOutput.getStatus().getArgs();
      url = stuff[0];
      output.append("Follow redirect to " + url).append("\n");

      if (normalizers != null) {
        url = normalizers.normalize(url, URLNormalizers.SCOPE_DEFAULT);
      }

      turl.set(url);

      // try again
      protocolOutput = getProtocolOutput(url, datum, checkRobotsTxt, output);
      numRedirects++;
    }

    if (checkRobotsTxt && protocolOutput == null) {
      answer.put("output", output.append("Fetch disallowed by robots.txt").toString());
      return answer;
    }

    if (!protocolOutput.getStatus().isSuccess()) {
      output.append("Fetch failed with protocol status: " + protocolOutput.getStatus()).append("\n");

      if (protocolOutput.getStatus().isRedirect()) {
        output.append("Redirect(s) not handled due to configuration.").append("\n");
        output.append("Max Redirects to handle per config: " + maxRedirects).append("\n");
        output.append("Number of Redirects handled: " + numRedirects).append("\n");
      }
      answer.put("output", output.toString());
      return answer;
    }

    Content content = protocolOutput.getContent();

    if (content == null) {
      answer.put("output", output.append("No content for " + url).toString());
      return answer;
    }

    String contentType;
    if (forceAsContentType != null) {
      content.setContentType(forceAsContentType);
      contentType = forceAsContentType;
    } else {
      contentType = content.getContentType();
    }

    if (contentType == null) {
      log.error("Failed to determine content type!");
      answer.put("output", output.append("Failed to determine content type!").toString());
    }

    // store the guessed content type in the crawldatum
    datum.getMetaData().put(new Text(Metadata.CONTENT_TYPE), new Text(contentType));

    if (ParseSegment.isTruncated(content)) {
      log.warn("Content is truncated, parse may fail!");
      output.append("Content is truncated, parse may fail!").append("\n");
    }

    // call the scoring filters
    try {
      scfilters.passScoreBeforeParsing(turl, datum, content);
    } catch (Exception e) {
      output.append("Couldn't pass score before parsing, url " + turl + " (" + e + ")").append("\n");
      output.append(StringUtils.stringifyException(e)).append("\n");
    }

    ParseResult parseResult = new ParseUtil(conf).parse(content);

    if (parseResult == null) {
      answer.put("output", output.append("Parsing content failed!").toString());
      return answer;
    }

    // calculate the signature
    byte[] signature = SignatureFactory.getSignature(conf).calculate(content, parseResult.get(new Text(url)));

    output.append("parsing: " + url).append("\n");

    Map<String, Object> result = new HashMap<>();
    result.put("contentType", contentType);
    result.put("signature", StringUtil.toHexString(signature));

    for (Map.Entry<Text, Parse> entry : parseResult) {
      turl = entry.getKey();
      Parse parse = entry.getValue();
      // call the scoring filters
      try {
        scfilters.passScoreAfterParsing(turl, content, parse);
      } catch (Exception e) {
        output.append("Couldn't pass score after parsing, url " + turl + " (" + e + ")").append("\n");
        output.append(StringUtils.stringifyException(e)).append("\n");
      }

      result.put("url", turl.toString());

      ParseData parseData = parse.getData();
      result.put("Version", parseData.getVersion());
      result.put("Status", parseData.getStatus().toString());
      result.put("Title", parseData.getTitle());

      Map<String, Object> outlinksResult = new HashMap<>();
      result.put("Outlinks", outlinksResult);

      List<Map<String, String>> outlinksList = new ArrayList<>();

      Outlink[] outlinks = parseData.getOutlinks();
      if (outlinks != null) {
        outlinksResult.put("count", outlinks.length);
        for (int i = 0; i < outlinks.length; i++) {
          Map<String, String> outlink = new HashMap<>();

          outlink.put("toUrl", outlinks[i].getToUrl());
          outlink.put("anchor", outlinks[i].getAnchor());

          outlinksList.add(outlink);
        }
        outlinksResult.put("outlinks", outlinksList);
      }

      Metadata contentMeta = parseData.getContentMeta();
      Map<String, Object> contentMetaMap = getMetadata(contentMeta);
      String strippedContent = ((String[]) contentMetaMap.remove("strippedContent"))[0];
      result.put("Content Metadata", contentMetaMap);

      Metadata parseMeta = parseData.getParseMeta();
      Map<String, Object> parseMetaMap = getMetadata(parseMeta);
      result.put("Parse Metadata", parseMetaMap);

      if (dumpText) {
        result.put("raw_text", parse.getText());
        result.put("parse_text", strippedContent.replaceAll("\\n", " "));
      }
    }

    answer.put("output", output.toString());
    answer.put("result", result);
    return answer;
  }

  private Map<String, Object> getMetadata(Metadata metadata) {
    Map<String, Object> answer = new TreeMap<>();

    String[] names = metadata.names();
    for (int i = 0; i < names.length; i++) {
      answer.put(names[i], metadata.getValues(names[i]));
    }

    return answer;
  }

  protected ProtocolOutput getProtocolOutput(String url, CrawlDatum datum, boolean checkRobotsTxt, StringBuilder output) throws Exception {
    ProtocolFactory factory = new ProtocolFactory(conf);
    Protocol protocol = factory.getProtocol(url);
    Text turl = new Text(url);
    if (checkRobotsTxt) {
      output.append("Checking robots.txt ...");
      BaseRobotRules rules = protocol.getRobotRules(turl, datum, null);
      if (rules.isAllowed(url)) {
        output.append(" (allowed)").append("\n");
      } else {
        output.append("\nDenied by robots.txt: " + url).append("\n");
        return null;
      }
    }
    return protocol.getProtocolOutput(turl, datum);
  }

}
