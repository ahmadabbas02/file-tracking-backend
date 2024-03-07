package com.ahmadabbas.filetracking.backend.category;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@IdClass(SubCategoryPK.class)
public class Category {
    @Id
    @Column(updatable = false)
    @Builder.Default
    private Long parentCategoryId = -1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_generator")
    @SequenceGenerator(name = "category_generator", sequenceName = "category_seq", allocationSize = 1)
    @Column(updatable = false)
    private Long categoryId;
    @Column(nullable = false, unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category category)) return false;
        return Objects.equals(getParentCategoryId(), category.getParentCategoryId()) && Objects.equals(getCategoryId(), category.getCategoryId()) && Objects.equals(getName(), category.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParentCategoryId(), getCategoryId(), getName());
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
