package com.syngleton.chartomancy.model;

import com.syngleton.chartomancy.data.GenericData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "USER")
@ToString
public class User {

    private static final String DEFAULT_VALUE = "";

    public User()    {
        this.enabled = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Column(name = "USER_ID")
    private Integer userId;
    @Getter
    @Setter
    @Column(name = "FIRST_NAME")
    private String firstName = DEFAULT_VALUE;
    @Getter
    @Setter
    @Column(name = "LAST_NAME")
    private String lastName = DEFAULT_VALUE;
    @Getter
    @Setter
    @Column(name = "EMAIL")
    private String email = DEFAULT_VALUE;
    @Getter
    @Setter
    @Column(name = "PASSWORD")
    private String password = DEFAULT_VALUE;
    @Getter
    @Setter
    @Column(name = "VERIFIED")
    boolean verified;
    @Getter
    @Setter
    @Column(name = "ENABLED")
    boolean enabled;
    @Transient
    @Getter
    @Setter
    private GenericData genericData;
}
