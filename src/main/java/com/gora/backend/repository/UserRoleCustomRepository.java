package com.gora.backend.repository;

import com.gora.backend.model.entity.user.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gora.backend.model.entity.QPrivilegeEntity.privilegeEntity;
import static com.gora.backend.model.entity.QRoleEntity.roleEntity;
import static com.gora.backend.model.entity.QRolePrivilegeEntity.rolePrivilegeEntity;
import static com.gora.backend.model.entity.user.QUserRoleEntity.userRoleEntity;

@Repository
@RequiredArgsConstructor
public class UserRoleCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    //    select privilege.name from user_role
//    inner join role on user_role.role_seq = role.seq and user_role.user_seq = 1
//    inner join role_privilege on role.seq = role_privilege.role_seq
//    inner join privilege on role_privilege.privilege_seq = privilege.seq
//    ;
    public List<String> findUserPrivilege(UserEntity user) {
        return jpaQueryFactory.select(privilegeEntity.name)
                .from(userRoleEntity)
                .innerJoin(roleEntity).on(
                        userRoleEntity.roleSeq.eq(roleEntity.seq)
                        , userRoleEntity.userSeq.eq(user.getSeq())
                )
                .innerJoin(rolePrivilegeEntity).on(
                        roleEntity.seq.eq(rolePrivilegeEntity.roleSeq)
                )
                .innerJoin(privilegeEntity).on(
                        rolePrivilegeEntity.privilegeSeq.eq(privilegeEntity.seq)
                )
                .fetch();
    }
}
