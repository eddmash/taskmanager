package com.eddmash.app.authentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.eddmash.app.authentication.model.User;

@Repository
public interface UserRepository
                extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);

}
