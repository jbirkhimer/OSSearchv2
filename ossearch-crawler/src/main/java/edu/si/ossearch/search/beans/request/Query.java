package edu.si.ossearch.search.beans.request;

//import edu.si.ossearch.search.scratch.gsaapi.util.Util;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * This is a POJO that maps its fields to the request
 * parameters of the GSA's search query format. This
 * class definition is subject to change if the GSA
 * query parameter definitions are changed.
 * <br/>
 * This class
 * is for internal use only. GSA API users should use
 * the GSAQuery class instead which is expected to be
 * more stable.
 *
 * @author jbirkhimer
 */
@Slf4j
@Setter
@Getter
@EqualsAndHashCode
@Schema(description = "Retrieve by query")
public class Query implements Serializable {

    private static final long serialVersionUID = 242636095876263114L;

    private char as_dt; // {i, e}:{include [as_sitesearch], exclude [as_sitesearch]}
    private String as_epq; // additional query terms
    private String[] as_eq; // exclude terms
    private String as_lq; // pages linking to this url
    private String as_occt; // {any, title, URL}:{search anywhere on page, search in title, search in url} see net.sf.gsaapi.constants.SearchScope for details
    private String[] as_oq; // any of these
    private String[] as_q;

    //@NotBlank
    //@Setter(AccessLevel.NONE)
    private String q; // *REQUIRED when sitesearch specified* Search query

    private String as_sitesearch; // *MAX 118 after URL encoding* set the value for this into "site"

    /**
     * Specifies the domain name to restrict the search to.
     *
     * @param sitesearch
     */
    private String sitesearch; // needs q to be supplied

    /**
     * Set one or more site collections. Site collections are
     * preconfigured by a GSA admin - usually as a group of related
     * websites.
     * "site" collections are different from
     * <ul>
     * <li>The "sitesearch" or "as_sitesearch" parameters
     * which specifies the domain to include/exclude in the search</li>
     * <li>The "site" special query term (which is specified as a
     * part of the query string as 'site:some.domain.name')</li>
     * </ul>
     */
    @NotNull
    private String site; // *REQUIRED* (OR-ed) collection of "collections" ()

    @NotBlank
    private String client; // *REQUIRED* string specifying valid frontend      (default -needs to be speficied- is default_frontend)
    @NotBlank
    private String output; // *REQUIRED* format for search results      (default -needs to be specified- is xml_no_dtd) see net.sf.gsaapi.constants.OutputFormat for more details
    private String proxycustom; // {<HOME/>,<ADVANCED/>,<TEST/>}
    private boolean proxyreload; // {1} force reload of serverside stylesheet (else default reloaded after 15 mins)
    private String proxystylesheet; // if output==xml_no_dtd then {Omitted,}

    private char access; // {p, s, a}:{public, secure, all} see net.sf.gsaapi.constants.Access for details

    private char filter = 1; // 0,1,p,s  (default is 1) see net.sf.gsaapi.constants.Filter for details
    private String lr; // Language restrict

    private String ie = "UTF-8"; // Input encoding      (default is latin1)
    private String oe = "UTF-8"; // Output encoding     (default is UTF8)

    /**
     * Specifies the index (0-based) at which to scroll into the
     * search results. Thus a value of 10 would scroll into
     * the results to begin retrieving at the 11th result.
     * The default value is 0. This parameters in conjunction
     * with <code>num</code> is useful in paginating thru
     * the results.
     *
     * @param start
     */
    @Min(0)
    private long start = 0; // {0..999} scroll into the search results (constraint: start+num <= 1000)
    @Min(1)
    @Max(200)
    private int num = 10; // {1..100} max results per request     (default is 10)
    private byte numgm; // {0..5} max num of keymatches per result     (default is 3)

    private String getfields; // get meta tags

    /*@Setter(AccessLevel.NONE)
    private Map partialfields; // meta tag names and partial-values
    private boolean partialFieldsOr = true; // used in conjunction with partialFields

    @Setter(AccessLevel.NONE)
    private Map requiredfields; // meta tag names and complete-values
    private boolean requiredFieldsOr = true; // used in conjunction with requiredFields*/

    /*@ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private MetaField requiredfields;*/ // meta tag names and complete-values
    private String requiredfields;

    private boolean requiredFieldsOr = true; // used in conjunction with requiredFields

    /*@ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private MetaField partialfields;*/ // meta tag names and complete-values
    private String partialfields;

    private boolean partialFieldsOr = true; // used in conjunction with partialfields

    /**
     * Specifies the value for the sort parameter.
     * See http://code.google.com/gsa_apis/xml_reference.html#request_sort
     * for more information.
     *
     * @param sort
     */
    private String sort; // Only date is currently supported

    private String inmeta;

    //@Getter(AccessLevel.NONE)
    private String dirs; // Sub-path searching

