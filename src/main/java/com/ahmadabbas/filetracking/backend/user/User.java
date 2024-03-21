package com.ahmadabbas.filetracking.backend.user;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.token.Token;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Table(name = "_user")
@NamedEntityGraph(
        name = "User.eagerlyFetchRoles",
        attributeNodes = @NamedAttributeNode("roles")
)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "_user_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String picture;

    @Column(nullable = false)
    private String phoneNumber;

    @Singular
    @NotEmpty(message = "At least one role must be specified")
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    private Set<Role> roles;

    @Builder.Default
    @Column(nullable = false)
    private boolean isEnabled = true;

    @OneToMany(mappedBy = "user")
    private Set<Token> tokens;

    @OneToOne(mappedBy = "user")
    private Advisor advisor;
    @OneToOne(mappedBy = "user")
    private Student student;

    @Version
    private Integer version;

    public void setRoles(Role... roles) {
        this.roles = Stream.of(roles).collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Stream<List<SimpleGrantedAuthority>> stream = roles.stream().map(Role::getAuthorities);
        return stream.flatMap(List::stream).toList();
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
    public String toString() {
        return "User{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", picture='" + picture + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", roles=" + roles +
               '}';
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Deprecated
    public void setFullName(String fullName) {

    }
}


