package bakerace;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BakeRaceServer {

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static WaitingRoom waitingRoom = new WaitingRoom();

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Server started...");

        while (true) {
            Socket client = serverSocket.accept();
            System.out.println("New client connected");

            ClientHandler clientThread =
                    new ClientHandler(client, clients, waitingRoom);

            clients.add(clientThread);
            new Thread(clientThread).start();
        }
    }
}
