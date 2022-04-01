package com.vbsoft.redditup.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vbsoft.redditup.views.widgets.MainWidget;
import com.vbsoft.redditup.views.widgets.RedditUsersWidget;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Ordinal user content.
 * @author Vboy
 * @version 1.0
 */
@Route("/content")
@Component
@Scope("prototype")
@PreserveOnRefresh
public class RootPage extends AppLayout {

    /**
     * Page reference.
     */
    @Getter
    private final Map<String, Object> pagesRef = new HashMap<>();

    /**
     * Navigation pages.
     */
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
        this.remove(this.getContent());
        this.setContent(main);
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
        HorizontalLayout flexLayout = new HorizontalLayout();
        flexLayout.setWidth("10%");
        Button logout = new Button("Logout", VaadinIcon.EXIT.create());
        logout.getStyle().set("background", "none");
        logout.setWidthFull();
        logout.setHeightFull();
        logout.addClickListener(event -> this.getUI().ifPresent(ui -> ui.getPage().setLocation("/logout")));
        flexLayout.add(logout);
        flexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        addToNavbar(flexLayout);
    }

}
