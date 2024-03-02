package com.ahmadabbas.filetracking.backend.category;

import com.ahmadabbas.filetracking.backend.category.payload.CategoryPermissionRequestDto;
import com.ahmadabbas.filetracking.backend.category.payload.FullCategoryResponse;
import com.ahmadabbas.filetracking.backend.category.permission.CategoryPermission;
import com.ahmadabbas.filetracking.backend.category.permission.CategoryPermissionRepository;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryPermissionRepository categoryPermissionRepository;
    private final UserService userService;

    public Category getParentCategory(Long categoryId) {
        return getCategory(categoryId, -1L);
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "category with name %s not found".formatted(name)
                ));
    }

    public Category getCategory(Long categoryId, Long parentCategoryId) {
        return categoryRepository.findByCategoryIdAndParentCategoryId(categoryId, parentCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "category with id %s and parent_id %s not found".formatted(categoryId, parentCategoryId)
                ));
    }

    @Transactional
    public Category createCategory(Category category) {
        if (!categoryRepository.existsByName(category.getName())) {
            return categoryRepository.save(category);
        }
        throw new DuplicateResourceException("category with the name '%s' already exists!".formatted(category.getName()));
    }

    public List<Category> getAllParentCategories(Authentication authentication) {
        List<Category> categories = new ArrayList<>(Collections.emptyList());
        if (authentication != null && authentication.isAuthenticated()) {
            Set<Role> roles = userService.getRoles(authentication);
            if (roles.contains(Role.ADMINISTRATOR)) {
                return categoryRepository.findByParentCategoryId(-1L);
            } else {
                categories = getAllowedCategories(roles);
            }
//            List<Category> allParentCategories = categoryRepository.findByParentCategoryId(-1L);
//            return categories.stream().filter(allParentCategories::contains).collect(Collectors.toSet());
        }
        return categories;
    }

    public List<Category> getAllowedCategories(Set<Role> roles) {
        List<Category> categories = new ArrayList<>(Collections.emptyList());
        for (var role : roles) {
            Set<CategoryPermission> categoryPermissions = categoryPermissionRepository.findByRole(role);
            categoryPermissions.forEach(permission -> {
                Category category = permission.getCategory();
                if (category != null && category.getParentCategoryId() == -1) categories.add(category);
            });
        }
        return categories;
    }

    public List<Long> getAllowedCategoriesIds(Set<Role> roles) {
        List<Long> categoryIds = new ArrayList<>(Collections.emptyList());
        for (var role : roles) {
            Set<CategoryPermission> categoryPermissions = categoryPermissionRepository.findByRole(role);
            categoryPermissions.forEach(permission -> {
                Category category = permission.getCategory();
                if (category != null && category.getParentCategoryId() == -1)
                    categoryIds.add(category.getCategoryId());
            });
        }
        return categoryIds;
    }

    public List<Category> getAllChildrenCategories(Long parentId) {
        return categoryRepository.findByParentCategoryId(parentId);
    }

//    public Map<String, List<Category>> getAllCategories1() {
//        Map<String, List<Category>> result = new HashMap<>();
//        List<Category> parentCategories = getAllParentCategories();
//        for (var parent : parentCategories) {
//            List<Category> childrenCategories = getAllChildrenCategories(parent.getCategoryId());
//            result.put(parent.getName(), childrenCategories);
//        }
//        return result;
//    }

    public List<FullCategoryResponse> getAllCategories2(Authentication authentication) {
        List<Category> parentCategories = getAllParentCategories(authentication);
        List<FullCategoryResponse> result = new LinkedList<>();
        for (var parent : parentCategories) {
            List<Category> childrenCategories = getAllChildrenCategories(parent.getCategoryId());
            if (!childrenCategories.isEmpty()) {
                result.add(new FullCategoryResponse(parent, childrenCategories));
                continue;
            }
            result.add(new FullCategoryResponse(parent, Collections.emptyList()));
        }
        return result;
    }

    @Transactional
    public Category updateCategoryPermission(CategoryPermissionRequestDto request) {
        Category category = getParentCategory(request.categoryId());
        if (request.delete()) {
            CategoryPermission permission = categoryPermissionRepository.findByCategoryIdAndRole(request.categoryId(), request.role())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "category permission with id %s not found".formatted(request.categoryId())
                    ));
            categoryPermissionRepository.delete(permission);
        } else {
            var permission = categoryPermissionRepository.findByCategoryIdAndRole(request.categoryId(), request.role());
            if (permission.isPresent()) {
                throw new DuplicateResourceException("category permission already exists!");
            }
            categoryPermissionRepository.save(new CategoryPermission(
                    request.role(),
                    category
            ));
        }
        return category;
    }
}
