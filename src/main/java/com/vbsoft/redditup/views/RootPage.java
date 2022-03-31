package com.vbsoft.redditup.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vbsoft.redditup.views.widgets.MainWidget;
import com.vbsoft.redditup.views.widgets.RedditUsersWidget;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Ordinal user content.
 * @author Vboy
 * @version 1.0
 */
@Route("content")
@Component
@PreserveOnRefresh
public class RootPage extends AppLayout {

    @Getter
    private final Map<String, Object> pagesRef = new HashMap<>();
    @Getter
    private final Tabs pages = new Tabs();


    /**
     * Constructor.
     */
    @Autowired
    public RootPage(MainWidget main, RedditUsersWidget redditUsers) {
        pagesRef.put("main", main);
        pagesRef.put("reddit_user", redditUsers);
        this.setContent(main);
        this.addMainLabel();
        this.addHeaders();
        this.addLogout();
        this.setContent((com.vaadin.flow.component.Component) pagesRef.get("main"));
    }

    /**
     * Add navigation tabs.
     */
    private void addHeaders() {
        this.pages.addThemeVariants(TabsVariant.LUMO_CENTERED);
        this.pages.getStyle()
                .set("width", "80%");
        Tab main = new Tab(VaadinIcon.STAR.create(), new Span("MAIN"));
        main.setId("main");
        Tab users = new Tab(VaadinIcon.USER_CARD.create(), new Span("REDDIT USERS"));
        users.setId("reddit_user");
        this.pages.add(main, users);
        this.pages.setSelectedTab(main);
        this.pages.addSelectedChangeListener(this::fireSelect);
        addToNavbar(this.pages);
    }

    private void fireSelect(Tabs.SelectedChangeEvent event) {
        this.remove(this.getContent());
        event.getSelectedTab().getId().ifPresent(id -> this.setContent((com.vaadin.flow.component.Component) this.pagesRef.get(id)));
    }

    /**
     * Add main label.
     */
    private void addMainLabel() {
        Label title = new Label("RedditUp");
        title.getStyle()
                .set("margin", "0 2% 0 2%")
                .set("width", "10%");
        addToNavbar(title);
    }

    /**
     * Add logout button.
     */
    private void addLogout() {
        Label out = new Label("LOGOUT");
        out.getStyle().set("margin", "0 5% 0 5%");
        Span logout = new Span(VaadinIcon.EXIT_O.create(), out);
        logout.getStyle()
                .set("margin", "0 0 0 2%")
                .set("width", "10%");
        logout.addClickListener(event -> this.getUI().ifPresent(ui -> ui.getPage().setLocation("/logout")));
        addToNavbar(out);
    }

}
