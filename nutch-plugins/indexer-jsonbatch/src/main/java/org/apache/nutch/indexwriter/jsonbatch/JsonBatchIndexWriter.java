package org.apache.nutch.indexwriter.jsonbatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.indexer.IndexWriter;
import org.apache.nutch.indexer.IndexWriterParams;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.indexer.NutchField;
import org.apache.nutch.util.TableUtil;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.util.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * JsonBatchIndexWriter. This pluggable indexer writes json lines to a
 * plain text file for EDAN loading.
 */
public class JsonBatchIndexWriter implements IndexWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Configuration config;
    private String path;
    private int batchSize;

    private String weightField;
    private int numDeletes = 0;
    private int totalAdds = 0;
    private int totalDeletes = 0;
    private boolean delete = false;
    private String jsonString;

    private List<String> inputDocs = new ArrayList<>();
    private final List<String> deleteIds = new ArrayList<>();

    private String collectionID;

    private JSONObject edanFieldMapping;

    @Override
    public void open(Configuration conf, String name) throws IOException {
        //Implementation not required
    }

    /**
     * Initializes the internal variables from a given index writer configuration.
     *
     * @param parameters Params from the index writer configuration.
     * @throws IOException Some exception thrown by writer.
     */
    @Override
    public void open(IndexWriterParams parameters) throws IOException {
        delete = parameters.getBoolean(JsonBatchConstants.DELETE, false);
        batchSize = parameters.getInt(JsonBatchConstants.BATCH_SIZE, 100);
        weightField = parameters.get(JsonBatchConstants.WEIGHT_FIELD, "");

        path = parameters.get(JsonBatchConstants.PATH, "/");

        if (path == null) {
            String message = "Missing path.";
            message += "\n" + describe();
            LOG.error(message);
            throw new RuntimeException(message);
        }

        collectionID = String.join("|", config.get("moreIndexingFilter.collectionIDs", "0").split(" "));

        edanFieldMapping = new JSONObject(config.get("edanFieldMapping"));

        Files.createDirectories(Paths.get(path));
    }

    @Override
    public void delete(String key) throws IOException {
        // escape hash separator
        key = key.replaceAll("!", "\\!");
        key = TableUtil.reverseUrl(key);

        if (delete) {
            Map<String, Object> source = new HashMap<String, Object>();

            source.put("id", key);
            source.put("status", -1);

            jsonString = new ObjectMapper().writeValueAsString(source);

            deleteIds.add(jsonString);
            totalDeletes++;
        }

        if (deleteIds.size() >= batchSize) {
            push();
        }
    }

    @Override
    public void update(NutchDocument doc) throws IOException {
        write(doc);
    }

    @Override
    public void write(NutchDocument doc) throws IOException {
        /*Map<String, Object> source = new HashMap<String, Object>();

        // Loop through all fields of this doc
        for (String fieldName : doc.getFieldNames()) {
            Set<String> allFieldValues = new HashSet<String>();
            if (fieldName.equals("collectionID")) {
                for (Object value : doc.getField(fieldName).getValues()) {
                    String[] collectionIds = (String[]) ((LinkedHashMap) value).get("add");
                    allFieldValues.addAll(Arrays.asList(collectionIds));
                }
            } else {
                for (Object value : doc.getField(fieldName).getValues()) {
                    allFieldValues.add(value.toString());
                }
            }

            String[] fieldValues = allFieldValues
                    .toArray(new String[allFieldValues.size()]);
            source.put(fieldName, fieldValues);
//            source.put("status", 0);
        }

        jsonString = new ObjectMapper().writeValueAsString(source);*/

        final SolrInputDocument inputDoc = new SolrInputDocument();

        for (final Map.Entry<String, NutchField> e : doc) {
            if (e.getKey().equals("id")) {
                String val = (String) e.getValue().getValues().get(0);
                String id = TableUtil.reverseUrl(val);
                inputDoc.addField(e.getKey(), id);
            } else if (e.getKey().equals("meta__all_metatags")) {
                List<String> meta__all_metatags = new ArrayList<>();
                e.getValue().getValues().forEach(value -> meta__all_metatags.add((String) value));
                Collections.sort(meta__all_metatags);
                inputDoc.addField(e.getKey(), meta__all_metatags);
            } /*else if (e.getKey().equals("collectionID")) {
                List<Object> collectionIDs = e.getValue().getValues();
                Map<String, Object> collectionIDFieldModifier = new HashMap<>(1);
                collectionIDFieldModifier.put("add", collectionIDs);
                inputDoc.addField("collectionID", collectionIDFieldModifier);

            }*/ else {
                for (final Object val : e.getValue().getValues()) {
                    Object val2 = val;

                    // normalise the string representation for a Date
                    if (val instanceof Date) {
                        val2 = DateTimeFormatter.ISO_INSTANT.format(((Date) val).toInstant());
                    }

                    if (e.getKey().equals("content") || e.getKey().equals("title")) {
                        val2 = stripNonCharCodepoints((String) val);
                    }

                    inputDoc.addField(e.getKey(), val2);
                }
            }
        }

        if (!weightField.isEmpty()) {
            inputDoc.addField(weightField, doc.getWeight());
        }

        // add timestamp when indexed
        inputDoc.addField("_last_time_updated", DateTimeFormatter.ISO_INSTANT.format(new Date().toInstant()));

        JSONObject json = new JSONObject();
        inputDoc.keySet().forEach(field -> {
            SolrInputField solrField = inputDoc.getField(field);
            if (solrField.getName().equals("collectionID")) {
             LOG.debug("stop");
                json.put(solrField.getName(), collectionID.split("\\|"));
            } else {
                json.put(solrField.getName(), solrField.getValue());
            }
        });


        jsonString = json.toString();

        inputDocs.add(jsonString);
        totalAdds++;

        if (inputDocs.size() == batchSize) {
            push();
        }
    }

    static String stripNonCharCodepoints(String input) {
        StringBuilder retval = new StringBuilder();
        char ch;

        for (int i = 0; i < input.length(); i++) {
            ch = input.charAt(i);

            // Strip all non-characters
            // http://unicode.org/cldr/utility/list-unicodeset.jsp?a=[:Noncharacter_Code_Point=True:]
            // and non-printable control characters except tabulator, new line and
            // carriage return
            if (ch % 0x10000 != 0xffff && // 0xffff - 0x10ffff range step 0x10000
                    ch % 0x10000 != 0xfffe && // 0xfffe - 0x10fffe range
                    (ch <= 0xfdd0 || ch >= 0xfdef) && // 0xfdd0 - 0xfdef
                    (ch > 0x1F || ch == 0x9 || ch == 0xa || ch == 0xd)) {

                retval.append(ch);
            }
        }

        return retval.toString();
    }

    @Override
    public void close() throws IOException {
        commit();
    }

    @Override
    public void commit() throws IOException {
        push();
    }

    private void push() throws IOException {
        String outputDir = path + "/collectionID_" + collectionID;
        Files.createDirectories(Paths.get(outputDir));

        if (inputDocs.size() > 0) {
            String fileName = outputDir + "/" + new Date().getTime() + ".txt";

            try {
                LOG.info("Batch count: {}, file: {}}", inputDocs.size(), fileName);
                LOG.info("Writing batch {}/{} documents", inputDocs.size(), totalAdds);
                LOG.info("Deleting {} documents", numDeletes);
                numDeletes = 0;

                //write to file
                Files.write(Paths.get(fileName), inputDocs);

                inputDocs.clear();
            } catch (IOException e) {
                LOG.error("Unable to write batch {}", fileName, e);
            }
        }

        if (deleteIds.size() > 0) {
            //write to file
            String fileName = outputDir + "/delete_" + new Date().getTime() + ".txt";

            try {
                LOG.info("SolrIndexer: deleting {}/{} documents", deleteIds.size(), totalDeletes);
                LOG.info("Delete Batch count: {}, file: {}}", deleteIds.size(), fileName);

                //write to file
                Files.write(Paths.get(fileName), deleteIds);

                deleteIds.clear();
            } catch (IOException e) {
                LOG.error("Unable to write batch {}", fileName, e);
            }
        }
    }

    @Override
    public Configuration getConf() {
        return config;
    }

    @Override
    public void setConf(Configuration conf) {
        config = conf;
    }

    /**
     * Returns {@link Map} with the specific parameters the IndexWriter instance can take.
     *
     * @return The values of each row. It must have the form <KEY,<DESCRIPTION,VALUE>>.
     */
    @Override
    public Map<String, Map.Entry<String, Object>> describe() {
        Map<String, Map.Entry<String, Object>> properties = new LinkedHashMap<>();

        properties.put(JsonBatchConstants.DELETE, new AbstractMap.SimpleEntry<>(
                "If delete operations should be written to the file.", this.delete));
        properties.put(JsonBatchConstants.PATH, new AbstractMap.SimpleEntry<>(
                "Path where the file will be created.", this.path));

        return properties;
    }
}
