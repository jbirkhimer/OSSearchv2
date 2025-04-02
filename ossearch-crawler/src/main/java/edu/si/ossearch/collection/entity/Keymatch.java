package edu.si.ossearch.collection.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.collection.entity.converters.CreationTypeConverter;
import edu.si.ossearch.collection.entity.converters.KeymatchTypeConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.util.Date;

/**
 * @author jbirkhimer
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "keymatch")
public class Keymatch {
    public enum CreationType {
        created,
        imported
    }

    public enum KeymatchType {
        keyword,
        phrase,
        exact
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String titleForMatch;

    private String searchTerm;

    @JsonSerialize(converter = KeymatchTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private KeymatchType keymatchType;

    private String urlForMatch;

    private String imgUrlForMatch;

    @JsonSerialize(converter = CreationTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private CreationType creationType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "collection_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Collection collection;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;


}
