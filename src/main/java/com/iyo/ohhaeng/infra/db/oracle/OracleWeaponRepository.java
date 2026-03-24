package com.iyo.ohhaeng.infra.db.oracle;

import com.iyo.ohhaeng.domain.weapon.ElementType;
import com.iyo.ohhaeng.domain.weapon.Weapon;
import com.iyo.ohhaeng.infra.db.WeaponRepository;
import com.iyo.ohhaeng.infra.db.mapper.WeaponMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OracleWeaponRepository implements WeaponRepository {

    private final WeaponMapper weaponMapper;

    @Override
    public void insert(String ownerId, ElementType elementType) {
        weaponMapper.insert(ownerId, elementType.name());
    }

    @Override
    public Weapon findByUserId(String userId) {
        return weaponMapper.findByUserId(userId);
    }

    @Override
    public void update(Weapon weapon) {
        weaponMapper.update(weapon);
    }
}
