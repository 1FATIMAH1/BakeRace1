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
    private JLabel[] playerLabels;
    private JLabel waitingTitle;

    private PrintWriter out;
    private Clip currentClip;
    
    
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
        stopCurrentSound();

        java.net.URL soundURL = getClass().getResource(path);
        if (soundURL == null) {
            System.out.println("SOUND NOT FOUND: " + path);
            return;
        }

        AudioInputStream audio = AudioSystem.getAudioInputStream(soundURL);
        currentClip = AudioSystem.getClip();
        currentClip.open(audio);
        currentClip.start();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void stopCurrentSound() {
    try {
        if (currentClip != null) {
            if (currentClip.isRunning()) {
                currentClip.stop();
            }
            currentClip.close();
            currentClip = null;
        }
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
            JOptionPane.showMessageDialog(
                    this,
                    "Could not connect to server.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JPanel createIntroPanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/logo_bg .PNG");
        panel.setLayout(null);

        JButton startButton = createImageButton("/resources/start.btn.png", 420, 300);
        startButton.setBounds(300, 350, 420, 300);
startButton.addActionListener(e -> {
    stopCurrentSound();
    cardLayout.show(mainPanel, "ABOUT");
    playSound("/resources/intro.wav");
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
nextButton.addActionListener(e -> {
    stopCurrentSound();
    cardLayout.show(mainPanel, "CONNECT");
    playSound("/resources/next-sound.wav");
});

        panel.add(rulesText);
        panel.add(nextButton);
        return panel;
    }

    private JPanel createConnectPanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/connect_bg.png");
        panel.setLayout(null);

        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameField.setHorizontalAlignment(JTextField.LEFT);
        nameField.setBounds(350, 270, 365, 55);
        nameField.setOpaque(true);
        nameField.setBackground(Color.WHITE);
        nameField.setForeground(Color.BLACK);
        nameField.setCaretColor(Color.BLACK);
        nameField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
        nameField.setMargin(new Insets(0, 70, 0, 0));

        connectedArea = new JTextArea();
        connectedArea.setEditable(false);
        connectedArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        connectedArea.setOpaque(false);
        connectedArea.setBackground(new Color(0, 0, 0, 0));
        connectedArea.setBorder(null);

JScrollPane connectedScroll = new JScrollPane(connectedArea);
connectedScroll.setBounds(720, 220, 200, 150); 
connectedScroll.setOpaque(false);
connectedScroll.getViewport().setOpaque(false);
connectedScroll.setBorder(null);
connectedScroll.setBackground(new Color(0, 0, 0, 0));

        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setFont(new Font("SansSerif", Font.BOLD, 16));
        messagesArea.setOpaque(false);
        messagesArea.setBackground(new Color(0, 0, 0, 0));
        messagesArea.setBorder(null);

        JScrollPane msgScroll = new JScrollPane(messagesArea);
        msgScroll.setBounds(0, 0, 0, 0);
        msgScroll.setOpaque(false);
        msgScroll.getViewport().setOpaque(false);
        msgScroll.setBorder(null);
        msgScroll.setBackground(new Color(0, 0, 0, 0));

        JButton connectBtn = createImageButton("/resources/connect.btn.PNG", 350, 350);
        connectBtn.setBounds(320, 260, 350, 350);

        JButton exitBtn = createImageButton("/resources/exit.png", 220, 180);
        exitBtn.setBounds(770, 480, 220, 180);

        connectBtn.addActionListener(e -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name.");
                return;
            }

            if (playerLabels != null && playerLabels.length > 0) {
                for (int i = 0; i < playerLabels.length; i++) {
                    playerLabels[i].setText("");
                }
                playerLabels[0].setText(name);
            }

            if (out != null) {
                out.println("CONNECT|" + name);
                messagesArea.setText("Connected as: " + name);
            } else {
                messagesArea.setText("Connected as: " + name);
            }

            cardLayout.show(mainPanel, "WAITING");
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
        waitingArea.setFont(new Font("SansSerif", Font.BOLD, 24));
        waitingArea.setForeground(new Color(255, 235, 200));
        waitingArea.setOpaque(false);
        waitingArea.setBackground(new Color(0, 0, 0, 0));
        waitingArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        waitingArea.setLineWrap(true);
        waitingArea.setWrapStyleWord(true);
        waitingArea.setText("Connected players:\n");

        waitingTitle = new JLabel("Connected players:");
        waitingTitle.setBounds(365, 225, 330, 40);
        waitingTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        waitingTitle.setForeground(new Color(255, 235, 200));

        playerLabels = new JLabel[5];
        int startX = 350;   
        int startY = 280;   
        int gap = 54;       

        for (int i = 0; i < playerLabels.length; i++) {
            playerLabels[i] = new JLabel("");
            playerLabels[i].setBounds(startX, startY + (i * gap), 250, 30);
            playerLabels[i].setFont(new Font("SansSerif", Font.BOLD, 22));
            playerLabels[i].setForeground(new Color(255, 235, 200));
            panel.add(playerLabels[i]);
        }

        JButton playBtn = createImageButton("/resources/Play.btn.png", 350, 270);
        playBtn.setBounds(310, 380, 350, 270);

        JButton exitBtn = createImageButton("/resources/exit.png", 220, 180);
        exitBtn.setBounds(770, 480, 220, 180);

        playBtn.addActionListener(e -> {
            if (out != null) {
                out.println("PLAY");
            }
        });

        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(waitingTitle);
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
        String text = data
                .replace("WAITING_LIST|", "")
                .replace("WAITING|", "")
                .trim();

        String[] names;
        if (text.isEmpty()) {
            names = new String[0];
        } else {
            names = text.split(",");
        }

        if (playerLabels != null) {
            for (int i = 0; i < playerLabels.length; i++) {
                playerLabels[i].setText("");
            }

            for (int i = 0; i < names.length && i < playerLabels.length; i++) {
                playerLabels[i].setText(names[i].trim());
            }
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
