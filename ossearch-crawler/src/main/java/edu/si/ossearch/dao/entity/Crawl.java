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
package edu.si.ossearch.dao.entity;

//import com.j256.ormlite.field.DatabaseField;
import lombok.Data;
//import org.apache.nutch.webui.model.SeedList;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "crawl")
public class Crawl implements Serializable {
  public enum CrawlStatus {
    NEW, CRAWLING, FINISHED, ERROR
  }

  @Id
  @GeneratedValue
  private Long id;

  @Column
  private String crawlId;

  @Column
  private String crawlName;

  @Column
  private CrawlStatus status = CrawlStatus.NEW;

  @Column
  private Integer numberOfRounds = 1;

  @OneToOne
//  @DatabaseField(foreign = true, foreignAutoRefresh = true)
  @JoinColumn(name = "seed_list_id", nullable = false)
  private SeedList seedList;

  @Column
  private String seedDirectory;

  @Column
  private int progress;

}
