package org.rabbit.login.repository;

import org.rabbit.login.entity.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author nine
 */
@Repository
public interface UserRepository extends JpaRepository<LoginUser, String> {

    Optional<UserDetails> findByUsername(String username);

}
