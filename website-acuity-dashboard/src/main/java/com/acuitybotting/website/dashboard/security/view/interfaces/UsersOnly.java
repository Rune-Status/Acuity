package com.acuitybotting.website.dashboard.security.view.interfaces;

import com.acuitybotting.website.dashboard.views.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
public interface UsersOnly extends BeforeEnterObserver {

    @Override
    default void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (getPrincipalUid() == null) beforeEnterEvent.rerouteTo(LoginView.class);
    }

    default String getPrincipalUid(){
        UI current = UI.getCurrent();
        if (current == null || current.getSession() == null) return null;
        return (String) current.getSession().getAttribute("principalUid");
    }
}
