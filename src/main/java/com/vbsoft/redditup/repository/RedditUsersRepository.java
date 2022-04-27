package com.vbsoft.redditup.repository;

import com.vbsoft.redditup.persistence.RedditUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedditUsersRepository extends CrudRepository<RedditUser, Long>, PagingAndSortingRepository<RedditUser, Long> {

    RedditUser findFirstByUsername(String username);

}