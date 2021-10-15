package com.security.jwt.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity(name = "User")
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_username_unique", columnNames = "username")
        })
public class User
{
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "id",
            length = 36,
            columnDefinition = "varchar(36)",
            updatable = false,
            nullable = false)
    private UUID id;


    @Version
    @Column(name = "version")
    private Long version;


    @CreationTimestamp
    @Column(name = "created_date",
            updatable = false)
    private Timestamp createdDate;


    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private Timestamp lastModifiedDate;

    @Column(nullable = false,
            name = "username")
    private String username;


    @Column(nullable = false,
            name = "first_name")
    private String firstName;


    @Column(nullable = false,
            name = "surname")
    private String surname;


    @Column(nullable = false,
            name = "password")
    private String password;


    @ManyToMany(fetch = FetchType.EAGER)
    @Column(nullable = false,
            name = "role")
    private Collection<Role> roles = new ArrayList<>();
}
