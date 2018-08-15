package com.acuitybotting.website.dashboard.views.resources.accounts;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents.RsAccountInfo;
import com.acuitybotting.website.dashboard.components.general.list_display.InteractiveList;
import com.acuitybotting.website.dashboard.services.AccountsService;
import com.acuitybotting.website.dashboard.utils.Components;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.annotation.SessionScope;

@SpringComponent
@SessionScope
public class AccountListComponent extends InteractiveList<RsAccountInfo> {

    @Autowired
    public AccountListComponent(AccountsService accountsService) {
        getControls().add(Components.button(VaadinIcon.PLUS_CIRCLE,  event -> getUI().ifPresent(ui -> ui.navigate(AccountEditView.class))));

        withColumn("Email", "30%", document -> {
            Span span = new Span();
            span.getElement().addEventListener("click", domEvent -> getUI().ifPresent(ui -> ui.navigate(AccountView.class, document.getSubKey())));
            return span;
        }, (document, span) -> span.setText(document.getSubKey()));
        withColumn("Last World", "10%", document -> new Span(), (document, span) -> span.setText(String.valueOf(document.getWorld())));
        withColumn("Password", "10%", document -> new Span(), (document, span) -> span.setText(document.getEncryptedPassword() != null ? "Set" : "None"));
        withColumn("", "5%", rsAccountInfo -> Components.button(VaadinIcon.EDIT, event -> getUI().ifPresent(ui -> ui.navigate(AccountEditView.class, rsAccountInfo.getSubKey()))), (rsAccountInfo, button) -> {});
        withLoad(RsAccountInfo::getSubKey, accountsService::loadAccounts);
    }
}