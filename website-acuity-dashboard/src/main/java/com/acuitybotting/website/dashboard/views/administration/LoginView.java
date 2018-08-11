package com.acuitybotting.website.dashboard.views.administration;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityBottingUser;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.acuitybotting.website.dashboard.views.user.ProfileView;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;


/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@Route(value = "login", layout = RootLayout.class)
public class LoginView extends VerticalLayout {

    public LoginView(AcuityUsersService acuityUserService) {
        setMargin(false);
        setPadding(false);

        TextField username = new TextField("Email");
        PasswordField password = new PasswordField("Password");
        Span error = new Span();
        error.setVisible(false);
        Button login = new Button("Login");

        login.addClickListener(buttonClickEvent -> {
            AcuityBottingUser user = acuityUserService.login(username.getValue(), password.getValue()).orElse(null);

            getUI().ifPresent(ui -> {
                if (user != null && Authed.applyUser(user)){
                    ui.navigate(ProfileView.class);
                }
                else {
                    error.setText("Failed to login.'");
                    error.setVisible(true);
                }
            });
        });

        add(username, password, error, login);
    }
}
