package com.iyo.ohhaeng.infra.db.mapper;

import com.iyo.ohhaeng.domain.weapon.Weapon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WeaponMapper {

    void insert(@Param("ownerId") String ownerId, @Param("elementType") String elementType);

    Weapon findByUserId(@Param("userId") String userId);

    void update(Weapon weapon);
}
