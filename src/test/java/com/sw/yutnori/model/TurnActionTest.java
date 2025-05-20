package com.sw.yutnori.model;

import com.sw.yutnori.model.enums.YutResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TurnActionTest {

    @Test
    void constructor_setsTurnAndResult() {
        Turn turn = new Turn();
        TurnAction action = new TurnAction();
        action.setTurn(turn);
        action.setResult(YutResult.DO);

        assertEquals(turn, action.getTurn());
        assertEquals(YutResult.DO, action.getResult());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Turn turn = new Turn();
        TurnAction action = new TurnAction();
        action.setTurn(turn);
        action.setResult(YutResult.DO);
        action.setId(100L);
        assertEquals(100L, action.getId());
    }
}
