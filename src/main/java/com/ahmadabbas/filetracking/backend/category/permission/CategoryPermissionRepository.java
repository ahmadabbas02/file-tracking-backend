package com.ahmadabbas.filetracking.backend.category.permission;

import com.ahmadabbas.filetracking.backend.user.Role;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface CategoryPermissionRepository extends JpaRepository<CategoryPermission, Long> {
    @Override
    @Query("""
            select c_perm from CategoryPermission c_perm
            order by c_perm.role
            """)
    @EntityGraph(value = "CategoryPermission.eagerlyFetchCategory")
    List<CategoryPermission> findAll();

    @Query("select c from CategoryPermission c where c.category.categoryId = :categoryId")
    @EntityGraph(value = "CategoryPermission.eagerlyFetchCategory")
    List<CategoryPermission> findAllByCategoryId(Long categoryId);

    @EntityGraph(value = "CategoryPermission.eagerlyFetchCategory")
    @Query("select c from CategoryPermission c where c.role = :role")
    Set<CategoryPermission> findAllByRole(Role role);

    @EntityGraph(value = "CategoryPermission.eagerlyFetchCategory")
    @Query("select c from CategoryPermission c where c.category.categoryId = :categoryId and c.role = :role")
    Optional<CategoryPermission> findAllByCategoryIdAndRole(Long categoryId, Role role);

}