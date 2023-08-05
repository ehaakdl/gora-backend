package com.gora.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social_user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserEntity extends DefaultColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @Column
    @Enumerated(EnumType.STRING)
    private eSocialType socialType;
}
