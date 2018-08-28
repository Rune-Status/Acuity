package com.acuitybotting.website.dashboard.components.general.nav;

import com.acuitybotting.db.arangodb.repositories.acuity.principal.domain.AcuityBottingUser;
import com.acuitybotting.website.dashboard.utils.Authentication;
import com.acuitybotting.website.dashboard.views.connections.clients.ClientsListView;
import com.acuitybotting.website.dashboard.views.resources.accounts.AccountsListView;
import com.acuitybotting.website.dashboard.views.user.ProfileView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Created by Zachary Herridge on 7/18/2018.
 */
public class TopMenuComponent extends HorizontalLayout {

    public TopMenuComponent() {
        getClassNames().add("acuity-top-menu");
        setWidth("100%");
        setHeight("50px");
        setMargin(false);
        setSpacing(false);

        HorizontalLayout bar = new HorizontalLayout();
        bar.getClassNames().add("acuity-top-menu-nav");

        Image image = new Image("/imgs/acuity-logo-128.png", "");
        image.setHeight("50px");
        image.setWidth("50px");
        image.getStyle().set("margin-right", "10px");
        bar.add(image);

        HorizontalLayout barLeft = new HorizontalLayout();
        barLeft.setSizeFull();
        barLeft.add(new NavigationButton("Connections", null, ClientsListView.class));
        barLeft.add(new NavigationButton("Resources", null, AccountsListView.class));
        bar.add(barLeft);

        HorizontalLayout barRight = new HorizontalLayout();

        Image profileImage = new Image(Authentication.getAcuityUserOptional().map(AcuityBottingUser::getProfileImgUrl).orElse("https://www.gravatar.com/avatar/e71231152729827c401741cf99d27e33?s=64&d=identicon&r=PG&f=1"), "");
        profileImage.getClassNames().add("acuity-top-nav-profile-img");
        profileImage.getElement().addEventListener("click", domEvent -> UI.getCurrent().navigate(ProfileView.class));
        barRight.add(profileImage);
        bar.add(barRight);

        add(bar);
    }
}
