package org.mantagar.web.userevents.repository;

import org.mantagar.web.userevents.dto.PostNoUserDTO;
import org.mantagar.web.userevents.entity.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    Iterable<Long> findAllByUserId(Long userId);

    PostNoUserDTO findByUserIdAndId(Long userId, Long postId);
}
