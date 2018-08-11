package com.acuitybotting.website.dashboard.utils;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityBottingUser;
import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.vaadin.flow.component.UI;

public class Authentication {

    public static final String ACUITY_USER = "acuityUser";
    public static final String ACUITY_PRINCIPAL_ID = "acuityPrincipalId";

    public static boolean updateSession(AcuityUsersService service){
        AcuityBottingUser acuityBottingUser = service.findUserByUid(getAcuityPrincipalId()).orElse(null);
        return acuityBottingUser != null && setSessionAttribute(ACUITY_USER, acuityBottingUser);
    }

    public static AcuityBottingUser getAcuityUser(){
        return (AcuityBottingUser) getSessionAttribute(ACUITY_USER);
    }

    public static String getAcuityPrincipalId(){
        return (String) getSessionAttribute(ACUITY_PRINCIPAL_ID);
    }

    public static void setAcuityPrincipalId(String principalId) {
        setSessionAttribute(ACUITY_PRINCIPAL_ID, principalId);
    }

    private static Object getSessionAttribute(String key){
        UI current = UI.getCurrent();
        if (current == null) return null;
        return current.getSession().getAttribute(key);
    }

    private static boolean setSessionAttribute(String key, Object value){
        UI current = UI.getCurrent();
        if (current == null) return false;
        current.getSession().setAttribute(key, value);
        return true;
    }
}