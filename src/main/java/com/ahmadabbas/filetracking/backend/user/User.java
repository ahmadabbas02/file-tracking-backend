package com.ahmadabbas.filetracking.backend.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Table(
        name = "_user",
        indexes = {
                @Index(name = "idx_user_name", columnList = "name")
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
@NamedEntityGraph(
        name = "User.eagerlyFetchRoles",
        attributeNodes = @NamedAttributeNode("roles")
)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "_user_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @NotEmpty(message = "At least one role must be specified")
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    private Set<Role> roles;

    @Builder.Default
    @Column(nullable = false)
    private boolean isEnabled = true;

    public void setRoles(Role... roles) {
        this.roles = Stream.of(roles).collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Stream<List<SimpleGrantedAuthority>> stream = roles.stream().map(Role::getAuthorities);
        List<SimpleGrantedAuthority> authorities = stream.flatMap(List::stream).collect(Collectors.toList());
        return authorities;
//        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name)
                && Objects.equals(email, user.email) && Objects.equals(password, user.password)
                && roles.equals(user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, roles);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("email='" + email + "'")
                .add("roles=" + roles)
                .add("isEnabled=" + isEnabled)
                .toString();
    }

    public static class UserBuilder {
        public UserBuilder roles(Role... roles) {
            this.roles = Stream.of(roles).collect(Collectors.toSet());
            return this;
        }
    }

}
