package com.sw.yutnori.repository;

import com.sw.yutnori.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {}