    public Query(String query, String output) {
        this.q = query;
        this.output = output == null ? _DEFAULT_OUTPUT : output;
        this.access = _DEFAULT_ACCESS;
        /*this.requiredfields = new HashMap();
        this.partialfields = new HashMap();*/

        /*this.requiredfields = new MetaField();
        this.partialfields = new MetaField();*/
    }

    public Query(String query) {
        this(query, _DEFAULT_OUTPUT);
    }

    public Query() {
        this(null);
    }

//    public void setQ(String q) {
//        //TODO: parse q for other special terms and solr fields that are not meta_ fields
//        //Handle inmeta part of q string
//        if (StringUtils.containsIgnoreCase(q, " AND inmeta:")) {
//            Pattern pattern = Pattern.compile(" AND inmeta:", Pattern.CASE_INSENSITIVE);
//            String[] incomingquery = pattern.split(q);
//
//            log.info("q split: {}", Arrays.toString(incomingquery));
//            this.q = incomingquery[0];
//            this.inmeta = incomingquery[1];
//        } else {
//            this.q = q;
//        }
//    }

    /*public void setPartialfields(Properties partialfields, boolean orIfTrueAndIfFalse) {
        this.partialfields.putAll(partialfields);
        this.partialFieldsOr = orIfTrueAndIfFalse;
    }

    public void setRequiredfields(Properties requiredfields, boolean orIfTrueAndIfFalse) {
        this.requiredfields.putAll(requiredfields);
        this.requiredFieldsOr = orIfTrueAndIfFalse;
    }*/

    /*public void setRequiredfields(MetaField requiredfields) {
        this.requiredfields = requiredfields;
        //this.requiredFieldsOr = requiredfields.isOr();
    }

    public void setPartialfields(MetaField partialfields) {
        this.partialfields = partialfields;
        //this.partialFieldsOr = partialfields.isOr();
    }*/

    public void setRequiredfields(String requiredfields) {
        this.requiredfields = requiredfields;
        //this.requiredFieldsOr = requiredfields.isOr();
    }

    public void setPartialfields(String partialfields) {
        this.partialfields = partialfields;
        //this.partialFieldsOr = partialfields.isOr();
    }

    /*public String getDirs() {
        String dirsAnswer = null;

        List<String> dirsList = Arrays.asList(dirs.split(","));

        if (dirsList != null && !dirsList.isEmpty()) {

            for (String dir : dirsList) {

                String dir_name = null;

                if (!dir.startsWith("/")) {
                    dir_name = "/" + dir;
                }

                dir_name = "id:*https" + dir_name + "*";

                dirsAnswer = dirsAnswer != null ? dirsAnswer + " OR " + dir_name : dir_name;
            }

            if (dirsAnswer != null) {
                dirsAnswer = " (" + dirsAnswer + ") ";
            }
        }

        return dirsAnswer;
    }*/

