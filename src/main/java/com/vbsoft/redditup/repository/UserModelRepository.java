package com.vbsoft.redditup.repository;

import com.vbsoft.redditup.persistence.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserModelRepository extends CrudRepository<UserModel, Long> {

    UserModel findUserModelByUsername(String username);

}