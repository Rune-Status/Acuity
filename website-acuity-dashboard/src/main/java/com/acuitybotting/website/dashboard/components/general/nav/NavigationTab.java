package com.acuitybotting.website.dashboard.components.general.nav;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;

/**
 * Created by Zachary Herridge on 8/8/2018.
 */
public class NavigationTab extends Tab {

    private final Class<? extends Component> view;

    public NavigationTab(String label, Class<? extends Component> view) {
        super(label);
        this.view = view;
    }

    public void navigateTo(){
        getUI().ifPresent(ui -> ui.navigate(view));
    }

    public Class<? extends Component> getView() {
        return view;
    }
}
