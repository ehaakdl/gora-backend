package com.gora.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Table(name = "social_user")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserEntity extends DefaultColumn{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @Column
    @Enumerated(EnumType.STRING)
    private eSocialType socialType;
}
