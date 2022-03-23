package edu.si.ossearch.dao.entity;

import com.google.common.collect.Maps;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class JobConfig implements Serializable {
  private static final long serialVersionUID = -6135554799375067070L;

  private int collectionId;
  private int numberOfRounds = 50;
  boolean index = true;
  boolean recrawl = true;

  @Singular("property")
  private Map<String, Object> nutch_properties = Maps.newHashMap();
}
