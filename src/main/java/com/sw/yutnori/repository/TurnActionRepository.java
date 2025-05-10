package com.sw.yutnori.repository;

import com.sw.yutnori.domain.TurnAction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TurnActionRepository extends JpaRepository<TurnAction, Long> {
    List<TurnAction> findByTurn_TurnId(Long turnId);
}
