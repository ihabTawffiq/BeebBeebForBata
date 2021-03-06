package com.trixpert.beebbeeb.data.repositories;

import com.trixpert.beebbeeb.data.entites.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, Long> {
    List<AuditEntity> findAllByAction(String action);

    int countAllByAction(String action);
}
