package edu.si.ossearch.collection.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.collection.entity.converters.RegexUrlFilterTypeConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Embeddable
public class RegexUrlFilters {
//    private static final long serialVersionUID = -5612376779513224751L;

    public enum Type {
        include,
        exclude
    }

    private int orderId;

    @Column(columnDefinition = "TEXT")
    private String expression;

    @JsonSerialize(converter = RegexUrlFilterTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private RegexUrlFilters.Type type = Type.exclude;

//    @CreationTimestamp
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date createdDate;
//
//    @UpdateTimestamp
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date updatedDate;
}