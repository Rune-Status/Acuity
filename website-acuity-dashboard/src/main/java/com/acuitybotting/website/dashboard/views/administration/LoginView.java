package com.acuitybotting.website.dashboard.views.administration;

import com.acuitybotting.db.arangodb.repositories.acuity.principal.domain.AcuityBottingUser;
import com.acuitybotting.db.arangodb.repositories.acuity.principal.service.AcuityUsersService;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.utils.Components;
import com.acuitybotting.website.dashboard.utils.Notifications;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.user.ProfileView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;


/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@Route(value = "login", layout = RootLayout.class)
public class LoginView extends HorizontalLayout {

    public LoginView(LoginComponent loginForm) {
        setPadding(false);
        setWidth("100%");
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(loginForm);
    }

    @SpringComponent
    @UIScope
    private static class LoginComponent extends FormLayout {

        public LoginComponent(AcuityUsersService acuityUserService) {
            TextField username = new TextField("Email");
            username.setAutocorrect(false);

            PasswordField password = new PasswordField("Password");
            Button login = Components.button("Login", event -> {
                AcuityBottingUser user = acuityUserService.login(username.getValue(), password.getValue()).orElse(null);

                getUI().ifPresent(ui -> {
                    if (user != null) {
                      /*  Authentication.setAcuityPrincipalId(user.getPrincipalId());*/
                        Authentication.updateSession(acuityUserService, false);

                        ui.navigate(ProfileView.class);
                        Notifications.display("Welcome " + user.getDisplayName() + ".");
                    } else {
                        Notifications.error("Failed to login.");
                    }
                });
            });

            Button register = Components.button("Register", event -> getUI().ifPresent(ui -> ui.navigate(RegisterView.class)));
            add(username, password, login, register);
        }
    }
}
