package com.ahmadabbas.filetracking.backend.category;

import com.ahmadabbas.filetracking.backend.category.payload.*;
import com.ahmadabbas.filetracking.backend.category.permission.*;
import com.ahmadabbas.filetracking.backend.exception.*;
import com.ahmadabbas.filetracking.backend.user.*;
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
    public Category createCategory(AddCategoryRequest request, User user) {
        Set<Role> roles = user.getRoles();
        if (roles.contains(Role.SECRETARY) && request.parentCategoryId() == -1L) {
            throw new AccessDeniedException("only allowed to create sub categories");
        }
        if (!categoryRepository.existsByName(request.name())) {
            return categoryRepository.save(
                    Category.builder()
                            .parentCategoryId(request.parentCategoryId())
                            .name(request.name())
                            .build()
            );
        }
        throw new DuplicateResourceException("category with the name '%s' already exists!".formatted(request.name()));
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
        if (roles.stream().anyMatch(role -> role.equals(Role.ADMINISTRATOR))) {
            return categoryRepository.findAll();
        }
        for (var role : roles) {
            Set<CategoryPermission> categoryPermissions = categoryPermissionRepository.findAllByRole(role);
            categoryPermissions.forEach(permission -> {
                Category category = permission.getCategory();
                if (category != null) categories.add(category);
            });
        }
        return categories;
    }

    public List<Long> getAllowedCategoriesIds(Set<Role> roles) {
        var allCategories = getAllowedCategories(roles);
        return allCategories.stream()
                .map(Category::getCategoryId).toList();
    }

    public List<Category> getAllChildrenCategories(Long parentId) {
        return categoryRepository.findByParentCategoryId(parentId);
    }

    public List<Category> getAllChildrenCategories(Long parentId, User user) {
        List<Long> allowedCategoriesIds = getAllowedCategoriesIds(user.getRoles());
        if (!allowedCategoriesIds.contains(parentId)) {
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
    public FullCategoryPermissionResponse updateCategoryPermission(CategoryPermissionRequestDto request, User loggedInUser) {
        Category category = getParentCategory(request.categoryId(), loggedInUser);
        if (request.delete()) {
            CategoryPermission permission = categoryPermissionRepository.findAllByCategoryIdAndRole(request.categoryId(), request.role())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "category permission with id %s not found".formatted(request.categoryId())
                    ));
            categoryPermissionRepository.delete(permission);
        } else {
            Optional<CategoryPermission> permission = categoryPermissionRepository.findAllByCategoryIdAndRole(request.categoryId(), request.role());
            if (permission.isPresent()) {
                throw new DuplicateResourceException("category permission already exists!");
            }
            categoryPermissionRepository.save(new CategoryPermission(
                    request.role(),
                    category
            ));
        }
        return getCategoryPermissions(request.categoryId());
    }

    public List<FullCategoryPermissionResponse> getAllCategoryPermissions() {
        Map<Long, FullCategoryPermissionResponse> categoryMap = new HashMap<>();
        List<Category> allParentCategories = categoryRepository.findAllParentCategories();
        for (var cat : allParentCategories) {
            long categoryId = cat.getCategoryId();
            String name = cat.getName();
            var categoryResponse = categoryMap.getOrDefault(categoryId,
                    new FullCategoryPermissionResponse(categoryId, name, new ArrayList<>()));
            List<CategoryPermission> allPermsByCategoryId = categoryPermissionRepository.findAllByCategoryId(categoryId);
            var roles = allPermsByCategoryId.stream().map(CategoryPermission::getRole).toList();
            categoryResponse.permittedRoles().addAll(roles);
            categoryMap.put(categoryId, categoryResponse);
        }
        return categoryMap.values().stream().toList();
    }

    private FullCategoryPermissionResponse getCategoryPermissions(Long categoryId) {
        Map<Long, FullCategoryPermissionResponse> categoryMap = new HashMap<>();
        List<CategoryPermission> allPerms = categoryPermissionRepository.findAllByCategoryId(categoryId);
        for (var perm : allPerms) {
            String name = perm.getCategory().getName();

            FullCategoryPermissionResponse categoryResponse = categoryMap.getOrDefault(categoryId,
                    new FullCategoryPermissionResponse(categoryId, name, new ArrayList<>()));

            categoryResponse.permittedRoles().add(perm.getRole());
            categoryMap.put(categoryId, categoryResponse);
        }
        return categoryMap.get(categoryId);
    }
}
