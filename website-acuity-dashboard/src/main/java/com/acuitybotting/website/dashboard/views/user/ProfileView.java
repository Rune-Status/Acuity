package com.acuitybotting.website.dashboard.views.user;

import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.components.general.separator.TitleSeparator;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Components;
import com.acuitybotting.website.dashboard.utils.Layouts;
import com.acuitybotting.website.dashboard.utils.Notifications;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.google.common.base.Strings;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;


/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@Route(value = "profile", layout = RootLayout.class)
public class ProfileView extends VerticalLayout implements Authed {

    private final AcuityUsersService acuityUsersService;

    public ProfileView(AcuityUsersService acuityUsersService) {
        this.acuityUsersService = acuityUsersService;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        setPadding(false);

        add(
                new TitleSeparator("Settings"),
                new AcuityUserSettingsComponent()
        );

        add(
                new TitleSeparator("Connection Key"),
                new Span("This key is used by your clients to connect to Acuity. If you regenerate it any current clients will lose access within a few mins as the cache clears."),
                new ConnectionKeyComponent()
        );

        add(
                new TitleSeparator("Master Key"),
                new Span("This key is used to store your RS-account, proxies, and other sensitive information. It is not stored anywhere in our databases so if you forget it we will not be able to help you recover it and you will need to delete and re-enter any information you have previously added."),
                new MasterKeyComponent()
        );

        add(
                new TitleSeparator("Linked Accounts"),
                new Span("These are the current links to your Acuity-Account, by adding more links you will be able to view more information on your dashboards."),
                new LinkAccountComponent()
        );
    }

    private class AcuityUserSettingsComponent extends VerticalLayout {




        public AcuityUserSettingsComponent() {
            setPadding(false);

            TextField profileImage = Components.textField("Profile Image (https://i.imgur.com/)", () -> Authentication.getAcuityUser().getProfileImgUrl());
            profileImage.setWidth("40%");
            Button setProfileImage = Components.button(VaadinIcon.PLUS_CIRCLE, event -> {
                String url = Strings.nullToEmpty(profileImage.getValue());
                if (!url.startsWith("https://i.imgur.com/")){
                    Notifications.error("URL must start with 'https://i.imgur.com/'.");
                    return;
                }

                if (!url.endsWith(".png") || !url.endsWith(".jpg")){
                    Notifications.error("URL must end with .png or .jpg");
                    return;
                }

                acuityUsersService.setProfileImage(Authentication.getAcuityPrincipalId(), url);
                Authentication.updateSession(acuityUsersService);
                Notifications.display("Updated profile image.");
            });
            add(Layouts.wrapHorizontal("100%", profileImage, setProfileImage));
        }
    }

    private class LinkAccountComponent extends InteractiveList<Principal> {

        public LinkAccountComponent() {
            withLoad(
                    principal -> principal.getType() + ":" + principal.getUid(),
                    Authentication.getAcuityUser()::getLinkedPrincipals
            );

            withColumn("Source", "15%", principal -> new Span(), (principal, span) -> span.setText(principal.getType()));
            withColumn("ID", "35%", principal -> new Span(), (principal, span) -> span.setText(principal.getUid()));

            TextField jwtField = new TextField();
            jwtField.setPlaceholder("JWT");
            Button addLink = new Button(VaadinIcon.PLUS_CIRCLE.create());

            addLink.addClickListener(event -> {
                acuityUsersService.linkToPrincipal(Authentication.getAcuityPrincipalId(), jwtField.getValue());
                Authentication.updateSession(acuityUsersService);
                Notifications.display("Added token.");
            });
            getControls().add(jwtField, addLink);
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            load();
        }
    }

    private class ConnectionKeyComponent extends HorizontalLayout {

        public ConnectionKeyComponent() {
            setPadding(false);

            String connectionKey = acuityUsersService.wrapConnectionKey(Authentication.getAcuityPrincipalId(), Authentication.getAcuityUser().getConnectionKey());

            PasswordField currentKey = new PasswordField();
            currentKey.setValue(connectionKey);
            currentKey.addValueChangeListener(event -> currentKey.setValue(connectionKey));
            currentKey.setPlaceholder("Not Set");

            Button generate = new Button(VaadinIcon.REFRESH.create());
            generate.setText("Generate");

            generate.addClickListener(buttonClickEvent -> {
                if (acuityUsersService.generateNewConnectionKey(Authentication.getAcuityPrincipalId())){
                    Authentication.updateSession(acuityUsersService);
                    Notifications.display("New connection key set.");
                }
            });

            add(currentKey, generate);
        }
    }

    private class MasterKeyComponent extends HorizontalLayout {

        public MasterKeyComponent() {
            setPadding(false);

            PasswordField keyField1 = new PasswordField();
            PasswordField keyField2 = new PasswordField();
            PasswordField keyField3 = new PasswordField();

            Button set = new Button(VaadinIcon.PLUS_CIRCLE.create());

            boolean passwordSet = Authentication.getAcuityUser().getMasterKey() != null;
            keyField1.setPlaceholder(passwordSet ? "Old Password" : "Master Password");
            keyField2.setPlaceholder(passwordSet ? "New Password" : "Confirm Password");
            keyField3.setPlaceholder("Confirm password.");

            add(keyField1, keyField2);
            if (passwordSet) add(keyField3);
            add(set);

            set.addClickListener(buttonClickEvent -> {
                boolean result;
                if (passwordSet) {
                    if (!keyField2.getValue().equals(keyField3.getValue())) {
                        Notifications.error("Passwords do not match.");
                        return;
                    }

                    result = acuityUsersService.createOrUpdateMasterKey(Authentication.getAcuityPrincipalId(), keyField1.getValue(), keyField2.getValue());
                } else {
                    if (keyField1.getValue().equals(keyField2.getValue())) {
                        result = acuityUsersService.createOrUpdateMasterKey(Authentication.getAcuityPrincipalId(), null, keyField1.getValue());
                    }
                    else {
                        Notifications.error("Passwords do not match.");
                        return;
                    }
                }

                if (result) {
                    Notifications.display("Password " + (passwordSet ? " set" : "updated") + ".");
                    Authentication.updateSession(acuityUsersService);
                }
                else {
                    Notifications.error("Failed to update password.");
                }
            });
        }
    }
}
