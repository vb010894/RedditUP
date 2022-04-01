package com.vbsoft.redditup.apis;

import com.vbsoft.redditup.persistence.UserModel;
import com.vbsoft.redditup.persistence.UserRole;
import com.vbsoft.redditup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * Init API Controller.
 * @author Vboy
 */
@RestController
@CrossOrigin
@RequestMapping("/apis/")
public class RedditApiController {

    /**
     * User service.
     */
    private final UserService serUsers;

    /**
     * Constructor.
     * @param service User service
     */
    @Autowired
    public RedditApiController(UserService service) {
        this.serUsers = service;
    }

    /**
     * Get request for add start user.
     * @return Status
     */
    @GetMapping
    public String createUsers() {
        UserModel userModel = new UserModel();
        userModel.setUsername("admin");
        userModel.setPassword("SamFisher010894@pc");
        userModel.setCredentialsNonExpired(true);
        userModel.setAccountNonExpired(true);
        userModel.setAccountNonLocked(true);
        userModel.setEnabled(true);

        UserRole role = new UserRole();
        role.setName("ROLE_ADMIN");
        role.setUsers(Collections.singletonList(userModel));
        userModel.setRoles(Collections.singletonList(role));
        this.serUsers.saveUser(userModel);
        return "ok";
    }
}
