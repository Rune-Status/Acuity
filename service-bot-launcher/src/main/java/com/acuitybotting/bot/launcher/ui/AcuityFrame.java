package com.acuitybotting.bot.launcher.ui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
public class AcuityFrame extends JFrame implements MouseListener, MouseMotionListener {

    private Point mouseCords;

    public AcuityFrame() {
        this.mouseCords = null;
        this.setIconImage(this.getImage("icon2.png").getImage());
        this.setLocationRelativeTo(null);
        this.setTitle("AcuityBotting");
        this.setUndecorated(true);
        this.setLayout(new BorderLayout());
        this.add(this.createTopPanel(), "North");
    }

    private Panel createTopPanel() {
        final Panel panel = new Panel(new BorderLayout());
        panel.setBackground(StyleConstants.ACUITY_BLUE);
        panel.setPreferredSize(new Dimension(getWidth(), 30));
        final Panel leftPanel = new Panel(new FlowLayout(0));
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
        leftPanel.add(new JLabel(new ImageIcon(this.getIconImage())));
        final Panel rightPanel = new Panel(new FlowLayout(2));
        final JButton minimizeButton = new AcuityButton(this.getImage("min.png"));
        minimizeButton.addActionListener(e -> this.setState(1));
        rightPanel.add(minimizeButton);
        final JButton closeButton = new AcuityButton(this.getImage("close.png"));
        closeButton.addActionListener(e -> System.exit(0));
        rightPanel.add(closeButton);
        panel.add(leftPanel, "West");
        panel.add(rightPanel, "East");
        return panel;
    }

    private ImageIcon getImage(final String name) {
        return new ImageIcon(AcuityFrame.class.getClassLoader().getResource(name));
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        this.mouseCords = e.getPoint();
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        this.mouseCords = null;
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        final Point mouse = e.getLocationOnScreen();
        this.setLocation(mouse.x - this.mouseCords.x, mouse.y - this.mouseCords.y);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
    }

    private static class AcuityButton extends JButton {
        public AcuityButton(final ImageIcon imageIcon) {
            super(imageIcon);
            this.setFocusPainted(false);
            this.setContentAreaFilled(false);
            this.setBorderPainted(false);
            this.setSize(16, 16);
        }
    }
}
