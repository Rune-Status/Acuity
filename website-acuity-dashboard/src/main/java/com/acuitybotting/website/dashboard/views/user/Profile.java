package com.acuitybotting.website.dashboard.views.user;

import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;



/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@Route(value = "profile", layout = RootLayout.class)
public class Profile extends VerticalLayout implements Authed {

    public Profile(AcuityUsersService acuityUsersService) {
        TextField link = new TextField("Link JWT:");
        add(link);
        link.addValueChangeListener(event -> acuityUsersService.linkToPrincipal(Authed.getAcuityPrincipalId(), event.getValue()));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        for (Principal principal : Authed.getAllPrincipals()) {
            add(new Span("Authed as " + principal));
        }
    }
}
