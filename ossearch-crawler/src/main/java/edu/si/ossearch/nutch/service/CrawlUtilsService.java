package edu.si.ossearch.nutch.service;

import edu.si.ossearch.OSSearchException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author jbirkhimer
 */
public interface CrawlUtilsService {

    void addUrls(String jobName, String jobGroup, boolean crawl, List<String> urls) throws IOException, ClassNotFoundException, InterruptedException, OSSearchException;

    @Async
    Future<Void> async_updateDb(String jobName, String jobGroup);

    List<String> readAll(MultipartFile file);

    void removeUrls(String jobName, String jobGroup, List<String> urls) throws OSSearchException, Exception;

    HashMap<String, Object> parseChecker(String collectionName, String url, boolean normalize, boolean checkRobotsTxt, boolean dumpText, boolean followRedirects) throws OSSearchException, Exception;
}
