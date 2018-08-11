package com.acuitybotting.website.dashboard.security.view.interfaces;

import com.acuitybotting.db.arango.acuity.identities.domain.AcuityBottingUser;
import com.acuitybotting.db.arango.acuity.identities.domain.Principal;
import com.acuitybotting.db.arango.acuity.identities.service.PrincipalLinkTypes;
import com.acuitybotting.website.dashboard.views.administration.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
public interface Authed extends BeforeEnterObserver {

    String ACUITY_USER = "acuityUser";
    String ACUITY_PRINCIPAL = "acuityPrincipal";
    String ALL_PRINCIPALS = "allPrincipals";

    @Override
    default void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (getAcuityUser() == null) beforeEnterEvent.rerouteTo(LoginView.class);
    }

    @SuppressWarnings("unchecked")
    static Collection<Principal> getAllPrincipals(){
        UI current = UI.getCurrent();
        if (current == null || current.getSession() == null) return Collections.emptySet();
        Collection<Principal> principals = (Collection<Principal>) current.getSession().getAttribute(ALL_PRINCIPALS);
        if (principals == null) principals = Collections.emptySet();
        return principals;
    }

    static Collection<String> getAllPrincipalsIds(){
        return getAllPrincipals().stream().map(Principal::getUid).collect(Collectors.toSet());
    }

    static boolean applyUser(AcuityBottingUser user){
        Principal acuityPrincipal = Principal.of(PrincipalLinkTypes.ACUITY, user.getPrincipalId());
        Set<Principal> principals = new HashSet<>(user.getLinkedPrincipals());
        principals.add(acuityPrincipal);

        UI current = UI.getCurrent();
        if (current == null) return false;
        current.getSession().setAttribute(Authed.ACUITY_USER, user);
        current.getSession().setAttribute(Authed.ACUITY_PRINCIPAL, acuityPrincipal);
        current.getSession().setAttribute(Authed.ALL_PRINCIPALS, principals);
        return true;
    }

    static AcuityBottingUser getAcuityUser(){
        return (AcuityBottingUser) UI.getCurrent().getSession().getAttribute(ACUITY_USER);
    }

    static String getAcuityPrincipalId(){
        Principal acuityPrincipal = getAcuityPrincipal();
        if (acuityPrincipal == null) return null;
        return acuityPrincipal.getUid();
    }

    static Principal getAcuityPrincipal(){
        UI current = UI.getCurrent();
        if (current == null || current.getSession() == null) return null;
        return (Principal) current.getSession().getAttribute(ACUITY_PRINCIPAL);
    }
}
