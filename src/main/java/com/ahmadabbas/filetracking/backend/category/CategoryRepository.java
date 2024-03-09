package com.ahmadabbas.filetracking.backend.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, SubCategoryPK> {
    @Query("select (count(c) > 0) from Category c where upper(c.name) = upper(:name)")
    boolean existsByName(String name);

    @Query("select c from Category c where upper(c.name) = upper(:name)")
    Optional<Category> findByNameIgnoreCase(String name);

    @Query("select c from Category c where c.parentCategoryId = ?1")
    List<Category> findByParentCategoryId(Long parentCategoryId);

    @Query("select c from Category c where c.categoryId = :categoryId and c.parentCategoryId = :parentCategoryId")
    Optional<Category> findByCategoryIdAndParentCategoryId(Long categoryId, Long parentCategoryId);

    @Query("select c from Category c where c.categoryId = :categoryId")
    Optional<Category> findById(Long categoryId);

}
