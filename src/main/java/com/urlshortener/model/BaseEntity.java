package com.urlshortener.model;


import com.urlshortener.util.Constants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @see <a href="https://vladmihalcea.com/2014/07/21/hibernate-hidden-gem-the-pooled-lo-optimizer/">Hibernate pooled-lo optimizer</a>
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public class BaseEntity {


    @Id
    @GenericGenerator(
            name = "sequenceGenerator",
            strategy = "enhanced-sequence",
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "optimizer",
                            value = "pooled-lo"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "initial_value",
                            value = Constants.SEQUENCE_START_ID_STRING
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "increment_size",
                            value = "10"
                    )
            }
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    private Integer id;

    public Integer getId() {
        return id;
    }

}
