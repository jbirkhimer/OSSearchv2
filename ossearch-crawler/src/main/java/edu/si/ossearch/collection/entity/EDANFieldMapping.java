package edu.si.ossearch.collection.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.collection.entity.converters.HashMapConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@Table(name = "edan_field_mapping")
public class EDANFieldMapping implements Serializable {
    private static final long serialVersionUID = -4052050707170846699L;
    @Id
    private Long id;

    @Convert(converter = HashMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> edanContentFields;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Collection collection;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;


}
