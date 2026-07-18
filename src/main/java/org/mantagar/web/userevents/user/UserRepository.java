package org.mantagar.web.userevents.user;

import org.mantagar.web.userevents.user.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u.id FROM User u")
    Iterable<Long> findAllIds();
}
