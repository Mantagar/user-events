package org.mantagar.web.userevents.post.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mantagar.web.userevents.user.model.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "post_seq_gen", sequenceName = "post_seq", allocationSize = 1)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq_gen")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
