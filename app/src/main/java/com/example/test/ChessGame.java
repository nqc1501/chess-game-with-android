package com.example.test;

import java.util.LinkedHashSet;
import java.util.Set;

public class ChessGame {
    private Set<ChessPiece> piecesBox = new LinkedHashSet<>();
    private boolean move = true;

    public ChessGame() {
        reset();
    }

    public void clear() {
        move = true;
        piecesBox.removeAll(piecesBox);
    }

    public void addPiece(ChessPiece piece) {
        piecesBox.add(piece);
    }

    private boolean canKnightMove(Square from, Square to) {
        if ((Math.abs(from.getCol() - to.getCol()) == 2 && Math.abs(from.getRow() - to.getRow()) == 1) ||
                (Math.abs(from.getCol() - to.getCol()) == 1 && Math.abs(from.getRow() - to.getRow()) == 2)) {
            return true;
        }
        return false;
    }

    private boolean canRookMove(Square from, Square to) {
        if (from.getCol() == to.getCol() && isClearVerticallyBetween(from, to) ||
                from.getRow() == to.getRow() && isClearHorizontallyBetween(from, to)) {
            return true;
        }
        return false;
    }

    private boolean isClearVerticallyBetween(Square from, Square to) {
        if (from.getCol() != to.getCol())
            return false;
        int gap = Math.abs(from.getRow() - to.getRow()) - 1;
        if (gap == 0)
            return true;
        for (int i = 1; i <= gap; i++) {
            int nextRow;
            if (to.getRow() > from.getRow()) {
                nextRow = from.getRow() + i;
            } else nextRow = from.getRow() - i;
            if (pieceAt(new Square(from.getCol(), nextRow)) != null)
                return false;
        }
        return true;
    }

