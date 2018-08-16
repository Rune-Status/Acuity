package com.acuitybotting.bot.launcher.ui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public abstract class LauncherFrame extends AcuityFrame {

    private JTextField connectionKey;
    private JTextField passwordField;

    public LauncherFrame() {
        setSize(400, 250);
        add(createPadding(), "West");
        add(createPadding(), "East");
        add(buildFields(), "Center");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public abstract void onConnect(String connectionKey, String masterPassword);

    public abstract void onSave(String connectionKey, String masterPassword);

    private JButton buildSaveButton() {
        JButton save = createButton("Save");
        save.addActionListener(e -> onSave(connectionKey.getText(), passwordField.getText()));
        return save;
    }

    private JButton buildConnectButton() {
        JButton connect = createButton("Connect");
        connect.addActionListener(e -> {
            connectionKey.setEnabled(false);
            passwordField.setEnabled(false);
            connect.setEnabled(false);
            onConnect(connectionKey.getText(), passwordField.getText());
        });

        return connect;
    }

    private Panel buildFields() {
        Panel panel = new Panel(new GridLayout(6, 1));
        panel.setBackground(StyleConstants.ACUITY_DARK_GREY);
        panel.add(createLabel("Connection Key:"));
        panel.add(connectionKey = createTextField(false));
        panel.add(createLabel("Master Password:"));
        panel.add(passwordField = createTextField(true));
        panel.add(buildSaveButton());
        panel.add(buildConnectButton());
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setOpaque(true);
        jLabel.setForeground(Color.WHITE);
        jLabel.setBackground(StyleConstants.ACUITY_DARK_GREY);
        return jLabel;
    }

    private JTextField createTextField(boolean password) {
        JTextField jTextField = password ? new JPasswordField() : new JTextField();
        jTextField.setForeground(Color.WHITE);
        jTextField.setBackground(StyleConstants.ACUITY_LIGHT_GREY);
        jTextField.setBorder(BorderFactory.createSoftBevelBorder(2));
        return jTextField;
    }

    private JButton createButton(String text) {
        JButton jButton = new JButton(text);
        jButton.setForeground(Color.white);
        jButton.setBackground(StyleConstants.ACUITY_DARK_GREY);
        jButton.setFocusPainted(false);
        jButton.setRolloverEnabled(false);
        jButton.setBorderPainted(false);
        return jButton;
    }

    private JPanel createPadding() {
        JPanel padding = new JPanel();
        padding.setSize(20, 200);
        padding.setBackground(StyleConstants.ACUITY_DARK_GREY);
        return padding;
    }
}