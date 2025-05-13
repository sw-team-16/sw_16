package com.sw.yutnori.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Turn {
    private Long id;
    private Game game;
    private Player player;
    private List<TurnAction> actions;
}
