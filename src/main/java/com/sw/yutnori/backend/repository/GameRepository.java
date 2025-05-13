package com.sw.yutnori.backend.repository;

import com.sw.yutnori.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {}
