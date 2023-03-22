package edu.si.ossearch.nutch.service.impl;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import edu.si.ossearch.OSSearchException;
import edu.si.ossearch.collection.entity.CrawlConfig;
import edu.si.ossearch.collection.repository.CrawlConfigRepository;
import edu.si.ossearch.nutch.NutchCrawldbUtils;
import edu.si.ossearch.nutch.ParserChecker;
import edu.si.ossearch.nutch.entity.Webpage;
import edu.si.ossearch.nutch.repository.WebpageRepository;
import edu.si.ossearch.nutch.service.CrawlUtilsService;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import edu.si.ossearch.scheduler.entity.NutchStepArgs;
import edu.si.ossearch.scheduler.repository.CrawlSchedulerJobInfoRepository;
import edu.si.ossearch.scheduler.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.nutch.crawl.Injector;
import org.apache.nutch.util.NutchConfiguration;
import org.apache.solr.client.solrj.SolrClient;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import static edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo.JobType.ADD_URLS;
import static org.apache.nutch.util.TableUtil.reverseUrl;

/**
 * @author jbirkhimer
 */
@Slf4j
@Service
public class CrawlUtilsServiceImpl implements CrawlUtilsService {

    @Value(value = "${ossearch.nutch.crawlDir}")
    File crawlDir;

    @Autowired
    private CrawlSchedulerJobInfoRepository schedulerRepository;

    @Autowired
    private CrawlConfigRepository crawlConfigRepository;

    @Autowired
    private WebpageRepository webpageRepository;

    @Value(value = "${spring.data.solr.collection}")
    String solrCollection;

    @Autowired
    @Qualifier("master")
    private SolrClient solrClient;

    @Autowired
    private JobService jobService;

    @Autowired
    private Scheduler scheduler;

    @Override
    public void addUrls(String jobName, String jobGroup, boolean crawl, List<String> urls) throws IOException, ClassNotFoundException, InterruptedException, OSSearchException {
        // su apache -s /bin/bash -c '/opt/nutch-1.18/bin/nutch inject /opt/ossearch-crawler/crawls/postal_88/db/crawldb /opt/ossearch-crawler/crawls/postal_88/add-seeds.txt -overwrite -noNormalize -noFilter'

        boolean isJobRunning = jobService.isJobRunning(jobName, jobGroup);
        boolean isAddUrlJobRunning = jobService.isJobRunning(jobName, "add_urls");

        if (isJobRunning || isAddUrlJobRunning) {
            throw new OSSearchException("A crawl is currently running for this collection. Try again when the crawl is finished or stop the current crawl and try again!");
        }

        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, jobGroup);

        CrawlConfig crawlConfig = crawlConfigRepository.getCrawlConfigByCollectionName(jobInfo.getCollectionName());

        crawlConfig.getExcludeSiteUrls().removeAll(urls);
        crawlConfig.getIncludeSiteUrls().addAll(urls);
        crawlConfigRepository.save(crawlConfig);

        if (jobInfo == null || crawlConfig == null) {
            throw new OSSearchException("addUrls Crawl Job not Created! Crawl Schedule or CrawlConfig does not exists for collection "+jobName+"!");
        }

        Properties properties = new Properties();
        properties.putAll(jobInfo.getNutchProperties());

        // Create a default configuration object, which will read
        // the content of nutch-default.xml file from the classpath,
        // parse it and take its entire content as the default
        // configuration. Funny, but this interface is from Hadoop:
        //        Configuration conf = NutchConfiguration.create();
        Configuration conf = NutchConfiguration.create(true, properties);
        log.info(">>>>> http.agent.name: {}", conf.get("http.agent.name"));

        // Now it's possible to reset some configuration parameters
        // by using this set() method. This one is mandatory, if you
        // don't set it the crawling won't work. The value is used
        // as User-Agent HTTP header.
        conf.set("hadoop.log.dir", "logs");
        conf.set("hadoop.tmp.dir", "hadoop_tmp");
        conf.set("hadoop.log.file", "logs/hadoop.log");

//        crawlConfig.getIncludeSiteUrls().addAll(urls);
//        crawlConfig.getExcludeSiteUrls().removeAll(urls);
//        crawlConfigRepository.save(crawlConfig);

