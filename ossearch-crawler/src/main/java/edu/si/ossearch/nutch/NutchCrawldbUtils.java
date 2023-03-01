package edu.si.ossearch.nutch;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.si.ossearch.nutch.entity.Inlink;
import edu.si.ossearch.nutch.entity.Webpage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.output.MapFileOutputFormat;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.CrawlDbReader;
import org.apache.nutch.crawl.FetchSchedule;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author jbirkhimer
 */
@Slf4j
public class NutchCrawldbUtils implements Closeable {

    private MapFile.Reader[] readers = null;
    //private SequenceFile.Reader[] readers = null;
    private long lastModified = 0;
    private ObjectMapper jsonMapper = new ObjectMapper();
    private ObjectWriter jsonWriter;

    public NutchCrawldbUtils() {
        jsonMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Writable.class, new CrawlDbReader.CrawlDatumJsonOutputFormat.WritableSerializer());
        jsonMapper.registerModule(module);
        jsonWriter = jsonMapper.writer(new CrawlDbReader.JsonIndenter());
    }

    public JSONArray dumpCrawldbJson(Path crawldb, Configuration conf) {

        JSONArray crawldbData = new JSONArray();
        List<Map<String, Object>> crawldbList = new ArrayList<>();

        try {
            openReaders(crawldb.toString(), conf);

            for (int i = 0; i < readers.length; i++) {

                MapFile.Reader reader = readers[i];
                //SequenceFile.Reader reader = readers[i];

                Text key = new Text();
                CrawlDatum value = new CrawlDatum();

                while (reader.next(key, value)) {

                    log.debug("crawldb: {}, key: {}, value: {}", crawldb, key, value);

                    String answer = writeJson(key, value);
                    crawldbData.put(new JSONObject(answer));
                }
                reader.close();
            }

            closeReaders();

        } catch (IOException e) {
            log.error("problem reading crawldb {}", crawldb, e);
            closeReaders();
        }

        log.debug("crawldb dump: {}", crawldbData.toString(2));
        log.info("crawldbData url count: {}", crawldbData.length());

        return crawldbData;
    }

    public ByteArrayInputStream dumpCrawldbCsv(Path crawldb, Configuration conf) {

        String[] csvHeader = "Url,Status code,Status name,Fetch Time,Modified Time,Retries since fetch,Retry interval seconds,Retry interval days,Score,Signature,Metadata".split(",");
        ByteArrayInputStream byteArrayOutputStream;

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // defining the CSV printer
                CSVPrinter csvPrinter = new CSVPrinter(
                        new PrintWriter(out),
                        // withHeader is optional
                        CSVFormat.DEFAULT.withHeader(csvHeader)
                );
        ) {
            openReaders(crawldb.toString(), conf);

            for (int i = 0; i < readers.length; i++) {

                MapFile.Reader reader = readers[i];
                //SequenceFile.Reader reader = readers[i];

                Text key = new Text();
                CrawlDatum value = new CrawlDatum();

                while (reader.next(key, value)) {

                    log.debug("crawldb: {}, key: {}, value: {}", crawldb, key, value);

                    // populating the CSV content
                    List<String> row = writeCsv(key, value);
                    csvPrinter.printRecord(row);
                    // writing the underlying stream
                    csvPrinter.flush();
                }
                reader.close();
            }

            closeReaders();

            byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            log.error("problem reading crawldb {}", crawldb, e);
            closeReaders();
            throw new RuntimeException(e.getMessage());
        }

        return byteArrayOutputStream;
    }

    public List<Map<String, Object>> dumpCrawldbList(Path crawldb, Configuration conf) {

        List<Map<String, Object>> crawldbData = new ArrayList<>();

        try {
            openReaders(crawldb.toString(), conf);

            for (int i = 0; i < readers.length; i++) {

                MapFile.Reader reader = readers[i];
                //SequenceFile.Reader reader = readers[i];

                Text key = new Text();
                CrawlDatum value = new CrawlDatum();

                while (reader.next(key, value)) {

                    log.debug("crawldb: {}, key: {}, value: {}", crawldb, key, value);

                    String answer = writeJson(key, value);
                    crawldbData.add(new JSONObject(answer).toMap());
                }
                reader.close();
            }

            closeReaders();

        } catch (IOException e) {
            log.error("problem reading crawldb {}", crawldb, e);
            closeReaders();
        }

        log.debug("crawldb dump: {}", crawldbData);
        log.info("crawldbData url count: {}", crawldbData.size());

        return crawldbData;
    }

    public List<Webpage> dumpCrawlDatumEntityList(Path crawldb, Configuration conf, Integer collectionId) {

        List<Webpage> crawldbData = new ArrayList<>();

        try {
            openReaders(crawldb.toString(), conf);

            for (int i = 0; i < readers.length; i++) {

                MapFile.Reader reader = readers[i];
                //SequenceFile.Reader reader = readers[i];

                Text key = new Text();
                CrawlDatum value = new CrawlDatum();

                while (reader.next(key, value)) {

                    log.debug("crawldb: {}, key: {}, value: {}", crawldb, key, value);

                    Webpage answer = writeEntity(key, value, collectionId);
                    crawldbData.add(answer);
                }
                reader.close();
            }

            closeReaders();

        } catch (IOException e) {
            log.error("problem reading crawldb {}", crawldb, e);
            closeReaders();
        }

        log.debug("crawldb dump: {}", crawldbData);
        log.info("crawldbData url count: {}", crawldbData.size());

        return crawldbData;
    }

    public Map<String, Inlink> dumpInlinksEntityList(Path crawldb, Configuration conf, Integer collectionId) {

        Map<String, Inlink> crawldbData = new HashMap<>();

        try {
            openReaders(crawldb.toString(), conf);

            for (int i = 0; i < readers.length; i++) {

                MapFile.Reader reader = readers[i];
                //SequenceFile.Reader reader = readers[i];

                Text key = new Text();
//                CrawlDatum value = new CrawlDatum();
                Inlinks value = new Inlinks();

                while (reader.next(key, value)) {

                    log.debug("crawldb: {}, key: {}, value: {}", crawldb, key, value);

                    Inlink inlinkList = writeInlinks(key, value, collectionId);
                    crawldbData.put(key.toString(), inlinkList);
                }
                reader.close();
            }

            closeReaders();

        } catch (IOException e) {
            log.error("problem reading crawldb {}", crawldb, e);
            closeReaders();
        }

        log.info("crawldb dump: {}", crawldbData);
        log.info("crawldbData url count: {}", crawldbData.size());

        return crawldbData;
    }

    public synchronized Inlink writeInlinks(Text key, Inlinks value, Integer collectionId) {
        Inlink inlink = new Inlink();
        inlink.setId(UUID.nameUUIDFromBytes(key.toString().getBytes(StandardCharsets.UTF_8)).toString());
        inlink.setUrl(key.toString());

        Iterator<org.apache.nutch.crawl.Inlink> it = value.iterator();
        while (it.hasNext()) {
            org.apache.nutch.crawl.Inlink next = it.next();

            String anchor = next.getAnchor();
            String fromUrl = next.getFromUrl();

            if (anchor != null && !anchor.isEmpty()) {
                inlink.getAnchors().add(anchor);
            }

            if (fromUrl != null && !fromUrl.isEmpty()) {
                inlink.getFromUrl().add(fromUrl);
            }
        }
        return inlink;
    }

    public synchronized String writeJson(Text key, CrawlDatum value) throws IOException {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("url", key.toString());
        data.put("statusCode", value.getStatus());
        data.put("statusName", CrawlDatum.getStatusName(value.getStatus()));
        data.put("fetchTime", new Date(value.getFetchTime()).toString());
        data.put("modifiedTime", new Date(value.getModifiedTime()).toString());
        data.put("retriesSinceFetch", value.getRetriesSinceFetch());
        data.put("retryIntervalSeconds", value.getFetchInterval());
        data.put("retryIntervalDays", (value.getFetchInterval() / FetchSchedule.SECONDS_PER_DAY));
        data.put("score", value.getScore());
        data.put("signature", (value.getSignature() != null ? StringUtil.toHexString(value.getSignature()) : "null"));
        Map<String, Object> metaData = null;
        if (value.getMetaData() != null) {
            metaData = new LinkedHashMap<String, Object>();
            for (Map.Entry<Writable, Writable> e : value.getMetaData().entrySet()) {
                metaData.put(e.getKey().toString(), e.getValue());
            }
        }
        if (metaData != null) {
            data.put("metadata", metaData);
        } else {
            data.put("metadata", "");
        }

        return jsonWriter.writeValueAsString(data);
    }

    public synchronized List<String> writeCsv(Text key, CrawlDatum value) throws IOException {
        List<String> row = new ArrayList<>();

        row.add(key.toString());
        row.add(Integer.toString(value.getStatus()));
        row.add(CrawlDatum.getStatusName(value.getStatus()));
        row.add(new Date(value.getFetchTime()).toString());
        row.add(new Date(value.getModifiedTime()).toString());
        row.add(Integer.toString(value.getRetriesSinceFetch()));
        row.add(Float.toString(value.getFetchInterval()));
        row.add(Float.toString((value.getFetchInterval() / FetchSchedule.SECONDS_PER_DAY)));
        row.add(Float.toString(value.getScore()));
        row.add(value.getSignature() != null ? StringUtil.toHexString(value.getSignature()) : "null");
        if (value.getMetaData() != null) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Writable, Writable> e : value.getMetaData().entrySet()) {
                sb.append(e.getKey().toString());
                sb.append(':');
                sb.append(e.getValue().toString());
                sb.append("|||");
            }
            row.add(sb.toString());
        }

        return row;
    }

    public synchronized Webpage writeEntity(Text key, CrawlDatum value, Integer collectionId) throws IOException {
        Webpage data = new Webpage();
        data.setId(UUID.nameUUIDFromBytes(key.toString().getBytes(StandardCharsets.UTF_8)).toString());
        data.setCollectionId(collectionId);
        data.setUrl(key.toString());
        data.setStatusCode(Integer.valueOf(value.getStatus()));
        data.setStatusName(CrawlDatum.getStatusName(value.getStatus()));
        data.setFetchTime(new Date(value.getFetchTime()));
        data.setModifiedTime(new Date(value.getModifiedTime()));
        data.setRetriesSinceFetch(Integer.valueOf(value.getRetriesSinceFetch()));
        data.setRetryIntervalSeconds(value.getFetchInterval());
        data.setRetryIntervalDays((value.getFetchInterval() / FetchSchedule.SECONDS_PER_DAY));
        data.setScore(value.getScore());
        data.setSignature((value.getSignature() != null ? StringUtil.toHexString(value.getSignature()) : "null"));
        Map<String, String> metaData = null;
        if (value.getMetaData() != null) {
            metaData = new LinkedHashMap<String, String>();
            for (Map.Entry<Writable, Writable> e : value.getMetaData().entrySet()) {
                metaData.put(e.getKey().toString(), e.getValue().toString());
            }
        }
        if (metaData != null) {
            data.setMetadata(new JSONObject(metaData).toString());
        }

        return data;
    }

    private void openReaders(String crawlDb, Configuration config) throws IOException {
        Path crawlDbPath = new Path(crawlDb, org.apache.nutch.crawl.CrawlDb.CURRENT_NAME);

        FileSystem fs = crawlDbPath.getFileSystem(config);
        FileStatus stat = fs.getFileStatus(crawlDbPath);
        long lastModified = stat.getModificationTime();

        //File file = new File(crawlDbPath.toString());
        //long lastModified = file.lastModified();

        synchronized (this) {
            if (readers != null) {
                if (this.lastModified == lastModified) {
                    // CrawlDB not modified, re-use readers
                    return;
                } else {
                    // CrawlDB modified, close and re-open readers
                    closeReaders();
                }
            }

            this.lastModified = lastModified;
            readers = MapFileOutputFormat.getReaders(crawlDbPath, config);
            //readers = getReaders(crawlDbPath, config);
        }
    }

    public static MapFile.Reader[] getReaders(Path dir,
                                              Configuration conf) throws IOException {
        FileSystem fs = dir.getFileSystem(conf);
        PathFilter filter = new PathFilter() {
            @Override
            public boolean accept(Path path) {
                String name = path.getName();
                if (name.startsWith("_") || name.startsWith("."))
                    return false;
                return true;
            }
        };
        Path[] names = FileUtil.stat2Paths(fs.listStatus(dir, filter));

        /*File file = new File(dir.toString());
        String[] names = file.list((current, name) -> new File(current, name).isDirectory());*/

        // sort names, so that hash partitioning works
        Arrays.sort(names);

        MapFile.Reader[] parts = new MapFile.Reader[names.length];
        for (int i = 0; i < names.length; i++) {
            //parts[i] = new MapFile.Reader(new Path(names[i]), conf);
            parts[i] = new MapFile.Reader(names[i], conf);
        }
        return parts;
    }

    private void closeReaders() {
        if (readers == null)
            return;
        for (int i = 0; i < readers.length; i++) {
            try {
                readers[i].close();
            } catch (Exception e) {
                log.error("error closing reader", e);
            }
        }
        /*try {
            fs.close();
        } catch (IOException e) {
            log.error("error closing filesystem", e);
        }*/
        readers = null;
    }


    /**
     * Use SequenceFile.Reader[] instead of MapFile.Reader[]
     **/
    /*public void openReaders(String crawlDb, Configuration conf) throws IOException {

        Path crawlDbPath = new Path(crawlDb, org.apache.nutch.crawl.CrawlDb.CURRENT_NAME);

        FileSystem fs = crawlDbPath.getFileSystem(conf);
        PathFilter filter = new PathFilter() {
            @Override
            public boolean accept(Path path) {
                String name = path.getName();
                if (name.startsWith("_") || name.startsWith("."))
                    return false;
                return true;
            }
        };

        Path[] names = FileUtil.stat2Paths(fs.listStatus(crawlDbPath, filter));

        *//*File file = new File(crawlDbPath.toString());
        String[] names = file.list((current, name) -> new File(current, name).isDirectory());*//*

        // sort names, so that hash partitioning works
        Arrays.sort(names);

        SequenceFile.Reader[] parts = new SequenceFile.Reader[names.length];
        for (int i = 0; i < names.length; i++) {
            //Path datafilePart = new Path(crawlDbPath, names[i]);
            //Path dataFile = new Path(datafilePart, DATA_FILE_NAME);
            //parts[i] = new SequenceFile.Reader(conf, SequenceFile.Reader.file(dataFile));
            Path dataFile = new Path(names[i], DATA_FILE_NAME);
            parts[i] = new SequenceFile.Reader(conf, SequenceFile.Reader.file(dataFile));
        }
        readers = parts;
    }*/


    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        closeReaders();
    }
}
