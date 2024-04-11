package com.ahmadabbas.filetracking.backend.category.permission;

import com.ahmadabbas.filetracking.backend.user.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryPermissionRepository extends JpaRepository<CategoryPermission, Long> {

    @Query("select c from CategoryPermission c where c.category.categoryId = :categoryId order by c.category.name")
    @EntityGraph(value = "CategoryPermission.eagerlyFetchCategory")
    List<CategoryPermission> findAllByCategoryId(Long categoryId);

    @EntityGraph(value = "CategoryPermission.eagerlyFetchCategory")
    @Query("select c from CategoryPermission c where c.role = :role order by c.category.name")
    Set<CategoryPermission> findAllByRole(Role role);

    @EntityGraph(value = "CategoryPermission.eagerlyFetchCategory")
    @Query("select c from CategoryPermission c where c.category.categoryId = :categoryId and c.role = :role order by c.category.name")
    Optional<CategoryPermission> findAllByCategoryIdAndRole(Long categoryId, Role role);

}