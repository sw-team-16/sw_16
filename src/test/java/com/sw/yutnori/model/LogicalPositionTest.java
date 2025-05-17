/*
 * LogicalPositionTest.java
 * LogicalPosition 클래스에 대한 테스트 코드
 * 
 * 
 * 
 */
package com.sw.yutnori.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

    // pieceId 1L, a값 5, b값 2로 LogicalPosition이 올바르게 초기화되는지 확인
class LogicalPositionTest {
    @Test
    void constructor_withPieceIdAandB_initializesCorrectly() {
        LogicalPosition pos = new LogicalPosition(1L, 5, 2);
        assertEquals(1L, pos.getPieceId());
        assertEquals(5, pos.getA());
        assertEquals(2, pos.getB());
    }

    // pieceId null, a값 3, b값 10으로 LogicalPosition이 올바르게 초기화되는지 확인
    @Test
    void constructor_withAandBOnly_initializesPieceIdToNull() {
        LogicalPosition pos = new LogicalPosition(3, 10);
        assertNull(pos.getPieceId());
        assertEquals(3, pos.getA());
        assertEquals(10, pos.getB());
    }
} 