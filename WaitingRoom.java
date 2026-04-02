package bakerace;

import java.util.ArrayList;

public class WaitingRoom {

    private ArrayList<String> players = new ArrayList<>();

    public void addPlayer(String name) {
        if (!players.contains(name)) {
            players.add(name);
        }
    }

    public String getPlayers() {
        return String.join(",", players);
    }
}
