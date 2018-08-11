package com.acuitybotting.website.dashboard.security.view.interfaces;

import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.views.administration.LoginView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
public interface Authed extends BeforeEnterObserver {

    @Override
    default void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (Authentication.getAcuityPrincipalId() == null) beforeEnterEvent.rerouteTo(LoginView.class);
    }
}
