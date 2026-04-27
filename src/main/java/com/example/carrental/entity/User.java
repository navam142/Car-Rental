package com.example.carrental.entity;

import com.example.carrental.enums.LicenseStatus;
import com.example.carrental.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private boolean isVerified = false;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    private LicenseStatus licenseStatus = LicenseStatus.PENDING;

    @Column(unique = true)
    private String licenseNo;

}