        Path crawlBaseDir = getCrawlBaseDir(jobInfo);
        Path dbDir = new Path(crawlBaseDir, "db");
        Path crawldbDir = new Path(dbDir, "crawldb");

        Path seedDir = new Path(crawlBaseDir, "seeds");
        Path addUrlsFile = new Path(seedDir, "add-urls.txt");

        Files.createDirectories(Paths.get(seedDir.toString()));
        Files.write(Paths.get(addUrlsFile.toString()), String.join("\n", urls).getBytes());

        new Injector(conf).inject(crawldbDir, addUrlsFile, true, true);

        Files.delete(Paths.get(addUrlsFile.toString()));

        if (crawl) {

            if (!jobService.isJobWithNamePresent(jobName,"add_urls")) {
                //run (1) crawl round after adding urls to crawldb
                CrawlSchedulerJobInfo addUrlsJobInfo = new CrawlSchedulerJobInfo();

                addUrlsJobInfo.setJobName(jobName);
                addUrlsJobInfo.setJobGroup("add_urls");
                addUrlsJobInfo.setJobType(ADD_URLS);
                addUrlsJobInfo.setNumberOfRounds(1);
                addUrlsJobInfo.setCronJob(false);

                addUrlsJobInfo.setCollectionId(jobInfo.getCollectionId());
                addUrlsJobInfo.setCollectionName(jobInfo.getCollectionName());


                addUrlsJobInfo.getCrawlOptions().putAll(jobInfo.getCrawlOptions());
                addUrlsJobInfo.getCrawlOptions().remove("size_fetchlist");
                addUrlsJobInfo.getCrawlOptions().put("sitemaps_from_hostdb", "never");

                addUrlsJobInfo.getNutchProperties().putAll(jobInfo.getNutchProperties());

                NutchStepArgs nutchStepArgs = new NutchStepArgs();
                nutchStepArgs.getInject().putAll(jobInfo.getNutchStepArgs().getInject());
                nutchStepArgs.getSitemap().putAll(jobInfo.getNutchStepArgs().getSitemap());
                nutchStepArgs.getGenerate().putAll(jobInfo.getNutchStepArgs().getGenerate());
                nutchStepArgs.getParse().putAll(jobInfo.getNutchStepArgs().getParse());
                nutchStepArgs.getUpdatedb().putAll(jobInfo.getNutchStepArgs().getUpdatedb());
                nutchStepArgs.getUpdatehostdb().putAll(jobInfo.getNutchStepArgs().getUpdatehostdb());
                nutchStepArgs.getInvertlinks().putAll(jobInfo.getNutchStepArgs().getInvertlinks());
                nutchStepArgs.getDedup().putAll(jobInfo.getNutchStepArgs().getDedup());
                nutchStepArgs.getFetch().putAll(jobInfo.getNutchStepArgs().getFetch());
                nutchStepArgs.getIndex().putAll(jobInfo.getNutchStepArgs().getIndex());

                nutchStepArgs.getGenerate().remove("topN");

                addUrlsJobInfo.setNutchStepArgs(nutchStepArgs);

                CrawlSchedulerJobInfo existingJobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, "add_urls");
                if (existingJobInfo != null) {
                    schedulerRepository.delete(existingJobInfo);
                }

                boolean scheduledAddUrlsCrawl = jobService.scheduleNewJob(addUrlsJobInfo);
                if (!scheduledAddUrlsCrawl) {
                    throw new OSSearchException("addUrls Crawl Job not Created!");
                }
            } else {
                throw new OSSearchException("addUrls Crawl Job not Created! Job already exists! Please wait until the existing job is complete!");
            }

        }
    }

    @Override
    public void removeUrls(String jobName, String jobGroup, List<String> urls) throws Exception, OSSearchException {
        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, jobGroup);

        CrawlConfig crawlConfig = crawlConfigRepository.getCrawlConfigByCollectionName(jobInfo.getCollectionName());

        if (jobInfo == null || crawlConfig == null) {
            throw new OSSearchException("addUrls Crawl Job not Scheduled! crawldb does not exists!");
        }

        crawlConfig.getExcludeSiteUrls().addAll(urls);
        crawlConfig.getIncludeSiteUrls().removeAll(urls);
        crawlConfigRepository.save(crawlConfig);

        List<String> deleteUrls = new ArrayList<>();

        for (String url : urls) {
            deleteUrls.add(reverseUrl(url.trim()));
        }
        log.debug("urls for delete from solr. count: {}", deleteUrls.size());

        solrClient.deleteById(solrCollection, deleteUrls, 1000);

        webpageRepository.deleteWebpagesByCollectionIdAndUrlIn(Integer.valueOf(jobInfo.getCollectionId()), urls);
    }

    @Override
    public HashMap<String, Object> parseChecker(String collectionName, String url, boolean normalize, boolean checkRobotsTxt, boolean dumpText, boolean followRedirects) throws OSSearchException, Exception {

        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(collectionName, "scheduled_crawl");

        if (jobInfo == null) {
            throw new OSSearchException("Parsechecker error! Crawl config does not exists for collection: "+collectionName+"!");
        }

        Properties properties = new Properties();
        properties.putAll(jobInfo.getNutchProperties());

        Configuration conf = NutchConfiguration.create(true, properties);
        log.info(">>>>> http.agent.name: {}", conf.get("http.agent.name"));

        conf.set("hadoop.log.dir", "logs");
        conf.set("hadoop.tmp.dir", "hadoop_tmp");
        conf.set("hadoop.log.file", "logs/hadoop.log");


        ParserChecker parserChecker = new ParserChecker(conf);
        return parserChecker.process(url, normalize, checkRobotsTxt, dumpText, followRedirects);
    }

    @Async
    @Override
    public Future<Void> async_updateDb(String jobName, String jobGroup) {
        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, jobGroup);

        Configuration conf = NutchConfiguration.create();
        conf.set("hadoop.tmp.dir", "hadoop_tmp");
        Path crawlBaseDir = getCrawlBaseDir(jobInfo);
        Path dbDir = new Path(crawlBaseDir, "db");
        Path crawldbDir = new Path(dbDir, "crawldb");

        NutchCrawldbUtils crawldbUtils = new NutchCrawldbUtils();

        List<Webpage> webpageList = crawldbUtils.dumpCrawlDatumEntityList(crawldbDir, conf, Integer.valueOf(jobInfo.getCollectionId()));

        webpageRepository.deleteAllByCollectionId(Integer.valueOf(jobInfo.getCollectionId()));

        webpageRepository.saveAllAndFlush(webpageList);

        log.info("****** Finished crawldb update for: {} ({}), url count: {} ******", jobName, jobInfo.getCollectionId(), webpageList.size());
        return new AsyncResult<>(null);
    }

    private Path getCrawlBaseDir(CrawlSchedulerJobInfo jobInfo) {
        // First, we need to have a directory where everything will happen.
        String collectionCrawlDir = jobInfo.getCollectionName() + "_" + jobInfo.getCollectionId();
        if (!jobInfo.getCollectionName().equals(jobInfo.getJobName())) {
            collectionCrawlDir = jobInfo.getJobName() + "_" + jobInfo.getCollectionName() + "_" + jobInfo.getCollectionId();
        }

        return new Path(crawlDir.getAbsolutePath(), collectionCrawlDir);
    }

    public List<String> readAll(MultipartFile file) {
        List<String> list = null;
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVParser parser = new CSVParserBuilder()
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();

            list = new ArrayList<>();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                list.add(line[0]);
            }
            reader.close();
            csvReader.close();
        } catch (Exception ex) {
            log.error("Problem reading urls csv", ex);
        }
        return list;
    }
}
