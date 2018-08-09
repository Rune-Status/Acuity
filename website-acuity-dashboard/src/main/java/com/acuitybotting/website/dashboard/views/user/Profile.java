package com.acuitybotting.website.dashboard.views.user;

import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;




/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@Route(value = "profile", layout = RootLayout.class)
public class Profile extends VerticalLayout implements Authed {

    private InteractiveList<Principal> linkedList = new InteractiveList<>();

    public Profile(AcuityUsersService acuityUsersService) {
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

        add(linkedList);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        linkedList.load();
    }
}
