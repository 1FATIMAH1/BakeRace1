package bakerace;

import java.util.ArrayList;

public class WaitingRoom {

    private ArrayList<String> players = new ArrayList<>();
    private boolean gameStarted = false;

    public void addPlayer(String name) {
        if (name != null && !name.isEmpty() && !players.contains(name)) {
            players.add(name);
        }
    }

    public void removePlayer(String name) {
        players.remove(name);
    }

    public String getPlayers() {
        return String.join(",", players);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
