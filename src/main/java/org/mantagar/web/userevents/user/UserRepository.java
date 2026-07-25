package org.mantagar.web.userevents.user;

import java.util.List;
import org.mantagar.web.userevents.user.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u.id FROM User u")
    List<Long> findAllIds();

    boolean existsByNameAndSurname(String name, String surname);
}
