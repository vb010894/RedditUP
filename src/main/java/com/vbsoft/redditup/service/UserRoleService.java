package com.vbsoft.redditup.service;

import com.vbsoft.redditup.persistence.UserRole;
import com.vbsoft.redditup.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRoleService {

    private UserRoleRepository repository;

    @Autowired
    public UserRoleService(UserRoleRepository roleRepository) {
        this.repository = roleRepository;
    }

    public List<UserRole> getRoles() {
        List<UserRole> result = new ArrayList<>();
        this.repository.findAll().forEach(result::add);
        return result;
    }

}
