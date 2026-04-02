package bakerace;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {

    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        java.net.URL url = getClass().getResource(imagePath);

        if (url != null) {
            backgroundImage = new ImageIcon(url).getImage();
        } else {
            System.out.println("BACKGROUND IMAGE NOT FOUND: " + imagePath);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
