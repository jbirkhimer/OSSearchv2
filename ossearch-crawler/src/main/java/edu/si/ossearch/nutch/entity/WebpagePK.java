package edu.si.ossearch.nutch.entity;

import lombok.*;

import java.io.Serializable;

/**
 * @author jbirkhimer
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WebpagePK implements Serializable {

    private String id;
    private Integer collectionId;
}
