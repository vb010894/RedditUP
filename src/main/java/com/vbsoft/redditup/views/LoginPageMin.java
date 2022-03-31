package com.vbsoft.redditup.views;

import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
@Route("login")
public class LoginPageMin extends LoginPage {

    @Autowired
    public LoginPageMin(DaoAuthenticationProvider redditUserService) {
        super(redditUserService);
    }
}
