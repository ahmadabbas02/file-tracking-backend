package com.ahmadabbas.filetracking.backend.document.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, SubCategoryPK> {

    List<Category> findByParentCategoryId(Long parentCategoryId);

}
