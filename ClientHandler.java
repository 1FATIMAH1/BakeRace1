package bakerace;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientHandler implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private WaitingRoom waitingRoom;
    private String playerName;

    private static final int MAX_PLAYERS = 4;
    private static boolean timerStarted = false;
    private static int currentRound = 1;
    private static boolean roundAnswered = false;
    private static HashMap<String, Integer> scores = new HashMap<>();

    public ClientHandler(Socket socket,
            ArrayList<ClientHandler> clients,
            WaitingRoom waitingRoom) throws IOException {

        this.client = socket;
        this.clients = clients;
        this.waitingRoom = waitingRoom;

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            String request;

            while ((request = in.readLine()) != null) {

                if (request.startsWith("CONNECT|")) {
                    playerName = request.split("\\|")[1];
                    broadcastConnected();

                } else if (request.equals("PLAY")) {
                    waitingRoom.addPlayer(playerName);
                    broadcastWaiting();

                    if (waitingRoom.getPlayerCount() >= MAX_PLAYERS && !waitingRoom.isGameStarted()) {
                        startGameRoundOne();
                    } else {
                        startThirtySecondTimer();
                    }

                } else if (request.startsWith("ANSWER|")) {
                    checkAnswer(request);

                } else if (request.equals("LEAVE")) {
                    waitingRoom.removePlayer(playerName);
                    broadcastWaiting();
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }

    private void checkAnswer(String request) {
        if (currentRound == 1) {
            checkRoundOneAnswer(request);
        } else if (currentRound == 2) {
            checkRoundTwoAnswer(request);
        }
    }

    private void checkRoundOneAnswer(String request) {
        String answer = request.replace("ANSWER|", "").trim();

        if (answer.equalsIgnoreCase("flour") && !roundAnswered) {
            roundAnswered = true;
            addScore(playerName);
            broadcast("CORRECT|" + playerName);
            broadcastScores();

            currentRound = 2;

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    roundAnswered = false;
                    broadcast("ROUND2");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void checkRoundTwoAnswer(String request) {
        String answer = request.replace("ANSWER|", "").trim();

        if (answer.equalsIgnoreCase("mixer") && !roundAnswered) {
            roundAnswered = true;
            addScore(playerName);
            broadcast("CORRECT|" + playerName);
            broadcastScores();
        }
    }

    private void broadcastConnected() {
        StringBuilder names = new StringBuilder("CONNECTED|");

        for (ClientHandler c : clients) {
            if (c.playerName != null) {
                names.append(c.playerName).append(",");
            }
        }

        for (ClientHandler c : clients) {
            c.out.println(names.toString());
        }
    }

    private void broadcastWaiting() {
        String list = "WAITING|" + waitingRoom.getPlayers();

        for (ClientHandler c : clients) {
            if (c.playerName != null && waitingRoom.getPlayers().contains(c.playerName)) {
                c.out.println(list);
            }
        }
    }

    private void startThirtySecondTimer() {
        if (timerStarted || waitingRoom.isGameStarted()) {
            return;
        }

        timerStarted = true;

        new Thread(() -> {
            try {
                Thread.sleep(30000);

                if (waitingRoom.getPlayerCount() > 1 && !waitingRoom.isGameStarted()) {
                    startGameRoundOne();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startGameRoundOne() {
        if (waitingRoom.isGameStarted()) {
            return;
        }

        waitingRoom.setGameStarted(true);

        currentRound = 1;

        roundAnswered = false;

        scores.clear();

        for (ClientHandler c : clients) {
            if (c.playerName != null && waitingRoom.getPlayers().contains(c.playerName)) {
                c.out.println("GAME_STARTED");
                c.out.println("ROUND1");
            }
        }
    }

    private void broadcast(String message) {
        for (ClientHandler c : clients) {
            if (c.playerName != null) {
                c.out.println(message);
            }
        }

    }

    private void addScore(String name) {
        int newScore = scores.getOrDefault(name, 0) + 1;
        scores.put(name, newScore);
    }

    private void broadcastScores() {
        StringBuilder scoreMessage = new StringBuilder("SCORES|");

        for (String name : scores.keySet()) {
            scoreMessage.append(name).append(":").append(scores.get(name)).append(",");
        }

        for (ClientHandler c : clients) {
            if (c.playerName != null && waitingRoom.getPlayers().contains(c.playerName)) {
                c.out.println(scoreMessage.toString());
            }
        }
    }
}
