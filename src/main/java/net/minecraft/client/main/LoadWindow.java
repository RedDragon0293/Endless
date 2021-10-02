package net.minecraft.client.main;

import cn.asone.endless.Endless;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LoadWindow extends JFrame {
    public void init() {
        this.setTitle(Endless.CLIENT_NAME + " " + Endless.CLIENT_VERSION);
        this.setAlwaysOnTop(true);
        try {
            InputStream x16 = LoadWindow.class.getResourceAsStream("/assets/minecraft/" + Endless.CLIENT_NAME.toLowerCase() + "/icon_16.png");
            InputStream x32 = LoadWindow.class.getResourceAsStream("/assets/minecraft/" + Endless.CLIENT_NAME.toLowerCase() + "/icon_32.png");
            ArrayList<BufferedImage> list = new ArrayList<>();
            if (x16 != null)
                list.add(ImageIO.read(x16));
            if (x32 != null)
                list.add(ImageIO.read(x32));
            this.setIconImages(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setUndecorated(true);
        this.setResizable(false);
        this.setType(Type.UTILITY);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationByPlatform(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Font font;
        try {
            InputStream stream = LoadWindow.class.getResourceAsStream("/assets/minecraft/" + Endless.CLIENT_NAME.toLowerCase() + "/fonts/HarmonyOS_Sans_Regular.ttf");
            if (stream == null)
                font = new Font("default", Font.PLAIN, 64);
            else {
                font = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Font.PLAIN, 64);
            }
        } catch (IOException | FontFormatException e) {
            font = new Font("default", Font.PLAIN, 64);
            e.printStackTrace();
        }
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setFont(font);
        FontMetrics metrics = graphics2D.getFontMetrics();
        int width = metrics.stringWidth("Launching " + Endless.CLIENT_NAME + "...");

        JLabel label = new JLabel();
        label.setSize(width, metrics.getHeight());
        label.setFont(font);
        label.setText("Launching " + Endless.CLIENT_NAME + "...");
        label.setOpaque(true);
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setLocation(1, 1);

        this.add(label);
        this.setSize(width, metrics.getHeight());
        this.setLocationRelativeTo(null);
    }
}
