package com.acuitybotting.website.dashboard.views.administration;

import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.website.dashboard.utils.Components;
import com.acuitybotting.website.dashboard.utils.Notifications;
import com.acuitybotting.website.dashboard.views.RootLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Route(value = "register", layout = RootLayout.class)
public class RegisterView extends VerticalLayout {

    public RegisterView(RegisterComponent registerForm) {
        setPadding(false);
        setWidth("100%");
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(registerForm);
    }

    @SpringComponent
    @UIScope
    private static class RegisterComponent extends FormLayout {

        public RegisterComponent(AcuityUsersService acuityUserService) {
            TextField email = new TextField("Email");
            email.setAutocorrect(false);
            email.setPlaceholder("email");

            TextField displayName = new TextField("Display Name");
            displayName.setAutocorrect(false);
            displayName.setPlaceholder("display name");

            PasswordField password = new PasswordField("Password");
            password.setPlaceholder("password");

            PasswordField confirmPassword = new PasswordField();
            confirmPassword.setPlaceholder("confirm password");

            Span error = new Span();
            error.setVisible(false);

            Button register = new Button("Register");
            register.addClickListener(buttonClickEvent -> {
                if (!confirmPassword.getValue().equals(password.getValue())) {
                    error.setText("Passwords do not match.");
                    error.setVisible(true);
                    return;
                }

                if (email.getValue().length() < 3 || !email.getValue().contains("@")){
                    Notifications.error("Invalid email.");
                    return;
                }

                if (displayName.getValue().length() < 4 && displayName.getValue().matches("[a-zA-Z0-9]*")){
                    Notifications.error("Invalid display name must be at least 4 characters and contain only letters and numbers.");
                    return;
                }

                if (password.getValue().length() < 7){
                    Notifications.error("Invalid password name must be at least 7 characters.");
                    return;
                }

                boolean result = acuityUserService.register(email.getValue(), displayName.getValue(), password.getValue());
                getUI().ifPresent(ui -> {
                    if (result) {
                        ui.navigate(LoginView.class);
                    } else {
                        error.setText("Failed to register.");
                        error.setVisible(true);
                    }
                });
            });

            Button login = Components.button("Login", event -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));

            add(email, displayName, password, confirmPassword, register, login);
        }
    }
}
