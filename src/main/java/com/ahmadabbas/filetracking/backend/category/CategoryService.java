package com.ahmadabbas.filetracking.backend.category;

import com.ahmadabbas.filetracking.backend.category.payload.CategoryPermissionRequestDto;
import com.ahmadabbas.filetracking.backend.category.payload.FullCategoryResponse;
import com.ahmadabbas.filetracking.backend.category.permission.CategoryPermission;
import com.ahmadabbas.filetracking.backend.category.permission.CategoryPermissionRepository;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    public Category getParentCategory(Long categoryId, User loggedInUser) {
        return getCategory(categoryId, -1L, loggedInUser);
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "category with name %s not found".formatted(name)
                ));
    }

    public Category getCategory(Long categoryId, @AuthenticationPrincipal User loggedInUser) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "category with id %s not found".formatted(categoryId)
                ));
        if (!getAllowedCategories(loggedInUser.getRoles()).contains(category)) {
            throw new AccessDeniedException("not authorized");
        }
        return category;
    }

    public Category getCategory(Long categoryId, Long parentCategoryId, @AuthenticationPrincipal User loggedInUser) {
        Category category = categoryRepository.findByCategoryIdAndParentCategoryId(categoryId, parentCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "category with id %s and parent_id %s not found".formatted(categoryId, parentCategoryId)
                ));
        if (!getAllowedCategories(loggedInUser.getRoles()).contains(category)) {
            throw new AccessDeniedException("not authorized");
        }
        return category;
    }

    @Transactional
    public Category createCategory(Category category, User user) {
        Set<Role> roles = user.getRoles();
        if (roles.contains(Role.SECRETARY) && category.getParentCategoryId() == -1L) {
            throw new AccessDeniedException("only allowed to create sub categories");
        }
        if (!categoryRepository.existsByName(category.getName())) {
            return categoryRepository.save(category);
        }
        throw new DuplicateResourceException("category with the name '%s' already exists!".formatted(category.getName()));
    }

    public List<Category> getAllParentCategories(User user) {
        List<Category> categories;
        Set<Role> roles = userService.getRoles(user);
        if (roles.contains(Role.ADMINISTRATOR)) {
            return categoryRepository.findByParentCategoryId(-1L);
        } else {
            categories = getAllowedCategories(roles);
        }
//            List<Category> allParentCategories = categoryRepository.findByParentCategoryId(-1L);
//            return categories.stream().filter(allParentCategories::contains).collect(Collectors.toSet());
        return categories;
    }

    public List<Category> getAllowedCategories(Set<Role> roles) {
        List<Category> categories = new ArrayList<>(Collections.emptyList());
        if (roles.stream().anyMatch(role -> role.equals(Role.ADMINISTRATOR)
                || role.equals(Role.CHAIR) || role.equals(Role.VICE_CHAR)
                || role.equals(Role.SECRETARY))) {
            return categoryRepository.findAll();
        }
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
        if (roles.stream().anyMatch(role -> role.equals(Role.ADMINISTRATOR)
                || role.equals(Role.CHAIR) || role.equals(Role.VICE_CHAR)
                || role.equals(Role.SECRETARY))) {
            return categoryRepository.findAll()
                    .stream()
                    .filter(c -> c.getParentCategoryId() == -1L)
                    .map(Category::getCategoryId).toList();
        }
        categoryIds.add(-1L);
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

    public List<Category> getAllChildrenCategories(Long parentId, User user) {
        List<Long> allowedCategories = getAllowedCategoriesIds(user.getRoles());
        if (!allowedCategories.contains(parentId)) {
            throw new AccessDeniedException("not allowed to get children categories of `%s`".formatted(parentId));
        }
        return categoryRepository.findByParentCategoryId(parentId);
    }

    public List<FullCategoryResponse> getAllCategories(User user) {
        List<Category> parentCategories = getAllParentCategories(user);
        List<FullCategoryResponse> result = new LinkedList<>();
        for (var parent : parentCategories) {
            List<Category> childrenCategories = getAllChildrenCategories(parent.getCategoryId());
            if (!childrenCategories.isEmpty()) {
                FullCategoryResponse response = new FullCategoryResponse(
                        parent.getCategoryId(),
                        parent.getParentCategoryId(),
                        parent.getName(),
                        childrenCategories
                );
                result.add(response);
                continue;
            }
            FullCategoryResponse response = new FullCategoryResponse(
                    parent.getCategoryId(),
                    parent.getParentCategoryId(),
                    parent.getName(),
                    Collections.emptyList()
            );
            result.add(response);
        }
        return result;
    }

    @Transactional
    public Category updateCategoryPermission(CategoryPermissionRequestDto request, User loggedInUser) {
        Category category = getParentCategory(request.categoryId(), loggedInUser);
        if (request.delete()) {
            CategoryPermission permission = categoryPermissionRepository.findByCategoryIdAndRole(request.categoryId(), request.role())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "category permission with id %s not found".formatted(request.categoryId())
                    ));
            categoryPermissionRepository.delete(permission);
        } else {
            Optional<CategoryPermission> permission = categoryPermissionRepository.findByCategoryIdAndRole(request.categoryId(), request.role());
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
