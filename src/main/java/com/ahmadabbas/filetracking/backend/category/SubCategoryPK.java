package com.ahmadabbas.filetracking.backend.category;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryPK implements Serializable {
    private Long parentCategoryId;
    private Long categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubCategoryPK that)) return false;
        return Objects.equals(parentCategoryId, that.parentCategoryId) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentCategoryId, categoryId);
    }
}
