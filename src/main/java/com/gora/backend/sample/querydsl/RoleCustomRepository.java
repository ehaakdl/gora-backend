package com.gora.backend.sample.querydsl;

import com.gora.backend.model.entity.user.QUserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoleCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    public Object get() {
        return jpaQueryFactory.selectFrom(QUserEntity.userEntity)
                .fetch();
    }
    public void save() {
        jpaQueryFactory.insert(QUserEntity.userEntity)
                .columns(QUserEntity.userEntity.email)
                .values("querydsl")
                .execute();
    }
}
