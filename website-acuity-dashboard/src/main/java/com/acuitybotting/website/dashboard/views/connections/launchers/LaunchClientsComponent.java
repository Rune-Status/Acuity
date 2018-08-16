package com.acuitybotting.website.dashboard.views.connections.launchers;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.LauncherConnection;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.Proxy;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.RsAccountInfo;
import com.acuitybotting.website.dashboard.components.general.separator.TitleSeparator;
import com.acuitybotting.website.dashboard.services.AccountsService;
import com.acuitybotting.website.dashboard.services.LaunchersService;
import com.acuitybotting.website.dashboard.services.ProxiesService;
import com.acuitybotting.website.dashboard.services.ScriptsService;
import com.acuitybotting.website.dashboard.utils.Components;
import com.acuitybotting.website.dashboard.utils.Layouts;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@SpringComponent
@UIScope
@Getter
public class LaunchClientsComponent extends VerticalLayout {

    private final LaunchClientsView.LauncherSelectComponent launcherSelectComponent;
    private final ProxiesService proxiesService;
    private final AccountsService accountsService;
    private final LaunchersService launchersService;
    private final ScriptsService scriptsService;

    private ComboBox<Proxy> proxyComboBox = new ComboBox<>();
    private ComboBox<RsAccountInfo> accountComboBox = new ComboBox<>();

    private Checkbox localScript = new Checkbox("Local Script");
    private ComboBox<String> scriptSelector = new ComboBox<>();
    private TextField scriptArgs = Components.textField("Script Args", () -> null);

    private TextField commandField = new TextField();
    private String defaultCommand = "{RSPEER_JAVA_PATH} -Dacuity.connection={CONNECTION} -Djava.net.preferIPv4Stack=true -jar \"{RSPEER_SYSTEM_HOME}RSPeer/cache/rspeer.jar\"";

    public LaunchClientsComponent(LaunchClientsView.LauncherSelectComponent launcherSelectComponent, ProxiesService proxiesService, AccountsService accountsService, LaunchersService launchersService, ScriptsService scriptsService) {
        this.launcherSelectComponent = launcherSelectComponent;
        this.proxiesService = proxiesService;
        this.accountsService = accountsService;
        this.launchersService = launchersService;
        this.scriptsService = scriptsService;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()){
            setPadding(false);

            commandField.setValue(defaultCommand);
            commandField.setWidth("100%");

            add(
                    new TitleSeparator("Command"),
                    Layouts.wrapHorizontal("65%",
                            commandField,
                            Components.button(VaadinIcon.REFRESH, event -> commandField.setValue(defaultCommand))
                    )
            );

            proxyComboBox.setWidth("65%");
            proxyComboBox.setItemLabelGenerator(proxy -> proxy.getHost() + ":" + proxy.getPort() + (proxy.getUsername() == null ? "" : " " + proxy.getUsername()));
            add(new TitleSeparator("Proxy"), proxyComboBox);

            accountComboBox.setWidth("65%");
            accountComboBox.setItemLabelGenerator(RsAccountInfo::getSubKey);
            add(new TitleSeparator("Account"), accountComboBox);

            scriptSelector.setWidth("100%");
            scriptSelector.setAllowCustomValue(true);

            scriptArgs.setWidth("100%");
            add(new TitleSeparator("Script"), Layouts.wrapVertical("65%", localScript, scriptSelector, scriptArgs));

            add(new TitleSeparator("Deploy"), Components.button("Launch", event -> deploy()));
        }

        scriptSelector.setItems(scriptsService.getAllRsPeerScripts());
        proxyComboBox.setItems(proxiesService.loadProxies());
        accountComboBox.setItems(accountsService.loadAccounts());
        launcherSelectComponent.load(attachEvent);
    }

    private void deploy() {
        Set<String> subIds = launcherSelectComponent.getSelectedValues().map(LauncherConnection::getSubKey).collect(Collectors.toSet());
        launchersService.deploy(
                subIds,
                commandField.getValue(),
                accountComboBox.getOptionalValue().orElse(null),
                proxyComboBox.getOptionalValue().orElse(null),
                localScript.getValue(),
                scriptArgs.getOptionalValue().orElse(null),
                scriptSelector.getOptionalValue().orElse(null)
        );
    }
}