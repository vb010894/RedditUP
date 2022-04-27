package com.vbsoft.redditup.service;

import com.vbsoft.redditup.persistence.RedditUser;
import com.vbsoft.redditup.repository.RedditUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RedditUserService {

    private final RedditUsersRepository repository;

    @Autowired
    public RedditUserService(RedditUsersRepository repository) {
        this.repository = repository;
    }


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

    public long getUserCount() {
        return this.repository.count();
    }

    public List<RedditUser> users(int start, int finish) {
        Page<RedditUser> page = this.repository.findAll(PageRequest.of(start, finish));
        return page.stream().parallel().collect(Collectors.toList());
    }

}
