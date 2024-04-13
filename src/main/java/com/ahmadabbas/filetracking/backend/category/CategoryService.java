package com.ahmadabbas.filetracking.backend.category;

import com.ahmadabbas.filetracking.backend.category.payload.AddCategoryRequest;
import com.ahmadabbas.filetracking.backend.category.payload.CategoryPermissionRequestDto;
import com.ahmadabbas.filetracking.backend.category.payload.FullCategoryPermissionResponse;
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
        if (!getAllowedCategoriesIds(loggedInUser.getRoles()).contains(category.getCategoryId())) {
            throw new AccessDeniedException("not authorized");
        }
        return category;
    }

    public Category getCategory(Long categoryId, Long parentCategoryId, @AuthenticationPrincipal User loggedInUser) {
        Category category = categoryRepository.findByCategoryIdAndParentCategoryId(categoryId, parentCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "category with id %s and parent_id %s not found".formatted(categoryId, parentCategoryId)
                ));
        if (!getAllowedCategoriesIds(loggedInUser.getRoles()).contains(category.getCategoryId())) {
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
            return categoryRepository.findAllByParentCategoryId(-1L);
        } else {
            categories = getAllowedParentCategories(roles);
        }
        return categories;
    }

    public List<Category> getAllowedCategories(Set<Role> roles) {
        List<Category> categories = new LinkedList<>();
        if (roles.contains(Role.ADMINISTRATOR)) {
            return categoryRepository.findAll();
        }
        for (var role : roles) {
            Set<CategoryPermission> categoryPermissions = categoryPermissionRepository.findAllByRole(role);
            categoryPermissions.forEach(permission -> {
                Category category = permission.getCategory();
                if (category != null) {
                    categories.add(category);
                    if (category.getParentCategoryId() == -1) {
                        List<Category> childrenCategories = categoryRepository.findAllByParentCategoryId(category.getCategoryId());
                        categories.addAll(childrenCategories);
                    }
                }
            });
        }
        return categories;
    }

    public List<Category> getAllowedParentCategories(Set<Role> roles) {
        List<Category> categories = new LinkedList<>();
        if (roles.stream().anyMatch(role -> role.equals(Role.ADMINISTRATOR))) {
            return categoryRepository.findAllParentCategories();
        }
        for (var role : roles) {
            Set<CategoryPermission> categoryPermissions = categoryPermissionRepository.findAllByRole(role);
            categoryPermissions.forEach(permission -> {
                Category category = permission.getCategory();
                if (category != null) {
                    if (category.getParentCategoryId() == -1) {
                        categories.add(category);
                    }
                }
            });
        }
        return categories;
    }

    public List<Long> getAllowedCategoriesIds(Set<Role> roles) {
        List<Category> allCategories = getAllowedCategories(roles);
        return allCategories.stream()
                .map(Category::getCategoryId).toList();
    }

    public List<Category> getAllChildrenCategories(Long parentId) {
        return categoryRepository.findAllByParentCategoryId(parentId);
    }

    public List<Category> getAllChildrenCategories(Long parentId, User user) {
        List<Long> allowedCategoriesIds = getAllowedCategoriesIds(user.getRoles());
        if (!allowedCategoriesIds.contains(parentId)) {
            throw new AccessDeniedException("not allowed to get children categories of `%s`".formatted(parentId));
        }
        return categoryRepository.findAllByParentCategoryId(parentId);
    }

    public List<FullCategoryResponse> getAllCategories(User user) {
        List<Category> parentCategories = getAllParentCategories(user);
        List<FullCategoryResponse> result = new LinkedList<>();
        for (var parent : parentCategories) {
            List<Category> childrenCategories = getAllChildrenCategories(parent.getCategoryId());
            FullCategoryResponse response;
            if (!childrenCategories.isEmpty()) {
                response = new FullCategoryResponse(
                        parent.getCategoryId(),
                        parent.getParentCategoryId(),
                        parent.getName(),
                        childrenCategories
                );
            } else {
                response = new FullCategoryResponse(
                        parent.getCategoryId(),
                        parent.getParentCategoryId(),
                        parent.getName(),
                        Collections.emptyList()
                );
            }
            result.add(response);
        }
        return result;
    }

    @Transactional
    public FullCategoryPermissionResponse updateCategoryPermission(CategoryPermissionRequestDto request,
                                                                   User loggedInUser) {
        Category category = getParentCategory(request.categoryId(), loggedInUser);
        if (request.delete()) {
            CategoryPermission permission =
                    categoryPermissionRepository.findAllByCategoryIdAndRole(request.categoryId(), request.role())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "category permission related with category_id %s not found".formatted(request.categoryId())
                            ));
            categoryPermissionRepository.delete(permission);
        } else {
            Optional<CategoryPermission> permission =
                    categoryPermissionRepository.findAllByCategoryIdAndRole(request.categoryId(), request.role());
            if (permission.isPresent()) {
                throw new DuplicateResourceException("category permission already exists!");
            }
            categoryPermissionRepository.save(new CategoryPermission(
                    request.role(),
                    category
            ));
        }
        return getCategoryPermissionResponse(request.categoryId(), category.getName(), loggedInUser);
    }

    public List<FullCategoryPermissionResponse> getAllCategoryPermissions(User loggedInUser) {
        Map<Long, FullCategoryPermissionResponse> categoryMap = new LinkedHashMap<>();
        List<Category> allParentCategories = getAllowedParentCategories(loggedInUser.getRoles());
        for (var category : allParentCategories) {
            long categoryId = category.getCategoryId();
            String categoryName = category.getName();
            FullCategoryPermissionResponse categoryResponse = categoryMap.getOrDefault(
                    categoryId,
                    getCategoryPermissionResponse(categoryId, categoryName, loggedInUser)
            );
            categoryMap.put(categoryId, categoryResponse);
        }
        return categoryMap.values().stream().toList();
    }

    private FullCategoryPermissionResponse getCategoryPermissionResponse(Long categoryId, String categoryName, User loggedInUser) {
        List<CategoryPermission> allPerms = categoryPermissionRepository.findAllByCategoryId(categoryId);
        List<Category> subcategories = getAllChildrenCategories(categoryId, loggedInUser);
        if (allPerms.isEmpty()) {
            FullCategoryPermissionResponse result = new FullCategoryPermissionResponse(categoryId, categoryName, new ArrayList<>(), new ArrayList<>());
            if (!subcategories.isEmpty() && result.subCategories().isEmpty()) {
                result.subCategories().addAll(subcategories);
            }
            return result;
        } else {
            FullCategoryPermissionResponse result = new FullCategoryPermissionResponse(categoryId, categoryName, new ArrayList<>(), new ArrayList<>());
            allPerms.forEach(perm -> {
                if (!subcategories.isEmpty() && result.subCategories().isEmpty()) {
                    result.subCategories().addAll(subcategories);
                }
                result.permittedRoles().add(perm.getRole());
            });
            return result;
        }
    }

}
