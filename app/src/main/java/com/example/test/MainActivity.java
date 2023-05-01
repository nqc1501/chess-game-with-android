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
    // IP wifi laptop: 192.168.41.105
    // IP wifi phone:192.168.41.101
    // 127.0.0.1
    private String socketHost = "10.0.2.2";
    private int socketPort = 50000;
    private int socketGuestPort = 50001;
    private static final String TAG = "MainActivity";
    private ChessView chessView;
    private Button resetButton;
    private Button listenButton;
    private Button connectButton;
    private PrintWriter printWriter = null;
    private ServerSocket serverSocket = null;
    private ChessGame chessGame = new ChessGame();
    private boolean isEmulator = Build.FINGERPRINT.contains("generic");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, String.valueOf(isEmulator));

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
                        throw new RuntimeException(e);
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
                int port;
                if (isEmulator) {
                    port = socketGuestPort;
                } else {
                    port = socketPort;
                }
                Toast.makeText(getBaseContext(), "listening on " + port, Toast.LENGTH_SHORT);
                Log.d(TAG, "socket server listen on port " + port);
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            serverSocket = new ServerSocket(port);
                            Socket socket = serverSocket.accept();
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
                        } catch (ConnectException e) {
                            Toast.makeText(getBaseContext(), "connection failed", Toast.LENGTH_SHORT);
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
            printWriter = new PrintWriter(socket.getOutputStream());
            while (scanner.hasNextLine()) {
                List<Integer> move = new ArrayList<>();
                for (String str : scanner.nextLine().split(",")) {
                    move.add(Integer.parseInt(str));
                }
                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        chessGame.movePiece(new Square(move.get(0), move.get(1)), new Square(move.get(3), move.get(4)));
                        chessView.invalidate();
                    }
                }));
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