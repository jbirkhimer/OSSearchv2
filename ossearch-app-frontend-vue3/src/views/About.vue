<template>
  <div class="about container">
    <h1>About Open Source Search</h1>
    <div class="row">
      <div class="col-md-12">
        <br>
        <p>Open Source Search (OSS) is a web crawling tool built for Smithsonian OCIO in order to replace the sunsetting Google Search Appliance (GSA). OSS is loosely-based on the Google Search Appliance, and was built to be a drop-in replacement for most of the functionality that Smithsonian units were using from the GSA. OSS is built with Apache Nutch. Nutch itself is built with the Crawler Commons open-source crawler. OSS is comprised of Nutch, some customized indexing filters, mongodb, a PHP-based application, MySQL, and Solr.</p>
        <div id="administration_schema">
          <img src="@/assets/images/administration_schema.png">
        </div>
        <br>
        <div>
          <h2>Connections</h2>
          <p>Connections status are located at the top left in the menu bar, these will be represend as follow:</p>
          <p>When OSS is successfully connected to Master or Slave servers will be shown as <i style="color:#008A00" class="fa fa-circle"></i></p>
          <p>When OSS is not connected to Master, Slave or both will be shown as <i style="color:#dc3545;font-size: 36pt;" class="fa fa-circle"></i></p>
          <p>
            <b>Note: if master is down user won't be able to crawl until server is backup again, this could happen when an schedule reboot is running</b>
          </p>
        </div>
        <div>
          <h2>Features</h2>
          <p>Users can create Search Collections (&quot;collections&quot;) and crawl their collections in order to provide search results in XML or HTML format. JSON format TBD. Facets (&quot;dynamic navigation&quot;) can be configured for collections, to allow filtering results based on facet values and displaying faceted result counts.</p>
        </div>
        <div id="configuringandediting">
          <h3>Configuring and Editing Collections</h3>
          <p>OSS provides a simple UI for creating and editing collections.</p>
        </div>
        <div>
          <h5>Sites to Crawl</h5>
          <p>
            The URLs entered in this textarea define two things:
          <ul>
            <li>1. The &quot;seed&quot; URLs that Nutch will initially crawl to find other URLs on the website.</li>
            <li>2. When documents are indexed into Solr, each document URL is compared against the URLs in &quot;Sites to Crawl&quot;. If the URL base matches
              one of the URLs in this textarea, and is not excluded by one of the exclusion expressions, it will be indexed into the collection.</li>
          </ul>
          </p>
        </div>
        <div>
          <h5>Site Collection Include Response Fields</h5>
          <p>
            By default search results will be returned with Title, Snippet, and URL. Site owners can optionally select Anchor, Meta Description and Meta Keywords to be returned with the search results.
          </p>
          <p>
            Meta Description and Meta Keywords are metatags that are included on some pages; this is website-dependent.
          </p>
          <p>
            Anchor is the text of the link that linked to the page. OSS won’t always find Anchors for all URLs, but here's an example:
          </p>
          <p>
            The URL <a href="https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition">https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition</a> has 2 anchors:
          <ul>
            <li>1. &quot;Latin American Research&quot;, derived from the left-hand menu on this page: <a href="https://siarchives.si.edu/history/featured-topics">https://siarchives.si.edu/history/featured-topics</a></li>
            <li>2. &quot;view in English&quot;, derived from the English language link on this page: <a href="https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0">https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0</a></li>
          </ul>
          </p>
        </div>
        <div>
          <h3>Exclusions</h3>
          <p>Site owners should use robots.txt to determine which website pages get crawled.
            In some cases website owners may want pages to be crawled, but may not want those pages included in the search index. OSS provides the means for website owners to enter &quot;exclusions&quot;, either specific URLs or URL patterns, that OSS will ignore when indexing documents to the search collection.</p>
          <p>OSS implements several exclusion patterns as well as accepting simple URL paths. Expressions are in the table below.</p>
          <div class="table-responsive">
            <table class="table table-striped table-bordered">
              <thead>
              <tr>
                <th style="width:100px">Expression</th>
                <th style="width:500px">Pattern Examples</th>
                <th>Description</th>
                <th>Notes</th>
              </tr>
              </thead>
              <tbody>
              <tr>
                <td>^</td>
                <td>^http:// <br> ^https://</td>
                <td>The ^ character specifies the start of a string.</td>
                <td></td>
              </tr>
              <tr>
                <td>contains:&lt;string&gt; </td>
                <td>
                  <ul>
                    <li>contains:? </li>
                    <li>contains:/benefit/support/flickr</li>
                    <li>contains:/more</li>
                    <li>contains:/quicklinks/</li>
                    <li>contains:blog-tags</li>
                    <li>contains:culid=</li>
                    <li>contains:GenericRequestAudioVisual</li>
                    <li>contains:page-not-found</li>
                    <li>contains:search/node</li>
                  </ul>
                </td>
                <td>
                  To match URLs with a specified string use the contains: prefix. The following example matches any URL containing the string &quot;product.&quot;
                  <br>
                  contains:product
                </td>
                <td></td>
              </tr>
              <tr>
                <td></td>
                <td>https://festival.si.edu/Festival.Collection/</td>
                <td>If a simple path is entered, anything including and below that path is excluded.</td>
                <td></td>
              </tr>
              <tr>
                <td>$</td>
                <td>https://festival.si.edu/blog/$</td>
                <td>The $ character specifies the end of a string.</td>
                <td>The example at left matches only, and exactly, https://festival.si.edu/blog/</td>
              </tr>
              <tr>
                <td>regexp</td>
                <td>
                  <ul>
                    <li>regexp:http://nmai.si.edu/searchcollections/item.aspx.*catids.* </li>
                    <li>regexp:http://nmai.si.edu/searchcollections/item.aspx.*page.* </li>
                    <li>regexp:http://nmai.si.edu/searchcollections/item.aspx[?].*&amp;irn=</li>
                    <li>regexp:http://nmai.si.edu/searchcollections/item.aspx[?]irn=[0-9]+&amp;</li>
                    <li>regexp:http://nmai.si.edu/searchcollections/item.aspx[?]irn=[0-9]+&amp;</li>
                    <li>regexp:http://nmai.si.edu/searchcollections/item.aspx[?]irn=[0-9]+&amp;</li>
                    <li>regexp:http://nmai.si.edu/searchcollections/item.aspx[?]irn=[0-9]+&amp;</li>
                    <li>regexp:http://siarchives.si.edu/[a-zA-Z!"#$%&amp;'()*+,./:;&gt;=&lt;?@\^_`{|}~-]*.htm</li>
                    <li>regexp:http://siarchives.si.edu/[a-zA-Z0-9!"#$%&amp;'()*+,./:;&gt;=&lt;?@\^_`{|}~-]*.htm$</li>
                    <li>regexp:http://siarchives.si.edu/blog[^/]</li>
                    <li>regexp:http://siarchives.si.edu/collections/.*?back=</li>
                    <li>regexp:http://siarchives.si.edu/collections/siris_arc_[0-9]*-</li>
                    <li>regexp:http://siarchives.si.edu/sites/default/files/[.]*</li>
                    <li>regexp:http://siarchives.si.edu/user/.*?=</li>
                    <li>regexp:http://www.aaa.si.edu/collections/findingaids/.*\.pdf</li>
                    <li>regexp:https://siarchives.si.edu/[a-zA-Z0-9!"#$%&amp;'()*+,./:;&gt;=>?@\^_`{|}~-]*.htm$</li>
                    <li>regexp:https://siarchives.si.edu/blog[^/]</li>
                    <li>regexp:https://siarchives.si.edu/blog/tag/[.]*</li>
                    <li>regexp:https://siarchives.si.edu/collections/.*?back=</li>
                    <li>regexp:https://siarchives.si.edu/collections/digitized-fa</li>
                    <li>regexp:https://siarchives.si.edu/collections/siris_arc_[0-9]*-</li>
                    <li>regexp:https://siarchives.si.edu/sites/default/files/[.]*</li>
                    <li>regexp:https://siarchives.si.edu/user/.*?=</li>
                  </ul>
                </td>
                <td>
                  Regular expressions from the GNU Regular Expression library (http://www.cs.utah.edu/dept/old/texinfo/reg ex/regex_toc.html). In the search appliance, regular expressions:
                  <ul>
                    <li>Are case sensitive unless you specify regexpIgnoreCase:</li>
                    <li>Must use two escape characters (a double backslash “\\”) when reserved characters are added to the regular expression.</li>
                  </ul>
                  Note: regexp: and regexpCase: are equivalent.
                </td>
                <td>
                  <ul>
                    <li>regexp:-sid=[0-9A-Z]+/</li>
                    <li>regexp: http://www\\.example\\.google\\.com/.*/images/</li>
                  </ul>
                </td>
              </tr>
              <tr>
                <td>regexpIgnoreCase</td>
                <td>
                  <ul>
                    <li>regexpIgnoreCase:^http://airandspace\\.si\\.edu/explore-and-learn/multimedia/.*name=</li>
                    <li>regexpIgnoreCase:^https://airandspace\\.si\\.edu/.*field_date_value </li>
                    <li>regexpIgnoreCase:^https://airandspace\\.si\\.edu/.*page=</li>
                    <li>regexpIgnoreCase:^https://airandspace\\.si\\.edu/.*tid_ </li>
                    <li>regexpIgnoreCase:americanart.si.edu/@res/\\?* </li>
                    <li>regexpIgnoreCase:americanart.si.edu/art_info/\\?*</li>
                    <li>regexpIgnoreCase:americanart.si.edu/calendar/featured/#/\\?* </li>
                    <li>regexpIgnoreCase:americanart.si.edu/collections/search/\\??resultsPerPage* </li>
                    <li>regexpIgnoreCase:americanart.si.edu/collections/search/\\?rows*</li>
                    <li>regexpIgnoreCase:americanart.si.edu/collections/search/artist/?id=7427</li>
                    <li>regexpIgnoreCase:americanart.si.edu/collections/search/artwork/results/index.cfm\\?*</li>
                    <li>regexpIgnoreCase:americanart.si.edu/collections/search/index_edan.cfm*</li>
                    <li>regexpIgnoreCase:americanart.si.edu/collections/search/index.cfm\\?*</li>
                    <li>regexpIgnoreCase:americanart.si.edu/collections/search/lod/about/browse_artists.cfm\\?*</li>
                    <li>regexpIgnoreCase:americanart.si.edu/exhibitions/online/helios/\\?*</li>
                    <li>regexpIgnoreCase:americanart.si.edu/Helios/\\?* </li>
                    <li>regexpIgnoreCase:americanart.si.edu/lunder/about.cfm?amp;</li>
                    <li>regexpIgnoreCase:americanart.si.edu/lunder/comments.cfm?page=</li>
                    <li>regexpIgnoreCase:americanart.si.edu/lunder/email_page.cfm?p=</li>
                    <li>regexpIgnoreCase:americanart.si.edu/lunder/index.cfm?amp;</li>
                    <li>regexpIgnoreCase:http://humanorigins\\.si\\.edu/.*\\?page=.*</li>
                    <li>regexpIgnoreCase:http://nationalzoo\\.si\\.edu/ConservationandScience/MigratoryBirds/Featured_photo/photo grapher\\.cfm\\?photographer=.*\\+$</li>
                  </ul>
                </td>
                <td>Regular expressions that ignore upper/lower casing of characters.</td>
                <td>regexpIgnoreCase: http://www\\.Example \\.Google\\.com/.*/IMAGES/</td>
              </tr>
              </tbody>
            </table>
          </div>
          <div>
            <h5>Excluding Content Within a Document</h5>
            <p>
              OSS provides a feature that will skip certain portions of content within a page. This is not something that gets configured within the OSS admin UI, but something site owners can leverage in the HTML of their website pages.
            </p>
            <p>The CSS class “robots-noindex” can be added to any HTML tag in order to strip the tag and its contents from the indexed content for a page.</p>
            <p><b>Caution: It is imperative that the website page’s HTML be well-formed for this function to work.</b></p>
            <p>If in doubt use an online validator tool to test: <a href="https://validator.w3.org/">https://validator.w3.org/</a></p>
          </div>
          <div>
            <h3>Crawling</h3>
            <p>OSS provides two methods for crawling. Site owners can manually crawl by clicking the “Re-crawl” button at the bottom of the collection page, or they can schedule a search collection to be crawled.</p>
            <p>If a collection is scheduled to be crawled, the Re-crawl button will be disabled. To manually crawl, users have to click Stop Schedule on the schedule page for the collection, then return to the collection page and click the crawl button.</p>
            <p>For information on what gets crawled and what gets excluded please refer to the section above, <a href="#configuringandediting">Configuring and Editing Collections.</a></p>
          </div>
          <div>
            <h3>Overlapping Search Collections</h3>
            <p>Search collections can “share” documents, which allows OSS to keep the overall search index much smaller than it would be otherwise. For example, the search collection for si.edu can include URLs from nationalzoo, siarchives, npg and other websites. This means that when nationalzoo’s search collection gets crawled, siedu’s search collection automatically receives the new content.</p>
            <p>The figure below illustrates the concept of overlapping search collections and the use of exclusions.</p>
            <div>
              <img src="@/assets/images/overlapping.png">
            </div>
          </div>
          <br>
          <div>
            <h3>Viewing Collection Documents</h3>
            <p>OSS provides reports allowing users to view documents in the search index (Solr) for their search collection, and to query the crawler database. Click “Report” at top right when viewing your search collection. The search index report page will be displayed. Links allow users to download URLs, and to click through to view and query records in the crawler database.</p>
            <p>Clicking a record in the table on either the search index report page or the crawler report page provides a detailed “Explain” report for the selected URL. The Explain report attempts to explain why the document was or was not crawled successfully, and why it was or was not successfully indexed for the search collection.</p>
            <p>Users can also query by a specific URL from the Explain report.</p>
          </div>
          <div>
            <h3>Searching</h3>
            <h5>Preview Search</h5>
            <p>To search against a search collection, navigate to the collection’s page, scroll to the bottom, enter a term and click Search. A new browser tab will be launched, and the results will be rendered as HTML or XML depending on the search configuration.</p>
            <h5>Search Queries</h5>
            <div>
              <p>Users can query directly against OSS to test and refine results before using OSS on their websites.</p>
              <p>Here’s an example of a search query: <a href="https://ossearch.si.edu/search?q=amelia&site=airandspace_v2&client=airandspace_v2&output=xml&filter=0&proxyreload=0&getfields=*&btnG=Sea rch&start=40&num=100" target="_blank">https://ossearch.si.edu/search?q=amelia&amp;site=airandspace_v2&amp;client=airandspace_v2&amp;output=xml&amp;filter=0&amp;proxyreload=0&amp;getfields=*&amp;btnG=Sea rch&amp;start=40&amp;num=100</a></p>
              <p>Breaking down the query parameters in the above URL:
              </p>
              <ul class="list-in-container">
                <li>q - the query keyword or phrase (amelia)</li>
                <li>start - an integer representing the start row (40)</li>
                <li>num - an integer representing the number of records to return, a max of 200 per request (100)</li>
                <li>getfields - which metatag values (MT tags in the XML) to return with the results; defaults to all (*); if specifying multiple metatag names, use a dot separator (getfields=Source.Title)</li>
              </ul>
            </div>
          </div>
          <div>
            <h3>Searching With Facets</h3>
            <p>Open Source Search (OSS) was built to support the same facet queries as the legacy Google Search Appliance. To use facets, users have to configure the facets using the OSS user interface, and index their content before facet queries will work.</p>
            <div>
              <h5>Configuring Facets</h5>
              <p>To configure one or more facets, sign in to OSS (<a href="https://ossearch.si.edu">https://ossearch.si.edu</a>) and edit the collection. Click on the Dynamic Navigation link at top right.</p>
              <p>Add a facet by selecting it from the list and clicking the button. You can optionally set the Display Name to override the default, and change the sort field and sort direction. Display Name and Sort affect the metatag values when returned in the XML.</p>
              <p>If you do not see the desired facet in the dropdown list, contact the system administrator to add the new field.</p>
            </div>
            <div>
              <h5>Updating Documents in Solr</h5>
              <p>Once you have added the desired facets, you will need to reindex your documents into Solr.</p>
              <ul class="list-in-container">
                <li>Select your collection from the list and click the Update Collection button.</li>
                <li>A message will appear below the button. </li>
                <li>You can click the View Logs link in this message to watch progress (you will have to reload the logs page to see periodic progress).</li>
              </ul>
            </div>
            <div>
              <h5>Querying With Facets</h5>
              <div>
                <h6>requiredfields</h6>
                <p>Users can do exact match searches using the requiredfields URL parameter.</p>
                <p>For example we can specify a topicids value of 312 like this:<br> <a href="https://ossearch.si.edu/search?q=apollo+spacesuit&site=airandspace_v2&client=airandspace_v2&output=xml&filter=0&proxyreload=0&getfields=*&btnG=Search&requiredfields=topicids:312">https://ossearch.si.edu/search?q=apollo+spacesuit&amp;site=airandspace_v2&amp;client=airandspace_v2&amp;output=xml&amp;filter=0&amp;proxyreload=0&amp;getfields=*&amp;btnG=Search&amp;requiredfields=topicids:312</a></p>
                <p>We can specify two mandatory topicids values like this:<br> <a href="https://ossearch.si.edu/search?q=apollo+spacesuit&site=airandspace_v2&client=airandspace_v2&output=xml&filter=0&proxyreload=0&getfields=*&btnG=Search&requiredfields=topicids:312.topicids:309">https://ossearch.si.edu/search?q=apollo+spacesuit&site=airandspace_v2&amp;client=airandspace_v2&amp;output=xml&amp;filter=0&amp;proxyreload=0&amp;getfields=*&amp;btnG=Search&amp;requiredfields=topicids:312.topicids:309</a></p>
                <p>Or several mandatory facet values like this:<br> <a href="https://ossearch.si.edu/search?q=blues&site=folkways&client=folkways&output=xml&filter=0&proxyreload=0&getfields=*&btnG=Search&requiredfields=Language:English.SubRegion:Northern%20America.Instrument:Autoharp">https://ossearch.si.edu/search?q=blues&amp;site=folkways&amp;client=folkways&amp;output=xml&amp;filter=0&amp;proxyreload=0&amp;getfields=*&amp;btnG=Search&amp;requiredfields=Language:English.SubRegion:Northern%20America.Instrument:Autoharp</a></p>
              </div>
              <div>
                <h6>getfields</h6>
                <p>By default all metatag values that were extracted are returned in the XML. To limit the returned metatags, we can specify just the ones we want like this: <a href="https://ossearch.si.edu/search?q=blues&site=folkways&client=folkways&output=xml&filter=0&proxyreload=0&getfields=CatalogNumber.Instrume nt.keywords.Culture&btnG=Search&requiredfields=Language:English.SubRegion:Northern%20America.Instrument:Autoharp">https://ossearch.si.edu/search?q=blues&amp;site=folkways&amp;client=folkways&amp;output=xml&amp;filter=0&amp;proxyreload=0&amp;getfields=CatalogNumber.Instrume nt.keywords.Culture&amp;btnG=Search&amp;requiredfields=Language:English.SubRegion:Northern%20America.Instrument:Autoharp</a></p>
                <p>To return all metatags use: <b>getfields=*</b> Multiple getfields values must be separated with dot (.) between each field, as in the example above; this is according to the GSA specification.</p>
              </div>
              <div>
                <h6>partialfields</h6>
                <p>Users can use partialfields to do wildcard searches against metatag values.</p>
                <p>For example: <a href="https://ossearch.si.edu/search?q=bear&site=siarchives&client=siarchives&output=xml&filter=0&proxyreload=0&getfields=*&btnG=Search&start=40&num=100&partialfields=dc.format:finding">https://ossearch.si.edu/search?q=bear&amp;site=siarchives&amp;client=siarchives&amp;output=xml&amp;filter=0&amp;proxyreload=0&amp;getfields=*&amp;btnG=Search&amp;start=40&amp;num=100&amp;partialfields=dc.format:finding</a></p>
              </div>
              <div>
                <h6>inmeta</h6>
                <p>The inmeta tag allows searching a range. At the time of writing of this document there is only a single inmeta tag in use across the Smithsonian, the “gEra” tag which represents years. Here’s an example search using a date range: <a href="https://ossearch.si.edu/search?q=bear&site=siarchives&client=siarchives&output=xml&filter=0&proxyreload=0&getfields=*&btnG=Search&inmeta=gEra:1938..1939">https://ossearch.si.edu/search?q=bear&amp;site=siarchives&amp;client=siarchives&amp;output=xml&filter=0&amp;proxyreload=0&amp;getfields=*&amp;btnG=Search&amp;inmeta=gEra:1938..1939</a></p>
              </div>
            </div>

          </div>
          <div>
            <h2>Website Checklist</h2>
            <p>Checklist for creating a new search collection.</p>
            <div class="table-responsive">
              <table class="table table-bordered table-striped">
                <thead>
                <tr>
                  <td>Step</td>
                  <td>Description</td>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td>Determine URLs to crawl</td>
                  <td>This is a combination of any specific URLs that you want OSS to crawl as well as the base URL(s) for the search collection. The base URLs determine which URLs get included in your collection, in conjunction with the robots.txt rules.</td>
                </tr>
                <tr>
                  <td>Review robots.txt</td>
                  <td>
                    <ul>
                      <li>Make sure sitemap is listed.</li>
                      <li>Check Disallow directives</li>
                      <li>Check Crawl Delay</li>
                    </ul>
                  </td>
                </tr>
                <tr>
                  <td>Review sitemap(s)</td>
                  <td></td>
                </tr>
                <tr>
                  <td>Consider exclusions</td>
                  <td>Consider excluding any documents that you want OSS to crawl, but which you do not want to show up in the search results.</td>
                </tr>
                <tr>
                  <td>Configure the collection in OSS</td>
                  <td></td>
                </tr>
                <tr>
                  <td>Run initial crawls</td>
                  <td></td>
                </tr>
                <tr>
                  <td>Test results</td>
                  <td></td>
                </tr>
                <tr>
                  <td>Tweak robots, sitemaps, exclusions as needed</td>
                  <td></td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<style scoped>
#administration_schema img{
  width: 100%;
}

td ul li{
  list-style: square;
  list-style-position: inside;
}
td{
  word-wrap: break-word;

}
.list-in-container li{
  list-style: square;
  list-style-position: inside;
}
table{
  table-layout:fixed;
}
</style>