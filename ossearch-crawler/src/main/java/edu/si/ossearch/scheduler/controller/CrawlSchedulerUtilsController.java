package edu.si.ossearch.scheduler.controller;

import edu.si.ossearch.scheduler.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.FileUtils.readFileToString;

/**
 * @author jbirkhimer
 */
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/scheduler")
@Tag(description = "Crawl Scheduler Utils", name = "Crawl Scheduler Utils")
@SecurityRequirement(name = "bearerAuth")
public class CrawlSchedulerUtilsController {

    @Value(value = "${ossearch.nutch.confDir}")
    @NonNull
    File nutchConfDir;

    @Autowired
    private JobService jobService;

    @Operation(summary = "Validate Quarts Cron expression", tags = "Crawl Scheduler Utils", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @GetMapping(value = "/utils/validate", params = {"expr"})
    public String validateCronExpression(@RequestParam(name = "expr") String expression) {
        boolean isValid = CronExpression.isValidExpression(expression);
        log.info("cron expression {} isValid: {}", expression, isValid);
        return "Quarts Cron " + expression + " isValid: " + isValid;
    }

    @Operation(summary = "Get the default Nutch properties from nutch-default.xml and nutch-site.xml", tags = "Crawl Scheduler Utils", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @GetMapping(value = "/utils/nutch/properties")
    public ResponseEntity<String> getNutchProperties() throws IOException {

        Configuration conf = new Configuration(false);
        conf.addResource("nutch-default.xml");
        conf.addResource("nutch-site.xml");

        List<Map.Entry<String, String>> list = new ArrayList<>();
        conf.iterator().forEachRemaining(list::add);
        list.sort(Map.Entry.comparingByKey());

        JSONArray configList = new JSONArray();
        List<String> configListKeys = new ArrayList<>();

        for (Map.Entry<String, String> e : list) {
            String key = e.getKey();
            String val = e.getValue();
            String substitutedVal = conf.get(key);
            JSONObject config = new JSONObject();
            config.put("name", key);
            if (val.equals(substitutedVal)) {
                config.put("value", val);
            } else {
                config.put("value", val);
                config.put("substituted.value", substitutedVal);
            }
            configList.put(config);
            configListKeys.add(key);
        }

        String nutchDefault = readFileToString(new File(nutchConfDir, "nutch-default.xml"), StandardCharsets.UTF_8);

        String nutchSite = readFileToString(new File(nutchConfDir, "nutch-site.xml"), StandardCharsets.UTF_8);

        JSONArray properties = new JSONArray();

        JSONArray nutch_default = XML.toJSONObject(nutchDefault).getJSONObject("configuration").getJSONArray("property");
        JSONArray nutch_site = XML.toJSONObject(nutchSite).getJSONObject("configuration").getJSONArray("property");

        configList.forEach(property -> {
//            log.info("nutch_default property {}", property);

            JSONObject prop = new JSONObject(property.toString());

            addMissingPropsAndDescrriptions(configListKeys, properties, nutch_default, prop);

            addMissingPropsAndDescrriptions(configListKeys, properties, nutch_site, prop);

            properties.put(prop);
        });

        return ResponseEntity.ok(properties.toString());
    }

    private void addMissingPropsAndDescrriptions(List<String> configListKeys, JSONArray properties, JSONArray baseProps, JSONObject prop) {
        for (Object siteProperty : baseProps) {
            String key = ((JSONObject) siteProperty).getString("name");
            if (!configListKeys.contains(key)) {
                configListKeys.add(key);
                properties.put(siteProperty);
            } else if (key.equals(prop.getString("name"))) {
                prop.put("description", String.valueOf(((JSONObject) siteProperty).get("description")));
                break;
            }
        }
    }

    @Operation(summary = "shutdown check", responses = {@ApiResponse(content = @Content(mediaType = "text/plain"))})
    @GetMapping(value = "shutdownCheck", produces = "text/plain")
    @PreAuthorize("permitAll()")
    @SecurityRequirement(name = "none")
    public String shutdownCheck() {
        return jobService.shutdownCheck().getBody().toString();
    }
}
