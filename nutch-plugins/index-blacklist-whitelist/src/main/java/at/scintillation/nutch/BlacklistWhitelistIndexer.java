package at.scintillation.nutch;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.parse.Parse;

/**
 * Class to index the content which has been parsed and stored in the {@link BlacklistWhitelistParser}.
 * The Lucene index field name containing the stripped content is called "strippedContent".
 * 
 * @author Elisabeth Adler
 */
public class BlacklistWhitelistIndexer implements IndexingFilter
{

    private Configuration conf;

    @Override
    public NutchDocument filter(NutchDocument doc, Parse parse, Text url, CrawlDatum datum, Inlinks inlinks)
        throws IndexingException
    {
        // Attempt to get the headings
        String strippedContent = parse.getData().getMeta("strippedContent");
        if (strippedContent != null)
        {
            //doc.add("rawContent", doc.getFieldValue("content"));
            doc.removeField("content");
            doc.add("content", strippedContent.replaceAll("\\n", " "));
        }

        return doc;
    }

    public void setConf(Configuration conf)
    {
        this.conf = conf;
    }

    public Configuration getConf()
    {
        return this.conf;
    }

}