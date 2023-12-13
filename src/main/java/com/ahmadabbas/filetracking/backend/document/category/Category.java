package com.ahmadabbas.filetracking.backend.document.category;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.*;

import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@IdClass(SubCategoryPK.class)
public class Category {
    @Id
    @Builder.Default
    private Long parentCategoryId = -1L;
    @Id
    @GeneratedValue
    private Long categoryId;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(parentCategoryId, category.parentCategoryId)
                && Objects.equals(categoryId, category.categoryId)
                && Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentCategoryId, categoryId, name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Category.class.getSimpleName() + "[", "]")
                .add("parentCategoryId=" + parentCategoryId)
                .add("categoryId=" + categoryId)
                .add("name='" + name + "'")
                .toString();
    }
}
