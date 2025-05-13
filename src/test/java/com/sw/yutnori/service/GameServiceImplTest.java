//package com.sw.yutnori.service;
//
//import com.sw.yutnori.backend.dto.game.request.*;
//import com.sw.yutnori.backend.dto.game.response.*;
//import com.sw.yutnori.backend.dto.piece.response.MovablePieceResponse;
//import com.sw.yutnori.model.*;
//import com.sw.yutnori.model.enums.BoardType;
//import com.sw.yutnori.model.enums.PieceState;
//import com.sw.yutnori.model.enums.YutResult;
//import com.sw.yutnori.repository.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//class GameServiceImplTest {
//
//    @InjectMocks
//    private GameServiceImpl gameService;
//
//    @Mock private GameRepository gameRepository;
//    @Mock private PlayerRepository playerRepository;
//    @Mock private TurnRepository turnRepository;
//    @Mock private PieceRepository pieceRepository;
//    @Mock private TurnActionRepository turnActionRepository;
//    @Mock private PathNodeRepository pathNodeRepository;
//    @Mock private BoardRepository boardRepository;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createGame_shouldCreateGameAndPieces() {
//        GameCreateRequest request = new GameCreateRequest();
//        PlayerInitRequest player = new PlayerInitRequest();
//        player.setName("Alice");
//        player.setColor("RED");
//        request.setBoardType(BoardType.SQUARE);
//        request.setPlayers(List.of(player));
//        request.setNumPieces(2);
//
//        Game savedGame = new Game();
//        savedGame.setGameId(1L);
//        savedGame.setBoards(new ArrayList<>());
//
//        when(gameRepository.save(any())).thenReturn(savedGame);
//        when(boardRepository.save(any())).thenReturn(new Board());
//
//        Player mockPlayer = new Player();
//        mockPlayer.setPlayerId(10L);
//        mockPlayer.setName("Alice");
//        mockPlayer.setColor("RED");
//        mockPlayer.setGame(savedGame);
//        when(playerRepository.save(any())).thenReturn(mockPlayer);
//
//        Piece mockPiece = new Piece();
//        mockPiece.setPieceId(100L);
//        when(pieceRepository.save(any())).thenReturn(mockPiece);
//
//        GameCreateResponse response = gameService.createGame(request);
//
//        assertThat(response.getGameId()).isEqualTo(1L);
//        verify(gameRepository).save(any());
//        verify(playerRepository, atLeastOnce()).save(any());
//        verify(pieceRepository, atLeastOnce()).save(any());
//    }
//
//    @Test
//    void throwYutManual_shouldCreateTurnAndAction() {
//        ManualThrowRequest request = new ManualThrowRequest();
//        request.setPlayerId(2L);
//        request.setPieceId(3L);
//        request.setResult(YutResult.YUT);
//
//        when(playerRepository.findById(any())).thenReturn(Optional.of(new Player()));
//        when(gameRepository.findById(any())).thenReturn(Optional.of(new Game()));
//        when(pieceRepository.findById(any())).thenReturn(Optional.of(new Piece()));
//        when(turnRepository.save(any())).thenReturn(new Turn());
//
//        gameService.throwYutManual(1L, request);
//
//        verify(turnActionRepository).save(any());
//    }
//
//    @Test
//    void movePiece_shouldMoveAndSave() {
//        MovePieceRequest request = new MovePieceRequest();
//        request.setPlayerId(1L);
//        request.setChosenPieceId(1L);
//        request.setMoveOrder(1);
//        request.setResult(YutResult.MO);
//        request.setA(0);
//        request.setB(1);
//
//        Piece piece = new Piece();
//        piece.setPieceId(1L);
//        piece.setPlayer(new Player());
//        piece.getPlayer().setPlayerId(1L);
//        piece.getPlayer().setGame(new Game());
//        piece.getPlayer().getGame().setBoards(List.of(new Board()));
//
//        when(pieceRepository.findById(any())).thenReturn(Optional.of(piece));
//        when(playerRepository.findById(any())).thenReturn(Optional.of(piece.getPlayer()));
//        when(pieceRepository.findAllByAAndB(anyInt(), anyInt())).thenReturn(List.of());
//        when(turnRepository.save(any())).thenReturn(new Turn());
//
//        MovePieceResponse response = gameService.movePiece(1L, request);
//
//        assertThat(response.isReachedEndPoint()).isTrue();
//        verify(pieceRepository).save(any());
//        verify(turnActionRepository).save(any());
//    }
//
//    @Test
//    void getRandomYutResult_shouldReturnResult() {
//        Player player = new Player();
//        when(playerRepository.findById(any())).thenReturn(Optional.of(player));
//        when(gameRepository.findById(any())).thenReturn(Optional.of(new Game()));
//        when(turnRepository.save(any())).thenReturn(new Turn());
//
//        AutoThrowResponse response = gameService.getRandomYutResultForPlayer(1L, new AutoThrowRequest(1L));
//        assertThat(response.getResult()).isNotNull();
//    }
//
//    @Test
//    void applyRandomYutResult_shouldReturnYutThrowResponse() {
//        Player player = new Player();
//        Piece piece = new Piece();
//        Turn turn = new Turn();
//        Game game = new Game();
//
//        AutoThrowApplyRequest request = new AutoThrowApplyRequest();
//        request.setPlayerId(1L);
//        request.setPieceId(1L);
//        request.setTurnId(1L);
//        request.setResult(YutResult.DO);
//
//        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
//        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
//        when(pieceRepository.findById(1L)).thenReturn(Optional.of(piece));
//        when(turnRepository.findById(1L)).thenReturn(Optional.of(turn));
//
//        YutThrowResponse response = gameService.applyRandomYutResult(1L, request);
//        assertThat(response.getResult()).isEqualTo("DO");
//    }
//
//    @Test
//    void getMovablePiecesByPlayer_shouldReturnList() {
//        Player player = new Player();
//        player.setPlayerId(1L);
//        Piece piece = new Piece();
//        piece.setPieceId(2L);
//        piece.setState(PieceState.READY);
//        piece.setPlayer(player);
//
//        when(playerRepository.findById(any())).thenReturn(Optional.of(player));
//        when(pieceRepository.findByPlayer_PlayerId(any())).thenReturn(List.of(piece));
//
//        List<MovablePieceResponse> responses = gameService.getMovablePiecesByPlayer(1L);
//        assertThat(responses).hasSize(1);
//    }
//
//    @Test
//    void getGameStatus_shouldReturnStatus() {
//        Game game = new Game();
//        game.setGameId(1L);
//        game.setState(GameState.PLAYING);
//        game.setBoardType(BoardType.SQUARE);
//        game.setNumPlayers(2);
//        game.setNumPieces(4);
//        game.setBoards(List.of(new Board()));
//
//        Piece piece = new Piece();
//        piece.setPieceId(1L);
//        Player player = new Player();
//        player.setPlayerId(10L);
//        Game gameRef = new Game();
//        gameRef.setBoards(List.of(new Board()));
//        player.setGame(gameRef);
//        piece.setPlayer(player);
//        piece.setA(0);
//        piece.setB(0);
//
//        when(gameRepository.findById(any())).thenReturn(Optional.of(game));
//        when(pieceRepository.findByPlayer_PlayerId(any())).thenReturn(List.of(piece));
//        Board dummyBoard = new Board();
//        dummyBoard.setBoardId(1L);
//        PathNode dummyNode = new PathNode();
//        dummyNode.setA(0);
//        dummyNode.setB(0);
//        dummyNode.setX(100);
//        dummyNode.setY(200);
//        dummyNode.setBoard(dummyBoard);
//        when(pathNodeRepository.findByBoardAndAAndB(any(Board.class), eq(0), eq(0)))
//                .thenReturn(Optional.of(dummyNode));
//
//        GameStatusResponse status = gameService.getGameStatus(1L);
//        assertThat(status.getGameId()).isEqualTo(1L);
//    }
//
//    @Test
//    void deleteGame_shouldDeleteSuccessfully() {
//        when(gameRepository.existsById(1L)).thenReturn(true);
//        gameService.deleteGame(1L);
//        verify(gameRepository).deleteById(1L);
//    }
//
//    @Test
//    void getTurnInfo_shouldReturnLatestInfo() {
//        Game game = new Game();
//        Player player = new Player();
//        player.setPlayerId(1L);
//        player.setName("Alice");
//        Turn turn = new Turn();
//        turn.setTurnId(1L);
//        turn.setPlayer(player);
//        TurnAction action = new TurnAction();
//        action.setMoveOrder(1);
//        action.setUsed(true);
//        action.setResult(YutResult.GAE);
//        Piece piece = new Piece();
//        piece.setPieceId(123L);
//        action.setChosenPiece(piece);
//        turn.setActions(List.of(action));
//
//        when(gameRepository.findById(any())).thenReturn(Optional.of(game));
//        when(turnRepository.findTopByGame_GameIdOrderByTurnIdDesc(any())).thenReturn(turn);
//
//        TurnInfoResponse info = gameService.getTurnInfo(1L);
//        assertThat(info.getPlayerName()).isEqualTo("Alice");
//        assertThat(info.getResult()).isEqualTo("GAE");
//    }
//}
