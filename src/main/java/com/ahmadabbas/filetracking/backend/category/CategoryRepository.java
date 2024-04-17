package com.ahmadabbas.filetracking.backend.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, SubCategoryPK> {

    @Override
    @Query("select c from Category c order by c.name")
    @NonNull
    List<Category> findAll();

    @Query("select (count(c) > 0) from Category c where upper(c.name) = upper(:name)")
    boolean existsByName(String name);

    @Query("select c from Category c where upper(c.name) = upper(:name)")
    Optional<Category> findByNameIgnoreCase(String name);

    @Query("select c from Category c where c.categoryId = :categoryId and c.parentCategoryId = :parentCategoryId")
    Optional<Category> findByCategoryIdAndParentCategoryId(Long categoryId, Long parentCategoryId);

    @Query("select c from Category c where c.categoryId = :categoryId")
    Optional<Category> findById(Long categoryId);

    @Query("select c from Category c where c.parentCategoryId = -1 order by c.name")
    List<Category> findAllParentCategories();

    @Query("select c from Category c where c.parentCategoryId = ?1 order by c.name")
    List<Category> findAllByParentCategoryId(Long parentCategoryId);

    @Transactional
    @Modifying
    @Query("update Category c set c.deleted = FALSE where c.categoryId in ?1")
    void undeleteAll(Collection<Long> categoryIds);

}
