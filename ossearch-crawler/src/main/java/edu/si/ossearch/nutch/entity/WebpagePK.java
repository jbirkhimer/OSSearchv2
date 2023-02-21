package edu.si.ossearch.nutch.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author jbirkhimer
 */
@Getter
@Setter
@EqualsAndHashCode
public class WebpagePK implements Serializable {

    private String id;
    private Long crawlDb;
}
