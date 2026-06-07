package org.cordeirops.compassbank.adapter.out.persistence.repository;

import jakarta.persistence.LockModeType;
import org.cordeirops.compassbank.adapter.out.persistence.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface ContaJpaRepository extends JpaRepository<ContaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ContaEntity c WHERE c.id = :id")
    Optional<ContaEntity> findByIdComLock(@Param("id") UUID id);
}
