package com.acuitybotting.website.dashboard.components.general.nav.tabs;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationListener;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
public class NavigationTabs extends Tabs implements AfterNavigationListener{

    public NavigationTabs() {
        setWidth("100%");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        getUI().ifPresent(ui -> ui.addAfterNavigationListener(this));
    }

    public NavigationTabs withTab(NavigationTab tab) {
        add(tab);
        return this;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        String path = afterNavigationEvent.getLocation().getPath();
        Component tab = getChildren()
                .filter(component -> component instanceof NavigationTab)
                .filter(component -> path.equals(getUI().map(ui -> ui.getRouter().getUrl(((NavigationTab) component).getView())).orElse(null)))
                .findAny().orElse(null);
        if (tab != null) setSelectedTab((NavigationTab) tab);
    }
}
