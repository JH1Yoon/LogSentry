package com.develop.logsentry.domain.user.entity;

import com.develop.logsentry.common.entity.Timestamped;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTeam> userTeams = new HashSet<>();

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    @Column
    private LocalDateTime deletedAt;

    public void deactivate() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }


    public void changeRole(UserRoleEnum newRole) {
        if (this.role == newRole) {
            return;
        }
        this.role = newRole;
    }
}
