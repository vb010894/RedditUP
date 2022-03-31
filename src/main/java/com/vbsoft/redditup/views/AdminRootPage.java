package com.vbsoft.redditup.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vbsoft.redditup.views.widgets.MainWidget;
import com.vbsoft.redditup.views.widgets.RedditUsersWidget;
import com.vbsoft.redditup.views.widgets.UsersWidget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Route("admin/content")
@Component
@PreserveOnRefresh
public class AdminRootPage extends RootPage {

    /**
     * Constructor.
     *
     * @param main
     * @param redditUsers
     */
    @Autowired
    public AdminRootPage(MainWidget main, RedditUsersWidget redditUsers, UsersWidget users) {
        super(main, redditUsers);
        Tab userTab = new Tab(VaadinIcon.USER.create(), new Span("Users"));
        userTab.setId("users");
        this.getPages().add(userTab);
        this.getPagesRef().put("users", users);
    }
}
