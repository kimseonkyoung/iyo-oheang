package com.iyo.ohhaeng.infra.db;

import com.iyo.ohhaeng.domain.weapon.ElementType;
import com.iyo.ohhaeng.domain.weapon.Weapon;

public interface WeaponRepository {
    void insert(String ownerId, ElementType elementType);

    Weapon findByUserId(String userId);
    void update(Weapon weapon);
}
