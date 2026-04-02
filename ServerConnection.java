package bakerace;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection implements Runnable {

    private Socket server;
    private BufferedReader in;
    private BakeRaceClientFrame frame;

    public ServerConnection(Socket socket, BakeRaceClientFrame frame) {
        this.server = socket;
        this.frame = frame;

        try {
            in = new BufferedReader(
                    new InputStreamReader(server.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String response;

            while ((response = in.readLine()) != null) {

                if (response.startsWith("CONNECTED")) {
                    frame.updateConnected(response);
                } else if (response.startsWith("WAITING")) {
                    frame.updateWaiting(response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
