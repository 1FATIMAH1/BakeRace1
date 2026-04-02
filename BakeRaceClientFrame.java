package bakerace;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class BakeRaceClientFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTextField nameField;
    private JTextArea connectedArea;
    private JTextArea waitingArea;
    private JTextArea messagesArea;

    private PrintWriter out;

    public BakeRaceClientFrame() {
        setTitle("BakeRace");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createIntroPanel(), "INTRO");
        mainPanel.add(createAboutPanel(), "ABOUT");
        mainPanel.add(createConnectPanel(), "CONNECT");
        mainPanel.add(createWaitingPanel(), "WAITING");

        add(mainPanel);

        connectToServer();

        cardLayout.show(mainPanel, "INTRO");

        playSound("/resources/hello.wav");
    }

    private void playSound(String path) {
        try {
            java.net.URL soundURL = getClass().getResource(path);
            if (soundURL == null) {
                System.out.println("SOUND NOT FOUND: " + path);
                return;
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 9090);
            out = new PrintWriter(socket.getOutputStream(), true);

            ServerConnection sc = new ServerConnection(socket, this);
            new Thread(sc).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not connect to server.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createIntroPanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/logo_bg .PNG");
        panel.setLayout(null);

        JButton startButton = createImageButton("/resources/start.btn.png", 420, 300);
        startButton.setBounds(300, 350, 420, 300);
        startButton.addActionListener(e -> {
            playSound("/resources/intro.wav");
            cardLayout.show(mainPanel, "ABOUT");
        });

        panel.add(startButton);
        return panel;
    }

    private JPanel createAboutPanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/play_bg .png");
        panel.setLayout(null);

        JTextArea rulesText = new JTextArea();
        rulesText.setText("Listen up, Bakers! 🔥🍰\n\n"
                + "Each round gives you a clue.\n"
                + "Type fast before time runs out ⏱️\n"
                + "The fastest correct answer earns a point.\n"
                + "Highest score wins! 🏆🔥");

        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setOpaque(false);
        rulesText.setBackground(null);
        rulesText.setForeground(new Color(80, 40, 20));
        rulesText.setFont(new Font("SansSerif", Font.BOLD, 22));
        rulesText.setBounds(270, 240, 460, 170);

        JButton nextButton = createImageButton("/resources/next.png", 350, 450);
        nextButton.setBounds(320, 300, 350, 450);
        nextButton.addActionListener(e -> cardLayout.show(mainPanel, "CONNECT"));

        panel.add(rulesText);
        panel.add(nextButton);

        return panel;
    }

    private JPanel createConnectPanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/connect_bg.PNG");
        panel.setLayout(null);

        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.BOLD, 22));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBounds(320, 215, 360, 45);

        connectedArea = new JTextArea();
        connectedArea.setEditable(false);
        connectedArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        connectedArea.setOpaque(true);
        connectedArea.setBackground(new Color(255, 255, 255, 210));

        JScrollPane connectedScroll = new JScrollPane(connectedArea);
        connectedScroll.setBounds(300, 320, 400, 170);

        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setFont(new Font("SansSerif", Font.BOLD, 16));
        messagesArea.setOpaque(true);
        messagesArea.setBackground(new Color(255, 255, 255, 210));

        JScrollPane msgScroll = new JScrollPane(messagesArea);
        msgScroll.setBounds(730, 130, 220, 180);

        JButton connectBtn = createImageButton("/resources/connect.btn.PNG", 220, 80);
        connectBtn.setBounds(300, 520, 220, 80);

        JButton exitBtn = createImageButton("/resources/exit.png", 220, 80);
        exitBtn.setBounds(560, 520, 220, 80);

        connectBtn.addActionListener(e -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name.");
                return;
            }

            if (out != null) {
                out.println("CONNECT|" + name);
                messagesArea.setText("Connected as: " + name);
                cardLayout.show(mainPanel, "WAITING");
            }
        });

        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(nameField);
        panel.add(connectedScroll);
        panel.add(msgScroll);
        panel.add(connectBtn);
        panel.add(exitBtn);

        return panel;
    }

    private JPanel createWaitingPanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/waitingRoom.PNG");
        panel.setLayout(null);

        waitingArea = new JTextArea();
        waitingArea.setEditable(false);
        waitingArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        waitingArea.setOpaque(true);
        waitingArea.setBackground(new Color(255, 255, 255, 210));

        JScrollPane waitingScroll = new JScrollPane(waitingArea);
        waitingScroll.setBounds(290, 170, 420, 220);

        JButton playBtn = createImageButton("/resources/Play.btn.png", 220, 80);
        playBtn.setBounds(300, 500, 220, 80);

        JButton exitBtn = createImageButton("/resources/exit.png", 220, 80);
        exitBtn.setBounds(560, 500, 220, 80);

        playBtn.addActionListener(e -> {
            if (out != null) {
                out.println("PLAY");
            }
        });

        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(waitingScroll);
        panel.add(playBtn);
        panel.add(exitBtn);

        return panel;
    }

    public void updateConnected(String data) {
        String text = data.replace("CONNECTED|", "").replace(",", "\n");
        if (connectedArea != null) {
            connectedArea.setText(text);
        }
    }

    public void updateWaiting(String data) {
        String text = data.replace("WAITING|", "").replace(",", "\n");
        if (waitingArea != null) {
            waitingArea.setText(text);
        }
    }

    private JButton createImageButton(String path, int width, int height) {
        ImageIcon icon = loadIcon(path, width, height);
        JButton button;

        if (icon != null) {
            button = new JButton(icon);
        } else {
            button = new JButton("Button");
        }

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        return button;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL == null) {
                System.out.println("IMAGE NOT FOUND: " + path);
                return null;
            }

            ImageIcon originalIcon = new ImageIcon(imgURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                    width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BakeRaceClientFrame().setVisible(true));
    }
}
