package com.vbsoft.redditup.views.widgets;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vbsoft.redditup.persistence.UserModel;
import com.vbsoft.redditup.persistence.UserRole;
import com.vbsoft.redditup.service.UserRoleService;
import com.vbsoft.redditup.service.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Users view.
 * @author Vboy
 */
@Component
@Scope("prototype")
public class UsersWidget extends VerticalLayout {

    /**
     * User service.
     */
    private final UserService serUsers;

    /**
     * User role service
     */
    private final UserRoleService serRoles;

    /**
     * User grid.
     */
    private final Grid<UserModel> fUsersGrid = new Grid<>(UserModel.class, false);

    /**
     * User grid data.
     */
    private GridListDataView<UserModel> gridData;

    /**
     * Constructor.
     * @param userService User service
     * @param roleService Role service
     */
    @Autowired
    public UsersWidget(UserService userService, UserRoleService roleService) {
        this.serUsers = userService;
        this.serRoles = roleService;
        this.addUserGrid();
    }

    /**
     * Add user grid.
     */
    private void addUserGrid() {
        this.gridData = this.fUsersGrid.setItems(this.serUsers.findAll());
        this.fUsersGrid
                .addColumn(UserModel::getUsername)
                .setHeader("Username")
                .setAutoWidth(true);
        this.fUsersGrid
                .addColumn(UserModel::getPassword)
                .setHeader("Password Hash")
                .setAutoWidth(true);
        this.fUsersGrid
                .addColumn(UserModel::isEnabled)
                .setHeader("Enabled")
                .setAutoWidth(true);
        this.fUsersGrid
                .addColumn(UserModel::isAccountNonLocked)
                .setHeader("Non Locked")
                .setAutoWidth(true);

        this.fUsersGrid.setAllRowsVisible(true);

        this.addControlColumn();

        TextField search = this.getSearchBar();
        search.addValueChangeListener(e -> gridData.refreshAll());
        this.gridData.addFilter(item -> {
           String searchMatch = search.getValue().trim();
           if(searchMatch.isEmpty())
               return true;

           return item.getUsername().toLowerCase().contains(searchMatch.toLowerCase());
        });

        Button add = new Button(VaadinIcon.PLUS.create(), event -> this.fireAddNewUser(null));
        HorizontalLayout tools = new HorizontalLayout(add, search);
        add(tools, fUsersGrid);
    }

    /**
     * Add search field.
     * @return Search field
     */
    private TextField getSearchBar() {
        TextField search = new TextField();
        search.setWidth("50%");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setValueChangeMode(ValueChangeMode.EAGER);
        return search;
    }

    /**
     * Add control column to user grid.
     */
    private void addControlColumn() {
        this.fUsersGrid.addComponentColumn(item -> {
            HorizontalLayout layout = new HorizontalLayout();
            Button delete = new Button(
                    VaadinIcon.TRASH.create(),
                    event -> this.fireDeleteUser(item));
            Button edit = new Button(
                    VaadinIcon.EDIT.create(),
                    event -> this.fireEditUser(item));

            Button block = new Button(
                    (item.isEnabled()) ? VaadinIcon.STOP.create() : VaadinIcon.START_COG.create(),
                    event -> this.fireBlockUser(item));
            layout.add(edit, block, delete);
            return layout;
        }).setHeader("Controls");
    }

    /**
     * Fire block user event.
     * @param model User model
     */
    private void fireBlockUser(UserModel model) {
        model.setEnabled(!model.isEnabled());
        this.gridData.removeItem(model);
        this.gridData.addItem(model);
        this.serUsers.saveUser(model);
        this.gridData.refreshAll();
    }

    /**
     * Fire add/update user.
     * @param model User model
     */
    private void fireAddNewUser(UserModel model) {
        Dialog edit = new Dialog();
        edit.setCloseOnEsc(true);

        VerticalLayout form = new VerticalLayout();
        TextField username = new TextField();
        username.setPrefixComponent(VaadinIcon.USER.create());
        username.setPlaceholder("Username");

        TextField password = new TextField();
        password.setPrefixComponent(VaadinIcon.PASSWORD.create());
        password.setPlaceholder("Password");

        ComboBox<UserRole> role = new ComboBox<>();
        role.setItems(this.serRoles.getRoles());
        role.setPlaceholder("Roles");
        role.setItemLabelGenerator(UserRole::getName);

        Label title = new Label();

        if(model != null) {
            username.setValue(model.getUsername());
            role.setValue(model.getRoles().get(0));
            title.setText("Edit User");
        } else {
            title.setText("New User");
        }

        Button save = new Button("Save", event -> {
            UserModel target = model != null ? model : new UserModel();
                if(
                        Strings.isBlank(username.getValue())
                                & Strings.isBlank(password.getValue())
                                & role.getValue() == null) {
                    Notification.show("Any field aren't filled");
                } else {
                    UserModel newModel = this.getUser(
                            target,
                        username.getValue(),
                        password.getValue(),
                        role.getValue());
                    this.saveUser(newModel);
                }

        });

        form.add(title, username, password, role, save);
        edit.add(form);
        add(edit);
        edit.open();
    }

    /**
     * Save user.
     * @param model User model
     */
    private void saveUser(UserModel model) {
        this.gridData.addItem(model);
        this.gridData.refreshAll();
        this.serUsers.saveUser(model);
    }

    /**
     * Get user by username, password, role.
     * @param username Username
     * @param password Password
     * @param role Role
     * @return User model
     */
    private UserModel getUser(UserModel target, String username, String password, UserRole role) {
        UserModel model = (target != null) ? target : new UserModel();
        model.setUsername(username);
        model.setPassword(password);
        model.setEnabled(true);
        model.setAccountNonExpired(true);
        model.setCredentialsNonExpired(true);
        model.setRoles(Collections.singletonList(role));
        return model;
    }

    /**
     * Fire edit user event
     * @param model User model
     */
    private void fireEditUser(UserModel model) {
        this.fireAddNewUser(model);
    }

    /**
     * Fire delete user event
     * @param item User model
     */
    private void fireDeleteUser(UserModel item) {
        this.gridData.removeItem(item);
        this.serUsers.deleteUser(item);
        this.gridData.refreshAll();
    }

}
