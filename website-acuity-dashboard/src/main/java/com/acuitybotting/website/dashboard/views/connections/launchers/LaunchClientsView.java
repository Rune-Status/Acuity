package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.data.flow.messaging.services.client.exceptions.MessagingException;
import com.acuitybotting.website.dashboard.DashboardRabbitService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.components.general.separator.TitleSeparator;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Components;
import com.acuitybotting.website.dashboard.utils.Layouts;
import com.acuitybotting.website.dashboard.utils.Notifications;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.google.gson.Gson;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
@Route(value = "connections/launchers/deploy", layout = RootLayout.class)
public class LaunchClientsView extends VerticalLayout implements Authed, HasUrlParameter<String> {

    private final LaunchClientsComponent launchClientsComponent;

    @Autowired
    public LaunchClientsView(LaunchClientsComponent launchClientsComponent) {
        this.launchClientsComponent = launchClientsComponent;

        setPadding(false);
        add(launchClientsComponent);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String subIdListEncode) {
        launchClientsComponent.getSubIds().clear();
        if (subIdListEncode != null) {
            Collections.addAll(
                    launchClientsComponent.getSubIds(),
                    new String(Base64.getDecoder().decode(subIdListEncode)).split(",")
            );
        }
    }

    @SpringComponent
    @UIScope
    @Getter
    public static class LaunchClientsComponent extends VerticalLayout {

        private final DashboardRabbitService rabbitService;

        private List<String> subIds = new ArrayList<>();

        private InteractiveList<String> launchersList = new InteractiveList<>();
        private TextField commandField = new TextField();

        private String defaultCommand = "{RSPEER_JAVA_PATH} {CENV_VARIABLES} -Djava.net.preferIPv4Stack=true -jar \"{RSPEER_SYSTEM_HOME}RSPeer/cache/rspeer.jar\"";

        public LaunchClientsComponent(DashboardRabbitService rabbitService) {
            this.rabbitService = rabbitService;

            setPadding(false);

            launchersList.withColumn("Launcher ID", "45%", s -> new Span(), (s, span) -> span.setText(s));
            launchersList.withLoad(s -> s, () -> subIds);
            launchersList.hideControls();
            add(new TitleSeparator("Selected Launchers"), launchersList);

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

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            launchersList.load();
        }

        private void deploy() {
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
                    rabbitService.getMessagingChannel().buildMessage(
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
}
