package com.example.test;

public final class ChessPiece {
    private int col;
    private int row;
    private ChessPlayer player;
    private Chessman chessman;
    private int resID;

    public ChessPiece(int col, int row, ChessPlayer player, Chessman chessman, int resID) {
        this.col = col;
        this.row = row;
        this.player = player;
        this.chessman = chessman;
        this.resID = resID;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public ChessPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ChessPlayer player) {
        this.player = player;
    }

    public Chessman getChessman() {
        return chessman;
    }

    public void setChessman(Chessman chessman) {
        this.chessman = chessman;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }
}
