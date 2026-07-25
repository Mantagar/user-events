package org.mantagar.web.userevents.post;

import java.util.List;
import org.mantagar.web.userevents.post.dto.PostNoUserResponse;
import org.mantagar.web.userevents.post.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    @Query("SELECT p.id FROM Post p WHERE p.user.id = :userId")
    List<Long> findAllIdsByUserId(Long userId);

    PostNoUserResponse findByIdAndUserId(Long postId, Long userId);
}
