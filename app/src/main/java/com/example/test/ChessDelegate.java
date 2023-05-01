package com.example.test;

public interface ChessDelegate {
    public ChessPiece pieceAt(Square square);
    public void movePiece(Square from, Square to);
}
