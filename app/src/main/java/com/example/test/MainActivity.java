package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements ChessDelegate {
    // 127.0.0.1
    private String socketHost = "change to ip you want to connect";
    private int socketPort = 50000;
    private static final String TAG = "MainActivity";
    private ChessView chessView;
    private Button resetButton;
    private Button listenButton;
    private Button connectButton;
    private PrintWriter printWriter = null;
    private ServerSocket serverSocket = null;
    private ChessGame chessGame = new ChessGame();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chessView = findViewById(R.id.chess_view);
        chessView.chessDelegate = this;

        resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chessGame.reset();
                chessView.invalidate();
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        // ignored
                    }
                }
                listenButton.setEnabled(true);
            }
        });

        listenButton = findViewById(R.id.listen_button);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenButton.setEnabled(false);
                Log.d(TAG, "socket server listen on port " + socketPort);
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            serverSocket = new ServerSocket(socketPort);
                            Socket socket = serverSocket.accept();
                            Log.d(TAG, "connected from " + socket.getInetAddress());
                            receiveMove(socket);
                        } catch (IOException e) {
                            // ignored
                        }
                    }
                });
            }
        });

        connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "socket client connecting to address:port...");
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        Socket socket = null;
                        try {
                            socket = new Socket(socketHost, socketPort);
                            receiveMove(socket);
                        } catch (IOException e) {
                            // ignored
                        }
                    }
                });
            }
        });
    }

    private void receiveMove(Socket socket) {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            while (scanner.hasNextLine()) {
                List<Integer> move = new ArrayList<>();
                for (String str : scanner.nextLine().split(",")) {
                    move.add(Integer.parseInt(str));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chessGame.movePiece(new Square(move.get(0), move.get(1)),
                                            new Square(move.get(2), move.get(3)));
                        chessView.invalidate();
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChessPiece pieceAt(Square square) {
        return chessGame.pieceAt(square);
    }

    @Override
    public void movePiece(Square from, Square to) {
        Log.d(TAG, from.getCol() + "," + from.getRow() + "," + to.getCol() + "," + to.getRow());
        if(from.getCol() == to.getCol() && from.getRow() == to.getRow())
            return;
        chessGame.movePiece(from, to);
        ChessView chessView = findViewById(R.id.chess_view);
        chessView.invalidate();

        if(printWriter != null) {
            String moveStr = from.getCol() + "," + from.getRow() + "," + to.getCol() + "," + to.getRow();
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    printWriter.println(moveStr);
                }
            });
        }
    }
}
