package com.acuitybotting.website.dashboard.views.resources.proxies;

import com.acuitybotting.db.arangodb.repositories.resources.proxies.domain.Proxy;
import com.acuitybotting.website.dashboard.components.general.fields.UserMasterPasswordField;
import com.acuitybotting.website.dashboard.components.general.separator.TitleSeparator;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.google.gson.Gson;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Route(value = "resources/proxies/edit", layout = RootLayout.class)
public class ProxyEditView extends VerticalLayout implements HasUrlParameter<String>, Authed {

    private UserMasterPasswordField masterPasswordField;
    private String proxyId;
    private Proxy proxy;

    @Autowired
    public ProxyEditView(UserMasterPasswordField masterPasswordField) {
        this.masterPasswordField = masterPasswordField;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        add(new TitleSeparator((proxyId == null ? "Add" : "Edit") + " Proxy"));
        TextField hostField = new TextField("Host");
        TextField portField = new TextField("Port");
        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Proxy Password");


        if (proxyId == null) proxy = new Proxy();
        else {
            /*GsonRabbitDocument proxy = rabbitDbService.queryByKey()
                    .withMatch(Authentication.getAcuityPrincipalId(), "services.bot-control-data.proxies", "proxy", proxyId)
                    .findOne(GsonRabbitDocument.class).orElse(null);

            if (proxy != null) {
                this.proxy = proxy.getSubDocumentAs(Proxy.class);
                hostField.setValue(this.proxy.getHost());
                portField.setValue(this.proxy.getPort());
                usernameField.setValue(this.proxy.getUsername());
            }*/
        }

        add(hostField, portField, usernameField, passwordField, masterPasswordField);

        Button save = new Button(VaadinIcon.PLUS_CIRCLE.create());
        save.addClickListener(buttonClickEvent -> {
            proxy.setHost(hostField.getValue());
            proxy.setPort(portField.getValue());
            proxy.setUsername(usernameField.getValue());

            String password = passwordField.getOptionalValue().orElse(null);
            if (password != null) {
                String encrypt = masterPasswordField.encrypt(password);
                if (encrypt == null) return;
                proxy.setEncryptedPassword(encrypt);
            }

            String document = new Gson().toJson(proxy);
/*            rabbitDbService.query()
                    .withMatch(Authentication.getAcuityPrincipalId(), "services.bot-control-data.proxies", "proxy", proxyId == null ? UUID.randomUUID().toString() : proxyId)
                    .upsert(document);*/
            getUI().ifPresent(ui -> ui.navigate(ProxiesListView.class));
        });
        add(save);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String proxySubId) {
        this.proxyId = proxySubId;
    }
}
