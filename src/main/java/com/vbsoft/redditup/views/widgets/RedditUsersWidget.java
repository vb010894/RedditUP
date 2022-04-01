package com.vbsoft.redditup.views.widgets;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vbsoft.redditup.persistence.RedditUser;
import com.vbsoft.redditup.service.RedditUserService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reddit user view.
 * @author Vboy
 */
@Component
@Scope("prototype")
public class RedditUsersWidget extends VerticalLayout {

    /**
     * Reddit's user service.
     */
    private final RedditUserService serRedditUsers;

    /**
     * Reddit's grid.
     */
    private final Grid<RedditUser> fRedditUserGrid = new Grid<>(RedditUser.class, false);

    /**
     * Reddit's grid data.
     */
    private GridListDataView<RedditUser> fGridData;

    /**
     * Constructor.
     * @param service Reddit's user service.
     */
    public RedditUsersWidget(RedditUserService service) {
        this.serRedditUsers = service;
        this.addToolBar();
        this.addGrid();
    }

    /**
     * Add toolbar.
     */
    private void addToolBar() {
        Button add = new Button(VaadinIcon.PLUS.create(), event -> this.fireAddUser(null));
        Button importUsers = new Button(VaadinIcon.INSERT.create(), event -> this.fireImport());
        HorizontalLayout tools = new HorizontalLayout(add, importUsers);
        add(tools);
    }

    /**
     * Add grid.
     */
    private void addGrid() {
        this.fRedditUserGrid.addColumn(RedditUser::getUsername).setHeader("Username");
        this.fRedditUserGrid.addColumn(RedditUser::getPassword).setHeader("Password");
        this.fRedditUserGrid.addColumn(RedditUser::isEnabled).setHeader("Enabled");

        this.setHeightFull();
        this.fGridData = this.fRedditUserGrid.setItems(this.serRedditUsers.getUsers());
        this.fRedditUserGrid.addComponentColumn(item -> {
            HorizontalLayout controls = new HorizontalLayout();
            Button delete = new Button(VaadinIcon.TRASH.create(), event -> this.fireDeleteUser(item));
            Button edit = new Button(VaadinIcon.EDIT.create(), event -> this.fireAddUser(item));
            Button stop = new Button(
                    (item.isEnabled()) ? VaadinIcon.STOP.create() : VaadinIcon.PLAY.create(),
                    event -> this.fireStopUser(item));
            controls.add(edit, stop, delete);
            return controls;
        }).setHeader("Controls");
        add(this.fRedditUserGrid);
    }

    /**
     * Fire user import.
     */
    private void fireImport() {
        Dialog importUsers = new Dialog();
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(event -> this.importUsers(event, buffer));
        importUsers.add(upload);
        add(importUsers);
        importUsers.open();
    }

    /**
     * Import user.
     * @param event Import event
     * @param buffer Import buffer
     */
    private void importUsers(SucceededEvent event, MultiFileMemoryBuffer buffer) {
        try {
            InputStream stream = buffer.getInputStream(event.getFileName());
            File out = new File("reddit/users/" + event.getFileName());
            if(!out.getParentFile().exists()) {
                if(!out.getParentFile().mkdirs())
                    event.getUpload().setDropAllowed(false);
            }
            Files.copy(stream, out.toPath());
            String users = FileUtils.readFileToString(out, StandardCharsets.UTF_8);
            this.saveUsers(users);
        } catch (IOException e) {
            Notification.show(e.getMessage());
            event.getUpload().setDropAllowed(false);
        }

    }

    /**
     * Save reddit user.
     * @param users Reddit user
     */
    private void saveUsers(String users) {
        List<RedditUser> mod = Arrays.stream(users.split("\r\n")).map(item -> {
           String[] cred = item.split(":");
           RedditUser user = new RedditUser();
           user.setUsername(cred[0].trim());
           user.setPassword(cred[1].trim());
           return user;
        }).collect(Collectors.toList());
        this.fGridData.addItems(mod);
        this.fGridData.refreshAll();
        this.serRedditUsers.addUsers(mod);
    }

    /**
     * Stop/start user.
     * @param user Reddit user
     */
    private void fireStopUser(RedditUser user) {
        user.setEnabled(!user.isEnabled());
        this.fGridData.refreshItem(user);
        this.saveUser(user);
    }

    /**
     * Fire delete user.
     * @param user Reddit user
     */
    private void fireDeleteUser(RedditUser user) {
        this.fGridData.removeItem(user);
        this.fGridData.refreshAll();
        this.serRedditUsers.deleteUser(user);
    }

    /**
     * Fire add user.
     * @param user Reddit user
     */
    private void fireAddUser(RedditUser user) {
        Dialog addWindow = new Dialog();
        VerticalLayout form = new VerticalLayout();
        String title = (user == null) ? "Add new Reddit user": "Edit Reddit user";
        Label label = new Label(title);

        TextField username = new TextField();
        username.setPrefixComponent(VaadinIcon.USER_CARD.create());
        username.setPlaceholder("Reddit username");

        TextField password = new TextField();
        password.setPrefixComponent(VaadinIcon.PASSWORD.create());
        password.setPlaceholder("Reddit user password");

        Button save = new Button("Save", event -> {
            if (Strings.isBlank(username.getValue()) & Strings.isBlank(password.getValue())) {
                Notification.show("Fill all fields, please");
                return;
            }
            RedditUser temp = (user == null) ? new RedditUser() : user;
            temp.setUsername(username.getValue());
            temp.setPassword(password.getValue());
            temp.setEnabled(true);
            if(user != null) {
                this.fGridData.removeItem(user);
            }

            this.fGridData.addItem(temp);
            this.fGridData.refreshAll();
            this.saveUser(temp);
        });

        form.add(label, username, password, save);
        addWindow.add(form);
        add(addWindow);
        addWindow.open();
    }

    /**
     * Save user.
     * @param model Reddit user
     */
    private void saveUser(RedditUser model) {
        this.serRedditUsers.addUser(model);
    }

}
