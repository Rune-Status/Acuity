package com.acuitybotting.bot.launcher.ui;

import com.acuitybotting.bot.launcher.services.LauncherRabbitService;
import com.acuitybotting.common.utils.ConnectionKeyUtil;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

@Getter
@Setter
public class LauncherFrame extends JFrame {

    private static LauncherFrame instance;

    private JPasswordField keyField = new JPasswordField();

    public LauncherFrame(LauncherRabbitService launcherRabbitService) throws HeadlessException {
        getContentPane().setLayout(new BorderLayout());

        String key = ConnectionKeyUtil.findKey();
        if (key != null) keyField.setText(key);
        getContentPane().add(keyField);

        JButton set = new JButton("Set");
        set.addActionListener(e -> {
            ConnectionKeyUtil.writeKey(getConnectionKey());
            launcherRabbitService.connect();
        });
        getContentPane().add(set, BorderLayout.SOUTH);

        setTitle("Acuity Launcher");
        setSize(new Dimension(300, 600));
    }

    public static LauncherFrame setInstance(LauncherFrame instance) {
        LauncherFrame.instance = instance;
        return instance;
    }

    public static LauncherFrame getInstance() {
        return instance;
    }

    public String getConnectionKey(){
        return String.valueOf(keyField.getPassword());
    }
}
