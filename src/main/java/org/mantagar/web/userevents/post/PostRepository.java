package org.mantagar.web.userevents.post;

import org.mantagar.web.userevents.post.dto.PostNoUserResponse;
import org.mantagar.web.userevents.post.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    // TODO verify if integration test works - it might need to be a @Query with JPQL instead of
    // method name query
    Iterable<Long> findAllByUserId(Long userId);

    PostNoUserResponse findByIdAndUserId(Long postId, Long userId);
}
