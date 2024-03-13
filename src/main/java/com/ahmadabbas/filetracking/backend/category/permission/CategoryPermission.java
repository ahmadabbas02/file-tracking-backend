package com.ahmadabbas.filetracking.backend.category.permission;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.user.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@NoArgsConstructor
@Getter
@Entity
@NamedEntityGraph(
        name = "CategoryPermission.eagerlyFetchCourse",
        attributeNodes = @NamedAttributeNode("category")
)
public class CategoryPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_permission_generator")
    @SequenceGenerator(name = "category_permission_generator", sequenceName = "category_permission_seq", allocationSize = 1)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToOne
    private Category category;

    public CategoryPermission(Role role, Category category) {
        this.role = role;
        this.category = category;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CategoryPermission.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("role=" + role)
                .add("category=" + category)
                .toString();
    }
}
