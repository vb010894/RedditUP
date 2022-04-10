package com.vbsoft.redditup.service;

import com.vbsoft.redditup.persistence.UserModel;
import com.vbsoft.redditup.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserModelRepository user;

    @Autowired
    public UserService(UserModelRepository user) {
        this.user = user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel target = this.user.findUserModelByUsername(username);

        if(target == null) {
           throw new UsernameNotFoundException("User not found");
        }

        return user.findUserModelByUsername(username);
    }

    public List<UserModel> findAll() {
        List<UserModel> result = new LinkedList<>();
        this.user.findAll().forEach(result::add);
        return result;
    }

    public void saveUser(UserModel user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        this.user.save(user);
    }

    public void deleteUser(UserModel user) {
        this.user.delete(user);
    }




}
