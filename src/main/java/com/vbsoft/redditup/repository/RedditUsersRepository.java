package com.vbsoft.redditup.repository;

import com.vbsoft.redditup.persistence.RedditUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedditUsersRepository extends CrudRepository<RedditUser, Long> {

    RedditUser findFirstByUsername(String username);

}