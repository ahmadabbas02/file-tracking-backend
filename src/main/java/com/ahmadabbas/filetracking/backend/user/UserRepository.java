package com.ahmadabbas.filetracking.backend.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph("User.eagerlyFetchRoles")
    Optional<User> findByEmail(String email);

//    @Query("select u from User u where u.role != 'CHAIR' and u.role != 'VICE_CHAIR'")
//    List<User> findAllNonAdminUsers();

    default List<User> findAllNonAdminUsers() {
        return findByRolesNotIn(List.of(Role.CHAIR, Role.VICE_CHAR));
    }

    @Query("""
            select u from User u
            join fetch u.roles roles
            where roles not in :roles
            """)
    List<User> findByRolesNotIn(Collection<Role> roles);

    boolean existsByEmail(String email);

    boolean existsByName(String name);
}
