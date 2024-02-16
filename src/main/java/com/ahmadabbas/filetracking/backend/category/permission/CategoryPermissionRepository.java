package com.ahmadabbas.filetracking.backend.category.permission;

import com.ahmadabbas.filetracking.backend.user.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface CategoryPermissionRepository extends JpaRepository<CategoryPermission, Long> {
    @EntityGraph(value = "CategoryPermission.eagerlyFetchCourse")
    @Query("select c from CategoryPermission c where c.role = :role")
    Set<CategoryPermission> findByRole(Role role);

    @EntityGraph(value = "CategoryPermission.eagerlyFetchCourse")
    @Query("select c from CategoryPermission c where c.category.categoryId = :categoryId and c.role = :role")
    Optional<CategoryPermission> findByCategoryIdAndRole(Long categoryId, Role role);

}