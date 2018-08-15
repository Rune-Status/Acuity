package com.acuitybotting.website.dashboard.components.general.fields;

import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.website.dashboard.security.view.interfaces.Authed;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.web.context.annotation.SessionScope;

@SpringComponent
@SessionScope
public class UserMasterPasswordField extends PasswordField implements Authed {

    private AcuityUsersService acuityUsersService;

    public UserMasterPasswordField(AcuityUsersService acuityUsersService) {
        this.acuityUsersService = acuityUsersService;
        setPlaceholder("Master Password");
        setLabel("Master Password");
    }

    public String encrypt(String value){
        return acuityUsersService.encrypt(Authentication.getAcuityPrincipalId(), getValue(), value);
    }
}
