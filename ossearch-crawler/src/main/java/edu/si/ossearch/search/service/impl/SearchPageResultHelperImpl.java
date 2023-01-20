package edu.si.ossearch.search.service.impl;

import edu.si.ossearch.collection.entity.PageResult;
import edu.si.ossearch.search.beans.result.GM;
import edu.si.ossearch.search.beans.request.Paging;
import edu.si.ossearch.search.beans.request.Query;
import edu.si.ossearch.search.service.SearchPageResultHelperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.*;

/**
 * @author jbirkhimer
 */
@Slf4j
@Service
public class SearchPageResultHelperImpl implements SearchPageResultHelperService {

    private static HashMap<String, String> pageResultTokenMapping;

    static {
        pageResultTokenMapping = new HashMap<>();
        pageResultTokenMapping.put("[[oss-pr-form]]", "<div th:replace=\"html/search-form\" xmlns:th=\"http://www.thymeleaf.org\"></div>");
        pageResultTokenMapping.put("[[oss-pr-pagination]]", "<div th:replace=\"html/search-pagination\" xmlns:th=\"http://www.thymeleaf.org\"></div>");
        pageResultTokenMapping.put("[[oss-pr-results]]", "<div th:replace=\"html/search-results\" xmlns:th=\"http://www.thymeleaf.org\"></div>");
        pageResultTokenMapping.put("[[oss-pr-keymatch]]", "<div th:replace=\"html/search-keymatch\" xmlns:th=\"http://www.thymeleaf.org\"></div>");
        pageResultTokenMapping.put("[[oss-pr-facets]]", "<div th:replace=\"html/search-facets\" xmlns:th=\"http://www.thymeleaf.org\"></div>");
    }

    private ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
        templateResolver.setResolvablePatterns(Collections.singleton("html/*"));
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    private ITemplateResolver stringTemplateResolver() {
        final StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(2));
        // No resolvable pattern, will simply process as a String template everything not previously matched
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Override
    public String generatePageResult(Query query, PageResult pageResult, Paging paging, QueryResponse rsp, List<GM> keymatches, Boolean edan) {

        Map<String, Object> solrParams = (HashMap) rsp.getResponseHeader().toMap(new HashMap()).get("params");
        int startIndex = Integer.parseInt(String.valueOf(solrParams.get("start")));
        int pageSize = Integer.parseInt(String.valueOf(solrParams.get("rows")));
        long numFound = rsp.getResults().getNumFound();
        int currentPage = (int) Math.ceil(startIndex / pageSize) + 1;

        List<Map<String, String>> solrResults = processSolrResults(rsp);

        TemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.addTemplateResolver(stringTemplateResolver());

        Context ctx = new Context(new Locale("US"));

        //Form Template props
        ctx.setVariable("action", "/search");
        ctx.setVariable("method", "GET");
        ctx.setVariable("q", query.getQ());
        ctx.setVariable("client", query.getClient());
        ctx.setVariable("site", query.getSite());
        ctx.setVariable("proxystylesheet", query.getProxystylesheet());
        ctx.setVariable("getfields", query.getGetfields());
        ctx.setVariable("num", pageSize);
        ctx.setVariable("edan", edan);

        //Keymatch Template props
        ctx.setVariable("keymatch", keymatches);

        //Results Template props
        String message = generateMessage(query.getQ(), currentPage, numFound, pageSize);
        ctx.setVariable("message", message);

        ctx.setVariable("results", solrResults);

        //Pagination props
        Map<String, Integer> pagination = processPaging(currentPage, pageSize, startIndex, numFound);
        ctx.setVariable("pagination", pagination);
        ctx.setVariable("query", query.getQ());

        if (pageResult == null) {
            return templateEngine.process("html/search-default-page-result.html", ctx);
        } else {
            String pageResultFullHtml = StringEscapeUtils.unescapeHtml4(pageResult.getFullHtml()) ;

            for (Map.Entry<String, String> token : pageResultTokenMapping.entrySet()) {
                pageResultFullHtml = pageResultFullHtml.replace(token.getKey(), token.getValue());
            }

            /*String formTemplate = generateFormTemplate(query, templateEngine);

            String resultsTemplate = generateResultsTemplate(query.getQ(), solrResults, currentPage, numFound, pageSize, templateEngine);

            String paginationTemplate = generatePaginationTemplate(query, currentPage, startIndex, numFound, pageSize, templateEngine);*/

            return templateEngine.process(pageResultFullHtml, ctx);
        }
    }

