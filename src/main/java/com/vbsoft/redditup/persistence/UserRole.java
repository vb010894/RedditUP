package com.vbsoft.redditup.persistence;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;

@Entity(name = "user_role")
@Data
public class UserRole implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    @Transient
    @ManyToMany(mappedBy = "roles")
    private List<UserModel> users;

    @Override
    public String getAuthority() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
