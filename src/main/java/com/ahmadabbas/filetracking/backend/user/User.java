package com.ahmadabbas.filetracking.backend.user;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.auth.token.Token;
import com.ahmadabbas.filetracking.backend.student.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ahmadabbas.filetracking.backend.user.Role.ADMINISTRATOR;
import static com.ahmadabbas.filetracking.backend.user.Role.STUDENT;

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
public class User {

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

    @Column(nullable = false)
    private boolean isCredentialsNonExpired;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Token> tokens;

    @OneToOne(mappedBy = "user")
    private Advisor advisor;
    @OneToOne(mappedBy = "user")
    private Student student;

    @Column(nullable = false)
    @Builder.Default
    @Version
    private Integer version = 0;

    public void setRoles(Role... roles) {
        this.roles = Stream.of(roles).collect(Collectors.toSet());
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
               ", isEnabled=" + isEnabled +
               ", isCredentialsNonExpired=" + isCredentialsNonExpired +
               '}';
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Deprecated
    public void setFullName(String fullName) {

    }

    public boolean hasRole(Role role) {
        return getRoles().contains(role);
    }

    public boolean isAdmin() {
        return hasRole(ADMINISTRATOR);
    }

    public boolean isAdvisor() {
        return hasRole(Role.ADVISOR);
    }

    public boolean isStudent() {
        return hasRole(STUDENT);
    }
}


