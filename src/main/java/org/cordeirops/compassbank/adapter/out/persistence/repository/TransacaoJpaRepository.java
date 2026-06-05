package org.cordeirops.compassbank.adapter.out.persistence.repository;

import org.cordeirops.compassbank.adapter.out.persistence.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransacaoJpaRepository extends JpaRepository<TransacaoEntity, UUID> {

    List<TransacaoEntity> findByContaOrigemIdOrContaDestinoIdOrderByCriadaEmDesc(
            UUID contaOrigemId, UUID contaDestinoId);
}
