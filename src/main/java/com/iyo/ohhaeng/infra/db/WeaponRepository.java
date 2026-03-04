package com.iyo.ohhaeng.infra.db;

import com.iyo.ohhaeng.domain.weapon.Weapon;

public interface WeaponRepository {
    Weapon findByUserId(String userId);
}
