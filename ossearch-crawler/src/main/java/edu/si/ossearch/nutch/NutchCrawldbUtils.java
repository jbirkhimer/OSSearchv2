package edu.si.ossearch.nutch;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.output.MapFileOutputFormat;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.CrawlDb;
import org.apache.nutch.crawl.CrawlDbReader;
import org.apache.nutch.crawl.FetchSchedule;
import org.apache.nutch.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
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

                    String answer = write(key, value);
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

                    String answer = write(key, value);
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

    public synchronized String write(Text key, CrawlDatum value) throws IOException {
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

    private void openReaders(String crawlDb, Configuration config) throws IOException {
        Path crawlDbPath = new Path(crawlDb, CrawlDb.CURRENT_NAME);

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

        Path crawlDbPath = new Path(crawlDb, CrawlDb.CURRENT_NAME);

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
