package bakerace;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class BakeRaceClientFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTextField nameField;
    private JTextArea messagesArea;

    private JLabel[] playerLabels;
    private JLabel waitingTitle;

    private JPanel connectedNamesPanel;

    private JTextPane questionArea;
    private JLabel roundLabel;
    private JLabel timerLabel;
    private JTextField answerField;
    private Timer roundTimer;
    private int timeLeft = 15;

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
        mainPanel.add(createConnectPanel(), "CONNECT");
        mainPanel.add(createConnectedListPanel(), "CONNECTED_LIST");
        mainPanel.add(createWaitingPanel(), "WAITING");
        mainPanel.add(createGamePanel(), "GAME");

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
            cardLayout.show(mainPanel, "CONNECT");
            playSound("/resources/intro.wav");
        });

        panel.add(startButton);
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

        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setFont(new Font("SansSerif", Font.BOLD, 16));
        messagesArea.setOpaque(false);
        messagesArea.setBackground(new Color(0, 0, 0, 0));
        messagesArea.setBorder(null);

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

            if (out != null) {
                out.println("CONNECT|" + name);
                messagesArea.setText("Connected as: " + name);
            }

            cardLayout.show(mainPanel, "CONNECTED_LIST");
        });

        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(nameField);
        panel.add(connectBtn);
        panel.add(exitBtn);

        return panel;
    }

    private JPanel createConnectedListPanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/connectedList.png");
        panel.setLayout(null);

        connectedNamesPanel = new JPanel();
        connectedNamesPanel.setLayout(new BoxLayout(connectedNamesPanel, BoxLayout.Y_AXIS));
        connectedNamesPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(connectedNamesPanel);
        scrollPane.setBounds(310, 230, 420, 270);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(0, 0, 0, 0));

        JButton playBtn = createImageButton("/resources/Play.btn.png", 350, 270);
        playBtn.setBounds(310, 380, 350, 270);

        JButton exitBtn = createImageButton("/resources/exit.png", 220, 180);
        exitBtn.setBounds(770, 480, 220, 180);

        playBtn.addActionListener(e -> {
            if (out != null) {
                out.println("PLAY");
            }
            cardLayout.show(mainPanel, "WAITING");
        });

        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(scrollPane);
        panel.add(playBtn);
        panel.add(exitBtn);

        return panel;
    }

    private JPanel createWaitingPanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/waitingRoom.PNG");
        panel.setLayout(null);

        waitingTitle = new JLabel("Waiting Bakers:");
        waitingTitle.setBounds(365, 225, 330, 40);
        waitingTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        waitingTitle.setForeground(Color.WHITE);

        playerLabels = new JLabel[5];
        int startX = 350;
        int startY = 280;
        int gap = 54;

        for (int i = 0; i < playerLabels.length; i++) {
            playerLabels[i] = new JLabel("");
            playerLabels[i].setBounds(startX, startY + (i * gap), 250, 30);
            playerLabels[i].setFont(new Font("SansSerif", Font.BOLD, 22));
            playerLabels[i].setForeground(Color.WHITE);
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

    private JPanel createGamePanel() {
        BackgroundPanel panel = new BackgroundPanel("/resources/game_bg.png");
        panel.setLayout(null);

        roundLabel = new JLabel("Round 1 - Easy");
        roundLabel.setBounds(330, 140, 300, 40);
        roundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roundLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        roundLabel.setForeground(new Color(80, 40, 20));

        timerLabel = new JLabel("15");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 46));
        timerLabel.setBounds(830, 45, 200, 50);
        timerLabel.setForeground(Color.BLACK);

        questionArea = new JTextPane();
        questionArea.setText("");
        questionArea.setEditable(false);
        questionArea.setOpaque(false);
        questionArea.setFont(new Font("SansSerif", Font.BOLD, 22));
        questionArea.setForeground(new Color(50, 25, 10));
        questionArea.setBounds(230, 220, 520, 140);
        centerQuestionText();

        answerField = new JTextField();
        answerField.setFont(new Font("SansSerif", Font.BOLD, 22));
        answerField.setBounds(280, 365, 420, 60);

        JButton submitBtn = createImageButton("/resources/submit.png", 350, 250);
        submitBtn.setBounds(310, 380, 350, 250);

        JButton leaveBtn = createImageButton("/resources/leave.png", 260, 180);
        leaveBtn.setBounds(770, 480, 250, 180);

        submitBtn.addActionListener(e -> {
            String answer = answerField.getText().trim();

            if (!answer.isEmpty() && out != null) {
                out.println("ANSWER|" + answer);
                answerField.setText("");
            }
        });

        leaveBtn.addActionListener(e -> {
            if (out != null) {
                out.println("LEAVE");
            }
            System.exit(0);
        });

        panel.add(roundLabel);
        panel.add(timerLabel);
        panel.add(questionArea);
        panel.add(answerField);
        panel.add(submitBtn);
        panel.add(leaveBtn);

        return panel;
    }

    public void showGameStarted() {
        SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, "GAME"));
    }

    public void showRoundOne() {
        SwingUtilities.invokeLater(() -> {
            String question =
                    "I am white powder used in baking.\n"
                    + "Without me, cupcake batter cannot be made.\n\n"
                    + "What am I?";

            roundLabel.setText("Round 1 - Easy");
            questionArea.setText(question);
            centerQuestionText();
            answerField.setText("");
            startRoundOneTimer();
        });
    }

    public void showCorrectAnswer(String playerName) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, playerName + " answered correctly!");
        });
    }

    private void centerQuestionText() {
        StyledDocument doc = questionArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    private void startRoundOneTimer() {
        if (roundTimer != null) {
            roundTimer.stop();
        }

        timeLeft = 15;
        timerLabel.setText(String.valueOf(timeLeft));
        timerLabel.setForeground(Color.BLACK);

        roundTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));

            if (timeLeft <= 5) {
                timerLabel.setForeground(Color.RED);
            } else {
                timerLabel.setForeground(Color.BLACK);
            }

            if (timeLeft <= 0) {
                roundTimer.stop();
                JOptionPane.showMessageDialog(this, "Time is up!");
            }
        });

        roundTimer.start();
    }

    public void updateConnected(String data) {
        SwingUtilities.invokeLater(() -> {
            String text = data.replace("CONNECTED|", "").trim();

            String[] names;
            if (text.isEmpty()) {
                names = new String[0];
            } else {
                names = text.split(",");
            }

            connectedNamesPanel.removeAll();

            for (String n : names) {
                String name = n.trim();

                if (!name.isEmpty()) {
                    JLabel label = new JLabel("   " + name);
                    label.setFont(new Font("SansSerif", Font.BOLD, 28));
                    label.setForeground(new Color(35, 15, 5));
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    label.setBorder(BorderFactory.createEmptyBorder(5, 40, 5, 5));

                    connectedNamesPanel.add(label);
                }
            }

            connectedNamesPanel.revalidate();
            connectedNamesPanel.repaint();
        });
    }

    public void updateWaiting(String data) {
        SwingUtilities.invokeLater(() -> {
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
        });
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
