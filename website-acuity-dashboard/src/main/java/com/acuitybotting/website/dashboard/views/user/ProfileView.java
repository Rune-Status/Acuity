package com.acuitybotting.website.dashboard.views.user;

import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.components.general.separator.TitleSeparator;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;


/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@Route(value = "profile", layout = RootLayout.class)
public class ProfileView extends VerticalLayout implements Authed {

    private final AcuityUsersService acuityUsersService;
    private InteractiveList<Principal> linkedList = new InteractiveList<>();

    public ProfileView(AcuityUsersService acuityUsersService) {
        this.acuityUsersService = acuityUsersService;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        setPadding(false);

        linkedList.withLoad(
                principal -> principal.getType() + ":" + principal.getUid(),
                Authed::getAllPrincipals
        );

        linkedList.withColumn("Type", "15%", principal -> new Span(), (principal, span) -> span.setText(principal.getType()));
        linkedList.withColumn("ID", "35%", principal -> new Span(), (principal, span) -> span.setText(principal.getUid()));

        TextField jwtField = new TextField();
        jwtField.setPlaceholder("JWT");
        Button addLink = new Button(VaadinIcon.PLUS_CIRCLE.create());

        addLink.addClickListener(event -> {
            acuityUsersService.linkToPrincipal(Authed.getAcuityPrincipalId(), jwtField.getValue());
            acuityUsersService.findUserByUid(Authed.getAcuityPrincipalId()).ifPresent(Authed::applyUser);
            linkedList.load();
        });
        linkedList.getControls().add(jwtField, addLink);
        add(
                new TitleSeparator("Linked Accounts"),
                new Span("These are the current links to your Acuity-Account, by adding more links you will be able to view more information on your dashboards."),
                linkedList
        );

        add(
                new TitleSeparator("Master Key"),
                new Span("This key is used to store your RS-account, proxies, and other sensitive information. It is not stored anywhere in our databases so if you forget it we will not be able to help you recover it and you will need to delete and re-enter any information you have previously added."),
                new MasterKeyComponent()
        );

        linkedList.load();
    }

    private class MasterKeyComponent extends HorizontalLayout {

        public MasterKeyComponent() {
            setPadding(false);

            PasswordField keyField1 = new PasswordField();
            PasswordField keyField2 = new PasswordField();
            Button set = new Button(VaadinIcon.PLUS_CIRCLE.create());
            add(keyField1, keyField2, set);

            boolean keySet = Authed.getAcuityUser().getMasterKey() != null;
            keyField1.setPlaceholder(keySet ? "Old Key" : "Master Key");
            keyField2.setPlaceholder(keySet ? "New Key" : "Confirm Key");

            set.addClickListener(buttonClickEvent -> {
                boolean result = false;
                if (keySet) {
                    result = acuityUsersService.createOrUpdateMasterKey(Authed.getAcuityPrincipalId(), keyField1.getValue(), keyField2.getValue());
                } else {
                    if (keyField1.getValue().equals(keyField2.getValue())) {
                        result = acuityUsersService.createOrUpdateMasterKey(Authed.getAcuityPrincipalId(), null, keyField1.getValue());
                    }
                }

                if (result) {
                    keyField1.clear();
                    keyField2.clear();
                }
            });
        }

    }

}
