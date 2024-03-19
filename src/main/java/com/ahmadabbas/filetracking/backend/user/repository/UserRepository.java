package com.ahmadabbas.filetracking.backend.user.repository;

import com.ahmadabbas.filetracking.backend.user.User;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

    Optional<User> findByEmail(String email);

//    @Query("""
//            select u from User u
//            left join u.roles roles
//            where roles in :roles
//            """)
//    Page<User> findAllByRoles(List<Role> roles, Pageable pageable);

//    @Query("""
//            select u from User u
//            where upper(u.name.firstName) like upper(concat('%', :name, '%'))
//            or upper(u.name.lastName) like upper(concat('%', :name, '%'))
//            """)
//    Page<User> findAllByNameContains(String name, Pageable pageable);

//    @Query("""
//            select u from User u
//            left join u.roles roles
//            where roles in :roles
//            and (upper(u.name.firstName) like upper(concat('%', :name, '%'))
//            or upper(u.name.lastName) like upper(concat('%', :name, '%')))
//            """)
//    Page<User> findAllByNameAndRoles(String name, List<Role> roles, Pageable pageable);


    boolean existsByEmail(String email);

    @Query("""
            select (count(u) > 0) from User u
            where upper(u.firstName) = upper(?1) and upper(u.lastName) = upper(?2)
            """)
    boolean existsByName(String firstName, String lastName);

}
