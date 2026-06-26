package org.mantagar.web.userevents.repository;

import org.mantagar.web.userevents.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query("SELECT u.id from User u")
    Iterable<Integer> findAllIds();

    boolean existsByNameAndSurname(String name, String surname);
}
