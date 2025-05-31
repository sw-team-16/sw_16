package com.sw.yutnori.logic;

import com.sw.yutnori.model.LogicalPosition;
import com.sw.yutnori.model.enums.BoardType;
import com.sw.yutnori.model.enums.YutResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardPathManagerTest {

    private final BoardPathManager pathManager = new BoardPathManager();

    @Test
    void testComputePathSquare_doSteps() {
        LogicalPosition start = new LogicalPosition(null, 0, 1);
        List<LogicalPosition> path = pathManager.computePath(start, "도", "square");
        assertEquals(1, path.size());
    }

    @Test
    void testComputePathPentagon_geolSteps() {
        LogicalPosition start = new LogicalPosition(null, 0, 1);
        List<LogicalPosition> path = pathManager.computePath(start, "걸", "pentagon");
        assertEquals(3, path.size());
    }

    @Test
    void testComputePathHexagon_moSteps() {
        LogicalPosition start = new LogicalPosition(null, 0, 1);
        List<LogicalPosition> path = pathManager.computePath(start, "모", "hexagon");
        assertEquals(5, path.size());
    }

    @Test
    void testComputePath_invalidBoardType_throwsException() {
        LogicalPosition start = new LogicalPosition(null, 0, 1);
        assertThrows(IllegalArgumentException.class, () ->
                pathManager.computePath(start, "도", "triangle"));
    }

    @Test
    void testCalculateDestination_squareGeneralPath() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 0, 1, 0, 1, YutResult.GAE, BoardType.SQUARE);
        assertEquals(2, dest.getA());
        assertEquals(1, dest.getB());
    }

    @Test
    void testCalculateDestination_pentagonShortcut() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 5, 1, 0, 1, YutResult.DO, BoardType.PENTAGON);
        assertEquals(10, dest.getA());
        assertEquals(1, dest.getB());
    }

    @Test
    void testCalculateDestination_hexagonShortcut() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 5, 2, 0, 1, YutResult.DO, BoardType.HEXAGON);
        assertEquals(20, dest.getA());
        assertEquals(1, dest.getB());
    }

    @Test
    void givenStartPosition_whenDoOnSquareBoard_thenMoveToFirstNode() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 0, 1, 0, 1, YutResult.DO, BoardType.SQUARE);
        assertEquals(1, dest.getA());
        assertEquals(1, dest.getB());
    }

    @Test
    void givenCornerPosition_whenMoOnSquareBoard_thenMoveToCorrectNode() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 5, 1, 0, 1, YutResult.MO, BoardType.SQUARE);
        assertEquals(30, dest.getA());
        assertEquals(1, dest.getB());
    }

    @Test
    void givenOnBoard_whenBackDoOnSquareBoard_thenMoveBackOrStay() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 1, 1, 0, 1, YutResult.BACK_DO, BoardType.SQUARE);
        assertEquals(0, dest.getA());
        assertEquals(1, dest.getB());
    }

    @Test
    void givenStartPosition_whenBackDoOnSquareBoard_thenStayAtStart() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 0, 1, 0, 1, YutResult.BACK_DO, BoardType.SQUARE);
        assertEquals(0, dest.getA());
        assertEquals(1, dest.getB());
    }

    @Test
    void givenNearGoal_whenDoOnSquareBoard_thenArriveAtGoal() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 4, 4, 0, 1, YutResult.DO, BoardType.SQUARE);
        assertEquals(0, dest.getA());
        assertEquals(1, dest.getB());
    }

    @Test
    void givenPentagonBranch_whenMoOnPentagonBoard_thenMoveToCorrectNode() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 5, 1, 0, 1, YutResult.MO, BoardType.PENTAGON);
        assertEquals(40, dest.getA());
        assertEquals(1, dest.getB());
    }

    @Test
    void givenNearGoal_whenDoOnPentagonBoard_thenArriveAtGoal() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 4, 5, 0, 1, YutResult.DO, BoardType.PENTAGON);
        assertEquals(0, dest.getA());
        assertEquals(1, dest.getB());
    }

    @Test
    void givenHexagonBranch_whenGeolOnHexagonBoard_thenMoveToCorrectNode() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 5, 2, 0, 1, YutResult.GEOL, BoardType.HEXAGON);
        assertEquals(3, dest.getA());
        assertEquals(10, dest.getB());
    }

    @Test
    void givenNearGoal_whenDoOnHexagonBoard_thenArriveAtGoal() {
        LogicalPosition dest = BoardPathManager.calculateDestination(1L, 4, 6, 0, 1, YutResult.DO, BoardType.HEXAGON);
        assertEquals(0, dest.getA());
        assertEquals(1, dest.getB());
    }
}
