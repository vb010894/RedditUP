package com.vbsoft.redditup.service;

import com.vbsoft.redditup.persistence.RedditUser;
import com.vbsoft.redditup.repository.RedditUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class RedditUserService {

    @Autowired
    private RedditUsersRepository repository;


    public List<RedditUser> getUsers() {
        Iterable<RedditUser> users = this.repository.findAll();
        List<RedditUser> result = new LinkedList<>();
        users.forEach(result::add);

        return result;
    }

    public void addUsers(List<RedditUser> users) {
        this.repository.saveAll(users);
    }

    public void addUser(RedditUser user) {
        this.repository.save(user);
    }

    public void addUser(String username, String password) {
        RedditUser users = new RedditUser();
        users.setUsername(username);
        users.setPassword(password);
        this.addUser(users);
    }

    public void deleteUser(RedditUser users) {
        this.repository.delete(users);
    }

}
