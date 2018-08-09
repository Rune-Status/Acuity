package com.acuitybotting.website.dashboard.views.administration;

import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Route(value = "register", layout = RootLayout.class)
public class RegisterView extends VerticalLayout {

    public RegisterView(AcuityUsersService acuityUsersService) {
        setMargin(false);
        setPadding(false);

        TextField email = new TextField("Email");
        TextField displayName = new TextField("Display Name");
        PasswordField password = new PasswordField("Password");
        PasswordField confirmPassword = new PasswordField("Confirm Password");

        Span error = new Span();
        error.setVisible(false);

        Button register = new Button("Register");
        register.addClickListener(buttonClickEvent -> {
            if (!confirmPassword.getValue().equals(password.getValue())){
                error.setText("Passwords do not match.");
                error.setVisible(true);
                return;
            }

            boolean result = acuityUsersService.register(email.getValue(), displayName.getValue(), password.getValue());
            getUI().ifPresent(ui -> {
                if (result) {
                    ui.navigate(LoginView.class);
                } else {
                    error.setText("Failed to register.");
                    error.setVisible(true);
                }
            });
        });

        add(email, displayName, password, confirmPassword, register);
    }
}
