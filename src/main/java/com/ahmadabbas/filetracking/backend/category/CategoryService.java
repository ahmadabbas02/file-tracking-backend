package com.ahmadabbas.filetracking.backend.category;

import com.ahmadabbas.filetracking.backend.category.payload.AddCategoryRequest;
import com.ahmadabbas.filetracking.backend.category.payload.CategoryPermissionRequestDto;
import com.ahmadabbas.filetracking.backend.category.payload.FullCategoryPermissionResponse;
import com.ahmadabbas.filetracking.backend.category.payload.FullCategoryResponse;
import com.ahmadabbas.filetracking.backend.category.permission.CategoryPermission;
import com.ahmadabbas.filetracking.backend.category.permission.CategoryPermissionRepository;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.EducationStatus;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryPermissionRepository categoryPermissionRepository;
    private final UserService userService;
    private final EntityManager entityManager;

    @Transactional
    public Category createCategory(AddCategoryRequest request, User loggedInUser) {
        Set<Role> roles = loggedInUser.getRoles();
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

    public List<Category> getAllParentCategories(User loggedInUser, boolean isDeleted) {
        return getCategoriesWithDeletionFilter(
                isDeleted,
                loggedInUser,
                () -> {
                    List<Category> categoryList;
                    Set<Role> roles = userService.getRoles(loggedInUser);
                    if (roles.contains(Role.ADMINISTRATOR)) {
                        categoryList = categoryRepository.findAllByParentCategoryId(-1L);
                    } else {
                        categoryList = getAllowedParentCategories(loggedInUser, isDeleted);
                    }
                    return categoryList;
                }
        );
    }

    public List<FullCategoryResponse> getAllCategories(User loggedInUser, boolean isDeleted) {
        List<Category> parentCategories = getAllParentCategoriesDeletedOrNot(loggedInUser);
        List<FullCategoryResponse> result = new LinkedList<>();
        for (var parent : parentCategories) {
            if (!canAccessCategory(loggedInUser, parent)) {
                continue;
            }
            List<Category> childrenCategories;
            try {
                childrenCategories = getAllChildrenCategories(parent.getCategoryId(), loggedInUser, isDeleted);
            } catch (AccessDeniedException e) {
                continue;
            }
            FullCategoryResponse response = getFullCategoryResponse(parent, childrenCategories);
            if (isDeleted) {
                if (response.deleted() || response.subCategories().parallelStream().anyMatch(Category::isDeleted)) {
                    result.add(response);
                }
            } else {
                result.add(response);
            }
        }
        return result;
    }

    public List<Category> getAllChildrenCategories(Long parentId, User loggedInUser) {
        return getAllChildrenCategories(parentId, loggedInUser, false);
    }

    @Transactional
    public FullCategoryResponse toggleVisibility(Long categoryId, User loggedInUser) {
        Category category = getCategoryNullable(categoryId, loggedInUser);
        if (category != null) {
            log.debug("We should delete categoryId: {}", categoryId);
            if (category.getParentCategoryId() == -1) {
                List<Category> children = getAllChildrenCategories(categoryId, loggedInUser);
                var response = getFullCategoryResponse(category, children);
                if (!children.isEmpty()) {
                    categoryRepository.deleteAll(children);
                }
                categoryRepository.delete(category);
                return response;
            }
            categoryRepository.delete(category);
            return getFullCategoryResponse(category);
        } else {
            log.debug("We should undelete categoryId: {}", categoryId);
            category = getCategoryWithDeletionFilter(categoryId, loggedInUser, true);
            if (category.getParentCategoryId() == -1) {
                List<Category> children = getAllChildrenCategories(categoryId, loggedInUser, true);
                var response = getFullCategoryResponse(category, children);
                if (!children.isEmpty()) {
                    categoryRepository.undeleteAll(children.stream().map(Category::getCategoryId).toList());
                }
                categoryRepository.undeleteAll(Collections.singletonList(category.getCategoryId()));
                return response;
            }
            categoryRepository.undeleteAll(Collections.singletonList(category.getCategoryId()));
            return getFullCategoryResponse(category);
        }
    }

    public List<FullCategoryPermissionResponse> getAllCategoryPermissions(User loggedInUser, boolean isDeleted) {
        Map<Long, FullCategoryPermissionResponse> categoryMap = new LinkedHashMap<>();
        List<Category> allParentCategories = getAllowedParentCategories(loggedInUser, isDeleted);
        for (var category : allParentCategories) {
            long categoryId = category.getCategoryId();
            FullCategoryPermissionResponse categoryResponse = categoryMap.getOrDefault(
                    categoryId,
                    getCategoryPermissionResponse(category, loggedInUser, isDeleted)
            );
            categoryMap.put(categoryId, categoryResponse);
        }
        return categoryMap.values().stream().toList();
    }

    @Transactional
    public FullCategoryPermissionResponse updateCategoryPermission(CategoryPermissionRequestDto request,
                                                                   User loggedInUser) {
        Category category = getParentCategory(request.categoryId(), loggedInUser);
        if (request.delete()) {
            CategoryPermission permission =
                    categoryPermissionRepository.findAllByCategoryIdAndRole(request.categoryId(), request.role())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "category permission related with category_id %s not found"
                                            .formatted(request.categoryId())
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
        return getCategoryPermissionResponse(category, loggedInUser, false);
    }

    public Category getParentCategory(Long categoryId, User loggedInUser) {
        return getCategoryWithDeletionFilter(categoryId, -1L, loggedInUser);
    }

    public Category getCategoryByName(String name) {
        return getCategoryWithDeletionFilter(
                false,
                null,
                () -> categoryRepository.findByNameIgnoreCase(name)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "category with name %s not found".formatted(name)
                        ))
        );
    }

    public Category getCategoryWithDeletionFilter(Long categoryId, User loggedInUser, boolean isDeleted) {
        Category category = getCategoryWithDeletionFilter(
                isDeleted,
                loggedInUser,
                () -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "category with id %s not found".formatted(categoryId)
                        ))
        );
        if (!getAllowedCategoriesIds(loggedInUser, isDeleted).contains(category.getCategoryId())) {
            throw new AccessDeniedException("not authorized");
        }
        return category;
    }

    public Category getCategoryWithDeletionFilter(Long categoryId, Long parentCategoryId, User loggedInUser) {
        Category category = getCategoryWithDeletionFilter(
                false,
                loggedInUser,
                () -> categoryRepository.findByCategoryIdAndParentCategoryId(categoryId, parentCategoryId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "category with id %s and parent_id %s not found"
                                        .formatted(categoryId, parentCategoryId)
                        ))
        );
        if (!getAllowedCategoriesIds(loggedInUser).contains(category.getCategoryId())) {
            throw new AccessDeniedException("not authorized");
        }
        return category;
    }

    public List<Category> getAllowedParentCategories(User loggedInUser, boolean isDeleted) {
        if (isDeleted) {
            List<FullCategoryResponse> allParents = getAllCategories(loggedInUser, isDeleted);
            return allParents
                    .stream()
                    .filter(fcr -> !fcr.subCategories().isEmpty() || fcr.deleted())
                    .map(fullCategoryResponse ->
                            new Category(
                                    fullCategoryResponse.parentCategoryId(),
                                    fullCategoryResponse.categoryId(),
                                    fullCategoryResponse.name(),
                                    fullCategoryResponse.deleted()
                            )
                    )
                    .toList();
        }
        return getCategoriesWithDeletionFilter(
                false,
                loggedInUser,
                () -> getAllowedParentCategories(loggedInUser.getRoles())
        );
    }

    public List<Long> getAllowedCategoriesIds(User loggedInUser) {
        return getAllowedCategoriesIds(loggedInUser, false);
    }

    public List<Category> getAllowedCategories(User loggedInUser, boolean isDeleted) {
        Set<Role> roles = loggedInUser.getRoles();
        return getCategoriesWithDeletionFilter(
                isDeleted,
                loggedInUser,
                () -> {
                    List<Category> categories = new LinkedList<>();
                    if (roles.contains(Role.ADMINISTRATOR)) {
                        return categoryRepository.findAll();
                    }
                    for (var role : roles) {
                        Set<CategoryPermission> categoryPermissions = categoryPermissionRepository.findAllByRole(role);
                        categoryPermissions.forEach(permission -> {
                            Category category = permission.getCategory();
                            if (category != null) {
                                if (canAccessCategory(loggedInUser, category)) {
                                    categories.add(category);
                                    if (category.getParentCategoryId() == -1) {
                                        List<Category> childrenCategories =
                                                categoryRepository.findAllByParentCategoryId(category.getCategoryId());
                                        categories.addAll(childrenCategories);
                                    }
                                }
                            }
                        });
                    }
                    return categories;
                });
    }

    public List<Category> getAllParentCategoriesDeletedOrNot(User loggedInUser) {
        List<Category> categoryList;
        Set<Role> roles = userService.getRoles(loggedInUser);
        if (roles.contains(Role.ADMINISTRATOR)) {
            categoryList = categoryRepository.findAllByParentCategoryId(-1L);
        } else {
            categoryList = getAllowedParentCategories(roles);
        }
        return categoryList;
    }

    private List<Category> getAllowedParentCategories(Set<Role> roles) {
        List<Category> categories = new LinkedList<>();
        if (roles.contains(Role.ADMINISTRATOR)) {
            categories = categoryRepository.findAllParentCategories();
        } else {
            for (var role : roles) {
                Set<CategoryPermission> categoryPermissions =
                        categoryPermissionRepository.findAllByRole(role);
                for (CategoryPermission permission : categoryPermissions) {
                    Category category = permission.getCategory();
                    if (category != null) {
                        if (category.getParentCategoryId() == -1) {
                            categories.add(category);
                        }
                    }
                }
            }
        }
        return categories;
    }

    private List<Category> getAllChildrenCategories(Long categoryId, User loggedInUser, boolean isDeleted) {
        List<Long> allowedCategoriesIds = getAllowedCategoriesIds(loggedInUser, isDeleted);
        if (!allowedCategoriesIds.contains(categoryId)) {
            throw new AccessDeniedException("not allowed to get children categories of `%s`".formatted(categoryId));
        }
        return getCategoriesWithDeletionFilter(isDeleted, loggedInUser, () -> categoryRepository.findAllByParentCategoryId(categoryId));
    }

    private List<Long> getAllowedCategoriesIds(User loggedInUser, boolean isDeleted) {
        if (isDeleted) {
            List<Category> allCategories = getAllowedCategories(loggedInUser, true);
            allCategories.addAll(getAllowedCategories(loggedInUser, false));
            return allCategories.stream()
                    .map(Category::getCategoryId).distinct().toList();
        }
        List<Category> allCategories = getAllowedCategories(loggedInUser, false);
        return allCategories.stream()
                .map(Category::getCategoryId).toList();
    }

    private Category getCategoryNullable(Long categoryId, User loggedInUser) {
        return getCategoryWithDeletionFilter(
                false,
                loggedInUser,
                () -> categoryRepository.findById(categoryId).orElse(null)
        );
    }

    private boolean canAccessCategory(User user, Category parent) {
        Student student = user.getStudent();
        if (student != null) {
            return !parent.getName().toLowerCase().contains("defense")
                   || student.getEducationStatus().equals(EducationStatus.GRADUATE);
        }
        return true;
    }

    private FullCategoryResponse getFullCategoryResponse(Category parent) {
        return getFullCategoryResponse(parent, Collections.emptyList());
    }

    private FullCategoryResponse getFullCategoryResponse(Category parent, List<Category> childrenCategories) {
        FullCategoryResponse response;
        if (!childrenCategories.isEmpty()) {
            response = new FullCategoryResponse(
                    parent.getCategoryId(),
                    parent.getParentCategoryId(),
                    parent.getName(),
                    parent.isDeleted(),
                    childrenCategories
            );
        } else {
            response = new FullCategoryResponse(
                    parent.getCategoryId(),
                    parent.getParentCategoryId(),
                    parent.getName(),
                    parent.isDeleted(),
                    Collections.emptyList()
            );
        }
        return response;
    }

    private FullCategoryPermissionResponse getCategoryPermissionResponse(Category category,
                                                                         User loggedInUser,
                                                                         boolean isDeleted) {
        Long categoryId = category.getCategoryId();
        String categoryName = category.getName();
        boolean categoryDeleted = category.isDeleted();
        List<CategoryPermission> allPerms = categoryPermissionRepository.findAllByCategoryId(categoryId);
        List<Category> subcategories = getAllChildrenCategories(categoryId, loggedInUser, isDeleted);
        FullCategoryPermissionResponse result =
                new FullCategoryPermissionResponse(categoryId, categoryName, categoryDeleted, new ArrayList<>(), new ArrayList<>());
        if (allPerms.isEmpty()) {
            if (!subcategories.isEmpty() && result.subCategories().isEmpty()) {
                result.subCategories().addAll(subcategories);
            }
        } else {
            for (CategoryPermission perm : allPerms) {
                if (!subcategories.isEmpty() && result.subCategories().isEmpty()) {
                    result.subCategories().addAll(subcategories);
                }
                result.permittedRoles().add(perm.getRole());
            }
        }
        return result;
    }

    private Category getCategoryWithDeletionFilter(boolean isDeleted,
                                                   User loggedInUser,
                                                   Supplier<Category> categorySupplier) {
        if (isDeleted && loggedInUser != null && !loggedInUser.isAdmin()) {
            isDeleted = false;
        }
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedCategoryFilter");
        filter.setParameter("isDeleted", isDeleted);
        Category category = categorySupplier.get();
        session.disableFilter("deletedCategoryFilter");
        return category;
    }

    private List<Category> getCategoriesWithDeletionFilter(boolean isDeleted,
                                                           User loggedInUser,
                                                           Supplier<List<Category>> categorySupplier) {
        if (isDeleted && loggedInUser != null && !loggedInUser.isAdmin()) {
            isDeleted = false;
        }
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedCategoryFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Category> categories = categorySupplier.get();
        session.disableFilter("deletedCategoryFilter");
        return categories;
    }

}
