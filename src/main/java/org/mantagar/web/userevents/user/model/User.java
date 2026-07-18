package org.mantagar.web.userevents.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
@Table(
        name = "users",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "UniqueNameSurname",
                    columnNames = {"name", "surname"})
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    private Long id;

    private String name;
    private String surname;
}
