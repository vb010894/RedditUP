package com.vbsoft.redditup.views;

import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vbsoft.redditup.persistence.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Route("/")
public class LoginPage extends VerticalLayout {

    private final DaoAuthenticationProvider provider;
    private final LoginForm loginForm = new LoginForm();

    @Autowired
    public LoginPage(DaoAuthenticationProvider manager) {
        this.provider = manager;
        this.loginForm.setForgotPasswordButtonVisible(false);
        loginForm.addLoginListener(this::login);
        setAlignItems(Alignment.CENTER);
        setWidthFull();
        setHeightFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(loginForm);
    }

    public void login(AbstractLogin.LoginEvent event) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(event.getUsername(), event.getPassword());
        try {
            authentication = provider.authenticate(authentication);
        } catch (BadCredentialsException ex) {
            this.loginForm.setError(true);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserModel model = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(model.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("ROLE_ADMIN")))
            getUI().get().getPage().setLocation("/admin/content");
        else
            getUI().get().getPage().setLocation("/content");

    }


}
