package com.jbs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by rizkykojek on 5/1/17.
 */
@NoArgsConstructor
@lombok.Getter
@lombok.Setter
@Entity
@Table(name = "letter_template")
public class LetterTemplate {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 3, unique = true)
    private String code;

    @Column(length = 100)
    private String name;

    @Column(name = "template_file")
    private String templateFile;

    @Column
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean status;

}
