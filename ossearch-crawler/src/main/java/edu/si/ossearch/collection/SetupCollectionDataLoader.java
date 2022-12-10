package edu.si.ossearch.collection;


import edu.si.ossearch.OSSearchException;
import edu.si.ossearch.collection.repository.CollectionRepository;
import edu.si.ossearch.security.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.util.NutchConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Component
public class SetupCollectionDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        try {
            if (false) {
                contextLoads();
            }
        } catch (OSSearchException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Transactional
    Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name).orElse(new Privilege(name));
        privilege = privilegeRepository.saveAndFlush(privilege);
        return privilege;
    }*/


    void contextLoads() throws OSSearchException, Exception {
        Properties properties = new Properties();
        properties.setProperty("hadoop.log.dir", "logs");
        properties.setProperty("hadoop.log.file", "hadoop.log");
        properties.setProperty("hadoop.tmp.dir", "hadoop_tmp");

//        properties.setProperty("urlnormalizer.regex.file", "regex-normalize.xml_TEST_2");

        Configuration conf = NutchConfiguration.create();
//        conf.addResource("conf/nutch-default_TEST.xml");
//        conf.addResource("conf/nutch-site.xml");

        logConf(conf);
        log.warn(">>>>> nutch-default.xml: {}", conf.getResource("nutch-default.xml"));
        log.warn(">>>>> urlnormalizer.regex.file: {}", conf.get("urlnormalizer.regex.file"));
        log.warn(">>>>> parser.html.text.blacklist: {}", conf.get("parser.html.text.blacklist"));

//        System.getProperties().forEach((k, v) -> log.info("{}:{}", k, v));
    }

    public void logConf(Configuration conf) {
//        String format3cols = "%-32s  %24s  %20s";

        List<Map.Entry<String, String>> list = new ArrayList<>();
        conf.iterator().forEachRemaining(list::add);
        list.sort(Map.Entry.comparingByKey());

        JSONArray configList = new JSONArray();
//        System.out.println(String.format(format3cols, "conf.name", "conf.value", "substituted.value"));
//        System.out.println("================================================================================");
        for (Map.Entry<String, String> e : list) {
            String key = e.getKey();
            String val = e.getValue();
            String substitutedVal = conf.get(key);
            JSONObject config = new JSONObject();
            config.put("name", key);
            if (val.equals(substitutedVal)) {
                config.put("value", val);
//                String format = String.format("%%-%ds  %%%ds", key.length(), (80 - 2 - key.length()));
//                System.out.println(String.format(format, key, val));
            } else {
                config.put("value", val);
                config.put("substituted.value", substitutedVal);
//                String format = String.format("%%-%ds  %%%ds  %%18s", key.length(), (60 - 2 - key.length()));
//                System.out.println(String.format(format, key, val, substitutedVal));
            }
            configList.put(config);
        }

        log.info("Nutch Properties:\n{}", configList.toString(4));
    }

}
