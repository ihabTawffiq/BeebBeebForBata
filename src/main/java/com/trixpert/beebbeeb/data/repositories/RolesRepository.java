package com.trixpert.beebbeeb.data.repositories;

import com.trixpert.beebbeeb.data.entites.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<RolesEntity, Long> {
    Optional<RolesEntity> findByName(String roleName);
}
