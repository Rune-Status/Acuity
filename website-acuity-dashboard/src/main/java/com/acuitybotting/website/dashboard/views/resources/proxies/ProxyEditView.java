package com.acuitybotting.website.dashboard.views.resources.proxies;

import com.acuitybotting.data.flow.messaging.services.db.domain.RabbitDbRequest;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.Proxy;
import com.acuitybotting.db.arango.acuity.rabbit_db.domain.gson.GsonRabbitDocument;
import com.acuitybotting.db.arango.acuity.rabbit_db.service.RabbitDbService;
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

import java.util.Map;
import java.util.UUID;

@Route(value = "resources/proxies/edit", layout = RootLayout.class)
public class ProxyEditView extends VerticalLayout implements HasUrlParameter<String>, Authed {

    private final RabbitDbService rabbitDbService;
    private final AcuityUsersService acuityUsersService;
    private UserMasterPasswordField masterPasswordField;
    private String proxyId;
    private Proxy proxy;

    @Autowired
    public ProxyEditView(RabbitDbService rabbitDbService, AcuityUsersService acuityUsersService, UserMasterPasswordField masterPasswordField) {
        this.rabbitDbService = rabbitDbService;
        this.acuityUsersService = acuityUsersService;
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
            GsonRabbitDocument proxy = rabbitDbService.loadByKey(getQueryMap(proxyId), GsonRabbitDocument.class);
            if (proxy != null){
                this.proxy = proxy.getSubDocumentAs(Proxy.class);
                hostField.setValue(this.proxy.getHost());
                portField.setValue(this.proxy.getPort());
                usernameField.setValue(this.proxy.getUsername());
            }
        }

        add(hostField, portField, usernameField, passwordField, masterPasswordField);

        Button save = new Button(VaadinIcon.PLUS_CIRCLE.create());
        save.addClickListener(buttonClickEvent -> {
            proxy.setHost(hostField.getValue());
            proxy.setPort(portField.getValue());
            proxy.setUsername(usernameField.getValue());

            String password = passwordField.getOptionalValue().orElse(null);
            if (password != null){
                String encrypt = masterPasswordField.encrypt(password);
                if (encrypt == null) return;
                proxy.setEncryptedPassword(encrypt);
            }

            String document = new Gson().toJson(proxy);
            rabbitDbService.save(RabbitDbRequest.SAVE_UPDATE, getQueryMap(proxyId == null ? UUID.randomUUID().toString() : proxyId), null, document, document);
            getUI().ifPresent(ui -> ui.navigate(ProxiesListView.class));
        });
        add(save);
    }

    private Map<String, Object> getQueryMap(String proxyId){
        return RabbitDbService.buildQueryMap(
                Authentication.getAcuityPrincipalId(),
                "services.bot-control-data.proxies",
                "proxy",
                proxyId
        );
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String proxySubId) {
        this.proxyId = proxySubId;
    }
}
