package edu.si.ossearch.nutch.service;

import edu.si.ossearch.OSSearchException;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author jbirkhimer
 */
public interface CrawlUtilsService {

    void addUrls(String jobName, String jobGroup, boolean crawl, List<String> urls) throws IOException, ClassNotFoundException, InterruptedException, OSSearchException;

    List<String> readAll(MultipartFile file);

    void removeUrls(String jobName, String jobGroup, List<String> urls) throws OSSearchException, Exception;
}
