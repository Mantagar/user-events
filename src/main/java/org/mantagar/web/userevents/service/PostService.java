package org.mantagar.web.userevents.service;

import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.dto.PostContentDTO;
import org.mantagar.web.userevents.dto.PostNoUserDTO;
import org.mantagar.web.userevents.exception.UserDoesNotExistException;
import org.mantagar.web.userevents.repository.PostRepository;
import org.mantagar.web.userevents.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Iterable<Long> getAllUserPostIds(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    public PostNoUserDTO getUserPost(Long userId, Long postId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException("User with id=%d not found".formatted(userId));
        }
        return postRepository.findByUserIdAndId(userId, postId);
    }

    public PostNoUserDTO createPost(PostContentDTO postDTO) {
        throw new UnsupportedOperationException("Not supported yet.");
        // TODO add test
    }
}
