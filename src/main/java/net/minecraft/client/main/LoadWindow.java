package net.minecraft.client.main;

import cn.asone.endless.Endless;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LoadWindow extends JFrame {
    public boolean exitConfirm = true;
    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
    }

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
        //this.setUndecorated(true);
        this.setResizable(false);
        this.setType(Type.POPUP);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationByPlatform(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        //通过ttf字体文件创建字体对象
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

        this.add(label);
        this.setSize(width, metrics.getHeight());
        this.setLocationRelativeTo(null);
        this.addWindowListener(new CloseListener());
        this.pack();
    }

    public class CloseListener implements WindowListener {
        /**
         * Invoked the first time a window is made visible.
         */
        @Override
        public void windowOpened(WindowEvent e) {

        }

        /**
         * Invoked when the user attempts to close the window
         * from the window's system menu.
         */
        @Override
        public void windowClosing(WindowEvent e) {
            int i = JOptionPane.showConfirmDialog(e.getComponent(), Endless.CLIENT_NAME + "未加载完成. 确定要退出吗?", "确认", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (i == JOptionPane.YES_OPTION && exitConfirm) {
                System.exit(0);
            }
        }

        /**
         * Invoked when a window has been closed as the result
         * of calling dispose on the window.
         */
        @Override
        public void windowClosed(WindowEvent e) {

        }

        /**
         * Invoked when a window is changed from a normal to a
         * minimized state. For many platforms, a minimized window
         * is displayed as the icon specified in the window's
         * iconImage property.
         *
         * @see Frame#setIconImage
         */
        @Override
        public void windowIconified(WindowEvent e) {

        }

        /**
         * Invoked when a window is changed from a minimized
         * to a normal state.
         */
        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        /**
         * Invoked when the Window is set to be the active Window. Only a Frame or
         * a Dialog can be the active Window. The native windowing system may
         * denote the active Window or its children with special decorations, such
         * as a highlighted title bar. The active Window is always either the
         * focused Window, or the first Frame or Dialog that is an owner of the
         * focused Window.
         */
        @Override
        public void windowActivated(WindowEvent e) {

        }

        /**
         * Invoked when a Window is no longer the active Window. Only a Frame or a
         * Dialog can be the active Window. The native windowing system may denote
         * the active Window or its children with special decorations, such as a
         * highlighted title bar. The active Window is always either the focused
         * Window, or the first Frame or Dialog that is an owner of the focused
         * Window.
         */
        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }
}
