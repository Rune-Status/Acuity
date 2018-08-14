package com.acuitybotting.bot.launcher.ui;

import com.acuitybotting.common.utils.ConnectionKeyUtil;

import javax.swing.*;
import java.awt.*;

public abstract class LoginFrame extends AcuityFrame {

    private JTextField connectionKey;
    private JTextField passwordField;
    private JButton connect;

    public LoginFrame() {
        this.setSize(400, 200);
        this.add(this.createPadding(), "West");
        this.add(this.createPadding(), "East");
        this.add(this.buildFields(), "Center");
        this.add(this.connect = this.buildLoginButton(), "South");

        String key = ConnectionKeyUtil.findKey();
        connectionKey.setText(key == null ? "" : key);

        this.setDefaultCloseOperation(2);
    }

    public abstract void onConnect(final String connectionKey, final String masterPassword);

    private JButton buildLoginButton() {
        return this.createButton("Connect");
    }

    private Panel buildFields() {
        final Panel panel = new Panel(new GridLayout(4, 1));
        panel.add(this.createLabel("Connection Key:"));
        panel.add(this.connectionKey = this.createTextField(false));
        panel.add(this.createLabel("Master Password:"));
        panel.add(this.passwordField = this.createTextField(true));
        return panel;
    }

    private JLabel createLabel(final String text) {
        final JLabel jLabel = new JLabel(text);
        jLabel.setOpaque(true);
        jLabel.setForeground(Color.WHITE);
        jLabel.setBackground(StyleConstants.ACUITY_DARK_GREY);
        return jLabel;
    }

    private JTextField createTextField(final boolean password) {
        final JTextField jTextField = password ? new JPasswordField() : new JTextField();
        jTextField.setForeground(Color.white);
        jTextField.setBackground(StyleConstants.ACUITY_LIGHT_GREY);
        jTextField.setBorder(BorderFactory.createSoftBevelBorder(2));
        return jTextField;
    }

    private JButton createButton(final String text) {
        final JButton jButton = new JButton(text);
        jButton.setForeground(Color.white);
        jButton.setBackground(StyleConstants.ACUITY_DARK_GREY);
        jButton.setRolloverEnabled(false);
        jButton.setBorderPainted(false);
        jButton.addActionListener(e -> {
            connectionKey.setEnabled(false);
            passwordField.setEnabled(false);
            connect.setEnabled(false);
            this.onConnect(this.connectionKey.getText(), this.passwordField.getText());
        });
        return jButton;
    }

    private JPanel createPadding() {
        final JPanel padding = new JPanel();
        padding.setSize(20, 200);
        padding.setBackground(StyleConstants.ACUITY_DARK_GREY);
        return padding;
    }
}