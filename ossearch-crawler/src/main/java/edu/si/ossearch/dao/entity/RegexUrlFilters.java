package edu.si.ossearch.dao.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.dao.entity.converters.RegexUrlFilterTypeConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Embeddable
public class RegexUrlFilters implements Serializable {
    private static final long serialVersionUID = -5612376779513224751L;

    public enum Type {
        include,
        exclude
    }

    private int orderId;
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