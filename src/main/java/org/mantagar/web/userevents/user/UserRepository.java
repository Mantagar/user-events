package org.mantagar.web.userevents.user;

import org.mantagar.web.userevents.user.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Iterable<Long> findAllBy();

    boolean existsByNameAndSurname(String name, String surname);
}
