package com.example.test;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import kotlin.collections.SetsKt;

public class ChessView extends View {
    private final float scaleFactor = 0.9F;
    private float originX;
    private float originY;
    private float cellSize;
    private Set<Integer> imgResIDs = SetsKt.setOf(
            R.drawable.bbishop,
            R.drawable.bking,
            R.drawable.bknight,
            R.drawable.bpawn,
            R.drawable.bqueen,
            R.drawable.brook,
            R.drawable.wbishop,
            R.drawable.wking,
            R.drawable.wknight,
            R.drawable.wpawn,
            R.drawable.wqueen,
            R.drawable.wrook);
    private Map<Integer, Bitmap> bitmaps = new LinkedHashMap<>();
    private Paint paint = new Paint();

    private Bitmap movingPieceBitmap;
    private ChessPiece movingPiece;
    private int fromCol = -1;
    private int fromRow = -1;
    private float movingPieceX = -1.0F;
    private float movingPieceY = -1.0F;

    ChessDelegate chessDelegate = new ChessDelegate() {
        @Override
        public ChessPiece pieceAt(Square square) {
            return null;
        }

        @Override
        public void movePiece(Square from, Square to) {

        }
    };

    public ChessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.loadBitmaps();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int smaller = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(smaller, smaller);
    }

    @Override
    public void onDraw(Canvas canvas) {
        float chessBoardSide = Math.min(canvas.getWidth(), canvas.getHeight()) * scaleFactor;
        cellSize = chessBoardSide / 8f;
        originX = (canvas.getWidth() - chessBoardSide) / 2f;
        originY = (canvas.getHeight() - chessBoardSide) / 2f;

        drawChessboard(canvas);
        drawPieces(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null)
                return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            fromCol = (int) ((event.getX() - originX) / cellSize);
            fromRow = 7 - (int) ((event.getY() - originY) / cellSize);
            if (chessDelegate != null) {
                ChessPiece chessPiece = chessDelegate.pieceAt(new Square(fromCol, fromRow));
                if (chessPiece != null) {
                    movingPieceBitmap = bitmaps.get(chessPiece.getResID());
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            int col = (int) ((event.getX() - originX) / cellSize);
            int row = 7 - (int)((event.getY() - originY) / cellSize);
            if(fromCol != col || fromRow != row) {
                if (chessDelegate != null) {
                    chessDelegate.movePiece(new Square(fromCol, fromRow), new Square(col, row));
                }
            }
            movingPiece = null;
            movingPieceBitmap = null;
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            movingPieceX = event.getX();
            movingPieceY = event.getY();
            invalidate();
        }

        return true;
    }

    private void drawPieces(Canvas canvas) {
        for (int row = 0; row < 8; row++) { 
            for (int col = 0; col < 8; col++) {
                if (chessDelegate != null) {
                    ChessPiece piece = chessDelegate.pieceAt(new Square(col, row));
                    if (piece != null && piece != movingPiece) {
                        drawPiecesAt(canvas, col, row, piece.getResID());
                    }
                }
            }
        }

        if (movingPieceBitmap != null) {
            canvas.drawBitmap(movingPieceBitmap, null, new RectF(movingPieceX - cellSize / 2,
                    movingPieceY - cellSize / 2, movingPieceX + cellSize / 2,
                    movingPieceY + cellSize / 2), paint);
        }
    }

    private void drawPiecesAt(Canvas canvas, int col, int row, int resID) {
        Bitmap bitmap = bitmaps.get(resID);
        canvas.drawBitmap(bitmap, null, new RectF(originX + col * cellSize,
                originY + (7 - row) * cellSize, originX + (col + 1) * cellSize,
                originY + ((7 - row) + 1) * cellSize), paint);
    }

    private void loadBitmaps() {
        for (Integer integer : imgResIDs) {
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), integer);
            bitmaps.put(integer, bitmap);
        }
    }

    private void drawChessboard(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    paint.setColor(getContext().getResources().getColor(R.color.colorBoardDark));
                }
                else paint.setColor(getContext().getResources().getColor(R.color.colorBoardLight));
                canvas.drawRect(originX + i * cellSize, originY + j * cellSize,
                        originX + (i + 1) * cellSize, originY + (j + 1) * cellSize, paint);
            }
        }
    }
}
