//package com.sw.yutnori.service;
//
//import com.sw.yutnori.common.enums.*;
//import com.sw.yutnori.domain.*;
//import com.sw.yutnori.dto.game.request.*;
//import com.sw.yutnori.dto.game.response.*;
//import com.sw.yutnori.dto.piece.response.MovablePieceResponse;
//import com.sw.yutnori.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class GameServiceImplTest {
//
//    @Mock GameRepository gameRepository;
//    @Mock PlayerRepository playerRepository;
//    @Mock TurnRepository turnRepository;
//    @Mock PieceRepository pieceRepository;
//    @Mock TurnActionRepository turnActionRepository;
//
//    @InjectMocks GameServiceImpl gameService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createGame_shouldCreateGameAndPieces() {
//        GameCreateRequest request = new GameCreateRequest();
//        PlayerInitRequest player = new PlayerInitRequest();
//        request.setBoardType(BoardType.SQUARE);
//        request.setPlayers(List.of(player));
//        request.setNumPieces(2);
//
//        Game savedGame = new Game();
//        savedGame.setGameId(1L);
//        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);
//
//        GameCreateResponse response = gameService.createGame(request);
//
//        assertThat(response.getGameId()).isEqualTo(1L);
//        verify(gameRepository).save(any(Game.class));
//        verify(playerRepository, atLeastOnce()).save(any(Player.class));
//        verify(pieceRepository, atLeastOnce()).save(any(Piece.class));
//    }
//
//    @Test
//    void getMovablePiecesByPlayer_shouldReturnCorrectPieces() {
//        Player player = new Player();
//        player.setPlayerId(1L);
//        Piece piece1 = new Piece();
//        piece1.setPieceId(10L);
//        piece1.setState(PieceState.READY);
//        Piece piece2 = new Piece();
//        piece2.setPieceId(11L);
//        piece2.setState(PieceState.ON_BOARD);
//        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
//        when(pieceRepository.findByPlayer_PlayerId(1L)).thenReturn(List.of(piece1, piece2));
//
//        List<MovablePieceResponse> result = gameService.getMovablePiecesByPlayer(1L);
//
//        assertThat(result).hasSize(2);
//    }
//
//    @Test
//    void getWinner_shouldReturnWinnerInfo() {
//        Player winner = new Player();
//        winner.setPlayerId(1L);
//        winner.setName("Alice");
//        Game game = new Game();
//        game.setGameId(1L);
//        game.setState(GameState.FINISHED);
//        game.setWinnerPlayer(winner);
//
//        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
//
//        GameWinnerResponse response = gameService.getWinner(1L);
//
//        assertThat(response.getWinnerPlayerId()).isEqualTo(1L);
//        assertThat(response.getWinnerName()).isEqualTo("Alice");
//    }
//
//    @Test
//    void deleteGame_shouldDeleteIfExists() {
//        when(gameRepository.existsById(1L)).thenReturn(true);
//
//        gameService.deleteGame(1L);
//
//        verify(gameRepository).deleteById(1L);
//    }
//
//    @Test
//    void getRandomYutResultForPlayer_shouldReturnResult() {
//        Player player = new Player();
//        Game game = new Game();
//        Turn turn = new Turn();
//        turn.setTurnId(100L);
//
//        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
//        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));
//        when(turnRepository.save(any(Turn.class))).thenReturn(turn);
//
//        AutoThrowResponse response = gameService.getRandomYutResultForPlayer(2L, new AutoThrowRequest(1L));
//
//        assertThat(response.getTurnId()).isEqualTo(100L);
//        assertThat(response.getResult()).isIn(YutResult.values());
//    }
//
//    @Test
//    void getGameStatus_shouldReturnStatus() {
//        Game game = new Game();
//        game.setGameId(1L);
//        game.setState(GameState.PLAYING);
//        game.setBoardType(BoardType.SQUARE);
//        game.setNumPlayers(2);
//        game.setNumPieces(2);
//
//        Piece piece = new Piece();
//        piece.setPieceId(1L);
//        Player player = new Player();
//        player.setPlayerId(10L);
//        piece.setPlayer(player);
//        piece.setX(3);
//        piece.setY(5);
//        piece.setFinished(false);
//
//        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
//        when(pieceRepository.findByPlayer_PlayerId(1L)).thenReturn(List.of(piece));
//
//        GameStatusResponse response = gameService.getGameStatus(1L);
//
//        assertThat(response.getPieces()).hasSize(1);
//    }
//
//    @Test
//    void applyRandomYutResult_shouldReturnResponse() {
//        Player player = new Player();
//        Game game = new Game();
//        Piece piece = new Piece();
//        Turn turn = new Turn();
//        turn.setTurnId(1L);
//
//        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
//        when(gameRepository.findById(anyLong())).thenReturn(Optional.of(game));
//        when(pieceRepository.findById(anyLong())).thenReturn(Optional.of(piece));
//        when(turnRepository.findById(anyLong())).thenReturn(Optional.of(turn));
//
//        AutoThrowApplyRequest request = new AutoThrowApplyRequest();
//        request.setPlayerId(1L);
//        //request.setGameId(1L);
//        request.setPieceId(1L);
//        request.setTurnId(1L);
//        request.setResult(YutResult.YUT);
//
//        YutThrowResponse response = gameService.applyRandomYutResult(1L, request);
//
//        assertThat(response.getTurnId()).isEqualTo(1L);
//        assertThat(response.getResult()).isEqualTo("YUT");
//    }
//
//    @Test
//    void throwYutManual_shouldSaveTurnAndAction() {
//        ManualThrowRequest request = new ManualThrowRequest(1L, 2L, YutResult.MO);
//        Game game = new Game();
//        game.setGameId(100L);
//        Player player = new Player();
//        player.setPlayerId(1L);
//        Piece piece = new Piece();
//        piece.setPieceId(2L);
//
//        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
//        when(gameRepository.findById(100L)).thenReturn(Optional.of(game));
//        when(pieceRepository.findById(2L)).thenReturn(Optional.of(piece));
//        when(turnRepository.save(any())).thenAnswer(i -> {
//            Turn t = i.getArgument(0);
//            t.setTurnId(10L);
//            return t;
//        });
//
//        gameService.throwYutManual(100L, request);
//
//        verify(turnRepository).save(any());
//        verify(turnActionRepository).save(any());
//    }
//
//    @Test
//    void restartGame_shouldResetGameStateAndDeletePieces() {
//        Game game = new Game();
//        game.setGameId(1L);
//        Player winner = new Player();
//        winner.setPlayerId(2L);
//
//        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
//        when(playerRepository.findById(2L)).thenReturn(Optional.of(winner));
//
//        gameService.restartGame(1L, 2L);
//
//        assertThat(game.getState()).isEqualTo(GameState.FINISHED);
//        assertThat(game.getWinnerPlayer()).isEqualTo(winner);
//        verify(pieceRepository).deleteByPlayerGame(1L);
//        verify(gameRepository).save(game);
//    }
//
//    @Test
//    void getTurnInfo_shouldReturnTurnDetails() {
//        Game game = new Game();
//        Turn turn = new Turn();
//        turn.setTurnId(1L);
//        Player player = new Player();
//        player.setPlayerId(3L);
//        player.setName("Bob");
//        turn.setPlayer(player);
//        TurnAction action = new TurnAction();
//        action.setMoveOrder(1);
//        action.setResult(TurnAction.ResultType.YUT);
//        action.setUsed(true);
//        Piece piece = new Piece();
//        piece.setPieceId(10L);
//        action.setChosenPiece(piece);
//        turn.setActions(List.of(action));
//
//        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
//        when(turnRepository.findTopByGame_GameIdOrderByTurnIdDesc(1L)).thenReturn(turn);
//
//        TurnInfoResponse res = gameService.getTurnInfo(1L);
//
//        assertThat(res.getPlayerId()).isEqualTo(3L);
//        assertThat(res.getResult()).isEqualTo("YUT");
//        assertThat(res.getChosenPieceId()).isEqualTo(10L);
//    }
//}
