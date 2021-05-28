/**
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
 *
 * Modified by Januzs Woyciechowsky
 */

package org.apache.nutch.indexer.metadata;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.io.File;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.avro.util.Utf8;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.storage.WebPage.Field;
import org.apache.nutch.util.Bytes;

/**
 * Indexer which can be configured to extract metadata from the crawldb, parse
 * metadata or content metadata. You can specify the properties "index.db",
 * "index.parse" or "index.content" who's values are comma-delimited
 * <value>key1,key2,key3</value>.
 * also adding * inside value tag will include all metas that records has in mongo
 */

public class MetadataIndexer implements IndexingFilter {
  private Configuration conf;
  private static Map<Utf8, String> parseFieldnames;
  private static final String PARSE_CONF_PROPERTY = "index.metadata";
  private static final String INDEX_PREFIX = "meta_";
  private static final String PARSE_META_PREFIX = "meta_";

  public NutchDocument filter(NutchDocument doc, String url, WebPage page)
      throws IndexingException {
    Map<String, String> map = new HashMap<String, String>();
    List<String> list = new ArrayList<String>();
    String filepath = "/var/www/html/GSA-replacement/app/cachecollection/system/dynamic-navigations-multivalue-fields.txt";
    File file = new File(filepath);
    BufferedReader reader = null;
    ArrayList<String> allmetafields = new ArrayList<String>();
    Map<CharSequence,ByteBuffer> metadatas = page.getMetadata(); 
    ArrayList<String> metadataindb = new ArrayList<String>();
    // just in case
    if (doc == null)
      return doc;
    // checking if dyanmic file exist
    if (file.exists() && !file.isDirectory()) {
      try {
        reader = new BufferedReader(new FileReader(file));
        String text = null;
        while ((text = reader.readLine()) != null) {
          String[] split = text.split("-dl-");
          String metaname = (split.length > 0) ? split[0] : null;
          String metavalue = (split.length > 1) ? split[1]: null;
          if(metaname != null && metavalue != null){
             map.put(metaname.toLowerCase(),metavalue);
          }
        }
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
          try {
              if (reader != null) {
                  reader.close();
              }
          } catch (IOException e) {
          }
      } 
    }
    // add the fields from parsemd
    if (parseFieldnames != null) {
      for (Entry<Utf8, String> metatag : parseFieldnames.entrySet()) {
        if ( (metatag.getKey().toString()).equals("meta_*") ) {
          for (Map.Entry<CharSequence,ByteBuffer> entry : metadatas.entrySet()) {
            String key = entry.getKey().toString();
            if (key.contains("meta_")) {
              
              ByteBuffer bvalues = page.getMetadata().get(entry.getKey());
              if (bvalues != null) {
                allmetafields.add(key);
                if (key.contains(":")) {
                  key = key.replaceAll(":","_");
                }
                key = key.replaceAll(" ","__");
                String value = Bytes.toString(bvalues.array());
                String delimeter = getMapDelimeterValue(map,key.toLowerCase());
                if (delimeter != null) {
                  String defaultdelimeter = "\t";
                  if ( value.contains(delimeter)) {
                    defaultdelimeter = delimeter;
                  }
                  String[] values = value.split("\\"+defaultdelimeter );
                  for (String eachvalue : values) {
                    if (eachvalue.contains("\t")) {
                      eachvalue = eachvalue.replaceAll("\t"," ");
                    }
                    doc.add(key, eachvalue.trim());
                  }
                }else{
                  if (value.contains("\t")) {
                    value = value.replaceAll("\t"," ");
                  }
                  doc.add(key, value.trim());
                }
                
              }
            }
          }
        }else{
          ByteBuffer bvalues = page.getMetadata().get(metatag.getKey());
          if (bvalues != null) {
            String key = metatag.getValue();
            allmetafields.add(key);
            if (key.contains(":")) {
              key = key.replaceAll(":","_");
            }
            key = key.replaceAll(" ","__");
            String value = Bytes.toString(bvalues.array());
            String delimeter = getMapDelimeterValue(map,key.toLowerCase());
            if (delimeter != null) {
              String defaultdelimeter = "\t";
              if ( value.contains(delimeter) ) {
                defaultdelimeter = delimeter;
              }
              String[] values = value.split("\\"+defaultdelimeter);
              
              for (String eachvalue : values) {
                if (eachvalue.contains("\t")) {
                  eachvalue = eachvalue.replaceAll("\t"," ");
                }
                doc.add(key, eachvalue.trim());
              }
            }else{
              if (value.contains("\t")) {
                value = value.replaceAll("\t"," ");
              }
              doc.add(key, value.trim());
            }
          }
        }
        
      }
    }
    for (String eachmeta : allmetafields) {
      if (!eachmeta.equals("")) {
        doc.add("meta__all_metatags",eachmeta);
      }
    }
    return doc;
  }
  private String getMapDelimeterValue(Map<String, ?> map, String key) {
    Object value = map.get(key.toLowerCase());
    return value == null ? null : value.toString();
  }
  public void setConf(Configuration conf) {
    this.conf = conf;
    String[] metatags = conf.getStrings(PARSE_CONF_PROPERTY);
    parseFieldnames = new TreeMap<Utf8, String>();
    for (int i = 0; i < metatags.length; i++) {
      parseFieldnames.put(
          new Utf8(PARSE_META_PREFIX + metatags[i].toLowerCase(Locale.ROOT)),
          INDEX_PREFIX + metatags[i]);
    }
    // TODO check conflict between field names e.g. could have same label
    // from different sources
  }

  public Configuration getConf() {
    return this.conf;
  }

  @Override
  public Collection<Field> getFields() {
    return null;
  }
}
