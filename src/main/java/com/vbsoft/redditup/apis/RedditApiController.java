package com.vbsoft.redditup.apis;

import com.vbsoft.redditup.persistence.TelegramBot;
import com.vbsoft.redditup.persistence.UserModel;
import com.vbsoft.redditup.persistence.UserRole;
import com.vbsoft.redditup.service.RedditUserService;
import com.vbsoft.redditup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.stream.Collectors;

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

    private final RedditUserService serReddit;

    /**
     * Constructor.
     * @param service User service
     */
    @Autowired
    public RedditApiController(UserService service, RedditUserService reddit) {
        this.serUsers = service;
        this.serReddit = reddit;
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
    /**
     * Get request for add start user.
     * @return Status
     */
    @GetMapping("/telegram")
    @Autowired
    public String testTelegram(TelegramBot telegramBot) {
        telegramBot.sendMessageToChat("Я готов к работе");
        return "ok";
    }

    @GetMapping("/reddit/users")
    public String importUsers() {
        return this.serReddit
                .getUsers()
                .parallelStream()
                .map(user -> user.getUsername() + ":" + user.getPassword())
                .collect(Collectors.joining("\r\n"));
    }
}