    private boolean isClearHorizontallyBetween(Square from, Square to) {
        if(from.getRow() != to.getRow())
            return false;
        int gap = Math.abs(from.getCol() - to.getCol()) - 1;
        if (gap == 0)
            return true;
        for (int i = 1; i <= gap; i++) {
            int nextCol;
            if (to.getCol() > from.getCol()) {
                nextCol = from.getCol() + i;
            } else nextCol = from.getCol() - i;
            if (pieceAt(new Square(nextCol, from.getRow())) != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isClearDiagonally(Square from, Square to) {
        if(Math.abs(from.getCol() - to.getCol()) != Math.abs(from.getRow() - to.getRow()))
            return false;
        int gap = Math.abs(from.getCol() - to.getCol()) - 1;
        for (int i = 1; i <= gap; i++) {
            int nextCol, nextRow;
            if (to.getCol() > from.getCol()) {
                nextCol = from.getCol() + i;
            } else nextCol = from.getCol() - i;

            if (to.getRow() > from.getRow()) {
                nextRow = from.getRow() + i;
            } else nextRow = from.getRow() - i;

            if(pieceAt(new Square(nextCol, nextRow)) != null)
                return false;
        }

        return true;
    }

    private boolean canBishopMove(Square from, Square to) {
        if (Math.abs(from.getCol() - to.getCol()) == Math.abs(from.getRow() - to.getRow()))
            return isClearDiagonally(from, to);
        return false;
    }

    private boolean canQueenMove(Square from, Square to) {
        return canRookMove(from, to) || canBishopMove(from, to);
    }

    private boolean canKingMove(Square from, Square to) {
        if (canQueenMove(from, to)) {
            int deltaCol = Math.abs(from.getCol() - to.getCol());
            int deltaRow = Math.abs(from.getRow() - to.getRow());
            if (deltaCol == 1 && deltaRow == 1 || deltaCol + deltaRow == 1)
                return true;
        }
        return false;
    }

    private boolean canWhitePawnMove(Square from, Square to) {
        if (from.getCol() == to.getCol()) {
            if (from.getRow() == 1)
                return to.getRow() == 2 || to.getRow() == 3;
            if (to.getRow() - from.getRow() == 1) {
                ChessPiece piece = pieceAt(to);
                if (piece == null)
                    return true;
            }
            return false;
        } else if (Math.abs(from.getCol() - to.getCol()) == 1 && (to.getRow() - from.getRow() == 1)) {
            ChessPiece piece = pieceAt(to);
            if (piece != null && piece.getPlayer() == ChessPlayer.BLACK)
                return true;
        }
        return false;
    }

    private boolean canBlackPawnMove(Square from, Square to) {
        if (from.getCol() == to.getCol()) {
            if (from.getRow() == 6)
                return to.getRow() == 5 || to.getRow() == 4;
            if (from.getRow() - to.getRow() == 1) {
                ChessPiece piece = pieceAt(to);
                if (piece == null)
                    return true;
            }
            return false;
        } else if (Math.abs(from.getCol() - to.getCol()) == 1 && (from.getRow() - to.getRow() == 1)) {
            ChessPiece piece = pieceAt(to);
            if (piece != null && piece.getPlayer() == ChessPlayer.WHITE)
                return true;
        }
        return false;
    }

    public boolean canMove(Square from, Square to) {
        ChessPiece movingPiece = pieceAt(from);
        if (movingPiece == null) {
            return false;
        }

        if (movingPiece.getPlayer() == ChessPlayer.WHITE && !move)
            return false;

        if(movingPiece.getPlayer() == ChessPlayer.BLACK && move)
            return false;

        if (from.getCol() == to.getCol() && from.getRow() == to.getRow())
            return false;

        if (movingPiece.getChessman() == Chessman.KNIGHT) {
            return canKnightMove(from, to);
        } else if (movingPiece.getChessman() == Chessman.ROOK) {
            return canRookMove(from, to);
        } else if (movingPiece.getChessman() == Chessman.BISHOP) {
            return canBishopMove(from, to);
        } else if (movingPiece.getChessman() == Chessman.QUEEN) {
            return canQueenMove(from, to);
        } else if (movingPiece.getChessman() == Chessman.KING) {
            return  canKingMove(from, to);
        } else {
            if (movingPiece.getPlayer() == ChessPlayer.WHITE)
                return canWhitePawnMove(from, to);
            else return canBlackPawnMove(from, to);
        }
    }

    public void movePiece(Square from, Square to) {
        if (canMove(from, to)) {
            ChessPiece chessPiece = pieceAt(from);
            if (chessPiece.getPlayer() == ChessPlayer.WHITE)
                move = false;
            else move = true;
            movePiece(from.getCol(), from.getRow(), to.getCol(), to.getRow());
        }
    }

    private void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        if (fromCol == toCol && fromRow == toRow)
            return;
        
        ChessPiece movingPiece = pieceAt(fromCol, fromRow);
        if (movingPiece == null)
            return;

        ChessPiece chessPiece = pieceAt(toCol, toRow);
        if (chessPiece != null) {
            if (chessPiece.getPlayer() == movingPiece.getPlayer()) {
                return;
            }
            piecesBox.remove(chessPiece);
        }

        piecesBox.remove(movingPiece);
        addPiece(new ChessPiece(
                toCol,
                toRow,
                movingPiece.getPlayer(),
                movingPiece.getChessman(),
                movingPiece.getResID()));
    }

    public void reset() {
        clear();
        for(int i = 0; i < 2; i++) {
            // rook
            addPiece(new ChessPiece(i * 7, 0, ChessPlayer.WHITE, Chessman.ROOK, R.drawable.wrook));
            addPiece(new ChessPiece(i * 7, 7, ChessPlayer.BLACK, Chessman.ROOK, R.drawable.brook));

            // knight
            addPiece(new ChessPiece(1 + i * 5, 0, ChessPlayer.WHITE, Chessman.KNIGHT, R.drawable.wknight));
            addPiece(new ChessPiece(1 + i * 5, 7, ChessPlayer.BLACK, Chessman.KNIGHT, R.drawable.bknight));

            // bishop
            addPiece(new ChessPiece(2 + i * 3, 0, ChessPlayer.WHITE, Chessman.BISHOP, R.drawable.wbishop));
            addPiece(new ChessPiece(2 + i * 3, 7, ChessPlayer.BLACK, Chessman.BISHOP, R.drawable.bbishop));
        }

        // pawn
        for(int i = 0; i < 8; i++) {
            addPiece(new ChessPiece(i, 1, ChessPlayer.WHITE, Chessman.PAWN, R.drawable.wpawn));
            addPiece(new ChessPiece(i, 6, ChessPlayer.BLACK, Chessman.PAWN, R.drawable.bpawn));
        }

        // queen
        addPiece(new ChessPiece(3, 0, ChessPlayer.WHITE, Chessman.QUEEN, R.drawable.wqueen));
        addPiece(new ChessPiece(3, 7, ChessPlayer.BLACK, Chessman.QUEEN, R.drawable.bqueen));

        // king
        addPiece(new ChessPiece(4, 0, ChessPlayer.WHITE, Chessman.KING, R.drawable.wking));
        addPiece(new ChessPiece(4, 7, ChessPlayer.BLACK, Chessman.KING, R.drawable.bking));
    }

    public ChessPiece pieceAt(Square square) {
        return pieceAt(square.getCol(), square.getRow());
    }

    private ChessPiece pieceAt(int col, int row) {
        for (ChessPiece piece: piecesBox) {
            if (col == piece.getCol() && row == piece.getRow()) {
                return piece;
            }
        }
        return null;
    }

    public String pgnBoard() {
        String desc = " \n";
        desc += " a b c d e f g h\n";
        for (int row = 7; row >= 0; row--) {
            desc += (row + 1);
            desc += boardRow(row);
            desc += " " + (row + 1);
            desc += "\n";
        }
        desc += " a b c d e f g h";
        return desc;
    }

    @Override
    public String toString() {
        String desc = " \n";
        for(int row = 7; row >= 0; row--) {
            desc += (row + 1);
            desc += boardRow(row);
        }
        desc += " a b c d e f g h";

        return desc;
    }

    private String boardRow(int row) {
        String desc = "";
        for (int col = 0; col < 8; col++) {
            desc += " ";
            ChessPiece piece = pieceAt(col, row);
            if (piece == null) {
                desc += ".";
            } else {
                Chessman rank = piece.getChessman();
                ChessPlayer player = piece.getPlayer();
                if (rank == Chessman.KING) {
                    if (player == ChessPlayer.WHITE) {
                        desc += "k";
                    } else {
                        desc += "K";
                    }
                } else if (rank == Chessman.QUEEN) {
                    if (player == ChessPlayer.WHITE) {
                        desc += "q";
                    } else {
                        desc += "Q";
                    }
                } else if (rank == Chessman.BISHOP) {
                    if (player == ChessPlayer.WHITE) {
                        desc += "b";
                    } else {
                        desc += "B";
                    }
                } else if (rank == Chessman.KNIGHT) {
                    if (player == ChessPlayer.WHITE) {
                        desc += "n";
                    } else {
                        desc += "N";
                    }
                } else if (rank == Chessman.ROOK) {
                    if (player == ChessPlayer.WHITE) {
                        desc += "r";
                    } else {
                        desc += "R";
                    }
                } else {
                    if (player == ChessPlayer.WHITE) {
                        desc += "p";
                    } else {
                        desc += "P";
                    }
                }
            }
        }
        return desc;
    }
}
