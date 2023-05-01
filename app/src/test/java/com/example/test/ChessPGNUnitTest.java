package com.example.test;

import org.junit.Test;

public class ChessPGNUnitTest {
    @Test
    public void pgn_isCorrect() {
        ChessGame chessGame = new ChessGame();
        System.out.println(chessGame.pgnBoard());
    }
}
