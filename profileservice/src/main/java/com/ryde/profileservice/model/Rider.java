package com.ryde.profileservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rider_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private Long userId;
    private Double latitude;
    private Double longitude;
    @CreationTimestamp
    private LocalDateTime createdAt;
}