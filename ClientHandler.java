package bakerace;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private WaitingRoom waitingRoom;
    private String playerName;

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

                if (request.startsWith("CONNECT")) {
                    playerName = request.split("\\|")[1];
                    broadcastConnected();
                }

                else if (request.equals("PLAY")) {
                    waitingRoom.addPlayer(playerName);
                    broadcastWaiting();
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }

    private void broadcastConnected() {
        StringBuilder names = new StringBuilder("CONNECTED|");

        for (ClientHandler c : clients) {
            if (c.playerName != null)
                names.append(c.playerName).append(",");
        }

        for (ClientHandler c : clients) {
            c.out.println(names.toString());
        }
    }

    private void broadcastWaiting() {
        String list = "WAITING|" + waitingRoom.getPlayers();

        for (ClientHandler c : clients) {
            c.out.println(list);
        }
    }
}
