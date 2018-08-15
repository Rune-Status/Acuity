package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.LauncherConnection;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.components.general.separator.TitleSeparator;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Components;
import com.acuitybotting.website.dashboard.utils.Layouts;
import com.acuitybotting.website.dashboard.utils.Notifications;
import com.google.gson.Gson;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringComponent
@UIScope
@Getter
public class LaunchClientsComponent extends VerticalLayout {

    private final DashboardRabbitService rabbitService;
    private final LauncherListComponent launcherListComponent;

    private TextField commandField = new TextField();
    private String defaultCommand = "{RSPEER_JAVA_PATH} {CENV_VARIABLES} -Djava.net.preferIPv4Stack=true -jar \"{RSPEER_SYSTEM_HOME}RSPeer/cache/rspeer.jar\"";

    public LaunchClientsComponent(DashboardRabbitService rabbitService, LauncherListComponent launcherListComponent) {
        this.rabbitService = rabbitService;
        this.launcherListComponent = launcherListComponent;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()){
            setPadding(false);

            commandField.setValue(defaultCommand);
            commandField.setWidth("100%");

            add(
                    new TitleSeparator("Command"),
                    Layouts.wrapHorizontal("100%",
                            commandField,
                            Components.button(VaadinIcon.REFRESH, event -> commandField.setValue(defaultCommand))
                    )
            );

            add(new TitleSeparator("Deploy"), Components.button("Start Client(s)", event -> deploy()));
        }

        launcherListComponent.load();
    }

    private void deploy() {
        Set<String> subIds = launcherListComponent.getSelectedValues().map(LauncherConnection::getSubKey).collect(Collectors.toSet());

        Notifications.display("Deploying to {} launchers.", subIds.size());

        Map<String, Object> launchConfig = new HashMap<>();
        launchConfig.put("command", commandField.getValue());

        Map<String, String> customEnvVars = new HashMap<>();
        customEnvVars.put("acuityConfig", "");
        launchConfig.put("cenvVariables", customEnvVars);

        String launchJson = new Gson().toJson(launchConfig);
        for (String subId : subIds) {
            String queue = "user." + Authentication.getAcuityPrincipalId() + ".queue." + subId;
            try {
                rabbitService.getRabbitChannel().buildMessage(
                        "",
                        queue,
                        launchJson
                ).setAttribute("type", "runCommand").send();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }

        Notifications.display("Deployment complete.");
    }
}