    /**
     * Generates the URL string for getting results from the
     * GSA using current state of this object.
     *
     * @return
     */
    /*public String getValue() {
        StringBuffer sbuf = new StringBuffer();
        Util.appendQueryParam(sbuf, "access", String.valueOf(access));
        if (output != null) Util.appendQueryParam(sbuf, "output", output);
        if (sort != null) Util.appendQueryParam(sbuf, "sort", sort);
        if (ie != null) Util.appendQueryParam(sbuf, "ie", ie);
        if (oe != null) Util.appendQueryParam(sbuf, "oe", oe);
        Util.appendQueryParam(sbuf, "client", client);
        if (start > 0) Util.appendQueryParam(sbuf, "start", String.valueOf(start));
        if (q != null) Util.appendQueryParam(sbuf, "q", q);
        if (as_dt == AS_DT_INCLUDE || as_dt == AS_DT_EXCLUDE)
            Util.appendQueryParam(sbuf, "as_dt", String.valueOf(as_dt));
        if (as_epq != null) Util.appendQueryParam(sbuf, "as_epq", as_epq);
        if (as_eq != null) {
            String temp = Util.stringSeparated(as_eq, null, " ");
            Util.appendQueryParam(sbuf, "as_eq", temp);
        }
        if (as_lq != null) Util.appendQueryParam(sbuf, "as_lq", as_lq);
        if (as_occt != null) Util.appendQueryParam(sbuf, "as_occt", as_occt);
        if (as_oq != null) {
            String temp = Util.stringSeparated(as_oq, null, " ");
            Util.appendQueryParam(sbuf, "as_oq", temp);
        }
        if (as_q != null) {
            String temp = Util.stringSeparated(as_q, null, " ");
            Util.appendQueryParam(sbuf, "as_q", temp);
        }

        if (as_sitesearch != null) Util.appendQueryParam(sbuf, "as_sitesearch", as_sitesearch);
        if (filter == FILTER_DUP_DIRECTORY || filter == FILTER_DUP_SNIPPET
                || filter == FILTER_DUP_SNIPPET_AND_DIRECTORY || filter == FILTER_OFF)
            Util.appendQueryParam(sbuf, "filter", String.valueOf(filter));
        if (lr != null) Util.appendQueryParam(sbuf, "lr", lr);
        if (num > 0) Util.appendQueryParam(sbuf, "num", String.valueOf(num));
        if (numgm > 0) Util.appendQueryParam(sbuf, "numgm", String.valueOf(numgm));
        if (proxycustom != null) Util.appendQueryParam(sbuf, "proxycustom", proxycustom);
        if (proxyreload) Util.appendQueryParam(sbuf, "proxyreload", "1");
        if (proxystylesheet != null) Util.appendQueryParam(sbuf, "proxystylesheet", proxystylesheet);
        if (sitesearch != null) Util.appendQueryParam(sbuf, "sitesearch", sitesearch);
        *//*if (requiredfields != null && requiredfields.size() > 0) {
            Util.appendMappedQueryParams(
                    sbuf,
                    "requiredfields",
                    requiredfields,
                    requiredFieldsOr ? "|" : ".");
        }
        if (partialfields != null && partialfields.size() > 0) {
            Util.appendMappedQueryParams(
                    sbuf,
                    "partialfields",
                    partialfields,
                    partialFieldsOr ? "|" : ".");
        }*//*
        *//*if (requiredfields != null && requiredfields.getMetaFieldMap().size() > 0) {
            Util.appendQueryParam(
                    sbuf,
                    "requiredfields", new MetaFieldsConverter().toParamValue(requiredfields));
        }
        if (partialfields != null && partialfields.getMetaFieldMap().size() > 0) {
            Util.appendQueryParam(
                    sbuf,
                    "partialfields", new MetaFieldsConverter().toParamValue(partialfields));
        }*//*
        if (requiredfields != null) {
            Util.appendQueryParam(
                    sbuf,
                    "requiredfields", requiredfields);
        }
        if (partialfields != null) {
            Util.appendQueryParam(
                    sbuf,
                    "partialfields", partialfields);
        }
        if (getfields != null *//*&& getfields.length > 0*//*) {
            //String allFields = Util.stringSeparated(getfields, "", ".");
            Util.appendQueryParam(sbuf, "getfields", getfields);
        }
        if (site != null *//*&& site.length > 0*//*) {
            //String allSites = Util.stringSeparated(site, "", "|");
            Util.appendQueryParam(sbuf, "site", site);
        }
        if (inmeta != null) Util.appendQueryParam(sbuf, "inmeta", inmeta);
        if (dirs != null) Util.appendQueryParam(sbuf, "dirs", dirs);
        return sbuf.toString();
    }*/

    static final char ACCESS_PUBLIC = 'p';
    static final char ACCESS_SECURE = 's';
    static final char ACCESS_ALL = 'a';

    static final char AS_DT_INCLUDE = 'i';
    static final char AS_DT_EXCLUDE = 'e';

    static final String SEARCH_IN_PAGE = "any";
    static final String SEARCH_IN_TITLE = "title";
    static final String SEARCH_IN_URL = "URL";

    static final char FILTER_DUP_SNIPPET_AND_DIRECTORY = '1';
    static final char FILTER_OFF = '0';
    static final char FILTER_DUP_SNIPPET = 'p';
    static final char FILTER_DUP_DIRECTORY = 's';

    static final String PROXY_CUSTOM_HOME = "<HOME/>";
    static final String PROXY_CUSTOM_ADVANCED = "<ADVANCED/>";
    static final String PROXY_CUSTOM_TEST = "<TEST/>";

    static final char SORT_DIRECTION_ASC = 'A';
    static final char SORT_DIRECTION_DESC = 'D';
    static final char SORT_MODE_RELEVANT_RESULTS = 'S';
    static final char SORT_MODE_ALL_RESULTS = 'R';
    static final char SORT_MODE_NO_SORT_DATE_LOOKUP = 'L';

    static final short DEFAULT_NUM_RESULTS = 10;
    static final short DEFAULT_NUM_KEYMATCHES = 3;
    static final String DEFAULT_INPUT_ENCODING = "latin1";
    static final String DEFAULT_OUTPUT_ENCODING = "UTF8";

    static final short MIN_NUM_RESULTS = 1;
    static final short MAX_NUM_RESULTS = 100;
    static final short MIN_NUM_KEYMATCHES = 0;
    static final short MAX_NUM_KEYMATCHES = 5;

    private static final char _DEFAULT_ACCESS = 'p';
    private static final String _DEFAULT_OUTPUT = "html"; //"xml_no_dtd";
}