    private List<Map<String, String>> processSolrResults(QueryResponse rsp) {

        List<Map<String, String>> resultsList = new ArrayList<>();

        SolrDocumentList solrResults = rsp.getResults();
        Map<String, Map<String, List<String>>> highlighting = rsp.getHighlighting();

        for (SolrDocument doc : solrResults) {

            Map<String, List<String>> docHighlighting = highlighting.getOrDefault((String) doc.getFieldValue("id"), null);

            Map<String, String> result = new HashMap<>();

            String url = (String) doc.getFieldValue("url");
            url = url != null && !url.isEmpty() ? url : "No url found";
            result.put("url", url);

            //TODO: handle title length, special char's and anything else we need to do
            String title = docHighlighting != null && docHighlighting.containsKey("title") && !docHighlighting.get("title").isEmpty()
                    ? docHighlighting.get("title").get(0)
                    : (String) doc.getFieldValue("title");
            title = title != null && !title.isEmpty()
                    //? StringEscapeUtils.escapeHtml4(title)
                    ? title
                    : "No title found...";
            result.put("title", title);

            String content = docHighlighting != null && docHighlighting.containsKey("content") && !docHighlighting.get("content").isEmpty()
                    ? "..." + docHighlighting.get("content").get(0) + "..."
                    : StringUtils.abbreviate((String) doc.getFieldValue("content"), 123);
            //content = content != null && !content.isEmpty() ? StringEscapeUtils.escapeHtml4(content) : "No content found";
            content = content != null && !content.isEmpty() ? content : "No content found...";
            result.put("content", content);

            resultsList.add(result);
        }
        return resultsList;
    }

    private String generateResultsTemplate(String query, List<Map<String, String>> solrResults, int currentPage, long numFound, int pageSize, TemplateEngine templateEngine) {

        String message = generateMessage(query, currentPage, numFound, pageSize);

        Context ctx = new Context(new Locale("US"));
        ctx.setVariable("message", message);
        ctx.setVariable("results", solrResults);

        return templateEngine.process("html/search-results.html", ctx);
    }

    private static String generateMessage(String query, int currentPage, long numFound, int pageSize) {
        String message = "Your search, " + query + " - did not match any documents.";
        if (numFound > 0) {
            int start = (currentPage - 1) * pageSize + 1;
            int end = (int) numFound;

            if (pageSize < numFound) {
                end = pageSize * currentPage;
                if (end > numFound) {
                    end = (int) numFound;
                }
            }

            //message = "About " + numFound + " results - ( showing " + start + " to " + end + " of " + numFound + " )";
            message = "About " + numFound + " results - ( " + end + " / " + numFound + " )";
        }
        return message;
    }

    public String generatePaginationTemplate(Query query, int currentPage, int startIndex, long numFound, int pageSize, TemplateEngine templateEngine) {

        Map<String, Integer> pagination = processPaging(currentPage, pageSize, startIndex, numFound);

        Context ctx = new Context(new Locale("US"));
        ctx.setVariable("pagination", pagination);
        ctx.setVariable("query", query.getQ());
        ctx.setVariable("client", query.getClient());
        ctx.setVariable("site", query.getSite());
        ctx.setVariable("proxystylesheet", query.getProxystylesheet());
        ctx.setVariable("getfields", query.getGetfields());
        ctx.setVariable("num", pageSize);
        ctx.setVariable("start", startIndex);

        return templateEngine.process("html/search-pagination.html", ctx);
    }

    private static String generateFormTemplate(Query query, TemplateEngine templateEngine) {
        Context ctx = new Context(new Locale("US"));
        ctx.setVariable("action", "/search");
        ctx.setVariable("method", "GET");
        ctx.setVariable("q", query.getQ());
        ctx.setVariable("client", query.getClient());
        ctx.setVariable("site", query.getSite());
        ctx.setVariable("proxystylesheet", query.getProxystylesheet());
        ctx.setVariable("getfields", query.getGetfields());
        ctx.setVariable("rows", query.getNum());
//        ctx.setVariable("rowsold", paging.getRowold());
//        ctx.setVariable("currentPage", currentPage);
//        ctx.setVariable("oldquery", paging.getOldquery());

        return templateEngine.process("html/search-form.html", ctx);
    }

    private Map<String, Integer> processPaging(int currentPage, int pageSize, int startIndex, long numFound) {

        int totalPages = (int) Math.ceil(numFound / pageSize);
        int pagesToShow = 25;

        int offset = ((currentPage-1) * 10 + 1)-1;

        // Set pages obtained so far, start with 1 since we have currentPage
        int pagesCount = 1;
        // Maintain a copy of pagesCount.
        // Used to detect whether any new pages were added in the iteration
        int newPagesCount = 1;

        // Set beginning and end as currentPage
        int start = currentPage;
        int end = currentPage;

        // Continue iteration till enough pages are obtained
        while (pagesCount < pagesToShow) {
            if (end + 1 <= totalPages) {
                // Ok to take one more page towards end
                end++;
                newPagesCount++;
            }
            if (start - 1 > 0) {
                //Ok to take one more page towards start
                start--;
                newPagesCount++;
            }
            /*
               Break loop if no additional pages were
               obtained in this iteration
               We have obtained maximum number of possible pages
            */
            if (newPagesCount == pagesCount)
                break;
            else
                pagesCount = newPagesCount;
        }

        log.info("start: {}, end: {}, currentPage: {}, startIndex: {}, offset: {}", start, end, currentPage, startIndex, offset);

        Map<String, Integer> pagination = new HashMap<>();
        pagination.put("currentPage", currentPage);
        pagination.put("start", start);
        pagination.put("end", end);
        pagination.put("totalPages", totalPages);

        log.info("pagination: {}", pagination);

        return pagination;
    }
}
