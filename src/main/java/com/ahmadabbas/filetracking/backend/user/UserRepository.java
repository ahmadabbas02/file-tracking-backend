package com.ahmadabbas.filetracking.backend.user;

import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph("User.eagerlyFetchRoles")
    Optional<User> findByEmail(String email);

    @Query("""
            select u from User u
            join fetch u.roles roles
            where roles not in :roles
            """)
    List<User> findByRolesNotIn(Role... roles);

    boolean existsByEmail(String email);

    @Query("""
            select (count(u) > 0) from User u
            where upper(u.name.firstName) = upper(?1) and upper(u.name.lastName) = upper(?2)""")
    boolean existsByName(String firstName, String lastName);


}
