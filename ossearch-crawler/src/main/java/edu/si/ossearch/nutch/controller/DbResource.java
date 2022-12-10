/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.si.ossearch.nutch.controller;

import edu.si.ossearch.OSSearchException;
import edu.si.ossearch.nutch.NutchCrawldbUtils;
import edu.si.ossearch.nutch.entity.projections.CrawldbUrlStatusCounts;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import edu.si.ossearch.scheduler.repository.CrawlSchedulerJobInfoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.nutch.crawl.CrawlDbReader;
import org.apache.nutch.util.NutchConfiguration;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/nutch/utils")
@Tag(description = "Nutch dbReader", name = "Nutch dbReader")
@SecurityRequirement(name = "bearerAuth")
public class DbResource {

    public static final String DEFAULT_JOB_GROUP_NAME = "scheduled_crawl";

    @Value(value = "${ossearch.nutch.pluginsDir}")
    @NonNull
    File pluginDir;
    @Value(value = "${ossearch.nutch.confDir}")
    @NonNull
    File nutchConfDir;
    @Value(value = "${ossearch.nutch.crawlDir}")
    File crawlDir;
    private int num_tasks = 2;
    @Autowired
    private CrawlSchedulerJobInfoRepository schedulerRepository;

    @Operation(summary = "get crawldb stats", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @GetMapping(value = "/crawldb/stats")
    public ResponseEntity<Object> getStats(@RequestParam(value = "jobName") String jobName,
                                           @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup,
                                           @RequestParam(value = "sort", required = false, defaultValue = "false") boolean sort
    ) throws OSSearchException, IOException {

        if (jobName == null || jobGroup == null) {
            return ResponseEntity.badRequest().body("jobName must not be null");
        }

        try {
            Map<String, String> args = new HashMap<>();
            args.put("sort", String.valueOf(sort));

            Configuration conf = NutchConfiguration.create();
            conf.set("hadoop.tmp.dir", "hadoop_tmp");

            Path crawlId = getCrawlDb(jobName, jobGroup);

            CrawlDbReader dbr = new CrawlDbReader();
            Object results = dbr.query(args, conf, "stats", crawlId.toString());
            dbr.close();

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
        }

    }

    @Operation(summary = "get url from crawldb", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @GetMapping(value = "/crawldb/url")
    public ResponseEntity<Object> getUrl(@RequestParam(value = "url") String url,
                                         @RequestParam(value = "jobName") String jobName,
                                         @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup
    ) throws OSSearchException, IOException {

        if (jobName == null || jobGroup == null || url == null) {
            return ResponseEntity.badRequest().body("jobName and url must not be null");
        }

        try {

            Map<String, String> args = new HashMap<>();
            args.put("url", url);

            Configuration conf = NutchConfiguration.create();
            conf.set("hadoop.tmp.dir", "hadoop_tmp");

            Path crawlId = getCrawlDb(jobName, jobGroup);

            CrawlDbReader dbr = new CrawlDbReader();
            Object results = dbr.query(args, conf, "url", crawlId.toString());
            dbr.close();

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
        }

    }

    @Operation(summary = "dump urls from crawldb", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = {@Content(mediaType = "application/csv"), @Content(mediaType = "application/json")})
    })
    @GetMapping(value = "/crawldb/dump")
    public ResponseEntity<Object> getDump(@RequestParam(value = "jobName") String jobName,
                                          @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup,
                                          //@RequestParam(value = "sort", required = false, defaultValue = "false") boolean sort,
                                          @Parameter(name = "type", schema = @Schema(type = "string", allowableValues = {"csv", "json"}, defaultValue = "csv")) String type
    ) throws OSSearchException {

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        Object principal = authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (jobName == null || jobGroup == null) {
            return ResponseEntity.badRequest().body("jobName must not be null");
        }

        try {
            Configuration conf = NutchConfiguration.create();
            conf.set("hadoop.tmp.dir", "hadoop_tmp");

            Path crawlId = getCrawlDb(jobName, jobGroup);

            NutchCrawldbUtils crawldbUtils = new NutchCrawldbUtils();

            if (type.equals("json")) {

                JSONArray result = crawldbUtils.dumpCrawldbJson(new Path(crawlId, "crawldb"), conf);
                //return ResponseEntity.ok().body(result.toString());

                ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(result.toString(2).getBytes(StandardCharsets.UTF_8));

                InputStreamResource fileInputStream = new InputStreamResource(byteArrayOutputStream);

                String csvFileName = jobGroup+"_"+jobName+"_dump_"+new Date().getTime()+".json";

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName)
                        .contentType(MediaType.parseMediaType("application/json"))
                        .body(fileInputStream);

            } else {
                ByteArrayInputStream byteArrayOutputStream = crawldbUtils.dumpCrawldbCsv(new Path(crawlId, "crawldb"), conf);

                InputStreamResource fileInputStream = new InputStreamResource(byteArrayOutputStream);

                String csvFileName = jobGroup+"_"+jobName+"_dump_"+new Date().getTime()+".csv";

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName)
                        .contentType(MediaType.parseMediaType("application/csv"))
                        .body(fileInputStream);
            }


        } catch (Exception e) {
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
        }

    }

    @Operation(summary = "get topN url's from crawldb", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @GetMapping(value = "/crawldb/topN")
    public ResponseEntity<Object> getDumpTopN(@RequestParam(value = "topN", defaultValue = "50000") long topN,
                                              @RequestParam(value = "jobName") String jobName,
                                              @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup,
                                              @RequestParam(value = "sort", required = false, defaultValue = "false") boolean sort
    ) throws OSSearchException {

        if (jobName == null || jobGroup == null) {
            return ResponseEntity.badRequest().body("jobName must not be null");
        }

        return ResponseEntity.ok("Not implemented yet");

        /*try {
            Configuration conf = NutchConfiguration.create();
            conf.set("hadoop.tmp.dir", "hadoop_tmp");

            Path crawlId = getCrawlDb(jobName, jobGroup);

            NutchCrawldbUtils crawldbUtils = new NutchCrawldbUtils();

            return ResponseEntity.ok().body(crawldbUtils.dumpTopN(new Path(crawlId, "crawldb"), conf).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
        }*/

        /*@SuppressWarnings("resource")
        CrawlDbReader dbr = new CrawlDbReader();
        try{
            return Response.ok(dbr.query(args, conf, "topN", crawlId), MediaType.APPLICATION_OCTET_STREAM).build();
        }catch(Exception e){
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
        }*/

    }

    private Path getCrawlDb(String jobName, String jobGroup) {
        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, jobGroup);

        // First, we need to have a directory where everything will happen.
        String collectionCrawlDir = jobInfo.getCollectionName() + "_" + jobInfo.getCollectionId();
        if (!jobInfo.getCollectionName().equals(jobInfo.getJobName())) {
            collectionCrawlDir = jobInfo.getJobName() + "_" + jobInfo.getCollectionName() + "_" + jobInfo.getCollectionId();
        }

        Path crawlBaseDir = new Path(crawlDir.getAbsolutePath(), collectionCrawlDir);
        Path dbDir = new Path(crawlBaseDir, "db");

        return dbDir;
    }

    // Only works with fetch with parse enabled or when running nutch server
  /*@Operation(summary = "fetchdb", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
  @GetMapping(value = "/fetchdb")
  public List<FetchNodeDbInfo> fetchDb(@DefaultValue("0")@QueryParam("to")int to, @DefaultValue("0")@QueryParam("from")int from){
    List<FetchNodeDbInfo> listOfFetchedNodes = new ArrayList<>();
    Map<Integer, FetchNode> fetchNodedbMap = FetchNodeDb.getInstance().getFetchNodeDb();

    if(to ==0 || to>fetchNodedbMap.size()){
      to = fetchNodedbMap.size();
    }
    for(int i=from;i<=to;i++){
      if(!fetchNodedbMap.containsKey(i)){
        continue;
      }
      FetchNode node = fetchNodedbMap.get(i);
      FetchNodeDbInfo fdbInfo = new FetchNodeDbInfo();
      fdbInfo.setUrl(node.getUrl().toString());
      fdbInfo.setStatus(node.getStatus());
      fdbInfo.setNumOfOutlinks(node.getOutlinks().length);
      fdbInfo.setChildNodes(node.getOutlinks());
      listOfFetchedNodes.add(fdbInfo);
    }

    return listOfFetchedNodes;
  }*/

    /*private Configuration setupConfigs() throws OSSearchException, IOException {

        Properties properties = new Properties();

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
        conf.set("hadoop.log.file", "hadoop.log");

        // note that some of the options listed here could be set in the
        // corresponding hadoop site xml param file
        //common options
        conf.set("mapreduce.job.reduces", String.valueOf(num_tasks));
        conf.set("mapreduce.reduce.speculative", "false");
        conf.set("mapreduce.map.speculative", "false");
        conf.set("mapreduce.map.output.compress", "true");

        // This one is also mandatory and we will discuss its
        // value below. You need to point Nutch to the directory
        // with compiled plugins and this collection is NOT in
        // its default JAR package, unfortunately.
        //File pluginDir = new File(System.getProperty("nutch.plugins.dir"));
        if (!Files.exists(pluginDir.toPath())) {
            throw new OSSearchException("Nutch Plugins directory " + pluginDir.getCanonicalPath() + " does not exist! Make sure to build nutch-plugins module first");
        }
        conf.set("plugin.folders", pluginDir.getCanonicalPath());

        return conf;
    }*/
}
